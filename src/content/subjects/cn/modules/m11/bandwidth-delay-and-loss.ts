import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m11.bdp-and-loss",
  status: "complete",
  objectives: [
    "Compute a path's bandwidth-delay product and the throughput ceiling a given window allows.",
    "Explain how an oversized queue becomes delay and why tail drop causes bursts of loss.",
    "Estimate the delay one loss adds when it is caught by duplicate ACKs versus a retransmission timeout.",
  ],
  prerequisites: ["m11.metrics"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• A TCP sender may have only one window of unacknowledged data in flight.\n• On a long path the window can be used up long before the first ACK returns.\n• Packets that do not fit in a queue are dropped, and each drop costs a retransmission.",
      whyItMatters:
        "• A copy between regions can crawl on a 10 Gbit/s link.\n• Big buffers make a busy link feel slow to everyone sharing it.\n• One lost segment at the end of a response can add a second to the request.",
      idea: "• BDP = bandwidth × RTT: the bytes that must be in flight to keep the path busy.\n• Throughput ≤ window ÷ RTT, whatever the link speed.\n• Queues absorb bursts but add delay; a full queue drops.\n• Recovering a loss costs at least one more RTT; a timeout costs far more.",
      howItWorks:
        "• Window = min(receiver's window, congestion window). Without window scaling it is at most 64 KiB.\n• A queue drains at the outgoing link's rate: added delay = queued bytes ÷ rate.\n• Fast retransmit: 3 duplicate ACKs → resend, roughly one RTT after the loss.\n• Timeout: no ACKs → wait the RTO, then resend and double the RTO for the next try.",
      whyItWorks:
        "• The sender cannot pass the window's edge until an ACK moves it, so it sends at most one window per RTT.\n• The sender learns of a loss only from what the receiver reports, which arrives a round trip later.",
      limits:
        "• window ÷ RTT is a ceiling. Congestion control and competing flows usually keep throughput below it.\n• TCP treats every loss as congestion, including corruption on a wireless link.\n• Numbers here follow RFC 5681 Reno-style recovery; CUBIC and BBR grow back faster.",
      connections:
        "• Module 5: flow control, congestion control, and the retransmission timer.\n• why.17: bytes behind a gap wait for the retransmission.\n• Next: timeouts and retries in the application.",
    },
    {
      type: "comparison",
      id: "window-ceiling",
      title: "Throughput ceiling = window ÷ RTT",
      caption:
        "Actual throughput is the lower of this ceiling and what the path and congestion control allow.",
      columns: ["RTT 1 ms", "RTT 20 ms", "RTT 100 ms"],
      rows: [
        { label: "64 KiB window", cells: ["524 Mbit/s", "26 Mbit/s", "5.2 Mbit/s"] },
        { label: "1 MiB window", cells: ["8.4 Gbit/s", "419 Mbit/s", "84 Mbit/s"] },
        { label: "16 MiB window", cells: ["134 Gbit/s", "6.7 Gbit/s", "1.3 Gbit/s"] },
      ],
    },
    {
      type: "example",
      id: "cross-region-copy",
      title: "A backup copy between regions",
      setup:
        "10 Gbit/s path, 80 ms RTT, one TCP connection, receive buffer capped at 4 MiB. 500 GB to copy. Illustrative numbers.",
      walkthrough: [
        "• BDP = 10 Gbit/s × 0.08 s = 800 Mbit = 100 MB. That much must be in flight to fill the path.",
        "• Ceiling = 4 MiB × 8 ÷ 0.08 s ≈ 419 Mbit/s, about 4% of the link.",
        "• 500 GB at 419 Mbit/s ≈ 2.7 hours. At line rate it would be about 7 minutes.",
        "• One loss near full speed: ssthresh drops to about half the flight. Reno then grows by about one segment per RTT.",
        "• Regaining 50 MB at 1448 bytes per RTT ≈ 34,500 RTTs ≈ 46 minutes. That is why loss in a long fat network is so costly.",
        "• Fixes: raise the buffer limits to at least the BDP (window scaling allows up to 1 GiB), run parallel streams, or use a congestion control that recovers faster.",
      ],
      result:
        "• A long RTT turns a small window into a low ceiling and makes every loss slow to recover from. Size buffers to the BDP.",
    },
    {
      type: "steps",
      id: "queues",
      title: "How a queue becomes delay, then loss",
      steps: [
        {
          title: "Arrivals exceed departures",
          body: "• Traffic arrives faster than the outgoing link sends, often at a fast-to-slow step such as a home uplink.\n• The excess waits in a buffer. Short bursts are normal and the queue drains.",
        },
        {
          title: "The queue adds delay",
          body: "• Delay = queued bytes ÷ link rate.\n• 1 MB queued at 100 Mbit/s adds 80 ms. At a 10 Mbit/s uplink it adds 800 ms.",
        },
        {
          title: "Loss-based senders keep it full",
          body: "• Reno and CUBIC grow until something is dropped, so a big buffer fills and stays full.\n• Every flow through it, including a small DNS query, waits behind the standing queue. This is often called bufferbloat.",
        },
        {
          title: "Tail drop",
          body: "• When the buffer is full, every new arrival is dropped.\n• A burst loses several packets from one flow, and many flows back off together.",
        },
        {
          title: "Active queue management",
          body: "• AQM drops or ECN-marks packets before the buffer is full, so senders slow down while the queue is still short.\n• Result: similar throughput, much less standing delay.",
        },
      ],
    },
    {
      type: "comparison",
      id: "loss-cost",
      title: "What one lost segment costs",
      columns: ["How it is detected", "Extra delay, roughly"],
      rows: [
        {
          label: "Middle of a transfer",
          cells: [
            "Later segments arrive and trigger 3 duplicate ACKs.",
            "About 1 RTT to resend and receive it. Bytes behind the gap wait.",
          ],
        },
        {
          label: "Last segment of a response",
          cells: [
            "No later segments, so no duplicate ACKs; a timer or a tail-loss probe (Module 5) must fire.",
            "The RTO: at least 200 ms on Linux, at least 1 s under RFC 6298.",
          ],
        },
        {
          label: "SYN",
          cells: ["No RTT sample yet, so the initial RTO applies.", "1 s, then 2 s, 4 s, …"],
        },
        {
          label: "Same segment lost again",
          cells: ["The RTO fires again.", "Doubles each time; Linux caps a single RTO at 120 s."],
        },
      ],
    },
    {
      type: "why",
      id: "why-bdp",
      questionId: "why.41",
    },
    {
      type: "why",
      id: "why-retransmissions",
      questionId: "why.42",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "bdp",
          prompt: "What is the bandwidth-delay product of a 1 Gbit/s path with a 40 ms RTT?",
          answer:
            "1 Gbit/s × 0.04 s = 40 Mbit = 5 MB. The sender needs about 5 MB in flight to keep the path full.",
        },
        {
          id: "unscaled-window",
          prompt:
            "Window scaling is off, so the window is at most 64 KiB. The RTT is 50 ms. What throughput ceiling does one connection have?",
          answer: "65,536 × 8 ÷ 0.05 ≈ 10.5 Mbit/s, regardless of link speed.",
        },
        {
          id: "buffer-delay",
          prompt:
            "A router has a 50 MB buffer on a 1 Gbit/s link. When the buffer is full, how much delay does it add?",
          answer:
            "50 MB × 8 = 400 Mbit; ÷ 1 Gbit/s = 400 ms of queueing for every packet behind it.",
        },
        {
          id: "tail-loss",
          prompt:
            "Why does losing the last segment of a response usually cost more than losing one in the middle?",
          answer:
            "Nothing follows it, so the receiver sends no duplicate ACKs and fast retransmit never triggers. The sender waits for a timer, which is at least 200 ms on Linux, instead of about one RTT.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "lfn-window",
      statement:
        "TCP performance problems arise when the bandwidth × delay product is large. With the 16-bit window field the largest window is 64 KiB, and when the BDP exceeds it the receive window limits the connection's throughput.",
      sourceId: "rfc7323",
      locator: "Section 1.1, TCP Performance (1) Window Size Limit",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "lfn-loss",
      statement:
        "Packet losses in a long, fat network can have a catastrophic effect on throughput.",
      sourceId: "rfc7323",
      locator: "Section 1.1, (2) Recovery from Losses",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "window-scale-limit",
      statement:
        "The window scale shift count is limited to 14, which allows windows of 2^30 = 1 GiB.",
      sourceId: "rfc7323",
      locator: "Section 2.3, Using the Window Scale Option",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "queueing",
      statement:
        "Queueing occurs whenever the arrival rate at a device exceeds the egress rate. It absorbs bursts, but excessive queueing causes unwanted delay.",
      sourceId: "rfc7567",
      locator: "Section 1.2, Active Queue Management to Manage Latency",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tail-drop",
      statement:
        "Tail drop signals congestion only when the queue is full, so queues can stay full for long periods; a burst arriving at a full queue loses multiple packets from one flow and can synchronise flows backing off.",
      sourceId: "rfc7567",
      locator: "Section 2, The Need for Active Queue Management (1. Full Queues)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "fast-retransmit",
      statement:
        "Fast retransmit treats the arrival of 3 duplicate ACKs as an indication that a segment has been lost and retransmits it without waiting for the timer.",
      sourceId: "rfc5681",
      locator: "Section 3.2, Fast Retransmit/Fast Recovery",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "ssthresh-after-loss",
      statement:
        "After a loss, ssthresh is set to no more than max(FlightSize / 2, 2 × SMSS); congestion avoidance then grows cwnd by about one SMSS per RTT.",
      sourceId: "rfc5681",
      locator: "Section 3.1, equations (3) and (4)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "rto-rules",
      statement:
        "The initial RTO SHOULD be 1 second, a computed RTO below 1 second SHOULD be rounded up to 1 second, and on each timer expiry the RTO MUST be doubled.",
      sourceId: "rfc6298",
      locator: "Section 2, rules 2.1 and 2.4; Section 5, rule 5.5",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "rto-backoff-required",
      statement:
        "Each time a retransmission timer detects a loss, the timeout MUST be exponentially backed off, and loss detected this way MUST be taken as a sign of congestion.",
      sourceId: "rfc8961",
      locator: "Section 4, requirements (3) and (4)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "linux-rto",
      statement:
        "On Linux master, TCP_RTO_MIN is HZ/5 (200 ms), TCP_TIMEOUT_INIT is 1 second, and TCP_RTO_MAX is 120 seconds.",
      sourceId: "linux-tcp-h",
      locator: "TCP_RTO_MIN, TCP_TIMEOUT_INIT, TCP_RTO_MAX_SEC",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "recovery-estimate",
      statement:
        "The 46-minute recovery assumes Reno-style congestion avoidance of one 1448-byte segment per RTT, no further loss, and no other limit. CUBIC and BBR recover differently and are not modelled.",
      sourceId: "rfc5681",
      locator: "Section 3.1, equation (3)",
      verification: "checked",
      subtle: false,
      scope: "teaching-simplification",
    },
  ],
  references: [
    { sourceId: "rfc7323", note: "Sections 1.1 and 2.3: long fat networks and window scaling." },
    { sourceId: "rfc7567", note: "Sections 1.2 and 2: queueing delay and tail drop." },
    {
      sourceId: "rfc5681",
      note: "Sections 3.1 and 3.2: congestion avoidance and fast retransmit.",
    },
    { sourceId: "rfc6298", note: "Sections 2 and 5: RTO bounds and backoff." },
    { sourceId: "rfc8961", note: "Section 4: requirements for any retransmission timer." },
    { sourceId: "linux-tcp-h", note: "Linux RTO constants." },
  ],
} satisfies LessonBody;
