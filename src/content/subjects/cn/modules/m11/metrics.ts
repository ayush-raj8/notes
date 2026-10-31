import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m11.metrics",
  status: "complete",
  objectives: [
    "Tell latency, bandwidth, throughput, jitter, and RTT apart, with the unit of each.",
    "Estimate a request's time from its round trips, size, and throughput, and say which term dominates.",
    "Split one slow request into DNS, connect, TLS, server, and transfer time from client timings.",
  ],
  prerequisites: ["m10.connectivity-failures"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• 'The network is slow' can mean high delay, low capacity, a low achieved rate, or uneven delay.\n• Each has a different cause and a different fix.\n• A request's total time mixes network waits with server work.",
      whyItMatters:
        "• A faster link does nothing for a request whose time is spent in round trips.\n• Blaming the network for a slow query, or the reverse, sends the investigation to the wrong team.",
      idea: "• Latency is time. Bandwidth is capacity. Throughput is the rate you actually got. Jitter is how much latency varies.\n• RTT is one round trip, and most protocols pay it several times per request.\n• Time a request by phase so network waits and server work are measured separately.",
      howItWorks:
        "• One-way delay = propagation + transmission (size ÷ link rate) + queueing + processing.\n• Request time ≈ setup round trips × RTT + server time + size ÷ throughput.\n• Small messages are RTT-bound; large transfers are throughput-bound.\n• Jitter is the variation in transit time between packets, not the delay itself.",
      whyItWorks:
        "• Propagation is set by distance, so only fewer round trips or a closer server reduce it.\n• Transmission and queueing depend on rates and load, so capacity or less traffic help those.",
      limits:
        "• `ping` measures one ICMP probe; it excludes TCP, TLS, and the server's own queues.\n• Averages hide spikes; tail latency is covered in the timeouts lesson.\n• What limits throughput (window, loss, the path) is the next lesson.",
      connections:
        "• Module 5: RTT samples drive TCP's retransmission timer.\n• Module 6: jitter buffers for real-time media.\n• Next: the bandwidth-delay product, queues, and loss.",
    },
    {
      type: "comparison",
      id: "five-measurements",
      title: "Five measurements",
      columns: ["What it is", "Unit", "Where you see it"],
      rows: [
        {
          label: "Latency",
          cells: [
            "Time for a packet or message to go from A to B, one way.",
            "ms or µs",
            "Timestamps at both ends with synchronised clocks; often guessed as RTT ÷ 2, which assumes a symmetric path.",
          ],
        },
        {
          label: "Bandwidth",
          cells: [
            "The most a link or path can carry.",
            "bit/s",
            "The link's negotiated speed; the slowest link on the path sets the path's bandwidth.",
          ],
        },
        {
          label: "Throughput",
          cells: [
            "The rate a flow actually achieved.",
            "bit/s or bytes/s",
            "Bytes moved ÷ seconds, from logs or a test tool such as `iperf3`.",
          ],
        },
        {
          label: "Jitter",
          cells: [
            "Variation in delay from one packet to the next.",
            "ms",
            "RTP receiver reports; the spread of `ping` times.",
          ],
        },
        {
          label: "RTT",
          cells: [
            "Time from sending until the reply arrives.",
            "ms",
            "`ping`, the handshake time in a capture, `ss -ti` (TCP's own estimate).",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "delay-parts",
      title: "Where one-way delay comes from",
      steps: [
        {
          title: "Propagation",
          body: "• Distance ÷ signal speed. Light in fibre covers roughly 200 km per ms (a round teaching figure).\n• About 28 ms one way for 5,600 km in a straight line; more on real cable routes.\n• Fixed for a path. No upgrade helps.",
        },
        {
          title: "Transmission",
          body: "• Packet size ÷ link rate. 1500 bytes takes 12 µs at 1 Gbit/s and 1.2 ms at 10 Mbit/s.\n• Paid again on every link.",
        },
        {
          title: "Queueing",
          body: "• Waiting behind other packets in a router, switch, or host buffer.\n• Zero when idle; can reach hundreds of ms on a full buffer. The only part that changes quickly with load.",
        },
        {
          title: "Processing",
          body: "• Forwarding lookups, firewall rules, NAT. Usually µs per hop.\n• The server application's time is separate: it is not network latency.",
        },
      ],
    },
    {
      type: "example",
      id: "rtt-vs-bandwidth",
      title: "A small API call and a large download on the same path",
      setup:
        "RTT 80 ms, path bandwidth 1 Gbit/s, server time 20 ms, HTTPS with TLS 1.3. Illustrative numbers. The team proposes a 10 Gbit/s upgrade.",
      walkthrough: [
        "• 2 KB call on a new connection: TCP handshake 1 RTT + TLS 1 RTT + request and response 1 RTT = 240 ms, plus 20 ms server = 260 ms.",
        "• Transmission of 2 KB at 1 Gbit/s is 16 µs. At 10 Gbit/s it is 1.6 µs. The upgrade saves about 0.01% of the call.",
        "• Same call on a reused connection: 1 RTT + 20 ms = 100 ms. Removing two round trips saved 160 ms.",
        "• 100 MB download: 800 Mbit ÷ 1 Gbit/s = 0.8 s at full rate, plus a few RTTs of handshake and slow start.",
        "• Here bandwidth matters, but only if the sender can keep 1 Gbit/s × 80 ms = 10 MB in flight (next lesson).",
      ],
      result:
        "• Small requests are paid in round trips; large transfers in throughput. Count the round trips before buying bandwidth.",
    },
    {
      type: "steps",
      id: "app-vs-network",
      title: "Splitting a slow request: application or network?",
      steps: [
        {
          title: "Time each phase from the client",
          body: "• `curl -w` prints cumulative times from the start: `time_namelookup`, `time_connect`, `time_appconnect`, `time_starttransfer`, `time_total`.",
        },
        {
          title: "Subtract to get phases",
          body: "• DNS = namelookup.\n• TCP connect ≈ connect − namelookup ≈ 1 RTT.\n• TLS ≈ appconnect − connect ≈ 1 RTT for TLS 1.3.\n• Waiting for the first byte = starttransfer − appconnect ≈ 1 RTT + server time.\n• Download = total − starttransfer.",
        },
        {
          title: "Compare with the server's own timing",
          body: "• The server logs how long it spent on the request.\n• Server says 20 ms, client waited 900 ms for the first byte, RTT is 40 ms: about 840 ms is unaccounted for.\n• That gap is a queue in front of the handler (accept backlog, proxy, thread pool) or the network.",
        },
        {
          title: "Decide",
          body: "• Connect and TLS ≈ 1 RTT each, and the gap is in time to first byte: application or what sits in front of it.\n• Connect itself is slow or varies by seconds: SYN loss and retransmission, or a full backlog.\n• Download slow for its size: throughput (window, loss, the path).",
        },
      ],
    },
    {
      type: "callout",
      id: "ping-is-not-latency",
      tone: "caution",
      title: "Ping is not your service's latency",
      body: "• `ping` times an ICMP echo. It skips DNS, TCP, TLS, and the server's queues.\n• Routers may rate-limit or deprioritise ICMP, and ICMP can take a different path than TCP to port 443.\n• A clean ping and a slow request usually point above the network.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "upgrade-no-help",
          prompt:
            "A link goes from 100 Mbit/s to 1 Gbit/s. 1 KB API calls over a 50 ms RTT path are no faster. Why?",
          answer:
            "They are RTT-bound. Transmitting 1 KB drops from about 80 µs to 8 µs, which is nothing next to one or more 50 ms round trips. Fewer round trips (connection reuse) would help.",
        },
        {
          id: "bandwidth-vs-throughput",
          prompt:
            "The NIC reports 1 Gbit/s. A single transfer reaches 200 Mbit/s. Which number is bandwidth and which is throughput, and is the NIC the limit?",
          answer:
            "1 Gbit/s is the link's bandwidth; 200 Mbit/s is the flow's throughput. The NIC is not the limit. Look at the window and RTT, loss, a slower link later on the path, competing traffic, or the endpoints.",
        },
        {
          id: "curl-split",
          prompt:
            "`curl -w` shows connect 0.081 s, appconnect 0.163 s, starttransfer 1.170 s, total 1.175 s. Where did the time go?",
          answer:
            "Connect and TLS took about 80 ms each, so RTT is about 80 ms. Time to first byte after TLS is about 1.0 s, of which one RTT is network. Roughly 0.93 s is the server or a queue in front of it. Download took 5 ms. Look at the application first.",
        },
        {
          id: "jitter-voice",
          prompt:
            "Two paths both average 40 ms. On one, packets take 39–41 ms; on the other, 10–150 ms. Which is worse for a voice call, and why?",
          answer:
            "The second. Its jitter is high, so the receiver's jitter buffer must hold audio for over 100 ms or drop late packets as lost. The average says nothing about that spread.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "rtp-jitter",
      statement:
        "RTP's interarrival jitter is the mean deviation of the difference in packet spacing at the receiver compared to the sender, smoothed as J(i) = J(i-1) + (|D(i-1,i)| - J(i-1))/16.",
      sourceId: "rfc3550",
      locator: "Section 6.4.1, interarrival jitter",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "srtt",
      statement:
        "A TCP sender keeps a smoothed RTT (SRTT) and RTT variation (RTTVAR) from measured samples and sets RTO = SRTT + max(G, 4 × RTTVAR).",
      sourceId: "rfc6298",
      locator: "Section 2, rules 2.2 and 2.3",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "tls13-1rtt",
      statement:
        "In TLS 1.3's full handshake the client sends its Finished and Application Data after one round trip of ClientHello and the server's flight.",
      sourceId: "rfc8446",
      locator: "Section 2, Figure 1",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "curl-timings",
      statement:
        "curl's time_namelookup, time_connect, time_appconnect, and time_starttransfer are measured from the start of the operation until name resolution, the TCP connect, the TLS handshake, and the first received byte completed; time_starttransfer includes the time the server needed to calculate the result.",
      sourceId: "curl-manpage",
      locator: "-w, --write-out variables",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    { sourceId: "rfc3550", note: "Section 6.4.1: interarrival jitter." },
    { sourceId: "rfc6298", note: "Section 2: SRTT, RTTVAR, and RTO." },
    { sourceId: "rfc8446", note: "Section 2, Figure 1: the 1-RTT handshake." },
    { sourceId: "curl-manpage", note: "--write-out timing variables." },
  ],
} satisfies LessonBody;
