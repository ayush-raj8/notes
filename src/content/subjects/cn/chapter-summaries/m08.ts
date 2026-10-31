import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m08",
  status: "complete",
  readingMinutes: { min: 14, max: 20 },
  thesis:
    "HTTP gives applications self-describing request and response messages over a reliable transport, and its versions, caches, and intermediaries exist to make that model fast and scalable.",
  outcomes: [
    "Explain what HTTP adds on top of a reliable byte stream: outcome, method semantics, and message framing.",
    "Read a request and response: method, status class, headers, body, cookies, and what a session really is.",
    "Compare HTTP/1.1, HTTP/2, and HTTP/3 by where each puts multiplexing and where head-of-line blocking remains.",
    "Say how connection reuse, compression, caching, and conditional requests save work.",
    "Describe what proxies, load balancers, WebSockets, gRPC, and streaming add, and how each rides on TCP or QUIC.",
  ],
  coverage: [
    {
      bullet: "HTTP request-response model.",
      notes:
        "• The client sends a request (method, target, headers, optional body); the server returns a response (status, headers, optional body).\n• Messages are self-describing and each is independent, so a hop can act on one without session memory (why.24).\n• TCP only says bytes reached the peer's kernel; HTTP reports whether the request was understood and done (why.01, why.23).\n• Message framing (Content-Length or chunked, or frames in HTTP/2 and HTTP/3) lets a truncated response be detected.",
    },
    {
      bullet: "HTTP methods and status codes.",
      notes:
        "• GET (read, safe), HEAD, POST (submit, not idempotent), PUT and DELETE (idempotent), PATCH.\n• Safe and idempotent semantics decide what a client or proxy may retry or cache.\n• Status classes: 1xx informational, 2xx success, 3xx redirect, 4xx client error, 5xx server error.\n• Know by sight: 200, 204, 301/302, 304, 400, 401, 403, 404, 429, 500, 502, 503, 504.",
    },
    {
      bullet: "Headers, bodies, cookies, and sessions.",
      notes:
        "• Headers carry metadata: Host, Content-Type, Content-Length, Authorization, Cache-Control, and more.\n• Body is the payload; its format is named by Content-Type.\n• HTTP is stateless; cookies (Set-Cookie then Cookie) let a server tie requests to a session.\n• A session is state on the server or a signed token on the client, not something the protocol keeps; Secure, HttpOnly, and SameSite limit exposure.",
    },
    {
      bullet: "Persistent connections.",
      notes:
        "• One TCP connection can carry many request-response pairs instead of one per request.\n• Default in HTTP/1.1; `Connection: close` ends it.\n• Saves the TCP and TLS handshakes and keeps a grown congestion window.\n• Idle timeouts on server or middlebox may close it; a client may find it dead on reuse.",
    },
    {
      bullet: "HTTP/1.1.",
      notes:
        "• Text-based messages; Host header lets one IP serve many sites.\n• Responses return strictly in request order on a connection; one slow response blocks those behind it.\n• Pipelining was rarely deployed because of that blocking, so browsers open several parallel connections.\n• Chunked transfer encoding streams a body of unknown length.",
    },
    {
      bullet: "HTTP/2 and multiplexing.",
      notes:
        "• Binary frames tagged with a stream ID; many requests and responses interleave on one connection (why.27).\n• HPACK compresses repeated headers; streams have priorities and flow control.\n• Fewer connections means one shared congestion controller and fewer handshakes.\n• Still one TCP byte stream underneath, so one lost segment stalls all streams (why.28).",
    },
    {
      bullet: "HTTP/3 and QUIC.",
      notes:
        "• HTTP semantics mapped onto QUIC streams; QUIC orders each stream separately, so a loss stalls only the streams whose data was lost (why.29).\n• QUIC runs over UDP and builds TLS 1.3 into its handshake.\n• QPACK compresses headers, adapted for out-of-order streams.\n• Clients discover support (Alt-Svc) and fall back to HTTP/2 over TCP when UDP is blocked.",
    },
    {
      bullet: "Connection reuse and pooling.",
      notes:
        "• Clients and proxies keep idle connections to a host and reuse them for later requests (why.30).\n• Skips TCP and TLS handshakes and slow start; fewer sockets on both ends.\n• Catch: an idle pooled connection may already be closed by the peer or a middlebox.\n• Retry automatically only idempotent requests; set pool limits and idle timeouts below the server's.",
    },
    {
      bullet: "Compression.",
      notes:
        "• Content encoding: client sends Accept-Encoding (gzip, br, zstd); server replies with Content-Encoding.\n• Helps text (HTML, JSON, JS, CSS); wastes CPU on already compressed media.\n• Header compression is separate: HPACK in HTTP/2, QPACK in HTTP/3.\n• Compressing secrets alongside attacker-controlled input can leak data (CRIME/BREACH class); mind this for sensitive pages.",
    },
    {
      bullet: "Caching and conditional requests.",
      notes:
        "• Cache-Control (max-age, no-store, no-cache, private, public) says who may reuse a response and for how long.\n• Validators: ETag and Last-Modified; client revalidates with If-None-Match or If-Modified-Since.\n• A 304 Not Modified means reuse your copy and sends no body.\n• Vary names the request headers that change the response, so caches key on them.",
    },
    {
      bullet: "Proxies and reverse proxies.",
      notes:
        "• Forward proxy acts for clients (policy, filtering, caching); reverse proxy acts for servers (TLS termination, routing, caching, shielding backends).\n• Because messages are self-describing, a hop can route by Host and path and pool connections (why.24).\n• Each hop has its own connection and its own timeouts, so errors can start at any hop.\n• Proxies add headers such as X-Forwarded-For or Forwarded, and Via, to carry the original client address.",
    },
    {
      bullet: "Load balancers.",
      notes:
        "• L4 balancers pick a backend per connection from addresses and ports; L7 balancers pick per request from host, path, and headers.\n• Health checks remove bad backends; algorithms include round robin, least connections, and hashing.\n• Sticky sessions pin a client to one backend; stateless services avoid needing them.\n• A 502 or 503 over a good TCP connection often means no healthy backend (why.20).",
    },
    {
      bullet: "WebSockets.",
      notes:
        "• Starts as an HTTP/1.1 request with Upgrade; a 101 response switches that connection to full-duplex message frames.\n• Either side can send at any time without a new request, which suits chat and live updates.\n• Long-lived: needs heartbeats (ping/pong), idle-timeout tuning on proxies, and reconnect logic.\n• Load balancers must support the upgrade and keep the connection open.",
    },
    {
      bullet: "gRPC and HTTP/2.",
      notes:
        "• Each call is an HTTP/2 stream; headers carry method path and deadline, DATA frames carry length-prefixed protobuf messages, trailers carry the status (why.54).\n• Reuses HTTP/2 multiplexing, flow control, cancellation, and TLS, and passes through compatible proxies and load balancers.\n• Four call shapes: unary, server streaming, client streaming, bidirectional.\n• GOAWAY and REFUSED_STREAM tell the client which calls never ran and can safely be retried.",
    },
    {
      bullet: "Streaming and long-lived connections.",
      notes:
        "• Options: chunked responses, Server-Sent Events, WebSockets, gRPC streams, and HTTP/2 or HTTP/3 streams.\n• Idle timeouts at proxies, NATs, and load balancers close quiet connections; keepalives or heartbeats prevent that.\n• Backpressure comes from transport flow control; a slow reader eventually stalls the sender.\n• Deploys and restarts need graceful drain (GOAWAY) and client reconnect with backoff.",
    },
    {
      bullet: "How application protocols interact with TCP or QUIC.",
      notes:
        "• HTTP/1.1 and HTTP/2 use TCP: the transport gives ordered bytes, retransmission, and congestion control; the app defines messages.\n• HTTP/3 uses QUIC: the transport also gives streams, loss recovery per stream, and integrated TLS.\n• Transport events surface in the app: loss becomes latency, a reset becomes a failed request, and a stalled window becomes a slow read.\n• Handshake costs, pooling, and head-of-line blocking determine how many connections the app opens and how it behaves under loss.",
    },
  ],
  mentalModel:
    "• One message = request out, response back, each self-contained; the transport only moves bytes or streams.\n• Versions change where requests share a connection: HTTP/1.1 serializes, HTTP/2 multiplexes over one TCP stream, HTTP/3 multiplexes over QUIC streams that lose independently.\n• Every hop (client, CDN, reverse proxy, load balancer, server) may hold its own connection, cache, and timeout.\n• Reuse and caching are the main levers: avoid new connections and avoid the request altogether.",
  failuresAndSignals: [
    "502, 503, or 504 with a healthy TCP connect: proxy or load balancer accepted the connection but the backend is down, slow, or unhealthy — check upstream health and timeouts at each hop.",
    "Intermittent connection reset or EOF on the first request after idle: a pooled connection was closed by the server or middlebox — retry idempotent requests and set client idle timeout below the server's.",
    "Slow page under packet loss on HTTP/2: one lost TCP segment stalls all streams — signs are latency spikes across unrelated requests; HTTP/3 or fewer lost packets reduces it.",
    "Stale or wrong cached content: missing or incorrect Cache-Control, ETag, or Vary — signs are old content after deploy or one user's response served to another; check response headers and cache keys.",
    "WebSocket or stream drops at a regular interval: an idle timeout on a proxy, load balancer, or NAT — add heartbeats and align timeouts across hops.",
    "HTTP/3 never used or slow to start: UDP blocked, so the client falls back to TCP — signs are Alt-Svc advertised but connections show HTTP/2.",
  ],
  whyAnchors: ["why.01", "why.23", "why.24", "why.27", "why.28", "why.29", "why.30", "why.54"],
  connections: {
    before:
      "• Module 7: DNS turns the URL's host into addresses before any connection.\n• Modules 5 and 6: TCP and UDP supply the transports HTTP versions ride on.",
    after:
      "• Module 9: TLS wraps HTTP as HTTPS and is built into QUIC's handshake.\n• Module 10: NAT, firewalls, and load balancing apply to the connections and proxies met here.",
  },
  selfCheck: [
    {
      id: "why-http",
      prompt: "If TCP is reliable, why do we need HTTP status codes and methods?",
      answer:
        "TCP only guarantees bytes reached the peer's kernel. HTTP reports whether the server understood and did the request, says whether it may be repeated (method semantics), and frames messages so truncation is visible (why.23).",
    },
    {
      id: "hol",
      prompt:
        "HTTP/2 multiplexes streams; why can packet loss still stall them all, and how does HTTP/3 fix it?",
      answer:
        "All streams share one ordered TCP byte stream, so a lost segment blocks later bytes for every stream (why.28). QUIC orders each stream separately, so only streams with lost data wait (why.29).",
    },
    {
      id: "pool",
      prompt: "What is the benefit and the risk of connection pooling?",
      answer:
        "Benefit: no repeated TCP and TLS handshakes and a warm congestion window (why.30). Risk: an idle pooled connection may be closed by the peer, so retry only idempotent requests automatically.",
    },
    {
      id: "grpc",
      prompt: "Why does gRPC use HTTP/2 instead of raw TCP?",
      answer:
        "HTTP/2 already provides streams, framing, flow control, cancellation, headers, and trailers, plus compatibility with TLS, proxies, and load balancers, so gRPC need not reinvent them (why.54).",
    },
  ],
} satisfies ChapterSummary;
