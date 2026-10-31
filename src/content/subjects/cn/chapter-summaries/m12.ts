import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m12",
  status: "complete",
  readingMinutes: { min: 12, max: 18 },
  thesis:
    "A socket is the kernel object a process uses to talk to the network, so every symptom you see in an application maps to a socket call, a kernel buffer or queue, or a state you can inspect with Linux tools.",
  outcomes: [
    "Walk the socket calls a TCP server and a TCP client make, and what the kernel does at each.",
    "Explain file descriptors, ports, and why a process needs both.",
    "Contrast blocking I/O with select, poll, and epoll, and say why multiplexing scales.",
    "Locate kernel buffers and the accept backlog, and say what happens when each fills.",
    "Explain TIME_WAIT and ephemeral-port exhaustion.",
    "Pick the right tool among `ss`, `ip`, `ping`, `traceroute`, `dig`, `curl`, and `tcpdump` for a question.",
  ],
  coverage: [
    {
      bullet: "Socket lifecycle.",
      notes:
        "• Create → configure (bind/listen or connect) → transfer → close.\n• Server: socket, bind, listen, accept loop. Client: socket, connect.\n• Close starts TCP teardown; the kernel may keep state after the process exits.\n• Every socket has local and remote address state the kernel tracks.",
    },
    {
      bullet:
        "`socket()`, `bind()`, `listen()`, `accept()`, `connect()`, `send()`, and `recv()` at a conceptual level.",
      notes:
        "• `socket()`: ask the kernel for an endpoint of a given family and type; returns an FD.\n• `bind()`: attach a local IP and port (clients usually skip it; the kernel picks an ephemeral port).\n• `listen()`: mark the socket passive and set the backlog size.\n• `accept()`: take a completed connection from the queue; returns a new FD for that peer.\n• `connect()`: TCP starts the handshake; UDP just sets a default peer.\n• `send()` / `recv()`: copy bytes to and from kernel buffers; they do not mean the peer got or read them.",
    },
    {
      bullet: "Ports and file descriptors.",
      notes:
        "• Port: 16-bit number that lets the kernel demultiplex to a socket; FD: a per-process integer handle.\n• A connection is identified by the 4-tuple (src IP, src port, dst IP, dst port); one listening port serves many connections.\n• Sockets are FDs, so `ulimit -n` caps connections per process.\n• Ports below 1024 are privileged by default; the ephemeral range is set by `ip_local_port_range`.",
    },
    {
      bullet: "Blocking vs non-blocking sockets.",
      notes:
        "• Blocking: the call sleeps until it can make progress (data arrives, buffer has space).\n• Non-blocking: returns at once, with EAGAIN / EWOULDBLOCK if it cannot.\n• Blocking is simple, but one thread per connection does not scale.\n• Non-blocking needs a readiness mechanism and a state machine in your code.\n• `connect()` on a non-blocking socket completes later; check readiness, then `SO_ERROR`.",
    },
    {
      bullet: "`select`, `poll`, and `epoll`.",
      notes:
        "• All wait for readiness on many FDs.\n• `select`: bitset with an FD-number limit, rescanned on every call.\n• `poll`: array of FDs, no hard limit, still O(n) per call.\n• `epoll` (Linux): register interest once, kernel returns only ready FDs; scales to many idle connections.\n• Level-triggered re-reports while ready; edge-triggered reports changes only, so you must drain until EAGAIN.",
    },
    {
      bullet: "I/O multiplexing.",
      notes:
        "• One thread watches many sockets and handles whichever are ready (event loop).\n• Fits many mostly idle connections (proxies, chat, web servers).\n• Cost: handlers must not block, or every connection stalls.\n• Often combined with a worker pool for CPU-heavy work.",
    },
    {
      bullet: "TCP sockets vs UDP sockets.",
      notes:
        "• TCP (`SOCK_STREAM`): connected, reliable, ordered byte stream; no message boundaries.\n• UDP (`SOCK_DGRAM`): one `sendto`/`recvfrom` = one datagram; no delivery or order guarantee.\n• TCP `recv()` may return part of a message or several together; the app frames messages.\n• UDP has no accept or backlog; one socket receives from many peers.",
    },
    {
      bullet: "What happens when a process reads from or writes to a socket.",
      notes:
        "• Write: copy into the kernel send buffer and return; TCP sends later, as the window allows. Full buffer → block or EAGAIN.\n• Read: copy from the receive buffer; empty → block or EAGAIN; return of 0 means the peer closed.\n• A successful `send()` says nothing about delivery or the peer reading.\n• Writing to a socket the peer has reset gives EPIPE or ECONNRESET.",
    },
    {
      bullet: "Kernel buffers.",
      notes:
        "• Each socket has a send buffer and a receive buffer (`SO_SNDBUF`, `SO_RCVBUF`, autotuned).\n• Receive buffer free space drives the advertised TCP window.\n• A slow reader fills the receive buffer, the window shrinks to 0, and the sender stalls (backpressure).\n• Send-Q and Recv-Q in `ss` show bytes stuck in each.",
    },
    {
      bullet: "Connection backlog.",
      notes:
        "• Two queues: half-open (SYN received) and the accept queue (handshake done, waiting for `accept()`).\n• `listen(backlog)` is capped by `net.core.somaxconn`.\n• A slow accept loop fills the queue; new SYNs or final ACKs get dropped, so clients see retries or timeouts.\n• `ss -ltn` on a listener: Recv-Q = current queue, Send-Q = limit.",
    },
    {
      bullet: "TIME_WAIT and socket exhaustion.",
      notes:
        "• The side that closes first holds TIME_WAIT (about 60 s on Linux) to absorb stray segments and ACK a retransmitted FIN.\n• Many short connections to one destination can use up ephemeral ports (about 28k by default) → `connect()` fails with EADDRNOTAVAIL.\n• Fixes: reuse connections or pooling, widen the port range, spread over more destination IPs. Do not just shorten TIME_WAIT.\n• Other exhaustion: FD limit (EMFILE), conntrack table.",
    },
    {
      bullet:
        "Common Linux network diagnostic tools: `ss`, `ip`, `ping`, `traceroute`, `dig`, `curl`, and `tcpdump`.",
      notes:
        "• `ss -tanp` / `ss -ltn`: socket states, queues, owning process, listeners.\n• `ip addr` / `ip route` / `ip neigh`: addresses, routing decision, ARP/ND.\n• `ping`: reachability and RTT at IP/ICMP level; may be filtered.\n• `traceroute`: per-hop path using TTL; stars can be rate limiting, not loss.\n• `dig`: resolver answers, TTLs, which server answered.\n• `curl -v` (and `-w` timings): full client view of DNS, connect, TLS, HTTP.\n• `tcpdump`: ground truth on the wire; filter by host/port, save with `-w`.",
    },
  ],
  mentalModel:
    "• Process ⇄ FD ⇄ socket object in the kernel ⇄ send/receive buffers ⇄ TCP/IP stack ⇄ NIC.\n• Server: a listening socket owns a SYN queue and accept queue; each `accept()` creates a new connected socket.\n• `send()` and `recv()` only move bytes between your memory and kernel buffers; the network runs asynchronously behind them.\n• Full buffers or queues are how the kernel pushes back: blocking, EAGAIN, a zero window, or dropped SYNs.\n• Tools map to layers: `ip` (host config), `ss` (kernel sockets), `dig` (name), `curl` (app), `tcpdump` (wire).",
  failuresAndSignals: [
    "Accept backlog full: clients see connect timeouts or SYN retries while the server process looks idle. `ss -ltn` shows Recv-Q at the Send-Q limit; check listen-overflow counters in `nstat`.",
    "FD exhaustion: `accept()` returns EMFILE and the server stops taking connections. Signal is errors in logs and an `lsof`/`/proc/<pid>/fd` count at the limit.",
    "TIME_WAIT / ephemeral port exhaustion: a client making many short connections fails with EADDRNOTAVAIL. `ss -s` shows tens of thousands of TIME_WAIT toward one peer.",
    "Slow reader: Recv-Q grows on one side and Send-Q grows on the peer, the TCP window drops to 0. Throughput collapses with no loss; the receiving app is the problem.",
    "Edge-triggered epoll bug: connection hangs even though data is waiting because the handler did not read until EAGAIN. Signal is data visible in Recv-Q with no wakeup.",
  ],
  whyAnchors: [],
  connections: {
    before:
      "• Modules 5 and 6: the TCP handshake, windows, and UDP semantics that the socket calls expose.\n• Module 11: pooling, backpressure, and keepalive are the application's side of the buffers and limits here.",
    after:
      "• Module 13: each troubleshooting scenario names these tools and queues as first commands.\n• Reads from this chapter feed directly into 'server stops accepting new connections'.",
  },
  selfCheck: [
    {
      id: "accept",
      prompt:
        "What is the difference between the listening socket and the socket `accept()` returns?",
      answer:
        "The listening socket only holds the queue of incoming connections. `accept()` returns a new FD for one established connection with a specific 4-tuple; the listener keeps accepting.",
    },
    {
      id: "send-returned",
      prompt: "`send()` returned success. Did the peer receive the data?",
      answer:
        "Not necessarily. It only means the bytes were copied into the kernel send buffer. Delivery, and the peer reading them, happen later and can still fail.",
    },
    {
      id: "time-wait",
      prompt:
        "A client opens many short connections and starts failing to connect. What do you check and fix?",
      answer:
        "Check `ss -s` for TIME_WAIT counts and the port range. Reuse connections via pooling or keep-alive, widen the ephemeral range, or spread across destination addresses.",
    },
  ],
} satisfies ChapterSummary;
