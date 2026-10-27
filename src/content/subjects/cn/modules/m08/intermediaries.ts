import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m08.intermediaries",
  status: "complete",
  objectives: [
    "Tell a forward proxy, a reverse proxy or gateway, and a tunnel apart, and say which connection each HTTP option applies to.",
    "Explain why a load balancer that balances connections spreads HTTP/2 and gRPC traffic poorly, and how per-request balancing fixes it.",
    "Describe how a WebSocket starts as an HTTP request and then stops being HTTP, and what keeps a long-lived connection alive.",
    "Explain what gRPC takes from HTTP/2: streams, framing, metadata, deadlines, status in trailers, and safe retries.",
  ],
  prerequisites: ["m08.reuse-and-cache"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Almost no production request goes straight from a client to the process that answers it. It passes a corporate proxy, a CDN, a load balancer, an ingress controller, or a sidecar, and each one terminates a connection and opens another. Some traffic also does not fit request and response at all: a chat window, a price feed, or an RPC that streams results for an hour. The software in the middle has to know which parts of a message are for it, which are for the far end, and when a connection that looks idle is still in use.",
      whyItMatters:
        "Most confusing HTTP failures happen at these hops: a 502 from a proxy whose backend closed the connection, one backend overloaded while others idle because a load balancer pinned long-lived HTTP/2 connections, a WebSocket that drops every 60 seconds at a proxy's idle timeout, or a streaming response that arrives all at once because something buffered it. Debugging them starts with knowing which hop terminates what.",
      idea: "An HTTP intermediary is an HTTP endpoint on both sides. It ends the client's connection as a server, makes its own connection inbound as a client, and forwards messages between them, so every hop has its own connection, its own timeouts, and its own view of the traffic. End-to-end meaning travels in the message; anything about one connection is hop-by-hop and is removed at each hop. Protocols that need more than one request and response either use HTTP to set up something else (WebSocket, CONNECT tunnels) or use HTTP/2 streams that stay open (gRPC streaming).",
      howItWorks:
        "A forward proxy is chosen by the client to reach many origins; a reverse proxy, which RFC 9110 calls a gateway, acts as the origin for clients and forwards inbound to one or more servers; a tunnel relays bytes blindly, as after a successful CONNECT. Each intermediary removes the Connection field and every field it names before forwarding, and appends itself to Via. A load balancer that understands HTTP picks a backend per request; one that sees only TCP picks per connection, so all requests on one HTTP/2 connection go to the same backend. A WebSocket begins as an HTTP/1.1 GET with `Upgrade: websocket`, and after a 101 Switching Protocols the connection carries WebSocket frames, not HTTP. gRPC sends each call as an HTTP/2 POST to `/Service/Method`, carries messages as length-prefixed bytes in DATA frames, and returns its own status in trailers.",
      whyItWorks:
        "Because the hop-by-hop parts are explicit, an intermediary can rewrite connection management without changing meaning, which is what lets a reverse proxy accept HTTP/2 from browsers and speak HTTP/1.1 to old backends. Because each request is self-describing, a layer-7 balancer can spread requests from one client connection across many servers. Reusing HTTP for setup lets WebSockets and gRPC pass through existing ports, TLS, proxies, and authentication, and HTTP/2 streams give long-lived calls independent flow control, cancellation, and a clean shutdown signal.",
      limits:
        "Each hop adds a connection that can fail on its own and a timeout that can be shorter than the work. A proxy's 502 or 504 hides what the backend did. Intermediaries may buffer, so a sender cannot rely on partial messages being delivered incrementally. Two parsers that disagree on where a message ends let an attacker smuggle a request past one of them. After a WebSocket upgrade or a CONNECT, the intermediary sees only bytes, and HTTP-level features such as caching and per-request routing are gone. A long-lived connection keeps going to the same backend, so balance and graceful draining must be designed in, not assumed.",
      connections:
        "The first lesson in this module introduced the 502, 503, and 504 codes that gateways write, and why.20 in Module 5 explained how a connection can succeed while the request fails. The versions lesson explained HTTP/2 streams, GOAWAY, and REFUSED_STREAM, which gRPC relies on. The caching lesson covered the shared caches that reverse proxies and CDNs run. Module 9 covers TLS termination at proxies, Module 10 covers NAT idle timeouts, and Module 13 investigates 502 and 503 scenarios.",
    },
    {
      type: "comparison",
      id: "intermediary-kinds",
      title: "What each intermediary terminates",
      caption:
        "Layer 4 and layer 7 are industry terms for balancing on TCP connections versus on HTTP requests. They are not RFC terms. One product can act in several roles.",
      columns: ["Chosen by", "Sees", "Typical job"],
      rows: [
        {
          label: "Forward proxy",
          cells: [
            "The client, through configuration.",
            "Every request, unless the client tunnels through it with CONNECT.",
            "Egress policy, filtering, shared caching for an organization.",
          ],
        },
        {
          label: "Reverse proxy (gateway)",
          cells: [
            "The origin's operator; clients think it is the origin.",
            "Every request and response, usually after terminating TLS.",
            "TLS termination, routing by host and path, caching, load balancing, retries.",
          ],
        },
        {
          label: "Tunnel",
          cells: [
            "Set up by a request such as CONNECT.",
            "Opaque bytes after setup.",
            "Carrying TLS through a proxy to the real origin.",
          ],
        },
        {
          label: "Layer-4 load balancer",
          cells: [
            "The operator.",
            "TCP or UDP connections and their addresses and ports, not HTTP.",
            "Spreading connections; one connection goes to one backend for its lifetime.",
          ],
        },
        {
          label: "Interception proxy",
          cells: [
            "Nobody: it redirects traffic without the client choosing it.",
            "Whatever it intercepts on common ports.",
            "Captive portals and network policy. Indistinguishable from an on-path attacker at the protocol level.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "grpc-behind-lb",
      title: "A gRPC service that stays unbalanced after scaling out",
      setup:
        "Two client pods call an `Orders` gRPC service. Each client opens one HTTP/2 connection and sends a few hundred concurrent calls on it. The service runs behind a load balancer. It scales from 2 to 6 backends during a traffic spike. Pod names, counts, and timings are illustrative.",
      walkthrough: [
        "Each call is an HTTP/2 POST to `/orders.v1.Orders/Get` with `content-type: application/grpc`, `te: trailers`, and `grpc-timeout: 1S`, on its own stream. The request message is a 5-byte prefix (a compression flag and a 4-byte length) followed by the encoded message, in DATA frames.",
        "The load balancer works at layer 4: it picks a backend when a TCP connection opens and forwards bytes after that. Two clients, two connections, so two backends get all the traffic. The four new backends receive nothing, because no new connections are opened. RFC 9113 tells clients to keep one HTTP/2 connection per origin, and they do.",
        "Fix one: balance per call. A layer-7 proxy terminates each client's HTTP/2 connection and spreads streams across connections to all six backends. Or the client balances itself: gRPC's `round_robin` policy keeps a subchannel to every address the resolver returns and sends successive calls to successive ready subchannels. gRPC's own documentation says balancing happens per call, not per connection.",
        "Fix two: drain old connections. When a backend is being removed, or to rebalance, it sends GOAWAY. Calls on streams above the Last-Stream-ID were not processed; gRPC maps that to UNAVAILABLE and the client may retry them elsewhere. Calls already accepted finish normally, and new calls go on a new connection, which the balancer can place on a new backend.",
        "During the spike one backend is overloaded and resets some new streams with REFUSED_STREAM. gRPC maps that to UNAVAILABLE and says the request can be retried, possibly elsewhere, because no processing occurred. Calls that fail any other way are retried only if marked idempotent, since gRPC does not assume they are.",
        "Each response carries `:status 200`, the message, and then trailers with `grpc-status`. A failed call can be HTTP 200 with `grpc-status: 14` (UNAVAILABLE) in the trailers. Monitoring that counts HTTP status codes alone will report every gRPC failure as a success.",
      ],
      result:
        "A long-lived multiplexed connection is the unit a layer-4 balancer sees, so it pins all of one client's calls to one backend. Balance per call at layer 7 or in the client, and use GOAWAY to move connections. Read gRPC health from `grpc-status`, not from the HTTP status.",
    },
    {
      type: "steps",
      id: "websocket-lifecycle",
      title: "A WebSocket from upgrade to close",
      steps: [
        {
          title: "An ordinary HTTP/1.1 request",
          body: "The browser sends `GET /chat HTTP/1.1` with `Host`, `Upgrade: websocket`, `Connection: Upgrade`, a random `Sec-WebSocket-Key`, `Sec-WebSocket-Version: 13`, and `Origin`. Cookies can ride along, so the server can authenticate the user with its normal HTTP machinery.",
        },
        {
          title: "101 Switching Protocols",
          body: "The server answers `HTTP/1.1 101 Switching Protocols` with `Upgrade: websocket` and a `Sec-WebSocket-Accept` value derived from the key. A server may only switch to a protocol the client listed. From this point the TCP connection carries WebSocket frames; no further HTTP messages are exchanged on it.",
        },
        {
          title: "Messages in frames, both directions",
          body: "Either side sends text or binary messages at any time, each made of one or more frames. Every client-to-server frame is masked, and a server closes the connection if a client frame is not masked. Server frames are never masked. This is required even over TLS, to keep crafted bytes from confusing intercepting proxies.",
        },
        {
          title: "Keeping it alive",
          body: "Either side may send a Ping, and the other must answer with a Pong. A Ping can serve as a keepalive or a liveness check. Proxies and NATs on the path have idle timeouts, so an application that sends nothing for minutes needs periodic pings shorter than the shortest timeout on the path.",
        },
        {
          title: "Closing handshake",
          body: "One side sends a Close frame, optionally with a 2-byte status code and a reason. The other replies with Close. Then the server closes the TCP connection. The in-band close lets both ends agree that the messages were finished, which a bare TCP close through a proxy may not show.",
        },
        {
          title: "Over HTTP/2",
          body: "HTTP/2 forbids Upgrade and Connection and has no 101, so RFC 8441 defines an extended CONNECT with a `:protocol` pseudo-header. The WebSocket then runs as a tunnel on a single HTTP/2 stream, sharing the connection with ordinary requests.",
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
      title: "When two hops disagree on where a message ends",
      body: "A front proxy and a backend that parse HTTP/1.1 framing differently can disagree on where one request ends. If a request has both Content-Length and Transfer-Encoding, one may use the length and the other the chunked coding, and the leftover bytes become a second request that the proxy never inspected. This is request smuggling. RFC 9112 lets a server reject such a request or use Transfer-Encoding alone, and either way requires it to close the connection afterward. Use HTTP/2 between proxy and backend, or keep both on parsers that follow RFC 9112 strictly.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "forwarding-rules",
      title: "Forwarding rules every proxy follows",
      paragraphs: [
        "A message through three intermediaries crosses four connections. Some options apply only to the nearest non-tunnel neighbour, some to the endpoints, and some to every hop. Later requests from the same client may take a different path, for example after load-balancer reconfiguration.",
        "Intermediaries forward unrecognized methods, status codes, and fields, which keeps HTTP extensible. They must parse Connection, remove every field it names and Connection itself, and must not forward a message to themselves without loop protection.",
        "Each proxy appends to Via, so a response's Via list shows the forwarding chain in order. A gateway must add Via to inbound requests and may add it to responses.",
        "The client's address seen by the backend is the proxy's. Proxies commonly pass the original address in a header such as X-Forwarded-For or Forwarded; neither is covered by the sources cited here, and a backend should trust them only when set by its own proxy.",
        "HTTP messages can be processed as streams, but senders cannot rely on incremental delivery: some intermediaries buffer or delay forwarding for efficiency, security checks, or transformations. A streaming response that arrives all at once at the client usually means a buffering hop.",
        "CONNECT asks a proxy to open a tunnel to `host:port` and then relay bytes both ways. A 2xx means the tunnel is up and everything after the header section comes from the destination. Most origin servers do not implement CONNECT.",
      ],
    },
    {
      type: "expandable",
      id: "long-lived",
      title: "Streaming and long-lived connections",
      paragraphs: [
        "There are three common shapes. A streaming response sends one long response a piece at a time, with chunked coding in HTTP/1.1 or DATA frames in HTTP/2 and HTTP/3; server-sent events in browsers are built this way, though their specification is not cited here. A WebSocket upgrades the connection to its own bidirectional framing. A gRPC streaming call keeps one HTTP/2 stream open with many length-prefixed messages in each direction.",
        "Every hop has an idle timeout, and long-lived connections hit them. Keep traffic flowing with protocol pings shorter than the shortest timeout: HTTP/2 PING, WebSocket Ping, or application heartbeats. gRPC closes all calls on a connection whose PING goes unanswered, with UNAVAILABLE on the client.",
        "A long-lived connection keeps its backend. Draining a backend for a deploy means sending GOAWAY or a WebSocket Close and letting clients reconnect, which they must be written to do, with backoff so that thousands do not reconnect at once.",
        "Connection-level failure affects everything multiplexed on it: gRPC closes all calls with UNAVAILABLE on the client and CANCELLED on the server when it detects a connection failure.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "hop-by-hop",
          prompt:
            "A client sends `Connection: keep-alive, X-Trace-Hop` and `X-Trace-Hop: 1` to a reverse proxy. Which of these fields reaches the backend, and why does HTTP need this rule?",
          answer:
            "Neither. The proxy must remove every field named in Connection, here X-Trace-Hop, and then Connection itself, before forwarding. Connection marks fields that describe only the current connection, so each hop can manage its own connection without the next hop misreading the previous one's options. The proxy may add its own Connection options for its connection to the backend.",
        },
        {
          id: "l4-imbalance",
          prompt:
            "After scaling a gRPC service from 3 to 10 pods behind a TCP load balancer, CPU stays high on the original 3 and near zero on the new 7. Why, and what are two fixes?",
          answer:
            "gRPC clients keep long-lived HTTP/2 connections and multiplex every call on them. A TCP load balancer chooses a backend only when a connection opens, so existing connections stay on the original pods and the new ones get only connections opened after they started. Fixes: balance per call, with a layer-7 proxy that terminates HTTP/2 or with client-side balancing such as gRPC's round_robin over all addresses; and make backends or the proxy rotate connections with GOAWAY so clients reconnect and get spread.",
        },
        {
          id: "websocket-drops",
          prompt:
            "A dashboard's WebSocket disconnects about every 60 seconds when no data is flowing, but never while updates are frequent. What is the likely cause and fix?",
          answer:
            "An intermediary on the path, such as a reverse proxy, load balancer, or NAT, closes connections idle for about 60 seconds. When updates flow, the connection is never idle. Send WebSocket Ping frames, which the peer must answer with Pong, at an interval well under the shortest idle timeout, or raise the timeout on the hop you control. The client should also reconnect with backoff, because a long-lived connection will eventually be closed by a deploy or a failure.",
        },
        {
          id: "grpc-200-failure",
          prompt:
            "A gRPC dashboard built from proxy access logs shows 100% HTTP 200 during an outage. How can that be?",
          answer:
            "gRPC sends `:status 200` for calls that reach the gRPC layer and reports the call's result in the `grpc-status` trailer, which is sent even for OK. A failing call can be HTTP 200 with a non-zero grpc-status, such as 14 UNAVAILABLE. A Trailers-Only response for an immediate error also uses HTTP 200. Measure grpc-status from the trailers, not the HTTP status.",
        },
        {
          id: "upgrade-after",
          prompt:
            "After a WebSocket upgrade, can the reverse proxy route individual messages to different backends or cache them? Why or why not?",
          answer:
            "No. After 101 Switching Protocols the connection no longer carries HTTP messages; it carries WebSocket frames between the client and the one backend the upgrade request was routed to. The proxy relays the frames. It has no request target, method, or cache semantics to act on per message. Any routing of individual messages has to happen in the application.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "intermediary-roles",
      statement:
        "The three common intermediaries are proxy, gateway, and tunnel. A message crossing three intermediaries crosses four connections, and options may apply only to the nearest non-tunnel neighbour. A proxy is chosen by the client. A gateway, or reverse proxy, acts as the origin for the outbound connection and forwards inbound, often for caching or load balancing. A tunnel relays without changing messages. Interception proxies are not chosen by the client and are indistinguishable at the protocol level from an on-path attacker.",
      sourceId: "rfc9110",
      locator: "Section 3.7, Intermediaries",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "forwarding",
      statement:
        "Intermediaries are expected to forward unrecognized protocol elements. A non-tunnel intermediary must implement Connection and must not forward a message to itself without loop protection. Senders and recipients cannot rely on incremental delivery of partial messages, because some implementations buffer or delay forwarding.",
      sourceId: "rfc9110",
      locator: "Section 7.6, Message Forwarding",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "connection-field",
      statement:
        "Intermediaries must parse Connection before forwarding, remove each field named in it, and remove or replace Connection itself. This separates hop-by-hop fields from end-to-end fields.",
      sourceId: "rfc9110",
      locator: "Section 7.6.1, Connection",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "via",
      statement:
        "Each proxy or gateway that forwards a message appends to Via, so the list follows the forwarding order. A proxy must send Via in each message it forwards; a gateway must send it on inbound requests and may on responses.",
      sourceId: "rfc9110",
      locator: "Section 7.6.3, Via",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "upgrade",
      statement:
        "Upgrade invites a server to switch an HTTP/1.1 connection to another protocol; the server may ignore it. A server sending 101 must name the new protocol in Upgrade and must not switch to a protocol the client did not list.",
      sourceId: "rfc9110",
      locator: "Section 7.8, Upgrade",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "connect",
      statement:
        "CONNECT asks the recipient to open a tunnel to the host and port in the request target and then forward data blindly in both directions. A 2xx means the tunnel is formed after the response header section. Most origin servers do not implement CONNECT.",
      sourceId: "rfc9110",
      locator: "Section 9.3.6, CONNECT",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "gateway-codes",
      statement:
        "502 means a gateway or proxy received an invalid response from an inbound server; 504 means it did not receive a timely response.",
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
        "Clients should not open more than one HTTP/2 connection to a given host and port pair. A client configured to use a proxy over HTTP/2 sends all requests through a single connection to it.",
      sourceId: "rfc9113",
      locator: "Section 9.1, Connection Management; Section 9.1.1, Connection Reuse",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-goaway-refused",
      statement:
        "Requests on streams above a GOAWAY's last stream identifier, or reset with REFUSED_STREAM, were not processed and may be retried automatically, even with non-idempotent methods. RST_STREAM requests cancellation of a stream or signals an error.",
      sourceId: "rfc9113",
      locator: "Section 6.4, RST_STREAM; Section 8.7, Request Reliability",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-ping-idle",
      statement:
        "Idle connections can break because middleboxes such as NATs or load balancers silently discard connection bindings; PING lets a client test a connection without sending a request.",
      sourceId: "rfc9113",
      locator: "Section 8.7, Request Reliability",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "ws-handshake",
      statement:
        "A WebSocket opening handshake is an HTTP/1.1 GET with Upgrade: websocket, Connection: Upgrade, Sec-WebSocket-Key, and Sec-WebSocket-Version 13, answered by 101 Switching Protocols with Sec-WebSocket-Accept, the base64 SHA-1 of the key concatenated with a fixed GUID. After it, each side sends messages, made of frames, independently at will.",
      sourceId: "rfc6455",
      locator: "Section 1.2, Protocol Overview; Section 1.3, Opening Handshake",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ws-design",
      statement:
        "WebSocket is a layer over TCP that adds an origin-based security model, addressing for multiple services and host names, message framing, and an in-band closing handshake designed to work through proxies. Its handshake is a valid HTTP Upgrade request so it can share a port with HTTP; its only relationship to HTTP is that handshake.",
      sourceId: "rfc6455",
      locator: "Section 1.5, Design Philosophy; Section 1.7, Relationship to TCP and HTTP",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ws-masking",
      statement:
        "A client must mask every frame it sends, even over TLS, to avoid confusing intermediaries; a server must close the connection on an unmasked client frame and must not mask its own frames.",
      sourceId: "rfc6455",
      locator: "Section 5.1, Overview",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ws-control",
      statement:
        "Control frames are Close, Ping, and Pong, at most 125 bytes. A Close may carry a 2-byte status code; the receiver replies with Close and then the server closes the TCP connection. An endpoint must answer Ping with Pong, and Ping may serve as a keepalive or liveness check.",
      sourceId: "rfc6455",
      locator: "Sections 5.5, 5.5.1, and 5.5.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ws-over-h2",
      statement:
        "HTTP/2 does not allow Upgrade, Connection, or 101, which the RFC 6455 handshake needs. RFC 8441 extends CONNECT with a :protocol pseudo-header so a WebSocket runs as a tunnel on one HTTP/2 stream, sharing the connection with other streams.",
      sourceId: "rfc8441",
      locator: "Section 1, Introduction; Section 4, The Extended CONNECT Method",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "grpc-mapping",
      statement:
        "A gRPC request is HTTP/2 HEADERS with :method POST, :path /Service/Method, te: trailers, optional grpc-timeout, and content-type application/grpc, followed by length-prefixed messages (a 1-byte compressed flag and a 4-byte length) in DATA frames. DATA frame boundaries have no relation to message boundaries. HTTP/2 stream IDs serve as call identifiers.",
      sourceId: "grpc-protocol-http2",
      locator: "Requests; HTTP2 Transport Mapping, Stream Identification and Data Frames",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "grpc-trailers",
      statement:
        "A gRPC response is headers with :status 200, messages, and trailers carrying grpc-status, which must be sent even when the status is OK. Trailers-Only is permitted for immediate errors. Application or runtime errors are delivered as Status in Trailers.",
      sourceId: "grpc-protocol-http2",
      locator: "Responses; HTTP2 Transport Mapping, Errors",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "grpc-retries",
      statement:
        "gRPC calls are not assumed idempotent unless marked so; calls that cannot be proven to have started are not retried. REFUSED_STREAM maps to UNAVAILABLE and can be retried, possibly elsewhere. After GOAWAY, streams above the last accepted one are UNAVAILABLE and can be retried elsewhere.",
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
        "gRPC uses HTTP/2 PING to check liveness; an expired client PING closes all calls with UNAVAILABLE. On a detected connection failure, client calls close with UNAVAILABLE and server calls with CANCELLED.",
      sourceId: "grpc-protocol-http2",
      locator: "Connection Management, PING Frame and Connection failure",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "grpc-lb-per-call",
      statement:
        "Load balancing in gRPC is per call, not per connection. The default pick_first policy sends all RPCs to the first reachable address; round_robin keeps a subchannel per address and sends successive RPCs to successive READY subchannels.",
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
      note: "Sections 3.7, 7.6, 7.6.1, 7.6.3, 7.8, 9.3.6, 15.6.3, and 15.6.5: intermediaries and forwarding.",
    },
    {
      sourceId: "rfc9112",
      note: "Sections 6.1 and 11.2: framing conflicts and request smuggling.",
    },
    { sourceId: "rfc9113", note: "Sections 6.4, 8.7, 9.1, and 9.1.1: streams, retries, PING." },
    {
      sourceId: "rfc6455",
      note: "Sections 1.2, 1.3, 1.5, 1.7, 5.1, 5.5, 5.5.1, and 5.5.2: WebSockets.",
    },
    { sourceId: "rfc8441", note: "Sections 1 and 4: WebSockets over HTTP/2." },
    { sourceId: "grpc-protocol-http2", note: "gRPC's wire mapping onto HTTP/2, master branch." },
    {
      sourceId: "grpc-load-balancing",
      note: "Per-call load balancing and policies, master branch.",
    },
  ],
} satisfies LessonBody;
