import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m08.intermediaries",
  status: "complete",
  objectives: [
    "Tell a forward proxy, reverse proxy (gateway), and tunnel apart, and say which connection each option applies to.",
    "Explain why connection-level balancing spreads HTTP/2 and gRPC poorly, and how per-request balancing fixes it.",
    "Describe how a WebSocket starts as HTTP and then stops being HTTP, and what keeps long-lived connections alive.",
    "Say what gRPC takes from HTTP/2: streams, framing, metadata, deadlines, trailers status, and safe retries.",
  ],
  prerequisites: ["m08.reuse-and-cache"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Requests rarely go straight to the process that answers: proxy, CDN, load balancer, ingress, sidecar.\n• Each hop ends one connection and opens another.\n• Some traffic is not request-response: chat, price feeds, hour-long streaming RPCs.\n• Middle hops must know which parts are for them and when a quiet connection is still in use.",
      whyItMatters:
        "• Most confusing HTTP failures live here: a proxy's 502, pinned HTTP/2 connections overloading one backend, a WebSocket dropped at a 60 s idle timeout, a stream buffered into one burst.\n• Debugging starts with knowing which hop terminates what.",
      idea: "• An intermediary is a server on one side and a client on the other.\n• Every hop has its own connection, timeouts, and view of traffic.\n• End-to-end meaning travels in the message. Connection-specific parts are hop-by-hop and stripped.\n• Long-lived protocols either use HTTP to set up something else (WebSocket, CONNECT) or keep HTTP/2 streams open (gRPC).",
      howItWorks:
        "• Forward proxy: chosen by the client. Reverse proxy (gateway): acts as the origin, forwards inbound. Tunnel: blind byte relay after CONNECT.\n• Each hop removes Connection and the fields it names, and appends itself to Via.\n• L7 balancer picks a backend per request. L4 picks per connection, so one HTTP/2 connection = one backend.\n• WebSocket: HTTP/1.1 GET with `Upgrade: websocket`, then 101, then WebSocket frames.\n• gRPC: HTTP/2 POST to `/Service/Method`, length-prefixed messages in DATA, status in trailers.",
      whyItWorks:
        "• Explicit hop-by-hop fields let a proxy rewrite connection handling without changing meaning (HTTP/2 in front, HTTP/1.1 behind).\n• Self-describing requests let an L7 balancer spread one client connection across many servers.\n• Reusing HTTP for setup lets WebSockets and gRPC pass existing ports, TLS, proxies, and auth.\n• HTTP/2 streams give long calls independent flow control, cancellation, and clean shutdown.",
      limits:
        "• Each hop adds a failure point and a timeout that may be shorter than the work. A 502 or 504 hides what the backend did.\n• Intermediaries may buffer. Do not rely on incremental delivery.\n• Parsers that disagree on message length allow request smuggling.\n• After an upgrade or CONNECT, the hop sees only bytes: no caching, no per-request routing.\n• Long-lived connections stick to one backend. Balance and draining must be designed in.",
      connections:
        "• First lesson: the 502, 503, 504 that gateways write. Module 5 why.20: connect succeeds, request fails.\n• Versions lesson: streams, GOAWAY, REFUSED_STREAM, which gRPC relies on.\n• Caching lesson: the shared caches that reverse proxies and CDNs run.\n• Module 9: TLS termination. Module 10: NAT idle timeouts. Module 13: 502/503 investigations.",
    },
    {
      type: "comparison",
      id: "intermediary-kinds",
      title: "What each intermediary terminates",
      caption:
        "Layer 4 and layer 7 are industry terms (balance TCP connections vs HTTP requests), not RFC terms. One product can play several roles.",
      columns: ["Chosen by", "Sees", "Typical job"],
      rows: [
        {
          label: "Forward proxy",
          cells: [
            "Client config.",
            "Every request, unless tunnelled with CONNECT.",
            "Egress policy, filtering, shared cache.",
          ],
        },
        {
          label: "Reverse proxy (gateway)",
          cells: [
            "Origin operator; clients think it is the origin.",
            "Every request and response, usually after TLS.",
            "TLS termination, host/path routing, caching, balancing.",
          ],
        },
        {
          label: "Tunnel",
          cells: ["A request such as CONNECT.", "Opaque bytes.", "TLS through a proxy."],
        },
        {
          label: "L4 load balancer",
          cells: [
            "Operator.",
            "TCP/UDP addresses and ports, not HTTP.",
            "One connection → one backend for life.",
          ],
        },
        {
          label: "Interception proxy",
          cells: [
            "Nobody; traffic is redirected.",
            "Whatever it intercepts.",
            "Captive portals, policy. Looks like an on-path attacker.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "grpc-behind-lb",
      title: "A gRPC service that stays unbalanced after scaling out",
      setup:
        "Two client pods call `Orders` over gRPC. Each opens one HTTP/2 connection with hundreds of concurrent calls. Service scales 2 → 6 backends behind a load balancer. Names and counts are illustrative.",
      walkthrough: [
        "Each call: HTTP/2 POST `/orders.v1.Orders/Get`, `content-type: application/grpc`, `te: trailers`, `grpc-timeout: 1S`, own stream. Message = 1-byte compressed flag + 4-byte length + bytes, in DATA frames.",
        "L4 balancer picks a backend at connect time. Two connections, two busy backends. The four new ones get nothing: no new connections. RFC 9113 tells clients to keep one connection per origin.",
        "Fix 1, balance per call: an L7 proxy terminates HTTP/2 and spreads streams over all six. Or client-side: gRPC `round_robin` keeps a subchannel per address and rotates calls.",
        "Fix 2, drain: a backend sends GOAWAY. Calls above Last-Stream-ID are UNAVAILABLE and retried elsewhere. Accepted calls finish. New calls open a new connection, which can land on a new backend.",
        "Overloaded backend resets new streams with REFUSED_STREAM. gRPC maps it to UNAVAILABLE, retryable: nothing was processed. Other failures retry only if the call is marked idempotent.",
        "Responses: `:status 200`, message, then trailers with `grpc-status`. A failure can be HTTP 200 + `grpc-status: 14`. HTTP-status-only monitoring reports it as success.",
      ],
      result:
        "An L4 balancer pins all of a client's calls to one backend. Balance per call at L7 or in the client, rotate with GOAWAY, and read health from `grpc-status`.",
    },
    {
      type: "steps",
      id: "websocket-lifecycle",
      title: "A WebSocket from upgrade to close",
      steps: [
        {
          title: "Ordinary HTTP/1.1 request",
          body: "`GET /chat HTTP/1.1` with `Upgrade: websocket`, `Connection: Upgrade`, random `Sec-WebSocket-Key`, `Sec-WebSocket-Version: 13`, `Origin`. Cookies ride along, so normal HTTP auth works.",
        },
        {
          title: "101 Switching Protocols",
          body: "Server replies 101 with `Upgrade: websocket` and `Sec-WebSocket-Accept` derived from the key. Only a protocol the client listed. After this: WebSocket frames, no more HTTP on this connection.",
        },
        {
          title: "Frames both ways",
          body: "Either side sends text or binary messages any time. Client frames are always masked, even over TLS, so crafted bytes cannot confuse intercepting proxies. Server closes on an unmasked client frame. Server frames are never masked.",
        },
        {
          title: "Keepalive",
          body: "Ping must be answered with Pong. Ping works as keepalive. Send it more often than the shortest idle timeout on the path.",
        },
        {
          title: "Closing handshake",
          body: "One side sends Close (optional 2-byte code + reason). The other replies Close. The server then closes TCP. Both ends agree the messages were finished.",
        },
        {
          title: "Over HTTP/2",
          body: "HTTP/2 forbids Upgrade, Connection, and 101. RFC 8441 adds extended CONNECT with `:protocol`. The WebSocket runs as a tunnel on one stream, beside normal requests.",
        },
      ],
    },
    {
      type: "why",
      id: "why-grpc-http2",
      questionId: "why.54",
    },
    {
      type: "callout",
      id: "framing-disagreement",
      tone: "caution",
      title: "Two hops that disagree on where a message ends",
      body: "A request with both Content-Length and Transfer-Encoding: one parser uses the length, the other the chunks. The leftover becomes a second request the front proxy never checked. That is request smuggling. RFC 9112: reject it or use Transfer-Encoding alone, and close the connection after responding. Prefer HTTP/2 between proxy and backend, or strict RFC 9112 parsers on both.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "forwarding-rules",
      title: "Forwarding rules",
      paragraphs: [
        "Three intermediaries = four connections. Some options apply only to the nearest non-tunnel neighbour. Later requests may take a different path.",
        "Forward unknown methods, status codes, and fields. Parse Connection, drop every field it names, then drop Connection. Never forward to yourself without loop protection.",
        "Each proxy appends to Via, so Via shows the chain in order. A gateway must add Via to inbound requests and may add it to responses.",
        "The backend sees the proxy's address. X-Forwarded-For and Forwarded carry the client's, but are not covered by sources cited here. Trust them only from your own proxy.",
        "Senders cannot rely on incremental delivery: hops may buffer. A stream that arrives in one burst usually means a buffering hop.",
        "CONNECT `host:port` opens a tunnel. A 2xx means bytes after the header section come from the destination. Most origin servers do not implement CONNECT.",
      ],
    },
    {
      type: "expandable",
      id: "long-lived",
      title: "Streaming and long-lived connections",
      paragraphs: [
        "Three shapes: a streaming response (chunked in HTTP/1.1, DATA frames in HTTP/2 and HTTP/3; server-sent events use this, spec not cited); a WebSocket; a gRPC streaming call on one open HTTP/2 stream.",
        "Every hop has an idle timeout. Keep traffic below it: HTTP/2 PING, WebSocket Ping, or app heartbeats. An unanswered gRPC client PING closes all calls with UNAVAILABLE.",
        "A long-lived connection keeps its backend. Draining means GOAWAY or WebSocket Close, and clients that reconnect with backoff so thousands do not reconnect at once.",
        "A connection failure hits everything multiplexed on it. gRPC closes client calls with UNAVAILABLE, server calls with CANCELLED.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "hop-by-hop",
          prompt:
            "A client sends `Connection: keep-alive, X-Trace-Hop` and `X-Trace-Hop: 1` to a reverse proxy. What reaches the backend, and why the rule?",
          answer:
            "Neither field. The proxy drops fields named in Connection, then Connection itself. That lets each hop manage its own connection without confusing the next.",
        },
        {
          id: "l4-imbalance",
          prompt:
            "gRPC service scaled 3 → 10 pods behind a TCP load balancer. The original 3 stay hot, the new 7 idle. Why, and two fixes?",
          answer:
            "Clients keep long-lived HTTP/2 connections, and an L4 balancer chooses only at connect time. Balance per call (L7 proxy or client `round_robin`), and rotate connections with GOAWAY.",
        },
        {
          id: "websocket-drops",
          prompt:
            "A dashboard WebSocket drops about every 60 s when idle, never when busy. Cause and fix?",
          answer:
            "A proxy, balancer, or NAT closes connections idle for about 60 s. Send WebSocket Pings well under that, or raise the timeout you control. Reconnect with backoff anyway.",
        },
        {
          id: "grpc-200-failure",
          prompt:
            "A gRPC dashboard built from proxy access logs shows 100% HTTP 200 during an outage. How?",
          answer:
            "gRPC reports the call result in the `grpc-status` trailer; HTTP status is 200 even for failures such as 14 UNAVAILABLE. Measure `grpc-status`.",
        },
        {
          id: "upgrade-after",
          prompt:
            "After a WebSocket upgrade, can the reverse proxy route or cache individual messages?",
          answer:
            "No. After 101 there are no HTTP messages, only WebSocket frames to one backend. Per-message routing belongs in the application.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "intermediary-roles",
      statement:
        "Common intermediaries are proxy, gateway, and tunnel. A message through three intermediaries crosses four connections, and some options apply only to the nearest non-tunnel neighbour. A proxy is chosen by the client. A gateway (reverse proxy) acts as origin outbound and forwards inbound, often for caching or load balancing. A tunnel relays without changing messages. Interception proxies are not chosen by the client and are indistinguishable at protocol level from an on-path attacker.",
      sourceId: "rfc9110",
      locator: "Section 3.7, Intermediaries",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "forwarding",
      statement:
        "Intermediaries are expected to forward unrecognized protocol elements. A non-tunnel intermediary must implement Connection and must not forward to itself without loop protection. Partial messages may not be delivered incrementally, because some implementations buffer.",
      sourceId: "rfc9110",
      locator: "Section 7.6, Message Forwarding",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "connection-field",
      statement:
        "Intermediaries must parse Connection, remove each field it names, then remove or replace Connection. This separates hop-by-hop fields from end-to-end fields.",
      sourceId: "rfc9110",
      locator: "Section 7.6.1, Connection",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "via",
      statement:
        "Each forwarding proxy or gateway appends to Via, in forwarding order. A proxy must send Via on each forwarded message; a gateway must on inbound requests and may on responses.",
      sourceId: "rfc9110",
      locator: "Section 7.6.3, Via",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "upgrade",
      statement:
        "Upgrade invites a server to switch an HTTP/1.1 connection to another protocol; the server may ignore it. A server sending 101 must name the new protocol and must not switch to one the client did not list.",
      sourceId: "rfc9110",
      locator: "Section 7.8, Upgrade",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "connect",
      statement:
        "CONNECT asks the recipient to open a tunnel to the target host and port and then forward data blindly both ways. A 2xx means the tunnel starts after the response header section. Most origin servers do not implement CONNECT.",
      sourceId: "rfc9110",
      locator: "Section 9.3.6, CONNECT",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "gateway-codes",
      statement:
        "502: a gateway or proxy got an invalid response from an inbound server. 504: it did not get a timely response.",
      sourceId: "rfc9110",
      locator: "Sections 15.6.3 and 15.6.5",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "smuggling",
      statement:
        "Request smuggling exploits parsing differences between recipients to hide requests. A server may reject a request with both Content-Length and Transfer-Encoding or process it by Transfer-Encoding alone, and must close the connection after responding.",
      sourceId: "rfc9112",
      locator: "Section 6.1, Transfer-Encoding; Section 11.2, Request Smuggling",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-one-conn-proxy",
      statement:
        "Clients should not open more than one HTTP/2 connection per host and port pair. A client using a proxy over HTTP/2 sends all requests through one connection to it.",
      sourceId: "rfc9113",
      locator: "Section 9.1, Connection Management; Section 9.1.1, Connection Reuse",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-goaway-refused",
      statement:
        "Requests above a GOAWAY's last stream ID, or reset with REFUSED_STREAM, were not processed and may be retried automatically, even non-idempotent ones. PING tests idle connections, which middleboxes may silently drop.",
      sourceId: "rfc9113",
      locator: "Section 8.7, Request Reliability",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "ws-handshake",
      statement:
        "A WebSocket opening handshake is an HTTP/1.1 GET with Upgrade: websocket, Connection: Upgrade, Sec-WebSocket-Key, and Sec-WebSocket-Version: 13, answered by 101 with Sec-WebSocket-Accept, derived from the key and a fixed GUID.",
      sourceId: "rfc6455",
      locator: "Section 1.2, Protocol Overview; Section 1.3, Opening Handshake",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ws-design",
      statement:
        "WebSocket is a layer over TCP adding an origin-based security model, addressing for many services and host names, framing, and an in-band closing handshake designed to work through proxies. Its only relationship to HTTP is that the handshake is interpreted as an Upgrade request.",
      sourceId: "rfc6455",
      locator: "Section 1.5, Design Philosophy; Section 1.7, Relationship to TCP and HTTP",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ws-masking",
      statement:
        "A client must mask every frame, even over TLS, to avoid confusing intermediaries; a server must close on an unmasked client frame and must not mask its own frames.",
      sourceId: "rfc6455",
      locator: "Section 5.1, Overview",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ws-control",
      statement:
        "Control frames (Close, Ping, Pong) carry at most 125 bytes. A Close may carry a 2-byte status code and a reason. An endpoint must answer Ping with Pong, and Ping may serve as a keepalive.",
      sourceId: "rfc6455",
      locator: "Sections 5.5, 5.5.1, and 5.5.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ws-over-h2",
      statement:
        "HTTP/2 does not allow Upgrade, Connection, or 101, which RFC 6455 needs. RFC 8441 extends CONNECT with :protocol so a WebSocket runs as a tunnel on one HTTP/2 stream, multiplexed with other streams.",
      sourceId: "rfc8441",
      locator: "Section 1, Introduction; Section 4, The Extended CONNECT Method",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "grpc-mapping",
      statement:
        "A gRPC request is HTTP/2 headers with :method POST, :path /Service/Method, te: trailers, optional grpc-timeout, and content-type application/grpc, then length-prefixed messages (1-byte compressed flag, 4-byte length) in DATA frames. DATA frame boundaries are unrelated to message boundaries. HTTP/2 stream IDs are call identifiers.",
      sourceId: "grpc-protocol-http2",
      locator: "Requests; HTTP2 Transport Mapping, Stream Identification and Data Frames",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "grpc-trailers",
      statement:
        "A gRPC response is headers, messages, and trailers carrying status, which must be sent even when OK. Trailers-Only is allowed for immediate errors.",
      sourceId: "grpc-protocol-http2",
      locator: "Responses",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "grpc-retries",
      statement:
        "gRPC calls are not assumed idempotent unless marked; calls that cannot be proven to have started are not retried. REFUSED_STREAM maps to UNAVAILABLE and can be retried elsewhere. After GOAWAY, streams after the last accepted one are UNAVAILABLE and retried elsewhere.",
      sourceId: "grpc-protocol-http2",
      locator:
        "Idempotency and Retries; Errors (RST_STREAM mapping); Connection Management, GOAWAY Frame",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "grpc-ping-failure",
      statement:
        "gRPC uses HTTP/2 PING for liveness; an expired client PING closes all calls with UNAVAILABLE. On a detected connection failure, client calls close with UNAVAILABLE and server calls with CANCELLED.",
      sourceId: "grpc-protocol-http2",
      locator: "Connection Management, PING Frame and Connection failure",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "grpc-lb-per-call",
      statement:
        "gRPC load balancing is per call, not per connection. pick_first is the default; round_robin keeps a subchannel per address and sends successive RPCs to successive ready subchannels.",
      sourceId: "grpc-load-balancing",
      locator: "Background; Load Balancing Policies, pick_first and round_robin",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    {
      sourceId: "rfc9110",
      note: "Sections 3.7, 7.6, 7.6.1, 7.6.3, 7.8, 9.3.6, 15.6.3, and 15.6.5.",
    },
    { sourceId: "rfc9112", note: "Sections 6.1 and 11.2: framing conflicts and smuggling." },
    { sourceId: "rfc9113", note: "Sections 8.7, 9.1, and 9.1.1: retries, PING, one connection." },
    {
      sourceId: "rfc6455",
      note: "Sections 1.2, 1.3, 1.5, 1.7, 5.1, 5.5, 5.5.1, and 5.5.2.",
    },
    { sourceId: "rfc8441", note: "Sections 1 and 4: WebSockets over HTTP/2." },
    { sourceId: "grpc-protocol-http2", note: "gRPC's wire mapping onto HTTP/2, master branch." },
    {
      sourceId: "grpc-load-balancing",
      note: "Per-call load balancing and policies, master branch.",
    },
  ],
} satisfies LessonBody;
