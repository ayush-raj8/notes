import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.intermittent-timeouts",
  status: "complete",
  objectives: [
    "Find the dimension an intermittent timeout follows.",
    "Separate idle NAT expiry, overloaded queues, and bad backends.",
    "Hold one variable fixed when reproducing.",
  ],
  prerequisites: ["m13.tls-ok-http-5xx"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Some requests succeed; others hit the client or proxy deadline.\n• Retries hide most failures, so the rest look random.",
      whyItMatters:
        "• Random-looking failures are usually state-dependent (why.49).\n• Without a fixed dimension you cannot tell NAT, LB, or app overload apart.",
      idea: "• Intermittent means a changing input: backend, path, idle time, size, or concurrency.\n• Reproduce along one axis; then apply the method order on that slice.",
      howItWorks:
        "• Log peer address, backend id, idle age of the connection, and payload size.\n• Compare fresh connections vs pooled idle ones.\n• Check SNAT/conntrack pressure and upstream error rates by backend.",
      whyItWorks:
        "• Holding the dimension fixed turns 'flaky' into a deterministic scenario from earlier lessons.",
      limits:
        "• True packet loss can be rare; you still need enough samples.\n• Clock skew makes 'same second' correlations misleading across hosts.",
      connections: "• Module 10 SNAT exhaustion; Module 11 retries and partial failure.",
    },
    {
      type: "why",
      id: "why-intermittent",
      questionId: "why.49",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "One bad backend in a pool; only some requests hit it.",
        "Idle pooled connection killed by NAT/LB; first reuse times out.",
        "SNAT port or conntrack exhaustion under bursts to one destination.",
        "Accept queue / thread pool full only at peak concurrency.",
        "Timeouts set tighter than p99 dependency latency; retries amplify load.",
        "ECMP/path diversity: one path drops, another works.",
      ],
      order: [
        "Quantify rate and whether failures cluster by backend, client, size, or idle time.",
        "Disable connection reuse briefly; if failures vanish, suspect idle middleboxes.",
        "Pin or log backend id; drain a suspect instance.",
        "Watch conntrack/SNAT and listen backlog during a failure window.",
        "Capture only when the dimension is forced (one backend, large body, long idle).",
      ],
      commands: [
        {
          command: "ss -tan 'dport = :443' | awk '{print $1}' | sort | uniq -c",
          shows: "Connection state mix; many SYN-SENT under load hints path or backlog issues.",
        },
        {
          command: "sysctl net.netfilter.nf_conntrack_count net.netfilter.nf_conntrack_max",
          shows: "Whether the table is near capacity during bursts.",
        },
        {
          command: "curl -v --max-time 2 https://api.example.com/  # repeat; log time_connect",
          shows: "Whether failures are connect-time or after TLS/HTTP starts.",
        },
      ],
      implications: [
        "Only after idle: NAT/LB idle timeout or server keepalive shorter than pool idle.",
        "Only one backend IP in logs: that instance or its AZ path.",
        "Only under RPS spike: queues, ports, or thread pools—not DNS.",
        "Fresh TCP always works, reuse fails: connection pooling vs middlebox.",
      ],
      distinguishing: [
        "App slowness vs connect timeout: curl write-out phases.",
        "Port exhaustion vs full conntrack: one destination vs all new flows.",
        "Retry amplification vs root cause: error rate rises after deploy of aggressive retries.",
      ],
      mistakes: [
        "Calling it 'heisenbug' without logging backend id and idle age.",
        "Raising timeouts everywhere, hiding overload.",
        "One tcpdump on a good attempt and concluding the network is fine.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "idle-reuse",
          prompt: "Failures only on the first request after 5 minutes quiet. Likely cause?",
          answer:
            "Idle connection dropped by NAT/LB/server while the pool still thought it was live. Retire idle connections earlier or use keepalive probes.",
        },
        {
          id: "dimension",
          prompt: "Why log the backend instance id on timeouts?",
          answer:
            "To see if failures follow one instance (or AZ). Without that dimension, retries make every failure look random (why.49).",
        },
      ],
    },
  ],
  claims: [
    {
      id: "conntrack-count",
      statement:
        "nf_conntrack_count is the number of currently allocated connection-tracking entries; nf_conntrack_max is the allowed maximum.",
      sourceId: "kernel-nf-conntrack-sysctl",
      locator: "nf_conntrack_max, nf_conntrack_count",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "curl-maxtime",
      statement: "curl --max-time aborts the whole operation after the given seconds.",
      sourceId: "curl-manpage",
      locator: "-m, --max-time",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    { sourceId: "kernel-nf-conntrack-sysctl", note: "Table pressure under bursts." },
    { sourceId: "curl-manpage", note: "Deadlines and timing phases." },
    { sourceId: "rfc5382", note: "NAT session behaviour for idle flows." },
    { sourceId: "man-ss", note: "Connection state samples." },
  ],
} satisfies LessonBody;
