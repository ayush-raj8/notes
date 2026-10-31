import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m11.partial-failure",
  status: "complete",
  objectives: [
    "Count what a new connection costs and configure a pool that avoids paying it per request.",
    "Keep pooled connections below the idle timeout of every NAT, balancer, and server on the path.",
    "Explain why a successful write, ping, or health check does not prove a request succeeded.",
    "Recognise partial failure and find the dimension an intermittent failure follows.",
  ],
  prerequisites: ["m11.timeouts-and-retries"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• A connection per request repeats handshakes and slow start and leaves TIME_WAIT and NAT state behind.\n• A reused idle connection can die silently in a NAT or balancer.\n• Parts of a system fail while the rest works, so symptoms look random.",
      whyItMatters:
        "• Setup can be most of a short request's latency and can exhaust ports under load.\n• A stale pooled connection fails the first request after a quiet period.\n• 'Sent' is easily mistaken for 'done'.",
      idea: "• Reuse connections through a pool, and keep them alive or retire them before the path forgets them.\n• Let pressure flow back: bounded queues and windows make a slow consumer slow its producer.\n• Treat a remote result as unknown until the peer's application confirms it.",
      howItWorks:
        "• Pool: a per-host limit, an idle timeout below every timeout on the path, and one retry on a fresh connection for safe requests.\n• Keepalive: TCP SO_KEEPALIVE (off by default) or a protocol PING such as HTTP/2's.\n• Backpressure: TCP and HTTP/2 windows, bounded application queues, fast 503 when full.\n• Confirmation: an application response, an idempotency key, or a read-back.",
      whyItWorks:
        "• Reuse pays setup once and keeps a congestion window that has already grown.\n• Bounded buffers make the producer wait, so memory and latency stay bounded.\n• Only the peer's application knows whether it processed the request.",
      limits:
        "• Long-lived connections stick to one backend; balancing needs per-request routing or periodic reconnects (Module 10).\n• Keepalive notices a dead path slowly and cannot say whether the process or the network failed.\n• During a partition a slow peer and a dead one look the same; you must choose between waiting and acting on stale state.",
      connections:
        "• Module 8: persistent connections; why.47 also appears there.\n• Module 10: NAT idle timeouts and SNAT port exhaustion.\n• Module 12: kernel buffers (why.48).\n• Module 13: intermittent timeouts and large responses that hang.",
    },
    {
      type: "comparison",
      id: "new-vs-pooled",
      title: "New connection per request vs a pooled connection",
      columns: ["New connection", "Pooled connection"],
      rows: [
        {
          label: "Round trips before the request",
          cells: ["TCP 1 + TLS 1.3 1 = 2 RTTs.", "0."],
        },
        {
          label: "Congestion window",
          cells: [
            "Starts at about 10 segments (up to 14,600 bytes); a large response needs extra RTTs.",
            "Already grown from earlier requests.",
          ],
        },
        {
          label: "CPU",
          cells: ["Key exchange and certificate checks every time.", "Once per connection."],
        },
        {
          label: "State left behind",
          cells: [
            "A TIME_WAIT entry on the side that closes first; a NAT or conntrack entry.",
            "One socket per pooled connection.",
          ],
        },
        {
          label: "Typical failure",
          cells: [
            "Port exhaustion; SYN or accept backlog overflow.",
            "Stale connections after an idle timeout; load stuck on one backend.",
          ],
        },
      ],
    },
    {
      type: "why",
      id: "why-new-connection",
      questionId: "why.47",
    },
    {
      type: "steps",
      id: "keep-pool-usable",
      title: "Keeping pooled connections usable",
      steps: [
        {
          title: "Find the shortest idle timeout on the path",
          body: "• Server keep-alive timeout, load balancer idle timeout, NAT or conntrack timeout, firewall.\n• The smallest one decides when an idle connection silently dies.",
        },
        {
          title: "Retire or ping below it",
          body: "• Pool idle timeout below that value, or a keepalive more often than it.\n• TCP keepalive defaults are too slow: off, then 7200 s idle, 75 s between probes, 9 probes. Set TCP_KEEPIDLE and TCP_KEEPINTVL per socket.",
        },
        {
          title: "Prefer a protocol-level ping",
          body: "• HTTP/2 PING checks that the peer's HTTP layer answers, not only its kernel.\n• RFC 9113 names NATs and balancers that silently drop bindings as the reason.",
        },
        {
          title: "Expect the close race",
          body: "• The server can close an idle connection just as the client sends on it.\n• Retry once on a fresh connection, but only for requests that are safe to repeat.",
        },
        {
          title: "Bound the pool",
          body: "• Cap connections per host. Too many connections cost server memory and can cause congestion on their own.",
        },
      ],
    },
    {
      type: "example",
      id: "first-request-after-idle",
      title: "The first request after a quiet hour fails",
      setup:
        "A service keeps a pool of HTTPS connections to an API behind a load balancer with a 350 s idle timeout. The pool keeps idle connections for 15 minutes. TCP keepalive is off. Illustrative numbers.",
      walkthrough: [
        "• Traffic stops for 10 minutes. After 350 s the balancer drops its state for every pooled connection. Neither end is told.",
        "• Both kernels still show the connections as ESTABLISHED.",
        "• The next request goes out on a pooled connection. The balancer has no state for it: it answers RST, or drops it and the client waits for its read timeout.",
        "• Only the first request on each stale connection fails. Retries succeed on new connections. On-call sees 'random' resets in the morning.",
        "• Fix: pool idle timeout 60 s (below 350 s) or keepalives every 60 s; retry idempotent requests once on a fresh connection when no response bytes arrived.",
      ],
      result:
        "• Idle connections die where state lives, in the middle. Keep the pool's idle timeout below the shortest timeout on the path.",
    },
    {
      type: "comparison",
      id: "backpressure",
      title: "Where backpressure comes from",
      columns: ["Signal", "What the sender sees"],
      rows: [
        {
          label: "TCP receive window",
          cells: [
            "The receiver stops reading; its window shrinks to zero.",
            "send() blocks, or returns EAGAIN on a non-blocking socket.",
          ],
        },
        {
          label: "HTTP/2 flow control",
          cells: [
            "Per-stream and per-connection credit, refreshed by WINDOW_UPDATE. One hop only.",
            "DATA for that stream waits; other streams continue while the connection window allows.",
          ],
        },
        {
          label: "Bounded application queue",
          cells: [
            "The worker queue is full.",
            "A fast 503 or a reject, instead of a slow timeout later.",
          ],
        },
        {
          label: "No backpressure",
          cells: [
            "Unbounded queues absorb everything.",
            "Latency and memory grow until requests time out while still queued; the work is then wasted.",
          ],
        },
      ],
    },
    {
      type: "comparison",
      id: "what-success-proves",
      title: "What each success proves",
      columns: ["Proves", "Does not prove"],
      rows: [
        {
          label: "`ping` replies",
          cells: [
            "The host's kernel answers ICMP on this path.",
            "That TCP to the port works, or that the service is up.",
          ],
        },
        {
          label: "connect() succeeds",
          cells: [
            "The peer's kernel accepted a handshake on that port.",
            "That the application will accept(), read, or answer (why.20).",
          ],
        },
        {
          label: "send() returns n",
          cells: [
            "n bytes were copied into the local send buffer.",
            "That any byte left the host, let alone arrived.",
          ],
        },
        {
          label: "Peer's TCP ACKs the bytes",
          cells: ["The peer's kernel holds them.", "That the application read or processed them."],
        },
        {
          label: "Application response (HTTP 200, explicit ack)",
          cells: [
            "The peer's application reports it handled the request.",
            "That it was durable, unless the protocol says so.",
          ],
        },
      ],
    },
    {
      type: "why",
      id: "why-write",
      questionId: "why.48",
    },
    {
      type: "steps",
      id: "large-response-hang",
      title: "Small responses work, large ones hang",
      steps: [
        {
          title: "Small packets pass",
          body: "• Handshake, TLS, and short replies fit in small packets.",
        },
        {
          title: "Full-sized segments exceed the path MTU",
          body: "• A tunnel or VPN on the path has a smaller MTU, e.g. 1400 bytes. TCP sends 1500-byte packets with Don't Fragment set.",
        },
        {
          title: "The 'too big' message is lost",
          body: "• The router drops the packet and sends ICMP Fragmentation Needed (IPv6: Packet Too Big). A firewall filters it.",
        },
        {
          title: "TCP retransmits the same size",
          body: "• Without the ICMP message the sender never shrinks its packets. The transfer stalls and finally times out: a PMTUD black hole.",
        },
        {
          title: "Fix",
          body: "• Allow ICMP type 3 code 4 and ICMPv6 Packet Too Big. Clamp the MSS at the tunnel. Or set `net.ipv4.tcp_mtu_probing` so Linux probes when it detects a black hole.",
        },
      ],
    },
    {
      type: "why",
      id: "why-large-hangs",
      questionId: "why.40",
    },
    {
      type: "callout",
      id: "partitions",
      tone: "note",
      title: "Partitions and partial failure",
      body: "• A partition splits hosts that are each still running: A reaches C, C reaches B, A cannot reach B.\n• A cannot tell whether B is slow, dead, or cut off. It can wait (and stall) or act alone (and risk disagreeing with B).\n• Partial failures follow something: one backend, one zone, one path through ECMP, packet size, idle time, or a table filling up.",
    },
    {
      type: "why",
      id: "why-intermittent",
      questionId: "why.49",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "pool-math",
          prompt:
            "RTT 50 ms, TLS 1.3, 1 KB request, 10 ms server time. How long does one request take on a new connection versus a pooled one?",
          answer:
            "New: TCP 1 RTT + TLS 1 RTT + request 1 RTT + 10 ms = 160 ms. Pooled: 1 RTT + 10 ms = 60 ms.",
        },
        {
          id: "nat-idle",
          prompt:
            "A NAT drops idle flows after 5 minutes. The pool keeps idle connections for 30 minutes, and TCP keepalive is at Linux defaults. What happens, and what do you change?",
          answer:
            "Connections idle for more than 5 minutes lose their NAT mapping, and the next request on them hangs or is reset. Default keepalive would not probe for 2 hours. Lower the pool idle timeout below 5 minutes, or send keepalives every minute or two.",
        },
        {
          id: "send-then-crash",
          prompt:
            "send() returned the full length of a request, then the server process crashed. Did the server handle the request?",
          answer:
            "Unknown. The bytes may be in the local buffer, in the network, or in the server kernel's buffer unread. Only an application response would say. Retry only if the request is idempotent or carries an idempotency key.",
        },
        {
          id: "one-backend",
          prompt:
            "One request in 50 fails. The logs show every failure went to backend 10.0.3.7, and its health check passes. What does that tell you?",
          answer:
            "A partial failure tied to one backend that the health check does not exercise (why.53). Take it out of the pool and test it directly; the failure follows the backend, not the client.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "http-persistence",
      statement:
        "HTTP/1.1 defaults to persistent connections, which carry multiple requests and responses over one connection.",
      sourceId: "rfc9112",
      locator: "Section 9.3, Persistence",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "connection-limits",
      statement:
        "A client ought to limit simultaneous connections to a server: each consumes server resources, and many connections can cause congestion through their synchronised sending.",
      sourceId: "rfc9112",
      locator: "Section 9.4, Concurrency",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "idle-close-race",
      statement:
        "Any side may close a connection at any time; a client may start a request just as the server closes what it sees as an idle connection.",
      sourceId: "rfc9112",
      locator: "Section 9.5, Failures and Timeouts",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "initial-window",
      statement: "TCP's initial window is min(10 × MSS, max(2 × MSS, 14600)) bytes.",
      sourceId: "rfc6928",
      locator: "Section 2",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "tls13-1rtt",
      statement:
        "In TLS 1.3's full handshake the client sends Application Data after one round trip.",
      sourceId: "rfc8446",
      locator: "Section 2, Figure 1",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tcp-keepalive-rfc",
      statement:
        "TCP keep-alives are optional, MUST default to off, MUST be sent only after a configurable idle interval of no less than two hours, and a single unanswered probe MUST NOT be taken as a dead connection.",
      sourceId: "rfc9293",
      locator: "Section 3.8.4, TCP Keep-Alives",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "tcp-keepalive-linux",
      statement:
        "Linux defaults are tcp_keepalive_time 7200 s, tcp_keepalive_intvl 75 s, and tcp_keepalive_probes 9, and probes are sent only with SO_KEEPALIVE; connection tracking and application timeouts may be much shorter.",
      sourceId: "man-tcp",
      locator: "/proc interfaces: tcp_keepalive_time, tcp_keepalive_intvl, tcp_keepalive_probes",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "h2-ping",
      statement:
        "Idle HTTP/2 connections can break because middleboxes such as NATs or load balancers silently discard bindings; PING lets a client test a connection without sending a request.",
      sourceId: "rfc9113",
      locator: "Section 8.7, Request Reliability",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "h2-flow-control",
      statement:
        "HTTP/2 flow control is credit-based through WINDOW_UPDATE, applies per stream and per connection, operates over a single hop, and applies only to DATA frames.",
      sourceId: "rfc9113",
      locator: "Section 5.2.1, Flow-Control Principles",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "send-no-delivery",
      statement:
        "No indication of failure to deliver is implicit in a send(); only locally detected errors return -1. When the message does not fit in the send buffer, send() normally blocks unless the socket is non-blocking.",
      sourceId: "man-send",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "pmtud-black-hole",
      statement:
        "When routers fail to send, or firewalls suppress, the ICMP Fragmentation Needed messages PMTUD relies on, the sender keeps sending large packets. Pings and interactive connections work, while bulk transfers fail at the first large packet and eventually time out.",
      sourceId: "rfc2923",
      locator: "Section 2.1, Black Hole Detection",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "mtu-probing",
      statement:
        "net.ipv4.tcp_mtu_probing controls TCP Packetization-Layer Path MTU Discovery: 0 disabled, 1 enabled when an ICMP black hole is detected, 2 always enabled.",
      sourceId: "kernel-ip-sysctl",
      locator: "tcp_mtu_probing",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc9112", note: "Sections 9.3 to 9.5: persistence, concurrency, idle close." },
    { sourceId: "rfc6928", note: "Section 2: initial window." },
    { sourceId: "rfc8446", note: "Section 2, Figure 1: one round trip." },
    { sourceId: "rfc9293", note: "Section 3.8.4: keep-alives." },
    { sourceId: "man-tcp", note: "Keepalive sysctls and socket options." },
    { sourceId: "rfc9113", note: "Sections 5.2.1 and 8.7: flow control and PING." },
    { sourceId: "man-send", note: "What a successful send() does not say." },
    { sourceId: "rfc2923", note: "Section 2.1: PMTUD black holes." },
    { sourceId: "kernel-ip-sysctl", note: "tcp_mtu_probing." },
  ],
} satisfies LessonBody;
