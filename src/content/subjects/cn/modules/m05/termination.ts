import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m05.termination",
  status: "complete",
  objectives: [
    "Step through a FIN close, including half-close, and say who enters TIME_WAIT and why.",
    "Explain what RST means, when a stack sends one, and how the application sees it.",
    "Distinguish a process crash, a host crash, and a reboot by what the peer sees on the wire.",
    "Read FIN_WAIT_2, CLOSE_WAIT, and TIME_WAIT counts in `ss` and say what each implies about the application.",
  ],
  prerequisites: ["m05.interactions"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "A connection is two byte streams, and each side decides independently when it has finished sending. Ending one must not lose the last bytes in flight in either direction, and both sides must eventually free their state. Sometimes one side cannot finish cleanly: its application gave up, or its host lost all memory of the connection. Then the peer must be told to stop, in a way a stranger on the network cannot easily forge.",
      whyItMatters:
        "Teardown shows up in production as ports exhausted by TIME_WAIT, a server accumulating CLOSE_WAIT sockets, 'connection reset by peer' in client logs, and connections that look established on one side long after the other side is gone. Each is the protocol working, and each points at a specific application behaviour.",
      idea: "Close each direction separately with a FIN, a marker that takes one sequence number so it is delivered and acknowledged in order after the last byte. The side that sends its FIN first, the active closer, waits after the final ACK in TIME_WAIT, so a lost final ACK can be repeated and old segments can die out. For everything else there is RST: an abort that discards state at once and is accepted only if its sequence number fits the connection.",
      howItWorks:
        "close() or shutdown(SHUT_WR) sends FIN after any buffered data. The active closer moves to FIN-WAIT-1, and to FIN-WAIT-2 when its FIN is acknowledged. The peer's kernel acknowledges the FIN at once and enters CLOSE-WAIT; its application sees read() return 0, and may keep sending (half-close). When that application closes, its FIN goes out and it enters LAST-ACK. The active closer acknowledges it and enters TIME-WAIT for 2×MSL (60 seconds on Linux) before deleting the connection. RST is sent when a segment arrives for a connection that does not exist, when an application aborts, or when data arrives for a socket that was closed with data unread. A RST in an established connection is accepted only if its sequence number is in the receive window. With RFC 5961 protection, it must exactly equal the next expected number. The receiving side discards all state, and its pending and next socket calls fail with ECONNRESET.",
      whyItWorks:
        "Because the FIN is sequenced, it cannot overtake data, and it is retransmitted like data until acknowledged. That is what makes a close graceful. TIME_WAIT keeps the active closer able to answer a retransmitted FIN, and keeps the same address-and-port pair from being reused while old segments might still arrive. Requiring the RST's sequence number to fit makes a blind attacker guess within a window of the 32-bit space.",
      limits:
        "A FIN says the peer will send no more; it does not say the peer application processed what it received. A RST loses whatever was in flight or buffered, and the sender of the RST may not know the peer got it. Nothing at all is sent when a host loses power or is cut off: the peer learns only by sending and timing out, or with keepalive. TIME_WAIT costs a little memory and ties up one 4-tuple per closed connection, which matters for a client opening many short connections to one server.",
      connections:
        "The handshake lesson used the same reset rules for refused connections. Module 11 builds on half-open connections and partial failure, Module 12 on CLOSE_WAIT leaks, ephemeral ports, and SO_LINGER, and Module 13 on 'connection reset by peer' investigations.",
    },
    {
      type: "viz",
      id: "timeline-fin",
      vizId: "tcp-timeline",
      scenario: "teardown-fin",
      learningObjective:
        "See each FIN take one sequence number and be acknowledged, see data still flow while half-closed, and see why the active closer stays in TIME-WAIT.",
      caption:
        "The client closes first. RFC 9293 Figure 12's numbers, with 50 bytes from the server added during the half-close.",
    },
    {
      type: "why",
      id: "why-time-wait",
      questionId: "why.16",
    },
    {
      type: "viz",
      id: "timeline-rst",
      vizId: "tcp-timeline",
      scenario: "teardown-rst",
      learningObjective:
        "See a half-open connection discovered when the surviving side sends, and see the RST's sequence number taken from the segment it answers.",
      caption:
        "RFC 9293 Figure 10: the server host rebooted and lost its connection state. A host that is still down would send nothing, and the client would retransmit until it gave up.",
    },
    {
      type: "comparison",
      id: "endings",
      title: "What the peer sees, by how a connection ends",
      columns: ["On the wire", "Peer application sees"],
      rows: [
        {
          label: "close() with nothing unread",
          cells: ["FIN after the last byte.", "read() returns 0 after the remaining data."],
        },
        {
          label: "shutdown(SHUT_WR)",
          cells: [
            "FIN. The closer keeps its receive direction open.",
            "read() returns 0. It can still send, and the closer can still read.",
          ],
        },
        {
          label: "close() with unread received data",
          cells: [
            "RST instead of FIN, which RFC 9293 recommends to show that data was lost.",
            "ECONNRESET, possibly before reading data that was already in flight to it.",
          ],
        },
        {
          label: "Process killed or crashes",
          cells: [
            "The kernel closes its sockets: normally FIN, or RST where data was unread.",
            "The same as close(). The kernel outlives the process.",
          ],
        },
        {
          label: "Host loses power or network",
          cells: [
            "Nothing.",
            "Nothing until it sends. Then retransmissions until a timeout, ETIMEDOUT, after about 15 minutes by default on Linux.",
          ],
        },
        {
          label: "Host reboots and comes back",
          cells: [
            "RST in answer to the peer's next segment.",
            "ECONNRESET on its next call, or EPIPE on a later write.",
          ],
        },
      ],
    },
    {
      type: "viz",
      id: "timeline-simultaneous",
      vizId: "tcp-timeline",
      scenario: "teardown-simultaneous",
      learningObjective:
        "See two FINs cross, both peers pass through CLOSING, and both end in TIME-WAIT.",
      caption:
        "RFC 9293 Figure 13. Simultaneous close is the conceptual case where both sides are active closers.",
    },
    {
      type: "example",
      id: "reading-ss",
      title: "Reading teardown states with ss",
      setup:
        "A Linux API server talks to a database and serves many short client connections. `ss -tan state <name> | wc -l` counts sockets in each state. `ss -tan state time-wait` and `ss -tano state fin-wait-2` show the timers.",
      walkthrough: [
        "Thousands of TIME_WAIT sockets toward clients, each lasting 60 seconds: the server closes first. That is normal and cheap, because each costs a little kernel memory and no file descriptor. `tcp_max_tw_buckets` caps the count; beyond it, TIME_WAIT sockets are destroyed with a warning.",
        "Thousands of TIME_WAIT sockets toward the database's single address and port, from a client making a new connection per query: each one ties up a local port for 60 seconds. At a few hundred connections per second this can exhaust the ephemeral port range. The fix is connection reuse. `tcp_tw_reuse` lets new connections reuse TIME_WAIT sockets when that is safe from the protocol's point of view; its default, 2, allows it on loopback only.",
        "A steady rise in CLOSE_WAIT: peers sent FIN, the kernel acknowledged it, and the application never called close(). This is an application bug, usually a code path that forgets to close the socket, and it leaks file descriptors. Nothing in TCP times it out.",
        "Sockets stuck in FIN_WAIT_2: the local side closed and the peer acknowledged, but the peer never sent its own FIN, often because the peer is itself stuck in CLOSE_WAIT. For sockets the application has already closed, Linux gives up after `tcp_fin_timeout`, 60 seconds by default.",
        "Clients logging 'connection reset by peer': capture both sides and find who sent the RST. A server closing with unread request data, a load balancer's idle timeout, a firewall forgetting the connection, or a peer that restarted are the usual sources.",
      ],
      result:
        "TIME_WAIT on the side that closes first is the protocol working. CLOSE_WAIT is the local application not closing. Resets need a capture to attribute.",
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Half-open, RST validation, TIME_WAIT shortcuts, and SO_LINGER",
      paragraphs: [
        "Half-open connections. If one side has closed, aborted, or lost its state without the other knowing, the connection is half-open. It is discovered only when someone sends. A rebooted host answers the survivor's next segment with RST. If the survivor reconnects from the same port, its SYN reaches a side that still thinks the old connection is established; that side answers with an ACK showing what it expects, the new side resets it, and the retry then succeeds.",
        "Reset rules. A segment for a connection that does not exist gets a RST, taking its sequence number from the segment's ACK field if it had one. A RST is valid in most states only if its sequence number is in the window. RFC 5961's mitigation, described in RFC 9293, tightens this: a RST that is in the window but does not exactly match RCV.NXT gets a 'challenge ACK' instead, so a legitimate peer will resend an exact RST and a blind attacker gains little. RFC 9293 also says the side sending a reset should enter TIME-WAIT, and that a RST may carry diagnostic data, though none is standardized.",
        "Assassination. A RST arriving in TIME-WAIT ends it early, which RFC 1337 calls TIME-WAIT assassination, because it reopens the hazard TIME-WAIT exists to prevent. Linux's `tcp_rfc1337` is off by default, so Linux closes the TIME_WAIT socket immediately on such a RST.",
        "Shortcuts. RFC 9293 allows a TIME-WAIT socket to accept a new SYN for the same 4-tuple if the new initial sequence number is larger than any used before, and recommends a timestamp-based version of that rule. Linux's `tcp_tw_reuse` is the client-side equivalent. The old `tcp_tw_recycle` was removed in Linux 4.12 because it broke clients behind NAT.",
        "SO_LINGER. With it enabled, close() blocks until queued data is sent or the linger time passes, instead of returning at once and finishing in the background. A linger time of zero is widely used to abort with a RST and skip TIME_WAIT; that loses buffered data and exists for error paths, not for normal closes.",
        "Who must know what. After a graceful close, the closer knows every byte it sent reached the peer's TCP. After a RST, neither side knows which in-flight bytes the other processed. An application that needs certainty needs an application-level acknowledgment before closing.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "who-time-wait",
          prompt:
            "An HTTP/1.1 server closes each connection after sending its response. Which machine accumulates TIME_WAIT sockets, and is that a problem?",
          answer:
            "The server, because it sends the first FIN and so is the active closer. Each TIME_WAIT socket lasts 60 seconds on Linux and costs a little memory but no file descriptor, so it is usually harmless on a server whose clients come from many addresses and ports. It becomes a problem when one client opens many connections to the same server port, which keep-alive avoids.",
        },
        {
          id: "close-wait",
          prompt:
            "`ss` shows 5,000 sockets in CLOSE_WAIT on a service and the number keeps growing. What is happening, and will TCP clean them up?",
          answer:
            "The peers closed their side (sent FIN), the local kernel acknowledged, and the local application never called close() on those sockets. TCP will not clean them up, because CLOSE_WAIT waits for the local application. Find the code path that drops a connection without closing it, such as an error path or a pool that never discards dead connections.",
        },
        {
          id: "rst-meaning",
          prompt:
            "A client's write() succeeds, and its next read() fails with ECONNRESET. What does that tell you about the request it wrote?",
          answer:
            "The peer, or a middlebox pretending to be the peer, sent a RST. The write had only queued the bytes locally. They may never have arrived, may have arrived and been discarded, or may have been processed before the reset. TCP cannot say which, so the client must treat the request's outcome as unknown.",
        },
        {
          id: "lost-last-ack",
          prompt:
            "In a normal close, the active closer's final ACK is lost. Walk through what happens with TIME_WAIT, and what would happen without it.",
          answer:
            "The peer in LAST-ACK times out and retransmits its FIN. The active closer, still in TIME-WAIT, acknowledges it again and restarts its TIME-WAIT timer, and the peer closes cleanly. Without TIME_WAIT, the active closer would have no state; the retransmitted FIN would get a RST, and the peer's application might see an error for a connection that closed correctly.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "close-simplex",
      statement:
        "CLOSE means 'I have no more data to send', and the closing user may continue to receive until told the peer has closed. TCP reliably delivers all data sent before CLOSE. A received FIN is acknowledged, but the receiver does not send its own FIN until its user closes. A connection may end with the FIN handshake or with an abort in which RST segments are sent and state is discarded immediately, and the application must be told which.",
      sourceId: "rfc9293",
      locator: "Section 3.6, Closing a Connection, Cases 1 to 3 and Figure 12 (MUST-12)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "half-close",
      statement:
        "The two directions close independently, so a connection can be half closed and a host may continue sending in the open direction. A host may implement a half-duplex close in which a closed socket cannot read; if CLOSE is called while received data is pending, or data arrives after CLOSE, it should send a RST to show that data was lost.",
      sourceId: "rfc9293",
      locator: "Section 3.6.1, Half-Closed Connections (MAY-1, SHLD-3)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "time-wait-duration",
      statement:
        "An actively closed connection must linger in TIME-WAIT for 2×MSL. It may accept a new SYN from the remote endpoint to reopen directly from TIME-WAIT if its new ISN is larger than any sequence number used before and it returns to TIME-WAIT if the SYN was an old duplicate. A timestamp-based improvement should be implemented.",
      sourceId: "rfc9293",
      locator: "Section 3.6.1, Half-Closed Connections (MUST-13, MAY-2, SHLD-4)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "time-wait-purpose",
      statement:
        "TIME-WAIT waits long enough to be sure the remote peer received the acknowledgment of its FIN, and to keep new connections from being affected by delayed segments from previous ones. MSL is taken to be 2 minutes. TIME-WAIT limits the rate of connection reuse, while ISN selection further protects against confusing incarnations.",
      sourceId: "rfc9293",
      locator: "Section 3.3.2, TIME-WAIT; Section 3.4.1; Section 3.4.2",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "time-wait-processing",
      statement:
        "In TIME-WAIT, the only segment expected is a retransmission of the remote FIN, which is acknowledged and restarts the 2 MSL timeout. A RST received in TIME-WAIT deletes the TCB. When the time-wait timeout expires, the TCB is deleted.",
      sourceId: "rfc9293",
      locator:
        "Section 3.10.7.4, fifth check (ACK) and second check (RST), TIME-WAIT STATE; Section 3.10.8",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "simultaneous-close",
      statement:
        "A simultaneous close exchanges FIN segments; each peer acknowledges the FIN it received once earlier segments are processed, passes through CLOSING, and both enter TIME-WAIT.",
      sourceId: "rfc9293",
      locator: "Section 3.6, Case 3 and Figure 13",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "half-open",
      statement:
        "A connection is half-open if one peer closed or aborted it, or lost memory of it in a crash or reboot, without the other knowing. Such connections are reset when data is sent in either direction: data arriving for a connection that no longer exists is answered with a RST, which the surviving peer accepts and aborts on.",
      sourceId: "rfc9293",
      locator: "Section 3.5.1, Half-Open Connections and Other Anomalies, Figures 9 and 10",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "reset-generation",
      statement:
        "A RST is sent when a segment arrives that is apparently not for the current connection, and must not be sent if that is unclear. For a nonexistent connection, a RST answers any segment except a RST, taking its sequence number from the segment's ACK field if present. In synchronized states, an unacceptable segment gets an empty ACK, not a RST. The side issuing a reset should enter TIME-WAIT.",
      sourceId: "rfc9293",
      locator: "Section 3.5.2, Reset Generation",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "reset-processing",
      statement:
        "Outside SYN-SENT, a RST is valid if its sequence number is in the window. In ESTABLISHED, FIN-WAIT-1, FIN-WAIT-2, and CLOSE-WAIT, a RST makes outstanding RECEIVEs and SENDs fail with 'reset', flushes queues, signals the user, and deletes the TCB. With RFC 5961 protection, a RST outside the window is dropped, one exactly at RCV.NXT resets the connection, and one elsewhere in the window gets a challenge ACK.",
      sourceId: "rfc9293",
      locator: "Section 3.5.3, Reset Processing; Section 3.10.7.4, second check (RST bit)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "abort",
      statement:
        "ABORT aborts pending SENDs and RECEIVEs, removes the TCB, and sends a RST to the peer.",
      sourceId: "rfc9293",
      locator: "Section 3.9.1.6, Abort",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "no-liveness",
      statement:
        "TCP is connection oriented, though it does not inherently include a liveness detection capability.",
      sourceId: "rfc9293",
      locator: "Section 2.2, Key TCP Concepts",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "assassination",
      statement:
        "TIME-WAIT can be terminated prematurely ('assassinated') by an old duplicate segment that provokes a RST, reopening the hazard of old duplicates that TIME-WAIT is meant to remove.",
      sourceId: "rfc1337",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "linux-time-wait-length",
      statement:
        "Linux defines TCP_TIMEWAIT_LEN as 60 seconds, and TCP_FIN_TIMEOUT, the FIN-WAIT-2 deadlock breaker, equal to it.",
      sourceId: "linux-tcp-h",
      locator: "#define TCP_TIMEWAIT_LEN, TCP_FIN_TIMEOUT",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-tw-sysctls",
      statement:
        "tcp_fin_timeout (default 60 seconds) is how long an orphaned connection stays in FIN_WAIT_2 before being aborted. tcp_max_tw_buckets caps TIME_WAIT sockets; beyond it they are destroyed with a warning. tcp_tw_reuse allows reusing TIME_WAIT sockets for new connections when safe; its default, 2, enables it for loopback only. tcp_orphan_retries (default 8) limits retransmissions on a locally closed connection.",
      sourceId: "kernel-ip-sysctl",
      locator:
        "/proc/sys/net/ipv4: tcp_fin_timeout, tcp_max_tw_buckets, tcp_tw_reuse, tcp_orphan_retries",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-rfc1337",
      statement:
        "With tcp_rfc1337 disabled (the default), a RST received in TIME_WAIT closes the socket immediately. tcp_tw_recycle existed from Linux 2.4 to 4.11 and was not recommended because peers behind NAT may not use monotonic timestamps.",
      sourceId: "man-tcp",
      locator: "/proc interfaces, tcp_rfc1337, tcp_tw_recycle",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "shutdown",
      statement:
        "shutdown() with SHUT_WR disallows further transmissions, SHUT_RD further receptions, and SHUT_RDWR both, on all or part of a full-duplex connection.",
      sourceId: "man-shutdown",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linger",
      statement:
        "With SO_LINGER enabled, close() or shutdown() does not return until queued data is sent or the linger timeout is reached; otherwise close() returns at once and closing happens in the background. A socket closed as part of _exit() always lingers in the background.",
      sourceId: "man-socket",
      locator: "Socket options, SO_LINGER",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "send-errors",
      statement:
        "send() returns ECONNRESET when the connection was reset by the peer, and EPIPE when the local end has been shut down on a connection-oriented socket, also raising SIGPIPE unless MSG_NOSIGNAL is set.",
      sourceId: "man-send",
      locator: "ERRORS, ECONNRESET, EPIPE; The flags argument, MSG_NOSIGNAL",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-dead-peer-timeout",
      statement:
        "With tcp_retries2 at its default of 15, an established connection whose data stays unacknowledged is killed after a hypothetical timeout of 924.6 seconds or more.",
      sourceId: "kernel-ip-sysctl",
      locator: "/proc/sys/net/ipv4: tcp_retries2",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "ss-state-filter",
      statement:
        "ss state filters include fin-wait-1, fin-wait-2, time-wait, close-wait, last-ack, and closing, and -o shows the timewait timer.",
      sourceId: "man-ss",
      locator: "STATE-FILTER; OPTIONS, -o",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    {
      sourceId: "rfc9293",
      note: "Sections 2.2, 3.3.2, 3.4.1, 3.4.2, 3.5.1 to 3.5.3, 3.6, 3.6.1, 3.9.1.6, 3.10.7.4, and 3.10.8.",
    },
    { sourceId: "rfc1337", note: "TIME-WAIT assassination." },
    { sourceId: "linux-tcp-h", note: "Linux's 60-second TIME_WAIT." },
    {
      sourceId: "kernel-ip-sysctl",
      note: "FIN_WAIT_2, TIME_WAIT reuse and limits, and the retransmission timeout.",
    },
    { sourceId: "man-tcp", note: "tcp_rfc1337 and tcp_tw_recycle." },
    { sourceId: "man-shutdown", note: "Half-close from the socket API." },
    { sourceId: "man-socket", note: "SO_LINGER." },
    { sourceId: "man-send", note: "ECONNRESET, EPIPE, and SIGPIPE." },
    { sourceId: "man-ss", note: "State filters and timers." },
  ],
} satisfies LessonBody;
