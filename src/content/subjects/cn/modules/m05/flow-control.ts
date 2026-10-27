import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m05.flow-control",
  status: "complete",
  objectives: [
    "Explain a sliding window: what the sender may have in flight, and what moves the window's edges.",
    "Compute the usable window from SND.UNA, SND.NXT, and the advertised window.",
    "Explain how the receive buffer becomes the advertised window on Linux, including window scaling and autotuning.",
    "Describe a zero window, the persist timer, and how a slow reader shows up in `ss`.",
  ],
  prerequisites: ["m05.reliability"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "The receiver's kernel holds arriving bytes in a buffer until the application reads them. A sender on a fast machine can produce data much faster than a busy receiver consumes it. If the sender keeps sending, the buffer overflows and the receiver must drop data it then has to ask for again, which wastes the network and gets nowhere.",
      whyItMatters:
        "A receiver that stops reading is one of the most common causes of a 'hung' TCP connection that is actually healthy. It also explains why a single slow consumer, such as a log shipper, a database replica, or a client on a phone, can make a fast server's write() calls block, and why throughput over a long path is capped by buffer sizes.",
      idea: "Let the receiver say, in every segment it sends, how many more bytes it is willing to accept beyond what it has acknowledged. The sender keeps its unacknowledged data within that window. As the application reads, space frees up and the receiver advertises a larger window. That is flow control: the receiver's free buffer space limits the sender.",
      howItWorks:
        "Every segment carries a window field. With the ACK number, it says: I have everything before ACK, and you may send bytes up to ACK + window − 1. The sender records it as SND.WND, measured from SND.UNA, and may send only while SND.NXT is below SND.UNA + SND.WND. The usable window is U = SND.UNA + SND.WND − SND.NXT. An ACK slides the left edge right; the application reading slides the right edge right. If the application stops reading, the receiver advertises a smaller window, and eventually zero. The sender then stops sending new data and starts a persist timer. When it fires, the sender sends a probe of at least one byte or a retransmission to make the receiver answer with its current window, backing off between probes. The connection stays open as long as the probes are answered.",
      whyItWorks:
        "The receiver is the only party that knows its free space, and every ACK already travels back to the sender, so the window costs nothing extra to send. Because the window is expressed in sequence numbers, it survives lost and reordered ACKs: the sender acts on the window from the segment with the highest ACK. Probing solves the one deadlock the scheme creates, where a window update is lost while the sender waits for it, because ACKs carrying no data are not retransmitted.",
      limits:
        "The window protects only the receiver's buffer. It says nothing about routers in between, which is congestion control's job. A sender with a large window still cannot send faster than the window divided by the round-trip time, so a small receive buffer caps throughput on a long path. Without the window scale option, the window is at most 64 KiB. A receiver that keeps its window at zero can hold a connection open indefinitely, which is legal but ties up memory on the sender.",
      connections:
        "The next lesson adds the second limit, the congestion window. The sender uses the smaller of the two. The interactions lesson covers high-latency paths, where the window size sets the throughput. Module 11 does the bandwidth-delay arithmetic and Module 12 covers socket buffers and backpressure in detail.",
    },
    {
      type: "steps",
      id: "sliding-window",
      title: "The sender's view of its sequence space",
      steps: [
        {
          title: "Acknowledged",
          body: "Bytes below SND.UNA. The receiver has them, and the sender has freed its copy.",
        },
        {
          title: "Sent, not yet acknowledged",
          body: "SND.UNA up to SND.NXT. In flight, or lost. The sender keeps a copy for retransmission.",
        },
        {
          title: "Allowed, not yet sent",
          body: "SND.NXT up to SND.UNA + SND.WND. This is the usable window. The sender may send these bytes now, if the application has written them and congestion control agrees.",
        },
        {
          title: "Not yet allowed",
          body: "At or beyond SND.UNA + SND.WND. The sender must wait for the window to move. Data the application writes here waits in the send buffer, and write() blocks when that buffer is full.",
        },
      ],
    },
    {
      type: "example",
      id: "slow-reader",
      title: "A reader that stops reading",
      setup:
        "A Linux server writes a 10 MB report to a client whose application has paused, for example stopped in a debugger. The client socket's receive buffer has room for about 128 KiB. Watch with `tcpdump -n port 9000` and `ss -tmio` on both hosts.",
      walkthrough: [
        "At first the client's kernel acknowledges every segment and advertises a large window. The server sends at full speed, and the client's buffer fills, because nothing reads it.",
        "Each ACK from the client advertises less space: the right edge stops moving while the ACK number keeps rising. Then the window field reaches 0. tcpdump shows `win 0` on the client's ACKs.",
        "The server stops sending new data. Its kernel send buffer fills with the rest of the report, and its next write() blocks, or returns EAGAIN on a non-blocking socket. The server application now waits on the client's application.",
        "`ss -tio` on the server shows `timer:(persist,...)` for this socket: the zero-window probe timer. The server sends a small probe periodically, with growing gaps. The client answers each with an ACK that still says `win 0`. No error occurs and no timeout ends the connection by default.",
        "The client application resumes and reads. Its kernel frees buffer space and sends a window update. The server resumes immediately at full speed.",
      ],
      result:
        "Nothing was lost and nothing failed. The connection stalled because the reader stopped, and flow control carried that backpressure all the way to the writer. To bound such a stall, set TCP_USER_TIMEOUT, which also covers data stuck behind a zero window, or put a deadline in the application.",
    },
    {
      type: "comparison",
      id: "window-numbers",
      title: "Three snapshots of one window",
      caption:
        "The receiver's buffer is 4000 bytes. MSS is 1000. Numbers are relative sequence numbers on the sender.",
      columns: ["SND.UNA", "SND.NXT", "Advertised window", "Usable window"],
      rows: [
        { label: "Start", cells: ["1", "1", "4000", "4000: up to four full segments may go."] },
        {
          label: "Four segments sent, two ACKed, app read nothing",
          cells: ["2001", "4001", "2000", "2001 + 2000 − 4001 = 0. The right edge has not moved."],
        },
        {
          label: "App reads 3000 bytes, all four ACKed",
          cells: ["4001", "4001", "3000", "3000: the right edge slid to 7000."],
        },
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Window scaling, Linux buffers, and silly windows",
      paragraphs: [
        "Window scaling is negotiated in the SYN and SYN-ACK: each side sends a shift count, and if both send one, every later window field is shifted left by the receiving direction's count. The maximum shift is 14, for a 1 GiB window. Linux enables it by default (`tcp_window_scaling = 1`). If a middlebox strips the option from the SYN, windows are limited to 64 KiB for the life of the connection.",
        "On Linux, the advertised window comes from the socket receive buffer. `tcp_rmem` holds the minimum, default, and maximum sizes; the default of 131072 bytes yields an initial window of 65535. With `tcp_moderate_rcvbuf` enabled, the default, Linux autotunes the buffer up to `tcp_rmem[2]` to match the path. Setting SO_RCVBUF disables that autotuning for the socket, and the kernel doubles the requested value for bookkeeping. A fixed small SO_RCVBUF is a common way to cap throughput by accident.",
        "Not all of the buffer is advertised. Part of it covers the kernel's overhead and the application's unread data, controlled by `tcp_adv_win_scale`. So the advertised window is smaller than the buffer size `ss -m` reports as `rb`.",
        "Silly window syndrome: if a receiver advertised every few freed bytes and a sender filled every tiny window, the connection would degenerate into tiny segments. TCP prevents it on both sides. The receiver waits to open the window until it can offer a full segment or half its buffer, and the sender waits to send until it can send a full segment or at least half the largest window it has seen, with a timer as a backstop.",
        "Shrinking the window, moving its right edge left, is strongly discouraged but must be tolerated by senders. A zero window is not shrinking: the right edge stays put while the left edge catches up with it.",
        "`ioctl(fd, SIOCINQ)` (or FIONREAD) returns the bytes waiting unread in a socket's receive buffer, which is what fills up in a zero-window stall.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "usable",
          prompt:
            "SND.UNA = 50,000, SND.NXT = 80,000, and the last ACK advertised a window of 40,000. How many more bytes may the sender transmit before the next ACK?",
          answer:
            "U = SND.UNA + SND.WND − SND.NXT = 50,000 + 40,000 − 80,000 = 10,000 bytes, assuming congestion control allows at least that much.",
        },
        {
          id: "zero-window-not-dead",
          prompt:
            "A capture shows the receiver advertising `win 0` for five minutes while the sender sends a one-byte segment every 60 seconds or so. Is the connection broken?",
          answer:
            "No. The receiving application is not reading, so its buffer is full. The sender's persist timer sends zero-window probes and the receiver answers each, so TCP must keep the connection open. The fix is on the receiving application, or an application deadline or TCP_USER_TIMEOUT on the sender if it should give up.",
        },
        {
          id: "write-blocks",
          prompt:
            "Why does a fast server's write() block when one particular client is slow, even though the server's network link is idle?",
          answer:
            "The client's receive buffer is full, so it advertises a small or zero window. The server's TCP cannot send more, so the data accumulates in the server's send buffer. When that buffer is full, write() blocks. Flow control turned the client's slowness into backpressure on the server application.",
        },
        {
          id: "lfn",
          prompt:
            "A transfer over a 100 ms round-trip path never exceeds about 5 Mbit/s, even with no loss. The receiver set SO_RCVBUF to 64 KiB. Why?",
          answer:
            "The sender can have at most one window of unacknowledged data per round trip. 64 KiB per 0.1 s is about 640 KB/s, roughly 5 Mbit/s. Setting SO_RCVBUF also turned off Linux's receive-buffer autotuning. Removing it, or raising it, lets the window grow.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "window-field",
      statement:
        "The window field is the number of data octets, beginning with the one indicated in the acknowledgment field, that the sender of the segment is willing to accept. It is shifted when window scaling is used and must be treated as unsigned.",
      sourceId: "rfc9293",
      locator: "Section 3.1, Header Format, Window",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "send-space",
      statement:
        "The send window is the sequence space from SND.UNA to SND.UNA + SND.WND; beyond it lie future sequence numbers not yet allowed. The receive window runs from RCV.NXT to RCV.NXT + RCV.WND − 1.",
      sourceId: "rfc9293",
      locator: "Section 3.3.1, Figures 3 and 4; Section 3.4",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "managing-window",
      statement:
        "The window indicates the sequence numbers the receiver is prepared to accept, related to its free buffer space. To avoid acting on old window reports, a sender should use the window from segments with the highest acknowledgment number. A receiver should not shrink the window, but a sender must be robust to it. When the receive window is zero, no segments are acceptable except ACKs, and RST and URG must still be processed.",
      sourceId: "rfc9293",
      locator: "Section 3.8.6, Managing the Window (SHLD-14, MUST-34); Section 3.4 (MUST-66)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "zero-window-probing",
      statement:
        "A sender must probe a zero window by periodically sending at least one octet of new data or a retransmission, so that the reopening is reliably reported. A receiver may keep its window closed indefinitely, and as long as it acknowledges the probes, the sender must keep the connection open. The first probe should be sent after the zero window has lasted one RTO, with exponentially increasing intervals after that.",
      sourceId: "rfc9293",
      locator: "Section 3.8.6.1, Zero-Window Probing (MUST-36, MAY-8, MUST-37, SHLD-29, SHLD-30)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "acks-not-reliable",
      statement: "ACK segments that contain no data are not reliably transmitted by TCP.",
      sourceId: "rfc9293",
      locator: "Section 3.8.4, TCP Keep-Alives",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "sws",
      statement:
        "A TCP must implement silly window syndrome avoidance in the sender and the receiver. The usable window is U = SND.UNA + SND.WND − SND.NXT. The sender sends when it can send a full segment, when pushed data fits and nothing is outstanding, when at least a fraction (recommended 1/2) of the maximum window seen can be sent, or when an override timeout of 0.1 to 1 second fires. The receiver advances its right edge only when it can offer at least min(1/2 of its buffer, one MSS).",
      sourceId: "rfc9293",
      locator: "Section 3.8.6.2.1 (MUST-38); Section 3.8.6.2.2 (MUST-39)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "window-scale-limit",
      statement:
        "The 16-bit window field limits windows to 64 KiB, which limits throughput on paths whose bandwidth-delay product exceeds it. Window scaling, agreed in the SYN by both sides, allows windows up to 1 GiB.",
      sourceId: "rfc7323",
      locator: "Section 1.1 (1); Section 2.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-rmem",
      statement:
        "tcp_rmem's default receive buffer of 131072 bytes results in an initial window of 65535. Calling setsockopt() with SO_RCVBUF disables automatic tuning of that socket's receive buffer. tcp_window_scaling defaults to enabled.",
      sourceId: "kernel-ip-sysctl",
      locator: "/proc/sys/net/ipv4: tcp_rmem, tcp_window_scaling",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-autotune",
      statement:
        "With tcp_moderate_rcvbuf enabled (the default), TCP autotunes the receive buffer up to tcp_rmem[2] to match the path. Part of the receive buffer is used as the advertised window and the rest as application buffer, per tcp_adv_win_scale. TCP_USER_TIMEOUT bounds how long transmitted data may stay unacknowledged or buffered data untransmitted because of a zero window before the connection is closed with ETIMEDOUT. SIOCINQ returns the amount of queued unread data in the receive buffer.",
      sourceId: "man-tcp",
      locator:
        "/proc interfaces, tcp_moderate_rcvbuf, tcp_adv_win_scale; Socket options, TCP_USER_TIMEOUT; Ioctls, SIOCINQ",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "so-rcvbuf",
      statement:
        "SO_RCVBUF sets the maximum socket receive buffer; the kernel doubles the requested value for bookkeeping overhead, and the maximum is net.core.rmem_max.",
      sourceId: "man-socket",
      locator: "Socket options, SO_RCVBUF",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "ss-persist",
      statement:
        "ss -o reports the zero-window probe timer as `persist`, and ss -m reports socket memory including the receive buffer (rb) and send buffer (tb).",
      sourceId: "man-ss",
      locator: "OPTIONS, -o and -m",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc9293", note: "Sections 3.1, 3.3.1, 3.4, 3.8.4, 3.8.6, 3.8.6.1, and 3.8.6.2." },
    { sourceId: "rfc7323", note: "Window scaling." },
    { sourceId: "kernel-ip-sysctl", note: "tcp_rmem and SO_RCVBUF disabling autotuning." },
    { sourceId: "man-tcp", note: "Autotuning, window accounting, TCP_USER_TIMEOUT, SIOCINQ." },
    { sourceId: "man-socket", note: "SO_RCVBUF." },
    { sourceId: "man-ss", note: "The persist timer and socket memory." },
  ],
} satisfies LessonBody;
