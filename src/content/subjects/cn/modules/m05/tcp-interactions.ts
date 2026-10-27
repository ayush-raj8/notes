import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m05.interactions",
  status: "complete",
  objectives: [
    "Explain Nagle's algorithm and delayed ACKs, and recognize the stall they cause together.",
    "Choose between TCP_NODELAY, TCP_CORK, and writing whole messages.",
    "Explain what TCP keepalive detects, its Linux defaults, and why applications often need their own heartbeat.",
    "Explain why throughput falls with latency, and how in-order delivery causes head-of-line blocking.",
  ],
  prerequisites: ["m05.congestion"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Each of TCP's mechanisms is reasonable alone, but they act on the same connection without knowing about one another or about the application. A sender that holds small writes to save packets and a receiver that holds ACKs to save packets can wait on each other. A window sized for a short path starves a long one. One missing segment blocks every byte behind it, including bytes that belong to unrelated requests.",
      whyItMatters:
        "These interactions produce the symptoms that look like bugs and are not: a request that takes exactly 40 ms longer than it should, an idle connection that a firewall silently forgets, a transfer that is fast in the data center and slow across an ocean, and an HTTP/2 connection where one lost packet stalls every stream.",
      idea: "TCP trades latency for efficiency in a few specific places, and each trade has a switch. Nagle holds small segments while data is unacknowledged; delayed ACK holds acknowledgments hoping to piggyback them; slow start and the window hold data until ACKs arrive; in-order delivery holds data behind a gap. Know which wait you are in, and either structure writes so the wait does not happen or turn the mechanism off for that socket.",
      howItWorks:
        "Nagle: if unacknowledged data is outstanding, the sender buffers new small writes until the outstanding data is acknowledged or a full segment's worth accumulates. Delayed ACK: the receiver may wait up to 500 ms (Linux: 40 to 200 ms) before acknowledging, but must ACK at least every second full-sized segment and should ACK out-of-order data immediately. Together: a client writes a request header, then the body, then waits. The header goes out at once. Nagle holds the body until the header is acknowledged. The server's TCP delays that ACK because the server application is waiting for the body and has sent nothing to piggyback on. The ACK timer breaks the deadlock. Keepalive: after a long idle period, the stack sends a probe the peer must acknowledge, and after several unanswered probes it aborts. Latency: at most one window can be in flight per round trip, so throughput ≤ window ÷ RTT, and every slow-start round trip is longer.",
      whyItWorks:
        "Each wait has a bound, so the connection never deadlocks: the delayed-ACK timer, the window growing as ACKs return, retransmission filling a gap. The cost is latency that shows up as a fixed number per occurrence (about one delayed-ACK timeout, or one round trip), which is how you recognize it in measurements.",
      limits:
        "Turning Nagle off makes every small write a packet, which costs headers and CPU when an application writes byte by byte. TCP_QUICKACK on Linux is not permanent. TCP keepalive is off by default, waits two hours before its first probe, and detects only that the peer's TCP is gone, not that its application is stuck. No socket option removes head-of-line blocking: it is a property of a single ordered byte stream.",
      connections:
        "The reliability lesson showed how a gap is repaired; this lesson shows what it costs the bytes behind it. Module 8 shows HTTP/1.1 and HTTP/2 inheriting head-of-line blocking from TCP, and Module 6 shows QUIC's per-stream ordering avoiding it. Module 11 does the bandwidth-delay arithmetic, and Module 12 covers connection pools and idle timeouts in middleboxes.",
    },
    {
      type: "example",
      id: "forty-ms",
      title: "The 40 ms request",
      setup:
        "A Linux client sends each RPC as two write() calls: a 20-byte header, then a 300-byte body. It then waits for the reply. The server on the same LAN reads the header and then the body before answering. Round trip is 0.2 ms, yet every RPC takes about 40 ms. `tcpdump -n -ttt` on the client shows the pattern.",
      walkthrough: [
        "`0.000000` client → server `Flags [P.] ... length 20`: the header goes out at once, because nothing was unacknowledged.",
        "The body is written immediately afterward, but Nagle holds it: 20 bytes are unacknowledged and 300 bytes is less than a full segment.",
        "The server's kernel has the header but delays its ACK. The server application has nothing to send yet, because it is waiting for the body.",
        "`0.04xxxx` server → client `Flags [.] ... ack 21`: the delayed-ACK timer fires, about 40 ms later.",
        "`0.0000xx` client → server `Flags [P.] ... length 300`: Nagle releases the body the instant the ACK arrives, and the reply follows within a fraction of a millisecond.",
        "Fixes, in order of preference: build the whole message and send it in one write(), or one writev() or sendmsg() with both buffers; set TCP_NODELAY if the application already writes whole messages and needs every write sent at once; or use TCP_CORK to collect pieces and uncork to send.",
      ],
      result:
        "Neither side was slow. The delay was the delayed-ACK timer, triggered by a write-write-read pattern. A latency that clusters at one specific value, such as 40 ms or 200 ms, points to a timer rather than to the network.",
    },
    {
      type: "comparison",
      id: "socket-options",
      title: "Linux knobs for these interactions",
      columns: ["What it does", "When to use it"],
      rows: [
        {
          label: "TCP_NODELAY",
          cells: [
            "Disables Nagle: small segments are sent as soon as possible. Also flushes pending output, even when corked.",
            "Request-response protocols and interactive traffic that write whole messages. Many RPC and HTTP libraries set it.",
          ],
        },
        {
          label: "TCP_CORK",
          cells: [
            "Holds partial segments until uncorked, with a 200 ms ceiling.",
            "Writing a header and then a file with sendfile(), as one stream of full segments.",
          ],
        },
        {
          label: "TCP_QUICKACK",
          cells: [
            "Switches to immediate ACKs for now. Not permanent: the stack may return to delayed ACKs.",
            "Rarely. Fixing the write pattern is better.",
          ],
        },
        {
          label: "SO_KEEPALIVE with TCP_KEEPIDLE, TCP_KEEPINTVL, TCP_KEEPCNT",
          cells: [
            "Enables keepalive probes and sets idle time, gap between probes, and number of probes per socket.",
            "Long idle connections through NAT or firewalls, or to detect a vanished peer without application traffic.",
          ],
        },
        {
          label: "TCP_USER_TIMEOUT",
          cells: [
            "Closes the connection with ETIMEDOUT when sent data stays unacknowledged this long, and overrides keepalive's give-up time.",
            "Failing fast on a dead path instead of after about 15 minutes of retransmissions.",
          ],
        },
      ],
    },
    {
      type: "prose",
      id: "keepalive",
      title: "Keepalive: what it detects and what it does not",
      paragraphs: [
        "An idle TCP connection sends nothing. If the peer host loses power, or a NAT or firewall between them forgets the connection, neither endpoint finds out until it next sends. A server can accumulate thousands of such dead connections, and a client can keep one in its pool and fail on the next request.",
        "Keepalive sends a probe after a connection has been idle for a set time. The probe carries a sequence number just below the peer's expected one, so a live peer's TCP answers with an ACK. A peer that rebooted answers with RST. No answer after several probes means the peer is unreachable, and the connection is aborted with ETIMEDOUT. On Linux the defaults are 7200 seconds idle, then 9 probes 75 seconds apart: about 2 hours 11 minutes before a dead peer is noticed. And only if the application set SO_KEEPALIVE.",
        "Middleboxes usually forget idle connections much sooner than two hours, so long-lived connections either lower TCP_KEEPIDLE or send application-level heartbeats. A heartbeat is often better, because a TCP keepalive ACK comes from the peer's kernel. It proves the host is up, not that the service is responsive.",
      ],
    },
    {
      type: "steps",
      id: "latency-and-hol",
      title: "High latency and head-of-line blocking",
      steps: [
        {
          title: "Throughput is window over round-trip time",
          body: "At most one window of unacknowledged data is in flight per round trip. With a 64 KiB window and a 100 ms RTT, the most a connection can do is about 5 Mbit/s, whatever the link speed. Filling a 1 Gbit/s path at 100 ms needs about 12.5 MB in flight, which needs window scaling and large buffers on both ends.",
        },
        {
          title: "Every round trip of slow start is longer",
          body: "A new connection's window doubles once per round trip. At 150 ms, reaching a window of a few megabytes takes over a second. Short transfers never get there, so their time is a count of round trips: handshake, then slow start.",
        },
        {
          title: "Idle restarts make it worse",
          body: "With tcp_slow_start_after_idle on, a keep-alive connection that pauses longer than one RTO starts its next burst from the initial window again.",
        },
        {
          title: "One gap blocks the whole stream",
          body: "The receiver delivers bytes in order. If segment N is lost, segments N+1 onward wait in the kernel until N is retransmitted, at least one more round trip, and longer on a long path. If those later bytes belong to an independent request multiplexed on the same connection, that request waits too, even though its bytes arrived.",
        },
        {
          title: "What helps",
          body: "Larger windows and buffers fix the throughput cap. Reusing warm connections avoids slow start. Separate connections, or a transport with independent streams such as QUIC, avoid cross-request head-of-line blocking. Nothing inside one TCP connection does.",
        },
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "More detail",
      paragraphs: [
        "RFC 9293 says an implementation should do Nagle but must let an application disable it per connection, and notes the problematic interaction with delayed ACKs. RFC 896, which introduced the algorithm, was solving keyboard traffic where each keystroke became a 41-byte packet carrying one byte of data.",
        "Delayed ACK limits: RFC 9293 requires the delay to be less than 0.5 seconds and says an ACK should be sent for at least every second full-sized segment. In the Linux kernel source, the delayed-ACK timeout ranges from TCP_DELACK_MIN (40 ms) to TCP_DELACK_MAX (200 ms). `ss -ti` shows the current `ato:` value.",
        "Linux also coalesces small writes on its own when earlier packets are still queued in the qdisc or device (`tcp_autocorking`, on by default). That is separate from Nagle and can merge writes even with TCP_NODELAY.",
        "Keepalive in RFC 9293 is a MAY, must default to off, and must wait at least two hours by default. A single unanswered probe must not be taken as a dead connection, because ACKs without data are not delivered reliably. Linux's tcp(7) warns that connection tracking and application timeouts may be much shorter than the keepalive defaults.",
        "Why the 40 ms example needs both mechanisms: with TCP_NODELAY, the body goes out at once; with immediate ACKs, Nagle waits only one round trip. The stall is the product of the two.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "nodelay-cost",
          prompt:
            "An application writes a 2 KB message one byte at a time with TCP_NODELAY set. What happens on the wire, and what would happen without TCP_NODELAY?",
          answer:
            "With TCP_NODELAY, many writes become their own segments with about 40 bytes of headers each, possibly thousands of packets for 2 KB. Without it, Nagle sends the first byte, buffers the rest until the ACK returns or a full segment accumulates, and then sends them together. The real fix is to build the message in user space and write it once.",
        },
        {
          id: "keepalive-detects",
          prompt:
            "A Linux service enables SO_KEEPALIVE with default settings, and a client's laptop loses Wi-Fi while idle. When does the server notice, and what would TCP keepalive not detect at all?",
          answer:
            "After about 2 hours 11 minutes: 7200 seconds idle, then 9 unanswered probes 75 seconds apart, after which the connection is aborted with ETIMEDOUT. Keepalive would not detect a peer whose kernel is up but whose application is hung, because the kernel answers the probes. That needs an application-level heartbeat or request timeout.",
        },
        {
          id: "bdp",
          prompt:
            "A backup over a 1 Gbit/s link with an 80 ms round trip stays around 6.5 Mbit/s. What window is it effectively using, and what would you check?",
          answer:
            "Window ≈ throughput × RTT = 6.5 Mbit/s × 0.08 s ≈ 520 kbit ≈ 65 KB, which is the unscaled 64 KiB maximum. Check whether window scaling was negotiated in the SYN and SYN-ACK (a middlebox may strip it), and whether either side fixed a small SO_RCVBUF or SO_SNDBUF.",
        },
        {
          id: "hol",
          prompt:
            "Ten independent requests share one TCP connection. The segment carrying request 3's response is lost. Why are the responses to requests 4 to 10 delayed even if their segments arrived?",
          answer:
            "TCP delivers bytes in order. The receiving kernel holds everything after the gap until the lost segment is retransmitted and arrives, so the application cannot read responses 4 to 10, which sit later in the stream, for at least one more round trip. That is head-of-line blocking within the byte stream.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "nagle",
      statement:
        "Nagle: if there is unacknowledged data, the sender buffers all user data, regardless of PSH, until the outstanding data is acknowledged or a full-sized segment can be sent. A TCP should implement it, must let an application disable it per connection, and some implementations vary it because of problematic interactions with delayed acknowledgments.",
      sourceId: "rfc9293",
      locator: "Section 3.7.4, Nagle Algorithm (SHLD-7, MUST-17)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "nagle-origin",
      statement:
        "Single-character messages from a keyboard produce 41-byte packets carrying one byte of data. The solution is to inhibit sending new segments when new data arrives from the user while previously transmitted data remains unacknowledged.",
      sourceId: "rfc896",
      locator: "The small-packet problem; The solution to the small-packet problem",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "delayed-ack",
      statement:
        "A TCP should implement delayed ACKs, but the delay must be less than 0.5 seconds, and an ACK should be generated for at least every second full-sized segment.",
      sourceId: "rfc9293",
      locator: "Section 3.8.6.3, Delayed Acknowledgments (SHLD-18, MUST-40, SHLD-19)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ack-out-of-order",
      statement:
        "Out-of-order segments should be acknowledged immediately, and an ACK must be generated within 500 ms of the first unacknowledged packet.",
      sourceId: "rfc5681",
      locator: "Section 4.2, Generating Acknowledgments",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-delack",
      statement:
        "Linux defines TCP_DELACK_MAX as HZ/5 (200 ms), the maximal time to delay an ACK, and TCP_DELACK_MIN as HZ/25 (40 ms) for HZ of 100 or more.",
      sourceId: "linux-tcp-h",
      locator: "#define TCP_DELACK_MAX, TCP_DELACK_MIN",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-socket-options",
      statement:
        "TCP_NODELAY disables Nagle and forces an explicit flush of pending output even if TCP_CORK is set. TCP_CORK holds partial frames until cleared, with a 200 ms ceiling. TCP_QUICKACK enables quickack mode but is not permanent. TCP_KEEPIDLE, TCP_KEEPINTVL, and TCP_KEEPCNT set keepalive timing per socket. tcp_autocorking (default on) coalesces small writes when earlier packets are queued.",
      sourceId: "man-tcp",
      locator:
        "Socket options, TCP_NODELAY, TCP_CORK, TCP_QUICKACK, TCP_KEEPIDLE, TCP_KEEPINTVL, TCP_KEEPCNT; /proc interfaces, tcp_autocorking",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "keepalive-rfc",
      statement:
        "Keep-alives may be implemented, must be switchable per connection, must default to off, and must be sent only after an idle interval that is configurable and defaults to at least two hours. A probe generally carries SEG.SEQ = SND.NXT − 1. Failure to answer one probe must not be taken to mean the connection is dead, because ACKs without data are not reliably transmitted.",
      sourceId: "rfc9293",
      locator: "Section 3.8.4, TCP Keep-Alives (MAY-5, MUST-24 to MUST-29)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "keepalive-linux",
      statement:
        "tcp_keepalive_time defaults to 7200 seconds, tcp_keepalive_intvl to 75, and tcp_keepalive_probes to 9, so an idle dead connection is terminated about 11 minutes after probing starts. Keep-alives are sent only with SO_KEEPALIVE. Connection tracking and application timeouts may be much shorter.",
      sourceId: "man-tcp",
      locator: "/proc interfaces, tcp_keepalive_time, tcp_keepalive_intvl, tcp_keepalive_probes",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "so-keepalive",
      statement: "SO_KEEPALIVE enables keep-alive messages on connection-oriented sockets.",
      sourceId: "man-socket",
      locator: "Socket options, SO_KEEPALIVE",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "user-timeout-keepalive",
      statement:
        "TCP_USER_TIMEOUT overrides keepalive in deciding when to close a connection after keepalive failure; it does not change when retransmissions or keepalive probes are sent. Without it, failure may take up to 20 minutes with default settings.",
      sourceId: "man-tcp",
      locator: "Socket options, TCP_USER_TIMEOUT",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "lfn-window",
      statement:
        "When the bandwidth-delay product exceeds 64 KiB, the 16-bit receive window limits the maximum throughput, the amount of unacknowledged data that can keep the path full.",
      sourceId: "rfc7323",
      locator: "Section 1.1, TCP Performance (1)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "in-order-hold",
      statement:
        "TCP provides an in-order byte stream; segments that begin above RCV.NXT should be held for later processing rather than delivered.",
      sourceId: "rfc9293",
      locator: "Section 2.2; Section 3.10.7.4, Other States (SHLD-31)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "idle-cwnd-linux",
      statement:
        "With tcp_slow_start_after_idle enabled (the default), the congestion window is timed out after an idle period of one RTO.",
      sourceId: "kernel-ip-sysctl",
      locator: "/proc/sys/net/ipv4: tcp_slow_start_after_idle",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc9293", note: "Sections 2.2, 3.7.4, 3.8.4, 3.8.6.3, and 3.10.7.4." },
    { sourceId: "rfc896", note: "Where Nagle's algorithm came from." },
    { sourceId: "rfc5681", note: "Section 4.2: when ACKs must not be delayed." },
    { sourceId: "rfc7323", note: "Section 1.1: the window limit on long paths." },
    { sourceId: "linux-tcp-h", note: "Delayed-ACK bounds." },
    { sourceId: "man-tcp", note: "Socket options and keepalive sysctls." },
    { sourceId: "man-socket", note: "SO_KEEPALIVE." },
    { sourceId: "kernel-ip-sysctl", note: "Slow start after idle." },
  ],
} satisfies LessonBody;
