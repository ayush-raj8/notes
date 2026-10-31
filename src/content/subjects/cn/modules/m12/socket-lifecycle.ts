import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m12.lifecycle",
  status: "complete",
  objectives: [
    "Walk the socket calls for a TCP server and a TCP client.",
    "Say what a port and a file descriptor name.",
    "Contrast a connected TCP socket with a UDP socket.",
  ],
  prerequisites: ["m11.partial-failure"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• User space cannot put packets on the wire. The kernel owns NICs, ports, and buffers.\n• A process needs a handle for one conversation (or one listening endpoint).\n• TCP needs shared state before data; UDP often does not.",
      whyItMatters:
        "• Every language's network API is a thin wrap of these calls.\n• Mis-ordered calls (listen without bind, accept without listen) fail in obvious ways; wrong assumptions about UDP fail subtly.\n• Debugging starts with which FD is in which state (`ss`).",
      idea: "• A socket is a kernel object exposed as a file descriptor.\n• TCP server: socket → bind → listen → accept loop; each accept returns a connected FD.\n• TCP client: socket → connect → send/recv.\n• UDP: often socket → bind (server) or connect (optional) → sendto/recvfrom.",
      howItWorks:
        "• `socket()` creates an unbound FD for a domain/type/protocol.\n• `bind()` assigns a local address and port (or lets the kernel pick).\n• `listen()` marks a TCP socket passive and sizes the accept backlog.\n• `accept()` dequeues a completed handshake and returns a new FD.\n• `connect()` on TCP starts the handshake; on UDP it sets a default peer.\n• `send`/`recv` (or write/read) move bytes through kernel buffers.",
      whyItWorks:
        "• One FD per endpoint conversation keeps multiplexing and closing simple.\n• Separating listen FD from accepted FDs lets one process serve many clients.\n• Ports demultiplex arrivals on one host; FDs demultiplex them inside one process.",
      limits:
        "• `send` success means bytes hit the local send buffer, not that the peer app read them.\n• Blocking calls stall the thread until the kernel is ready (next lesson).\n• Too many FDs / TIME_WAIT / full backlog shows up as refuse or hang (buffers lesson).",
      connections:
        "• Module 5: TCP states behind connect/accept/close.\n• Module 11: pooling reuses connected FDs.\n• Tools lesson: `ss -tanp` maps ports to processes.",
    },
    {
      type: "comparison",
      id: "server-vs-client",
      title: "TCP server vs client call sequence",
      columns: ["Server", "Client"],
      rows: [
        {
          label: "Create",
          cells: ["`socket(AF_INET, SOCK_STREAM, 0)`", "same"],
        },
        {
          label: "Local name",
          cells: ["`bind` to service port", "optional `bind`; often ephemeral"],
        },
        {
          label: "Role",
          cells: ["`listen`", "`connect` to remote"],
        },
        {
          label: "Per client",
          cells: ["`accept` → new FD", "use the one connected FD"],
        },
        {
          label: "Data",
          cells: ["`send`/`recv` on accepted FD", "`send`/`recv`"],
        },
      ],
    },
    {
      type: "comparison",
      id: "tcp-vs-udp-sock",
      title: "TCP socket vs UDP socket",
      columns: ["TCP (`SOCK_STREAM`)", "UDP (`SOCK_DGRAM`)"],
      rows: [
        {
          label: "connect",
          cells: ["Required for data path; handshake", "Optional; sets default peer only"],
        },
        {
          label: "listen/accept",
          cells: ["Yes for servers", "No"],
        },
        {
          label: "Message boundaries",
          cells: ["Byte stream; app frames", "Datagram boundaries preserved"],
        },
        {
          label: "Errors",
          cells: [
            "Connection errors on the FD",
            "Often via `recv`/`send` errno; ICMP may need connected socket or IP_RECVERR",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "ports-fds",
      title: "Ports and FDs (notes)",
      steps: [
        {
          title: "Port",
          body: "• 16-bit demux key on the host. Well-known vs ephemeral.\n• `ss -tan` shows who holds which.",
        },
        {
          title: "File descriptor",
          body: "• Integer handle in one process for one socket object.\n• Closing the FD releases the reference; TCP close/shutdown semantics still apply.",
        },
        {
          title: "Tuple",
          body: "• A TCP connection is local IP:port + remote IP:port.\n• Many FDs can share a listen port after accept; each connected tuple is unique.",
        },
      ],
    },
    {
      type: "example",
      id: "echo-sketch",
      title: "Minimal TCP echo (conceptual)",
      setup: "Server listens on `:7`. Client connects and sends `ping`.",
      walkthrough: [
        "• Server: socket, bind `:7`, listen, accept → `cfd`.",
        "• Client: socket, connect to server, send `ping`.",
        "• Server: recv on `cfd`, send same bytes back.",
        "• Both: close (or shutdown) when done.",
      ],
      result: "• One listening FD, one connected FD per client. Data never uses the listen FD.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "accept-fd",
          prompt: "After accept returns, which FD do you send on?",
          answer: "The new FD from accept, not the listening socket.",
        },
        {
          id: "udp-connect",
          prompt: "What does connect() do on a UDP socket?",
          answer:
            "It records a default peer for send/recv; it does not create a TCP-style connection or handshake.",
        },
        {
          id: "send-means",
          prompt: "send() returned the byte count. What is guaranteed?",
          answer:
            "Those bytes were accepted into the local kernel send buffer (subject to the call's semantics)—not that the peer application processed them.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "listen-backlog",
      statement:
        "listen() marks a socket as passive, meaning it will be used to accept incoming connection requests. The backlog argument defines the maximum length of the queue of pending connections.",
      sourceId: "man-listen",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "socket-create",
      statement:
        "socket() creates an endpoint for communication and returns a file descriptor that refers to that endpoint.",
      sourceId: "man-socket",
      locator: "DESCRIPTION (socket(2))",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "connect-tcp",
      statement:
        "connect() on a SOCK_STREAM socket attempts to make a connection to another socket. For SOCK_DGRAM, connect specifies the peer address for future datagrams.",
      sourceId: "man-connect",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "man-socket", note: "socket(2): creating the FD." },
    { sourceId: "man-listen", note: "listen(2): passive socket and backlog." },
    { sourceId: "man-connect", note: "connect(2): TCP connect vs UDP peer." },
    { sourceId: "man-send", note: "send(2): writes into local buffers." },
  ],
} satisfies LessonBody;
