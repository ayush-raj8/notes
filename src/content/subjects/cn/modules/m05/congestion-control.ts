import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m05.congestion",
  status: "complete",
  objectives: [
    "Explain why a sender needs a second limit, the congestion window, besides the receiver's window.",
    "Trace cwnd and ssthresh through slow start, congestion avoidance, fast recovery, and a timeout.",
    "Explain why a TCP connection can stall without either endpoint crashing.",
    "Find the congestion control algorithm, cwnd, and ssthresh of a Linux connection.",
  ],
  prerequisites: ["m05.flow-control"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "The receiver's window says how much the receiver can hold. It says nothing about the path. A sender that fills a large receive window at once can push more into a router queue than the slowest link can drain, and the queue overflows. Every sender sharing that link then loses packets, retransmits, and adds yet more load. Each sender sees only its own ACKs, and no router tells it the right rate.",
      whyItMatters:
        "This is why a new connection starts slow, why one lossy hop can cut throughput by more than the fraction of packets lost, and why bulk transfers on a shared link affect everyone's latency. In the 1980s, senders without congestion control drove the Internet into congestion collapse, with links busy carrying retransmissions and little useful data getting through.",
      idea: "Give the sender a second window, the congestion window (cwnd), that is its own estimate of what the path can carry. The sender may have at most min(cwnd, receiver's window) bytes unacknowledged. It probes upward while ACKs return and backs off sharply when loss shows the path is full. ACKs returning at the rate the bottleneck delivers data then pace new transmissions, the 'ACK clock'.",
      howItWorks:
        "A new connection starts with a small initial window: 2 to 4 segments in RFC 5681, up to 10 segments in RFC 6928 and on Linux. In slow start, while cwnd is below the slow start threshold (ssthresh), cwnd grows by up to one segment per ACK of new data, roughly doubling each round trip. Above ssthresh, congestion avoidance adds about one segment per round trip. On three duplicate ACKs, the sender sets ssthresh to about half the data in flight, retransmits the missing segment, and continues from that reduced window (fast recovery), without starting over. On a retransmission timeout, it sets ssthresh the same way, drops cwnd to one segment, and slow-starts again. After an idle period longer than the RTO, cwnd is reset toward its initial value.",
      whyItWorks:
        "Growing additively and cutting by half (AIMD) makes competing flows converge toward sharing a bottleneck, because the flow with the larger window loses more each time. Duplicate ACKs mean data is still leaving the network, so halving is enough. A timeout means the ACK clock has stopped, so the sender restarts from one segment. The algorithm needs nothing from routers except that they drop or mark packets when full.",
      limits:
        "Loss-based control fills router queues before it backs off, which adds delay for everyone sharing the queue (bufferbloat). It reads every loss as congestion, so random loss on Wi-Fi or a damaged link cuts throughput too. Slow start costs several round trips before a new connection reaches full speed, which is most of the life of a small transfer. Different algorithms such as Reno, CUBIC, and BBR grow and cut differently, so flows using them do not share a link equally. None of this is visible to the application, except as throughput and latency.",
      connections:
        "The flow-control lesson gave the receiver's limit; this lesson gives the network's, and the sender obeys the smaller. The interactions lesson shows slow start and idle restart on high-latency paths. Module 11 measures bandwidth-delay products and the cost of loss. Module 6 shows QUIC reusing the same ideas over UDP.",
    },
    {
      type: "comparison",
      id: "two-windows",
      title: "Flow control and congestion control side by side",
      columns: ["Flow control", "Congestion control"],
      rows: [
        {
          label: "Protects",
          cells: ["The receiver's buffer.", "The routers and links on the path."],
        },
        {
          label: "Who knows the limit",
          cells: [
            "The receiver. It counts its free buffer space exactly.",
            "Nobody exactly. The sender infers it from loss, delay, or ECN marks.",
          ],
        },
        {
          label: "How the sender learns it",
          cells: [
            "The window field in every ACK.",
            "No field. ACK timing, duplicate ACKs, timeouts, and ECN echoes.",
          ],
        },
        {
          label: "State variable",
          cells: [
            "SND.WND (the receiver's rwnd), set by the peer.",
            "cwnd and ssthresh, kept only by the sender.",
          ],
        },
        {
          label: "When it limits a transfer",
          cells: [
            "The receiving application is slow, or its buffer is small for the path.",
            "The path is shared, lossy, or the connection is new.",
          ],
        },
        {
          label: "Linux view",
          cells: [
            "`ss -ti` shows the peer's window indirectly; `ss -m` shows buffers.",
            "`ss -ti` shows `cubic` (or another algorithm), `cwnd:`, and `ssthresh:`.",
          ],
        },
      ],
    },
    {
      type: "why",
      id: "why-separate",
      questionId: "why.14",
    },
    {
      type: "example",
      id: "cwnd-trace",
      title: "cwnd over twelve round trips",
      setup:
        "A sender with MSS 1000 bytes and an initial window of 10 segments transfers a large file. The receiver's window is large and never limits it. The path can hold about 60 segments in flight before its bottleneck queue overflows. Units are segments, and growth is idealized per round trip.",
      walkthrough: [
        "Round trips 1 to 3, slow start: cwnd is 10, then 20, then 40. Each ACK adds one segment, so cwnd doubles per round trip. ssthresh starts very high.",
        "Round trip 4: cwnd would be 80, more than the path can hold. A queue overflows and several segments are lost. Duplicate ACKs arrive.",
        "Fast recovery: ssthresh = max(FlightSize/2, 2) ≈ 40. The sender retransmits the lost segments and continues with cwnd ≈ 40, not 1.",
        "Round trips 5 to 10, congestion avoidance: cwnd grows by about 1 per round trip, to 41, 42, … 46. Throughput is now about cwnd × MSS per RTT.",
        "Round trip 11: a burst of loss empties the flight entirely, so no duplicate ACKs come back and the RTO fires. ssthresh ≈ 23, cwnd = 1.",
        "Round trip 12 onward: slow start again from 1, doubling to 2, 4, 8, 16, then congestion avoidance from 23. The timeout cost several round trips of low throughput, on top of the RTO wait itself.",
      ],
      result:
        "The sawtooth is the algorithm working as designed. Loss detected by duplicate ACKs halves the window; loss detected by timeout restarts it. On Linux with CUBIC, growth in avoidance is a cubic curve rather than a line, but the reactions to loss have the same shape.",
    },
    {
      type: "why",
      id: "why-stall",
      questionId: "why.15",
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Initial windows, ECN, idle restart, and Linux",
      paragraphs: [
        "RFC 5681's initial window is 2 to 4 segments depending on the MSS. RFC 6928, an experimental update, allows up to min(10·MSS, max(2·MSS, 14600 bytes)), and Linux's TCP_INIT_CWND is 10. A route can override it with `ip route change ... initcwnd N`. If the SYN or SYN-ACK was lost, RFC 5681 requires the initial window to be one segment.",
        "After the connection has not sent data for more than one RTO, RFC 5681 says cwnd should be reduced to at most the restart window, min(IW, cwnd), so a long-idle connection does not dump a large burst into a network whose state it no longer knows. Linux does this when `tcp_slow_start_after_idle` is 1, the default. Long-lived connections that send in bursts, such as HTTP keep-alive or RPC, start every burst slow again. Some services set it to 0.",
        "ECN lets a router mark a packet instead of dropping it, when both ends negotiated it. The receiver echoes the mark with ECE, the sender reduces cwnd as for a loss and acknowledges with CWR. Nothing is retransmitted. RFC 9293 says endpoints should implement ECN, and Linux's `tcp_ecn` controls it.",
        "Linux chooses the algorithm per connection: `sysctl net.ipv4.tcp_congestion_control` shows the default, `tcp_available_congestion_control` the registered ones, and the TCP_CONGESTION socket option or `ip route ... congctl` overrides it. ss's manual names CUBIC as the default. Alternatives must still conform to IETF congestion-control guidance.",
        "A TCP must never send more than the standard algorithms allow, but it may be more conservative. Pacing, sending a window's segments spread over the round trip instead of back to back, is common in modern stacks. `ss -ti` shows `pacing_rate`.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "min-of-two",
          prompt:
            "The receiver advertises 256 KiB and the sender's cwnd is 40 segments of 1448 bytes. How much may be unacknowledged, and which mechanism is limiting?",
          answer:
            "40 × 1448 = 57,920 bytes, about 57 KiB, which is less than 256 KiB. So congestion control limits the sender. The receiver has plenty of room.",
        },
        {
          id: "dupack-vs-timeout",
          prompt:
            "Why does TCP cut cwnd to one segment after a timeout but only halve it after three duplicate ACKs?",
          answer:
            "Duplicate ACKs prove later segments are still getting through, so the path is working and the ACK clock is running; halving is a measured response. A timeout means no ACKs came back at all, so the sender has lost the ACK clock and knows nothing about the current path, and it restarts cautiously with slow start.",
        },
        {
          id: "short-transfer",
          prompt:
            "A 100 KB API response over a 50 ms round-trip path takes about 200 ms even though the link is 10 Gbit/s. Why isn't bandwidth the limit?",
          answer:
            "Slow start. With an initial window of 10 segments of about 1.4 KB, the first round trip carries about 14 KB, the next about 28 KB, then 56 KB: three to four round trips for 100 KB, plus the handshake. The transfer ends before cwnd reaches anything like the link's capacity, so round trips, not bandwidth, set the time.",
        },
        {
          id: "random-loss",
          prompt:
            "A long-distance link drops 1% of packets because of a faulty line card, not congestion. What happens to a loss-based TCP sender's throughput, and why?",
          answer:
            "It drops far more than 1%. Each loss looks like congestion, so the sender halves cwnd every few hundred packets and spends most of its time climbing back slowly in congestion avoidance, and occasional timeouts restart it from one segment. The longer the round trip, the slower the recovery. Fixing the link is the real fix; a delay-based algorithm such as BBR is less sensitive.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "cwnd-rwnd",
      statement:
        "The congestion window (cwnd) is a sender-side limit on data sent before an ACK; the receiver's advertised window (rwnd) is a receiver-side limit on outstanding data. The minimum of cwnd and rwnd governs transmission; a TCP must not send data above the highest acknowledged sequence number plus min(cwnd, rwnd).",
      sourceId: "rfc5681",
      locator: "Section 2, Definitions, CONGESTION WINDOW; Section 3.1",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "slow-start-avoidance",
      statement:
        "Slow start is used at the start of a transfer, or after repairing a loss detected by the retransmission timer, to probe the network's capacity and start the ACK clock. It is used while cwnd < ssthresh and increases cwnd by at most one SMSS per ACK of new data. Congestion avoidance, used above ssthresh, increases cwnd by about one full-sized segment per round trip and never by more than one SMSS per RTT.",
      sourceId: "rfc5681",
      locator: "Section 3.1, Slow Start and Congestion Avoidance",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "initial-window",
      statement:
        "The initial window is at most 2, 3, or 4 segments depending on SMSS; the SYN-ACK and its ACK must not increase cwnd; and if the SYN or SYN-ACK was lost, the initial window after the handshake must be one segment. ssthresh starts arbitrarily high and must be reduced in response to congestion.",
      sourceId: "rfc5681",
      locator: "Section 3.1, IW and ssthresh",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "loss-response",
      statement:
        "On loss detected by the retransmission timer, ssthresh is set to at most max(FlightSize/2, 2·SMSS) and cwnd to at most one full-sized segment. On the third duplicate ACK, ssthresh is set the same way, the lost segment is retransmitted, and cwnd is set to ssthresh + 3·SMSS, inflated by one SMSS per further duplicate ACK and deflated to ssthresh when new data is acknowledged. Duplicate ACKs indicate segments are leaving the network and the ACK clock is preserved, which is why slow start is not used.",
      sourceId: "rfc5681",
      locator:
        "Section 3.1, equation (4); Section 3.2, Fast Retransmit/Fast Recovery, steps 2 to 6",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "idle-restart",
      statement:
        "After an idle period, a TCP cannot use the ACK clock and could send a line-rate burst of cwnd. A TCP should set cwnd to no more than the restart window, min(IW, cwnd), before transmitting if it has not sent data for longer than the RTO.",
      sourceId: "rfc5681",
      locator: "Section 4.1, Restarting Idle Connections",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "must-congestion-control",
      statement:
        "A TCP endpoint must implement slow start, congestion avoidance, and exponential RTO backoff. It may implement alternative algorithms if they conform to IETF Standards Track congestion control specifications, and should implement ECN.",
      sourceId: "rfc9293",
      locator: "Section 3.8.2, TCP Congestion Control (MUST-19, MAY-18, SHLD-8)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "iw10",
      statement:
        "RFC 6928 raises the upper bound on the initial window to min(10·MSS, max(2·MSS, 14600)), optionally; neither the SYN-ACK nor its ACK should increase it.",
      sourceId: "rfc6928",
      locator: "Section 2, TCP Modification",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-init-cwnd",
      statement: "Linux defines TCP_INIT_CWND as 10, citing RFC 6928.",
      sourceId: "linux-tcp-h",
      locator: "#define TCP_INIT_CWND",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-congestion-sysctls",
      statement:
        "tcp_congestion_control sets the default algorithm for new connections, with 'reno' always available and the default chosen at kernel build time; tcp_available_congestion_control lists registered algorithms; TCP_CONGESTION selects one per socket.",
      sourceId: "man-tcp",
      locator:
        "/proc interfaces, tcp_congestion_control, tcp_available_congestion_control, tcp_allowed_congestion_control",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-slow-start-after-idle",
      statement:
        "With tcp_slow_start_after_idle enabled (the default), Linux times out the congestion window after an idle period of one RTO.",
      sourceId: "kernel-ip-sysctl",
      locator: "/proc/sys/net/ipv4: tcp_slow_start_after_idle",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ss-info",
      statement:
        "ss -i shows the congestion algorithm name (cubic by default), cwnd, ssthresh, rtt, rto, and pacing_rate for each TCP socket.",
      sourceId: "man-ss",
      locator: "OPTIONS, -i, --info",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ip-route-initcwnd",
      statement:
        "A route can set initcwnd, the initial congestion window in MSS units for connections to that destination, and rto_min.",
      sourceId: "man-ip-route",
      locator: "DESCRIPTION, ip route add: rto_min, initcwnd",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "zero-window-keep-open",
      statement:
        "As long as the receiver acknowledges zero-window probes, the sender must keep the connection open, and a receiver may keep its window closed indefinitely.",
      sourceId: "rfc9293",
      locator: "Section 3.8.6.1, Zero-Window Probing (MAY-8, MUST-37)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rto-backoff",
      statement: "Each time the retransmission timer expires, the RTO is doubled.",
      sourceId: "rfc6298",
      locator: "Section 5, rule (5.5)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "keepalive-default-off",
      statement:
        "Keep-alives are optional, must default to off, and must not be sent until a connection has been idle for an interval that defaults to no less than two hours.",
      sourceId: "rfc9293",
      locator: "Section 3.8.4, TCP Keep-Alives (MAY-5, MUST-25, MUST-28)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc5681",
      note: "Sections 2, 3.1, 3.2, and 4.1: the four algorithms and idle restart.",
    },
    { sourceId: "rfc9293", note: "Sections 3.8.2, 3.8.4, and 3.8.6.1." },
    { sourceId: "rfc6928", note: "The 10-segment initial window." },
    { sourceId: "rfc6298", note: "RTO backoff." },
    { sourceId: "linux-tcp-h", note: "TCP_INIT_CWND." },
    { sourceId: "man-tcp", note: "Choosing a congestion control algorithm." },
    { sourceId: "kernel-ip-sysctl", note: "tcp_slow_start_after_idle." },
    { sourceId: "man-ss", note: "Reading cwnd, ssthresh, and the algorithm." },
    { sourceId: "man-ip-route", note: "Per-route initcwnd and rto_min." },
  ],
} satisfies LessonBody;
