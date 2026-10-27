import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m05.handshake",
  status: "complete",
  objectives: [
    "Step through SYN, SYN-ACK, and ACK and say what each endpoint knows after each one.",
    "Explain why two messages are not enough, using an old duplicate SYN.",
    "Explain RST during establishment: a closed port, a bogus SYN-ACK, and how each shows up in connect().",
    "Describe the Linux SYN and accept queues, SYN retries, and simultaneous open.",
  ],
  prerequisites: ["m05.header-and-numbers"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Before any data can be numbered, both endpoints must agree on two starting points: the client's initial sequence number and the server's. Each side can choose its own, but it cannot know that the other side has learned it until the other side says so. The network can also deliver a SYN left over from an earlier attempt, and the receiver of a SYN has no way of telling by itself whether that SYN is current.",
      whyItMatters:
        "Connection setup is the first thing that fails in an outage, and how it fails is evidence: an immediate refusal, a SYN that goes unanswered, a SYN-ACK that never arrives back. Connection pools, load balancers, SYN floods, and backlog overflows are all about this exchange.",
      idea: "Each side sends its ISN in a SYN and must see it acknowledged. That is four messages, but the server's acknowledgment of the client's SYN and its own SYN travel in one segment, so the exchange is SYN, SYN-ACK, ACK. The third message is the client confirming the server's ISN, which also proves to the server that the client really started this attempt.",
      howItWorks:
        "The client picks ISN x and sends SYN seq=x, entering SYN-SENT. A listening server creates state for the attempt, picks ISN y, and replies SYN-ACK seq=y ack=x+1, entering SYN-RECEIVED. The client checks that the ack equals x+1. If it does, the client enters ESTABLISHED and replies ACK seq=x+1 ack=y+1, and connect() returns. If it does not, the client answers with a RST. When the server sees ack=y+1, it enters ESTABLISHED and, on Linux, moves the connection to the listening socket's accept queue. Options are agreed in the SYN and SYN-ACK: MSS, window scale, SACK-permitted, and timestamps. A port with no listener answers a SYN with RST, which connect() reports as ECONNREFUSED.",
      whyItWorks:
        "The server cannot judge a SYN alone, so it asks: its SYN-ACK acknowledges a specific ISN, and only a client that really sent that ISN will answer with a matching ACK. A client that did not send it answers with a RST, and the server discards the half-made connection. Each side ends knowing that the other has its ISN and is reachable in both directions.",
      limits:
        "The handshake proves that two TCP stacks agreed on numbers. It says nothing about the application: on Linux, the kernel completes the handshake before the server program calls accept(). Holding state for every SYN lets a flood of fake SYNs exhaust the server, which is why Linux has a SYN backlog and SYN cookies. A lost SYN costs a full retransmission timeout, 1 second initially and doubling, so a slow connect() is often one lost packet. It is not a slow server.",
      connections:
        "The previous lesson defined the numbers this exchange synchronizes. The termination lesson uses the same RST rules to end connections. Module 12 covers listen backlogs and accept() in depth, and Module 13's 'DNS resolves, but TCP connection establishment fails' scenario starts from the failures shown here.",
    },
    {
      type: "viz",
      id: "timeline-basic",
      vizId: "tcp-timeline",
      scenario: "handshake-basic",
      learningObjective:
        "See each side learn the other's initial sequence number, and see that the first data byte and the bare ACK share a sequence number.",
      caption:
        "Client and server states are shown after each segment arrives, following RFC 9293 Figure 6. The last step is the kernel's ACK of the request, not an application reply.",
    },
    {
      type: "viz",
      id: "timeline-old-duplicate",
      vizId: "tcp-timeline",
      scenario: "handshake-old-duplicate",
      learningObjective:
        "See an old duplicate SYN get a SYN-ACK, see the client reject it with RST, and see only the client's real attempt become a connection.",
      caption:
        "RFC 9293 Figure 8. The old SYN has the same ports as the new attempt and a different sequence number.",
    },
    {
      type: "why",
      id: "why-three-way",
      questionId: "why.11",
    },
    {
      type: "viz",
      id: "timeline-refused",
      vizId: "tcp-timeline",
      scenario: "handshake-refused",
      learningObjective:
        "See how a host with no listener answers a SYN, and why the client accepts that RST.",
      caption:
        "A SYN to a closed port on a host that is up. A firewall that drops the SYN instead produces no answer, and the client retries until it gives up.",
    },
    {
      type: "example",
      id: "linux-connect",
      title: "Watching a handshake, and a failed one, on Linux",
      setup:
        "On a client, run `tcpdump -n -i any 'tcp port 8080 and tcp[tcpflags] & (tcp-syn|tcp-rst) != 0'`, then `curl http://10.0.0.20:8080/` three times: once with a server listening, once with no server, and once with a firewall rule dropping the SYN.",
      walkthrough: [
        "Listening server: `Flags [S]` from the client, `Flags [S.]` back, then the client's bare ACK (which this filter hides). Each SYN shows `options [mss 1460,sackOK,TS ...,nop,wscale 7]` or similar. `ss -tn state syn-sent` is empty because the handshake took less than a round trip to finish.",
        "No server: `Flags [S]`, then `Flags [R.]` with `ack` one more than the SYN's seq, and `seq 0`. curl prints 'Connection refused' immediately. The reset proves the host is reachable; only the port is closed.",
        "Dropped SYN: `Flags [S]` repeated, each copy with the same sequence number, because they are retransmissions of the same SYN. The gaps grow: classically 1, 2, 4, 8 seconds and so on, while recent kernels first retry at 1-second intervals (`tcp_syn_linear_timeouts`). `ss -tno state syn-sent` shows the socket with a `timer:(on,...)` counting retries. With `tcp_syn_retries` at its default of 6, the attempt ends in ETIMEDOUT after roughly two minutes, unless curl's own timeout fires first.",
        "On the server under a SYN flood, `ss -tn state syn-recv` grows and `nstat -az | grep -i ListenOverflows` (or `netstat -s`) counts accept-queue overflows. `ss -ltn` shows, for each listening socket, the current accept queue in Recv-Q and its limit in Send-Q.",
      ],
      result:
        "RST means something answered. Silence with retransmitted SYNs means something dropped the packets. The SYN's options say what the connection will be able to do, so they are worth reading before debugging throughput.",
    },
    {
      type: "steps",
      id: "simultaneous-open",
      title: "Simultaneous open, at a conceptual level",
      steps: [
        {
          title: "Both sides send SYN at once",
          body: "Two peers each call connect() to the other's address and port at about the same time. Each sends a SYN and enters SYN-SENT. Neither is listening.",
        },
        {
          title: "Each receives a SYN with no ACK",
          body: "A SYN without an ACK while in SYN-SENT moves a peer to SYN-RECEIVED. Each answers with a SYN-ACK that repeats its own SYN and acknowledges the other's.",
        },
        {
          title: "Each receives a SYN-ACK",
          body: "The ACK confirms its own ISN, so each enters ESTABLISHED. There is one connection, not two, and no server. Four segments are exchanged, not three.",
        },
        {
          title: "Why it is rare",
          body: "It requires both sides to know each other's port in advance and dial at once. It shows up in peer-to-peer NAT traversal. RFC 9293 requires TCP to support it, and an old duplicate SYN can make an ordinary open look like one, which the RST rules sort out.",
        },
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Queues, retries, SYN cookies, and data in the SYN",
      paragraphs: [
        "Linux keeps two queues per listening socket. Half-open attempts in SYN-RECEIVED are limited by `tcp_max_syn_backlog`. Completed connections wait in the accept queue, whose length is the listen() backlog, capped by `net.core.somaxconn` (4096 by default since Linux 5.4). If the accept queue is full, Linux by default ignores the request, as listen(2) allows for a protocol that retransmits, so a later retry by the client may succeed. With `tcp_abort_on_overflow`, it resets the connection instead.",
        "SYN cookies (`tcp_syncookies`, default 1) are used when the SYN backlog overflows. The server encodes the connection's details in its ISN and keeps no state until the final ACK returns. Linux's man page calls this a violation of the TCP protocol and a last resort, because some options cannot be fully encoded in a cookie.",
        "SYN retries: RFC 9293 requires an endpoint to keep retransmitting a SYN for at least 3 minutes before giving up, though an application may give up sooner. Linux's `tcp_syn_retries` defaults to 6. tcp(7) puts that at about 127 seconds; the current kernel documentation, which adds a few linear 1-second retries first (`tcp_syn_linear_timeouts`, default 4), puts the final timeout at about 131 seconds. Either way it is below the RFC's 3 minutes. `tcp_synack_retries` (default 5) limits the server's SYN-ACK retransmissions. The initial RTO is 1 second, and if a SYN had to be retransmitted, RFC 6298 raises the RTO to 3 seconds when data transmission begins.",
        "A SYN may carry data. RFC 9293 allows it as long as the receiver does not deliver it to the application before the connection is ESTABLISHED. TCP Fast Open (RFC 7413, not covered in depth here) uses this with a cookie from an earlier connection to save a round trip. Linux enables the client side by default (`tcp_fastopen = 0x1`).",
        "A RST that answers a SYN is accepted only if its ACK acknowledges that SYN. A reset in SYN-RECEIVED returns a passively opened connection to LISTEN without telling the application, and reports 'connection refused' for an actively opened one.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "syn-ack-numbers",
          prompt:
            "A client sends SYN seq=4000. The server picks ISN 9000. Write the SYN-ACK and the final ACK.",
          answer:
            "SYN-ACK: seq=9000, ack=4001. Final ACK: seq=4001, ack=9001. Each ack is the other side's ISN plus one, because the SYN takes one sequence number, and the client's first data byte will also be 4001.",
        },
        {
          id: "two-way",
          prompt:
            "If the server entered ESTABLISHED as soon as it sent its SYN-ACK, what could go wrong when an old duplicate SYN arrives?",
          answer:
            "The server would create a connection the client never asked for, allocate buffers for it, and perhaps act on data that arrives on it. It would also never learn whether the client received its ISN. With the third message, the server waits for an ACK of its own ISN. The real client either never sends it or answers with a RST, so the stale attempt dies.",
        },
        {
          id: "refused-vs-timeout",
          prompt:
            "connect() to port 5432 fails at once with 'Connection refused' on one host and after two minutes with 'Connection timed out' on another. What reached each host?",
          answer:
            "In the first case, the SYN reached a host that answered with RST because nothing listens on 5432, so the path works. In the second, nothing came back for any SYN retransmission: a firewall dropped them, the host is down, or the replies are lost on the way back. Linux retransmitted the SYN up to tcp_syn_retries (6) times before giving up.",
        },
        {
          id: "accept-backlog",
          prompt:
            "A server process is stuck and never calls accept(). New clients' connect() still succeeds for a while, then starts timing out. Explain both phases.",
          answer:
            "The kernel completes handshakes without the application and puts them on the accept queue, so connect() succeeds until the queue reaches its backlog limit. After that, Linux ignores new requests (unless tcp_abort_on_overflow is set), so clients keep retransmitting and eventually time out. The clients that 'connected' first see their requests hang, because no program ever reads them.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "three-way-sync",
      statement:
        "Each side must send its own ISN and receive an acknowledgment of it, and must receive the peer's ISN and acknowledge it. Because the second and third of these four steps can be combined, this is the three-way handshake. It is necessary because sequence numbers are not tied to a global clock: the receiver of a SYN cannot know whether it is old unless it remembers the last sequence number used, so it must ask the sender to verify the SYN.",
      sourceId: "rfc9293",
      locator: "Section 3.4.1, Initial Sequence Number Selection",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "old-duplicate",
      statement:
        "The principal reason for the three-way handshake is to prevent old duplicate connection initiations from causing confusion. In Figure 8, an old duplicate SYN gets a SYN-ACK; the initiator sees an incorrect ACK and returns a RST whose sequence number comes from that ACK; the responder returns to LISTEN; and the real SYN then completes normally.",
      sourceId: "rfc9293",
      locator: "Section 3.5, Establishing a Connection, Figures 6 and 8",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "states",
      statement:
        "LISTEN waits for a connection request; SYN-SENT waits for a matching request after sending one; SYN-RECEIVED waits for an acknowledgment after both sending and receiving a connection request; ESTABLISHED is the data-transfer state. CLOSED is fictional and means no TCB exists.",
      sourceId: "rfc9293",
      locator: "Section 3.3.2, State Machine Overview",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "simultaneous-open",
      statement:
        "The handshake also works when both peers initiate it; each receives a SYN without an acknowledgment after sending a SYN, and each cycles CLOSED, SYN-SENT, SYN-RECEIVED, ESTABLISHED. A TCP implementation must support simultaneous open. An old duplicate SYN can make an ordinary open look simultaneous, and resets disambiguate it.",
      sourceId: "rfc9293",
      locator: "Section 3.5, Figure 7 and MUST-10",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "syn-data",
      statement:
        "Data-carrying segments may be used during synchronization, so long as the receiver does not deliver the data to the user until the connection reaches ESTABLISHED.",
      sourceId: "rfc9293",
      locator: "Section 3.5, Establishing a Connection",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "reset-closed-port",
      statement:
        "If no connection exists, a reset is sent in response to any segment except a reset. If the incoming segment has no ACK, the reset has sequence number zero and an ACK equal to the incoming sequence number plus its length. In SYN-SENT, a RST is acceptable if its ACK acknowledges the SYN. A RST in SYN-RECEIVED returns a passively opened connection to LISTEN, and signals 'connection refused' for an actively opened one.",
      sourceId: "rfc9293",
      locator:
        "Section 3.5.2, Reset Generation (1); Section 3.5.3, Reset Processing; Section 3.10.7.4, SYN-RECEIVED STATE",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "syn-retransmission",
      statement:
        "Opening a connection can fail by excessive SYN retransmissions, a RST, or ICMP Port Unreachable. R2 for a SYN must allow retransmission for at least 3 minutes, though the application may give up sooner.",
      sourceId: "rfc9293",
      locator: "Section 3.8.3, TCP Connection Failures (MUST-23)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "syn-rto",
      statement:
        "Until an RTT is measured, the RTO should be 1 second, with backoff on each retransmission. If the timer expires awaiting the ACK of a SYN and the RTO is under 3 seconds, the RTO must be reset to 3 seconds when data transmission begins.",
      sourceId: "rfc6298",
      locator: "Section 2, rule (2.1); Section 5, rule (5.7)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-syn-retries",
      statement:
        "tcp_syn_retries defaults to 6; the kernel documentation says that with an initial RTO of 1 second and tcp_syn_linear_timeouts = 4 the attempt finally times out after about 131 seconds. tcp_syn_linear_timeouts (default 4) is the number of SYN retransmissions with a linear backoff before exponential backoff begins.",
      sourceId: "kernel-ip-sysctl",
      locator: "/proc/sys/net/ipv4: tcp_syn_retries, tcp_syn_linear_timeouts",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-listen-knobs",
      statement:
        "tcp_syn_retries defaults to 6, which tcp(7) says corresponds to retrying for up to about 127 seconds. tcp_max_syn_backlog limits queued connection requests not yet acknowledged by the client. tcp_synack_retries (default 5) limits SYN-ACK retransmissions. tcp_syncookies (default 1) sends SYN cookies when a socket's SYN backlog overflows and is described as a violation of the TCP protocol to be used as a last resort. tcp_abort_on_overflow resets connections when the listener cannot accept them fast enough. tcp_fastopen defaults to 0x1, client-side Fast Open.",
      sourceId: "man-tcp",
      locator:
        "/proc interfaces: tcp_syn_retries, tcp_max_syn_backlog, tcp_synack_retries, tcp_syncookies, tcp_abort_on_overflow, tcp_fastopen",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-accept-queue",
      statement:
        "On Linux the listen() backlog limits the queue of completely established sockets waiting to be accepted; incomplete requests are limited by tcp_max_syn_backlog; the backlog is capped by somaxconn, 4096 by default since Linux 5.4. When the queue is full, a protocol with retransmission may ignore the request so a later retry succeeds.",
      sourceId: "man-listen",
      locator: "DESCRIPTION; NOTES",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "econnrefused",
      statement:
        "connect() fails with ECONNREFUSED when a stream socket finds no one listening on the remote address.",
      sourceId: "man-connect",
      locator: "ERRORS, ECONNREFUSED",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ss-states",
      statement:
        "ss can filter by TCP state, including syn-sent, syn-recv, established, and listening, and -o shows timers in the form timer:(name,expire_time,retrans), where `on` is the retransmission timer.",
      sourceId: "man-ss",
      locator: "STATE-FILTER; OPTIONS, -o",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc9293", note: "Sections 3.3.2, 3.4.1, 3.5 to 3.5.3, 3.8.3, and 3.10.7.4." },
    { sourceId: "rfc6298", note: "Initial RTO and the 3-second fallback after a lost SYN." },
    { sourceId: "man-tcp", note: "SYN backlog, SYN cookies, SYN-ACK retries, Fast Open." },
    { sourceId: "kernel-ip-sysctl", note: "Current tcp_syn_retries timing." },
    { sourceId: "man-listen", note: "The accept queue and somaxconn." },
    { sourceId: "man-connect", note: "ECONNREFUSED." },
    { sourceId: "man-ss", note: "State filters and timers." },
  ],
} satisfies LessonBody;
