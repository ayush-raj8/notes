import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.latency-cpu-low",
  status: "complete",
  objectives: [
    "Explain latency growth when CPU is idle.",
    "Separate RTT/window limits, queueing, and remote waits.",
    "Use percentiles and phase timings, not averages alone.",
  ],
  prerequisites: ["m13.intermittent-timeouts"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Users see high latency; host CPU and often disk look idle.\n• Teams look for a hot loop that is not there.",
      whyItMatters:
        "• Waiting on the network or a dependency burns wall time without burning CPU.\n• Averages hide the tail (why.46).",
      idea: "• Low CPU means the process is blocked or waiting—not that the system is healthy.\n• Find where time goes: DNS, connect, TLS, TTFB, or waiting on a peer.",
      howItWorks:
        "• Break latency with curl write-out or app spans.\n• Check BDP/window and socket buffer stalls (`ss -ti`).\n• Inspect dependency RTT and queue depth; head-of-line on shared connections.",
      whyItWorks:
        "• CPU counters ignore time spent in poll/sleep waiting for bytes or locks on remote I/O.",
      limits:
        "• Softirq/network CPU can be on another core than the app metric you watch.\n• GC pauses are CPU-ish but short; still check runtimes.",
      connections: "• Module 11 metrics and BDP.\n• Large-response and periodic-spike scenarios.",
    },
    {
      type: "why",
      id: "why-average",
      questionId: "why.46",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "Waiting on a remote dependency (DB, API) with low local CPU.",
        "TCP window or socket buffer limiting throughput on a long RTT path.",
        "Queueing in a proxy or kernel listen/accept path under concurrency.",
        "Head-of-line blocking on HTTP/1.1 or a single TCP connection.",
        "DNS or connect delays that never show as app CPU.",
        "Lock contention where threads sleep—CPU still low.",
      ],
      order: [
        "Confirm CPU metric scope (container vs node vs process).",
        "Split time: namelookup, connect, appconnect, starttransfer, total.",
        "Check whether latency tracks dependency RTT or local queue depth.",
        "Inspect `ss` for Recv-Q/Send-Q growth and TCP windows.",
        "Sample p99, not only the mean.",
      ],
      commands: [
        {
          command:
            "curl -o /dev/null -s -w 'namelookup:%{time_namelookup} connect:%{time_connect} tls:%{time_appconnect} ttfb:%{time_starttransfer} total:%{time_total}\\n' https://api.example.com/",
          shows: "Which phase owns the latency.",
        },
        {
          command: "ss -ti dst :443",
          shows: "RTT, congestion window, and whether the socket is stalled.",
        },
        {
          command: "ss -ltnp",
          shows: "Listen Recv-Q (accept backlog) when new work piles up.",
        },
      ],
      implications: [
        "time_starttransfer huge, CPU low: waiting on upstream/app logic, not local compute.",
        "time_connect high: path or backlog—not business logic.",
        "Send-Q stuck high: peer not reading or window closed.",
        "Mean fine, p99 bad: tail latency; averages lie (why.46).",
      ],
      distinguishing: [
        "Network RTT vs dependency wait: span the outbound call.",
        "CPU on another core: check softirq / network CPU separately.",
        "Throughput cap vs latency: large transfer limited by window (Module 11).",
      ],
      mistakes: [
        "Scaling CPU when the process is idle waiting on I/O.",
        "Tuning only the mean latency SLO.",
        "Ignoring listen Recv-Q because 'CPU is fine'.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "phase",
          prompt:
            "CPU idle; curl shows time_starttransfer ≈ time_total ≫ time_appconnect. Where is time spent?",
          answer:
            "After TLS, waiting for the first response byte—origin or an upstream dependency, not connect/TLS.",
        },
        {
          id: "p99",
          prompt: "Average latency is 80 ms; users complain. What metric next?",
          answer:
            "p99 / p99.9 or fraction over a threshold—averages hide rare slow calls (why.46).",
        },
      ],
    },
  ],
  claims: [
    {
      id: "curl-phases",
      statement:
        "curl write-out can report time_namelookup, time_connect, time_appconnect, time_starttransfer, and time_total.",
      sourceId: "curl-manpage",
      locator: "-w, --write-out",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "ss-info",
      statement: "ss can show TCP internal info such as rtt and cwnd with -i/-t.",
      sourceId: "man-ss",
      locator: "DESCRIPTION; -i, --info",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "curl-manpage", note: "Phase timings." },
    { sourceId: "man-ss", note: "Socket queues and TCP info." },
    { sourceId: "rfc7323", note: "Window scaling / large windows on long fat paths." },
  ],
} satisfies LessonBody;
