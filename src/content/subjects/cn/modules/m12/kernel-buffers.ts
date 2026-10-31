import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m12.kernel-buffers",
  status: "complete",
  objectives: [
    "Explain what read and write touch in the kernel for a TCP socket.",
    "Describe the listen backlog and what happens when it fills.",
    "Relate TIME_WAIT and FD limits to socket exhaustion symptoms.",
  ],
  prerequisites: ["m12.multiplexing"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• User buffers are not the network. Bytes sit in kernel send/receive queues first.\n• Handshakes complete in the kernel before accept; only then is there an FD for user space.\n• Closed connections can linger in TIME_WAIT and consume tuples.",
      whyItMatters:
        "• `send` returning success is not peer processing (why.48).\n• Full accept queue → client sees SYN-ACKs then silence or RST depending on sysctl.\n• TIME_WAIT + ephemeral port pressure → intermittent connect failures under load.",
      idea: "• Write copies into the socket send buffer; TCP drains it to the network.\n• Read copies out of the receive buffer; TCP fills it from segments.\n• listen backlog: queue of connections waiting for accept.\n• Exhaustion: too many FDs, full tables, or too many TIME_WAIT 4-tuples.",
      howItWorks:
        "• Write path: user → kernel send buffer → (possibly delayed) segments.\n• Read path: segments → kernel recv buffer → user `recv`.\n• If recv buffer fills, TCP advertises a smaller window (flow control).\n• `listen(backlog)` caps incomplete/completed queues (implementation splits syn/accept queues).\n• After active close, TIME_WAIT holds the tuple ~2MSL (60s on Linux typically).",
      whyItWorks:
        "• Buffers decouple app scheduling from packet timing.\n• Backlog absorbs connection bursts so the server can accept shortly after.\n• TIME_WAIT protects against delayed segments confusing a new incarnation.",
      limits:
        "• Huge send buffer ≠ the peer is keeping up; watch Recv-Q/Send-Q in `ss`.\n• Raising backlog without accepting faster only delays failure.\n• `SO_REUSEADDR` / `tcp_tw_reuse` trade safety for density—know the docs before flipping.",
      connections:
        "• Module 5: flow control window, TIME_WAIT purpose (why.16).\n• Module 11: backpressure when you stop reading.\n• Tools: `ss -tan`, `ss -ltn` for queues and listen sockets.",
    },
    {
      type: "steps",
      id: "read-write",
      title: "What read/write touch (notes)",
      steps: [
        {
          title: "write/send",
          body: "• Copies into kernel send buffer (may block or EAGAIN if full).\n• Returns when local buffer accepted bytes—not when ACKed.",
        },
        {
          title: "read/recv",
          body: "• Copies from kernel recv buffer.\n• Blocks (or EAGAIN) if empty; 0 means peer ordered close (TCP).",
        },
        {
          title: "Observe",
          body: "• `ss -ti` / Recv-Q Send-Q: data buffered in kernel awaiting read or send.",
        },
      ],
    },
    {
      type: "callout",
      id: "backlog-full",
      tone: "caution",
      title: "Accept queue full",
      body: "• Clients may complete the handshake in the kernel and still wait for accept.\n• If queues overflow, new SYNs can be dropped; symptoms look like intermittent connect timeouts.\n• Fix: accept faster, more workers, or raise backlog—and find why accept stalled.",
    },
    {
      type: "example",
      id: "time-wait-storm",
      title: "Client ephemeral ports and TIME_WAIT",
      setup:
        "Load generator opens a new TCP connection per tiny HTTP request to one server IP:port.",
      walkthrough: [
        "• Each close leaves the client side in TIME_WAIT for that 4-tuple.",
        "• Ephemeral port range is finite; new connects fail with EADDRNOTAVAIL.",
        "• Connection reuse/pooling collapses many requests onto fewer tuples.",
      ],
      result: "• Exhaustion is often local port/TIME_WAIT pressure, not a dead server.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "send-buffer",
          prompt: "Where do bytes go when send() succeeds on a TCP socket?",
          answer:
            "Into the kernel socket send buffer for that FD. TCP will transmit (and retransmit) from there; the peer app may not have read yet.",
        },
        {
          id: "backlog-role",
          prompt: "What does the listen backlog protect against?",
          answer:
            "Short bursts of new connections arriving faster than the process can accept—by queuing completed (and/or in-progress) handshakes up to a limit.",
        },
        {
          id: "tw-symptom",
          prompt: "Name one symptom of TIME_WAIT / ephemeral-port exhaustion on a client.",
          answer:
            "Intermittent connect failures such as EADDRNOTAVAIL despite the server being healthy.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "send-local-buffer",
      statement:
        "On success, send() copies data into a socket's send buffer; for TCP this does not guarantee the peer has received or read the data.",
      sourceId: "man-send",
      locator: "RETURN VALUE / DESCRIPTION",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "listen-queue",
      statement:
        "The backlog argument to listen() defines the maximum length of the queue of pending connections.",
      sourceId: "man-listen",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "tcp-window-buffer",
      statement:
        "TCP receive buffer space interacts with the advertised window; when the receiver does not read, the window shrinks and the sender stops.",
      sourceId: "man-tcp",
      locator: "NOTES / socket options related to buffers (tcp(7))",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "man-send", note: "send(2): local send buffer semantics." },
    { sourceId: "man-listen", note: "listen(2): backlog queue." },
    { sourceId: "man-tcp", note: "tcp(7): TCP socket behaviour and buffer-related options." },
    { sourceId: "rfc9293", note: "TIME_WAIT purpose (cross-check with Module 5)." },
  ],
} satisfies LessonBody;
