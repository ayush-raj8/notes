import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m05.service-model",
  status: "complete",
  objectives: [
    "State TCP's service: a reliable, ordered, full-duplex byte stream between two sockets, with no message boundaries.",
    "Say exactly what a successful connect(), write(), or ACK proves, and what it does not.",
    "Frame messages on top of a byte stream, and explain why retries and idempotency are the application's job.",
    "Explain why reliability and congestion control live in the endpoints and not in routers.",
  ],
  prerequisites: ["m04.icmp"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "IP delivers datagrams best effort: a datagram can be lost, duplicated, reordered, or damaged, and the network keeps no record of any conversation. Most programs want something else: hand bytes to the kernel and have the same bytes come out at the other end, in order, or be told that this is no longer possible.",
      whyItMatters:
        "Most bugs blamed on TCP are about the gap between what TCP promises and what a program assumes. A read that returns half a message, a request that ran twice after a retry, and a 'connected' client whose request then fails are all TCP keeping its promise exactly while the program expected a different one.",
      idea: "TCP turns datagrams into a reliable, ordered byte stream in each direction, between exactly two endpoints. It numbers every byte, has the receiver acknowledge what it holds, resends what is not acknowledged, and delivers to the reader in sequence order only. The promise stops at the receiving TCP. It covers bytes, not messages, and the peer's kernel, not the peer's application.",
      howItWorks:
        "connect() runs a handshake in which each side picks a starting sequence number and confirms the other's. After that, write() copies bytes into the kernel's send buffer and returns. The kernel cuts the buffer into segments whose sizes it chooses, numbers them by byte offset, and keeps a copy of each until the peer acknowledges it. The receiving kernel checks each segment's checksum and sequence numbers, discards duplicates, holds out-of-order bytes, acknowledges the highest in-order byte, and lets read() return any amount of in-order data that is available. close() sends a FIN after the last byte, so the reader sees end-of-file once everything before it has arrived.",
      whyItWorks:
        "Numbering bytes rather than messages lets the receiver detect loss, duplication, and reordering without knowing what the bytes mean, and lets the sender re-cut segments when it retransmits. Keeping all this state in the two hosts means no router has to remember anything, which is what lets packets reroute around a failed router without breaking the connection.",
      limits:
        "TCP does not preserve write() boundaries, so the application must frame its own messages. An acknowledgment means the peer's TCP has the bytes in a buffer, not that the peer application read or acted on them. A connection can fail with bytes already sent and their fate unknown, so TCP cannot make a retry safe. TCP has no built-in liveness check: an idle connection to a dead host looks healthy until something is sent. TCP does not encrypt or authenticate anything.",
      connections:
        "Module 1 placed TCP above IP and explained ports and the 4-tuple. The rest of this module opens up the mechanism: the header and sequence numbers, the handshake, retransmission, the two windows, and teardown. Module 8 builds HTTP framing on this byte stream, Module 9 adds TLS because TCP does not encrypt, and Module 11 returns to partial failure and retries.",
    },
    {
      type: "comparison",
      id: "promises",
      title: "What TCP promises, and what it does not",
      columns: ["TCP promises", "TCP does not promise"],
      rows: [
        {
          label: "Delivery",
          cells: [
            "Bytes the reader gets are the bytes written, in order, with no duplicates or gaps.",
            "That every byte written will arrive. A connection can be reset or time out.",
          ],
        },
        {
          label: "Boundaries",
          cells: [
            "A continuous stream in each direction.",
            "That one write() arrives as one read(). Writes can be split or merged.",
          ],
        },
        {
          label: "Acknowledgment",
          cells: [
            "An ACK means the peer's TCP holds every byte before the acknowledged number.",
            "That the peer application read, processed, or committed anything.",
          ],
        },
        {
          label: "Failure reporting",
          cells: [
            "The application is told whether the peer closed normally (FIN) or the connection was aborted (RST or timeout).",
            "Which of the bytes sent before the failure the peer acted on.",
          ],
        },
        {
          label: "Liveness",
          cells: [
            "Retransmission until the peer acknowledges or a timeout ends the connection.",
            "Detection of a dead peer on an idle connection. Keepalive is optional and off by default.",
          ],
        },
        {
          label: "Timing",
          cells: [
            "Data is not held forever by the sender's own buffering.",
            "Any delivery deadline. Loss recovery can add seconds.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "framing",
      title: "One request, three reads",
      setup:
        "A client writes a 10-byte length-prefixed message: a 4-byte big-endian length (6) followed by `HELLO!`. It then writes a second message of the same form. The server calls `read(fd, buf, 4096)` in a loop.",
      walkthrough: [
        "The first read returns 3 bytes. That is legal: the sending TCP may have cut the stream after the third byte, for example where its window ended, and read() returns whatever in-order bytes are available when the reader wakes up.",
        "A server that parses `buf` as a whole message now reads a length of garbage. The fix is to keep reading until 4 bytes of length are buffered, then until that many payload bytes are buffered.",
        "The second read returns 17 bytes: the rest of the first message and all of the second. Writes were merged, which is just as legal. A parser that handles one message per read() silently drops the second.",
        "The third read blocks, because no more bytes are in order. If the client had called `close()`, the next read would return 0, which is how the server learns the stream ended. Without a length prefix or another delimiter, 0 is the only boundary TCP itself provides.",
        "Checking the receive side with `ss -tni` while the server is stopped in a debugger shows the bytes sitting in Recv-Q. They were acknowledged by the server's kernel long before the server read them.",
      ],
      result:
        "TCP delivered every byte in order and the reads were still uneven. The framing (here, a length prefix; in HTTP/1.1, headers ending in a blank line and a Content-Length or chunked body) is the application's protocol, not TCP's.",
    },
    {
      type: "why",
      id: "why-byte-stream",
      questionId: "why.08",
    },
    {
      type: "steps",
      id: "what-success-means",
      title: "What each success actually proves",
      steps: [
        {
          title: "connect() returned 0",
          body: "The three-way handshake finished. The peer's kernel had a listening socket on that port and has queued the connection. On Linux the server application may not have called accept() yet, and may never do so if it is stuck or overloaded.",
        },
        {
          title: "write() returned n",
          body: "n bytes were copied into the local send buffer. Nothing has necessarily been sent. send(2) says plainly that no indication of failure to deliver is implicit in a successful send.",
        },
        {
          title: "An ACK covering your bytes arrived",
          body: "The peer's TCP holds them. The application on the other side may still be about to read them, may crash before reading them, or may read them and fail to act.",
        },
        {
          title: "The peer's FIN arrived (read() returned 0)",
          body: "The peer will send nothing more. If you closed first and then saw the peer close, every byte you sent before close() reached the peer's TCP. It still does not say what the peer application did with them.",
        },
        {
          title: "An application-level reply arrived",
          body: "This is the only signal that the remote program processed the request, and only to the extent the reply says so. Every guarantee beyond 'the bytes arrived' has to come from the application protocol.",
        },
      ],
    },
    {
      type: "why",
      id: "why-processed",
      questionId: "why.09",
    },
    {
      type: "why",
      id: "why-retries",
      questionId: "why.10",
    },
    {
      type: "why",
      id: "why-connect-ok-request-fails",
      questionId: "why.20",
    },
    {
      type: "why",
      id: "why-endpoints",
      questionId: "why.51",
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Edge cases and Linux details",
      paragraphs: [
        "The PSH bit is not a message marker. RFC 9293 says so directly, and says a sender should collapse successive pushes into the largest possible segment. A capture where each write appears as one segment with PSH set is a coincidence of timing, not something the receiver can rely on.",
        "TCP has an 'urgent' mechanism (the URG flag, MSG_OOB on Linux), but RFC 9293 says new applications should not use it, because implementations and middleboxes treat it inconsistently. It is not out-of-band messaging and does not create a message boundary.",
        "Linux's tcp(7) states the service in one line: reliable, stream-oriented, full-duplex, in order, retransmits lost packets, and 'TCP does not preserve record boundaries.' The per-packet checksum is 16 bits. It catches most corruption, but not all, which is one reason TLS adds its own integrity check.",
        "A process that exits does not leave its connections half-open: the kernel closes its sockets, normally with a FIN, and lingers in the background to send what is buffered. A host that loses power sends nothing, and its peer finds out only when it next sends or when a keepalive probe goes unanswered. The termination lesson covers both.",
        "UDP gives the opposite trade: it keeps datagram boundaries and adds nothing else. Module 6 shows protocols such as QUIC that rebuild reliability per stream on top of it, precisely to avoid one ordered stream for everything.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "two-writes-one-read",
          prompt:
            "A client calls write() twice, with 100 and 200 bytes. The server's single read() returns 300 bytes. Is something wrong?",
          answer:
            "No. TCP carries a byte stream and does not preserve write boundaries, so the kernel may merge the two writes into one segment, or the receiver may read after both arrived. The server must find message boundaries itself, with a length prefix or a delimiter, and must also handle the opposite case of one write arriving across several reads.",
        },
        {
          id: "ack-then-crash",
          prompt:
            "A payment client sends a request, sees it acknowledged at the TCP level in a packet capture, and then the connection is reset before any reply arrives. Did the server charge the card?",
          answer:
            "Unknown. The TCP acknowledgment means the server's kernel had the bytes. The server application may have read them and committed the charge, or crashed first. TCP cannot say which. The client needs an application-level answer, usually by retrying with an idempotency key so the server can recognize the repeat, or by querying the payment's status.",
        },
        {
          id: "connected-but-hangs",
          prompt:
            "connect() to a backend succeeds in 1 ms, but the HTTP request then hangs until the client times out. Name two causes that a successful handshake does not rule out.",
          answer:
            "Any two of: the server application is stuck or overloaded and never calls accept() or read(), because the kernel completed the handshake for it; a TLS or HTTP layer above TCP is failing or waiting; a proxy or load balancer accepted the connection itself and cannot reach a healthy backend; large packets are being dropped on the path (an MTU problem) even though the small handshake packets got through; or the request is processed but the response is slow.",
        },
        {
          id: "router-retransmit",
          prompt:
            "Why can't a router on the path retransmit a TCP segment it dropped, so the endpoints never notice?",
          answer:
            "Routers keep no per-connection state, by design, and a later packet of the same connection may take a different router. Only the receiving endpoint knows which bytes arrived, and only the sending endpoint still has a copy of the data. A router could at most retry its own hop, which would not cover loss elsewhere, so the end-to-end check is needed anyway.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "tcp-service",
      statement:
        "TCP provides a reliable, in-order, byte-stream service to applications. Its reliability consists of detecting losses via sequence numbers and errors via per-segment checksums, and correcting them by retransmission. It is connection-oriented, but does not inherently include liveness detection. Ports identify services and multiplex flows between hosts.",
      sourceId: "rfc9293",
      locator: "Section 2.2, Key TCP Concepts",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "no-segment-boundaries",
      statement:
        "TCP segments often do not correspond one-for-one to the application's send calls, and TCP guarantees no correlation between segment boundaries and the boundaries of the application's read or write buffers.",
      sourceId: "rfc9293",
      locator: "Section 3.7, Segmentation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "psh-not-record-marker",
      statement:
        "The PSH bit is not a record marker and is independent of segment boundaries; a transmitter should collapse successive PSH bits to send the largest possible segment. New applications should not use the urgent mechanism.",
      sourceId: "rfc9293",
      locator: "Section 3.9.1.2, Send; Section 3.8.5, The Communication of Urgent Information",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "close-delivers-to-tcp",
      statement:
        "CLOSE means the user has no more data to send; the user may keep receiving. TCP reliably delivers all buffers sent before CLOSE, so a user who sees the connection closed successfully knows the data was received at the destination TCP endpoint. A remote close or abort must be reported to the application, distinguishing a normal close from an abort.",
      sourceId: "rfc9293",
      locator: "Section 3.6, Closing a Connection",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-tcp-service",
      statement:
        "Linux TCP provides a reliable, stream-oriented, full-duplex connection between two sockets, guarantees data arrives in order, retransmits lost packets, and checks a per-packet checksum. TCP does not preserve record boundaries.",
      sourceId: "man-tcp",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "send-no-delivery",
      statement:
        "No indication of failure to deliver is implicit in a successful send(); only locally detected errors are returned as -1.",
      sourceId: "man-send",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "accept-queue",
      statement:
        "On Linux since 2.2, the listen() backlog is the length of the queue of completely established sockets waiting to be accepted; incomplete connection requests are limited separately by tcp_max_syn_backlog. The backlog is capped by /proc/sys/net/core/somaxconn, whose default is 4096 since Linux 5.4.",
      sourceId: "man-listen",
      locator: "NOTES",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "gateways-stateless",
      statement:
        "Internet gateways are designed to be stateless, forwarding each datagram independently. All state needed for end-to-end flow control and reliability lives in the hosts, so it is lost only if an endpoint fails, and redundant paths can be used around failed gateways.",
      sourceId: "rfc1122",
      locator: "Section 1.1.2, Architectural Assumptions (b)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "endpoint-congestion-control",
      statement:
        "A TCP endpoint must implement slow start, congestion avoidance, and exponential backoff of the retransmission timeout to avoid congestion collapse.",
      sourceId: "rfc9293",
      locator: "Section 3.8.2, TCP Congestion Control (MUST-19)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linger-on-exit",
      statement:
        "With SO_LINGER enabled, close() waits until queued data is sent or the linger timeout passes; otherwise close() returns at once and the close is completed in the background. A socket closed as part of _exit() always lingers in the background.",
      sourceId: "man-socket",
      locator: "Socket options, SO_LINGER",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc9293", note: "Sections 2.2, 3.6, 3.7, 3.8.2, 3.8.5, and 3.9.1.2." },
    { sourceId: "rfc1122", note: "Section 1.1.2: stateless gateways, state in the hosts." },
    { sourceId: "man-tcp", note: "Linux's statement of the service." },
    { sourceId: "man-send", note: "What a successful send() does not mean." },
    { sourceId: "man-listen", note: "The accept queue that holds completed handshakes." },
    { sourceId: "man-socket", note: "SO_LINGER and closing on exit." },
  ],
} satisfies LessonBody;
