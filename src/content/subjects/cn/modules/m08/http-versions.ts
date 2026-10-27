import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m08.http-versions",
  status: "complete",
  objectives: [
    "Explain why HTTP/1.1 keeps connections open, and why one connection still carries only one response at a time.",
    "Describe how HTTP/2 multiplexes requests as streams of frames on one TCP connection, and what flow control, GOAWAY, and REFUSED_STREAM add.",
    "Explain why one lost TCP segment stalls every HTTP/2 stream, and which part of that problem HTTP/3 over QUIC removes.",
    "Say how a client discovers and negotiates each version, and what it must do when QUIC is blocked.",
  ],
  prerequisites: ["m08.request-response"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "A web page or an API client rarely sends one request. It sends dozens, often at once, to the same server. HTTP's messages say nothing about how they share a transport, and the obvious mapping, one connection per request, pays a TCP handshake, a TLS handshake, and a fresh slow-start window every time. Keeping one connection and sending requests one after another avoids the setup, but then a slow response holds up every request behind it.",
      whyItMatters:
        "This is where most HTTP latency and many confusing failures come from. Whether a slow endpoint delays unrelated calls, whether a burst of requests needs ten connections or one, why a lossy mobile link hurts HTTP/2 more than expected, and why a proxy or load balancer behaves differently for HTTP/1.1 and HTTP/2 clients all follow from how each version maps messages onto the transport.",
      idea: "Keep the semantics from the previous lesson fixed and change only the wire mapping. HTTP/1.1 reuses a connection but answers requests strictly in order. HTTP/2 splits each request and response into frames tagged with a stream number, so many exchanges interleave on one TCP connection. HTTP/3 gives each exchange its own QUIC stream, so the transport itself keeps exchanges apart and a lost packet delays only the streams whose data it carried.",
      howItWorks:
        "HTTP/1.1 connections persist by default until either side sends `Connection: close`. Each message must carry its own length, through Content-Length or chunked coding, so the next message can start where it ends. There is no request identifier: responses come back in request order, and pipelining several requests does not change that order. HTTP/2 runs over TCP, negotiated as `h2` with ALPN during TLS. Every message becomes a HEADERS frame and DATA frames on a stream; client streams have odd numbers. Frames from many streams interleave, fields are compressed with HPACK, each stream and the connection have a credit-based flow-control window, and GOAWAY or RST_STREAM with REFUSED_STREAM tells the client which requests were never processed. HTTP/3 runs over QUIC, negotiated as `h3`. Each request and response uses one client-initiated bidirectional QUIC stream, QUIC provides the ordering, flow control, and encryption per stream, and QPACK replaces HPACK because QUIC does not deliver streams in order relative to each other.",
      whyItWorks:
        "Explicit framing is what makes reuse possible at all: the receiver knows where one message ends without the connection closing. Stream numbers remove HTTP/1.1's dependence on arrival order, so a slow response no longer blocks the ones behind it at the HTTP layer, and one connection shares one congestion controller instead of several connections competing. QUIC removes the last shared ordering: TCP has one sequence space for the whole connection and must deliver bytes in order, while QUIC orders each stream separately and retransmits stream data without blocking other streams.",
      limits:
        "HTTP/2 does not fix TCP head-of-line blocking; RFC 9113 says so directly. A single lost segment holds back every stream's bytes behind it in the kernel until the retransmission arrives. HTTP/3 removes that, but one QUIC packet that carries data for several streams still stalls all of them, and loss still shrinks the shared congestion window for everyone. QUIC needs UDP to pass, so clients must fall back to TCP. Multiplexing also concentrates risk: one connection carries everything, so a connection failure fails every in-flight request at once, and a load balancer that balances connections sees one client as one unit of load.",
      connections:
        "Module 5 explained the TCP machinery underneath HTTP/1.1 and HTTP/2: slow start, retransmission, and in-order delivery (why.17). Module 6 introduced QUIC's streams, packet numbers, and fallback. The next lesson covers what reuse buys and costs in a connection pool. The intermediaries lesson shows why gRPC builds on HTTP/2 and why long-lived multiplexed connections complicate load balancing. Module 9 covers the TLS handshake that ALPN rides on.",
    },
    {
      type: "comparison",
      id: "versions",
      title: "The three wire mappings of the same semantics",
      caption:
        "Each row compares what the protocol specifies. Real performance also depends on loss, RTT, implementation, and server prioritization.",
      columns: ["HTTP/1.1", "HTTP/2", "HTTP/3"],
      rows: [
        {
          label: "Transport",
          cells: [
            "TCP, usually with TLS. One HTTP connection is one transport connection.",
            "TCP. Over TLS it is negotiated as `h2` with ALPN; cleartext `h2c` needs prior knowledge.",
            "QUIC over UDP, with TLS 1.3 inside QUIC. Negotiated as `h3` with ALPN.",
          ],
        },
        {
          label: "Message format",
          cells: [
            "Text start-line and field lines, ended by an empty line.",
            "Binary frames. Control data travels as pseudo-header fields such as `:method` and `:status`.",
            "Binary frames on QUIC streams, with the same pseudo-header fields.",
          ],
        },
        {
          label: "Concurrency on one connection",
          cells: [
            "One response at a time, in request order. Clients open several connections for parallelism.",
            "Many streams interleaved, up to the peer's SETTINGS_MAX_CONCURRENT_STREAMS.",
            "One QUIC stream per request; servers should permit at least 100 at a time.",
          ],
        },
        {
          label: "Field compression",
          cells: [
            "None for header fields.",
            "HPACK, a dynamic table per connection that depends on in-order delivery.",
            "QPACK, which lets the encoder limit how much compression can cause blocking.",
          ],
        },
        {
          label: "Head-of-line blocking left",
          cells: [
            "At the HTTP layer: a slow or large response delays every later response on the connection. Plus TCP's.",
            "At the TCP layer only: one lost segment stalls every stream until it is retransmitted.",
            "Only for streams whose data was in the lost packet, plus any QPACK blocking the encoder allows.",
          ],
        },
        {
          label: "Graceful shutdown",
          cells: [
            "`Connection: close` on a message. An idle connection can be closed at any time.",
            "GOAWAY with the last stream the server might have processed.",
            "GOAWAY with a stream ID; requests at or above it were not processed.",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "same-page-three-ways",
      title: "Six requests to one server, three ways",
      steps: [
        {
          title: "HTTP/1.1 on one connection",
          body: "The client sends `GET /a`, waits for the whole response, then sends `GET /b`, and so on. If `/a` takes 800 ms to generate, `/b` through `/f` wait behind it even if they are ready at once. Pipelining lets the client send all six immediately, but the server must still return responses in the order the requests arrived, so the wait is the same.",
        },
        {
          title: "HTTP/1.1 on six connections",
          body: "Browsers and HTTP clients work around this by opening several connections to the same server. Each pays its own TCP and TLS handshake and starts with its own small congestion window, and together they compete with each other and with other flows. RFC 9112 asks clients to be conservative about how many they open.",
        },
        {
          title: "HTTP/2: six streams, one connection",
          body: "After one handshake, the client sends six HEADERS frames on streams 1, 3, 5, 7, 9, and 11 without waiting. The server answers each as it is ready and interleaves DATA frames, so `/b` through `/f` arrive while `/a` is still being generated. Repeated fields such as cookies and user-agent cost little after the first request, because HPACK refers to its dynamic table.",
        },
        {
          title: "HTTP/2 under one loss",
          body: "Now one TCP segment carrying part of `/c` is lost. The segments behind it, carrying bytes of `/d`, `/e`, and `/f`, arrive but sit in the kernel's receive buffer: TCP delivers bytes to the application only in order. Every stream waits about one round trip for the retransmission, though only `/c` lost anything.",
        },
        {
          title: "HTTP/3: six QUIC streams",
          body: "The client opens bidirectional QUIC streams 0, 4, 8, 12, 16, and 20. If one packet carrying only `/c` data is lost, QUIC delivers the other streams' data as it arrives and retransmits `/c`'s bytes in a later packet. Only `/c` waits. If the lost packet carried frames for `/c` and `/d`, both wait.",
        },
        {
          title: "What did not change",
          body: "All three exchanges have the same methods, targets, status codes, and fields. A server-side handler cannot tell which version carried the request unless it asks, and caches, retries, and status codes follow the same rules from the previous lesson.",
        },
      ],
    },
    {
      type: "example",
      id: "discover-h3",
      title: "Reading which version a client got, and how it would find HTTP/3",
      setup:
        "`curl` 8.7.1 on macOS, built with nghttp2 but without HTTP/3 support, fetched `https://www.cloudflare.com/` and `https://www.google.com/` in October 2026. The header lines below are observed; the reasoning about a browser's next step is what RFC 9114 allows.",
      walkthrough: [
        "curl reported `negotiated=2` for both. `curl -v` showed why: `ALPN: curl offers h2,http/1.1` and `ALPN: server accepted h2`, inside the TLS handshake. No HTTP Upgrade exchange happened. HTTP/2 over TLS must be negotiated this way.",
        "Cloudflare's reply began `HTTP/2 103` and then `HTTP/2 200`. The 103 is an interim 1xx response on the same stream, before the final one. An HTTP/2 client has to accept several HEADERS frames on one stream for exactly this reason.",
        "Both responses carried `alt-svc: h3=\":443\"`, with `ma=86400` and `ma=2592000` seconds. This is how an origin advertises that an equivalent HTTP/3 endpoint exists, here on UDP port 443 of the same host. An `http://` URI cannot go straight to HTTP/3, because that scheme names a TCP port.",
        "A client that supports HTTP/3 may now open a QUIC connection to that host on UDP 443, offering `h3` in ALPN, and use it for later requests. Until the QUIC connection succeeds, it keeps using the HTTP/2 connection it already has.",
        "If UDP to port 443 is blocked by a corporate firewall, the QUIC handshake never completes. RFC 9114 says clients should then use a TCP-based version. A well-behaved client does not fail the request; it stays on HTTP/2.",
        "On the server, the handlers saw the same request either way. What changes is visible only to the transport: one TCP connection with HPACK and stream IDs 1, 3, 5, or one QUIC connection with stream IDs 0, 4, 8 and QPACK.",
      ],
      result:
        "Version selection happens below the application: ALPN in the TLS handshake picks HTTP/1.1 or HTTP/2, and Alt-Svc advertises HTTP/3 for a later QUIC connection. Because QUIC may be blocked, HTTP/3 is always an upgrade a client tries, never the only path.",
    },
    {
      type: "why",
      id: "why-http2-multiplexing",
      questionId: "why.27",
    },
    {
      type: "why",
      id: "why-http2-still-hol",
      questionId: "why.28",
    },
    {
      type: "why",
      id: "why-http3-quic",
      questionId: "why.29",
    },
    {
      type: "callout",
      id: "multiplexing-not-free",
      tone: "caution",
      title: "One connection is now a single point of failure",
      body: "With HTTP/2 or HTTP/3, a client typically sends everything to one server over one connection. If that connection dies, every in-flight request on it fails together. If the server sends GOAWAY first, the Last-Stream-ID tells the client which requests were never processed and can be retried on a new connection, even POSTs; without GOAWAY, the client cannot know. A server that is about to restart should send GOAWAY and finish the streams it accepted.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "http2-mechanics",
      title: "HTTP/2 details that matter in production",
      paragraphs: [
        "Stream identifiers are 31-bit and never reused. Client streams are odd, server streams are even, and stream 0 is for connection control frames such as SETTINGS, PING, and GOAWAY. A very long-lived connection can run out of identifiers, and the fix is simply a new connection.",
        "SETTINGS_MAX_CONCURRENT_STREAMS is set by each receiver for the streams its peer may open. A server that lowers it below the client's open streams can let them finish or reset the extras. A stream refused for exceeding the limit can be retried if the error is REFUSED_STREAM.",
        "Flow control is per stream and per connection, applies only to DATA frames, and starts at 65,535 bytes for both. It is hop-by-hop, between the two ends of one HTTP/2 connection, not end to end through a proxy. If the window is smaller than the bandwidth-delay product, throughput is capped by flow control, not by the network. Endpoints must read frames from the socket promptly, or WINDOW_UPDATE frames can go unread and the connection can deadlock.",
        "HPACK's dynamic table starts at 4,096 bytes per direction. Because one table is shared by the whole connection, field blocks must be decoded in the order they were sent, which works only because TCP delivers in order. This is why HTTP/3 needed a different scheme.",
        "The priority tree from RFC 7540 was deprecated in RFC 9113 because it was complex and unevenly implemented. Servers should use other context when clients send no priority signals; a newer scheme exists but is not covered here.",
        "HTTP/2 forbids connection-specific fields: Connection, Keep-Alive, Proxy-Connection, Transfer-Encoding, and Upgrade make a message malformed. A proxy converting HTTP/1.1 to HTTP/2 must strip them. HTTP/2 also does not support Upgrade to another protocol on the same connection, which is why WebSockets over HTTP/2 needed a separate mechanism, covered in the intermediaries lesson.",
        "PING lets a client check an idle connection without sending a request. RFC 9113 notes that NATs and load balancers may silently discard idle connection bindings, so an idle HTTP/2 connection can be dead without either side knowing.",
      ],
    },
    {
      type: "expandable",
      id: "http11-edges",
      title: "HTTP/1.1 edge cases",
      paragraphs: [
        "Persistence needs every message to have a self-defined length. A server must read the entire request body or close the connection after responding; otherwise the leftover bytes would be parsed as the next request. A client must read the whole response body to reuse the connection. This is why abandoning a large download often costs the connection.",
        "A client that receives bytes on a connection with no outstanding request must not treat them as a response and should close the connection, because the message boundaries are now ambiguous.",
        "Any side may close an idle connection at any time, and a client may start a new request at the same moment the server closes. From the server's side the connection was idle; from the client's side a request was lost. RFC 9110's retry rules decide whether the client may resend it, which is the subject of the next lesson.",
        "HTTP/1.0 connections close after each response unless both sides use the old `keep-alive` option, and a proxy must not keep a persistent connection with an HTTP/1.0 client.",
        "Chunked transfer coding sends content of unknown length as length-prefixed chunks ending with a zero-length chunk, so a server can stream a response and still keep the connection. It is an HTTP/1.1 feature only; HTTP/2 and HTTP/3 frames do that job.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "slow-endpoint",
          prompt:
            "A client sends `GET /report` (takes 2 seconds to generate) and then `GET /status` (ready immediately) to the same server. Compare when `/status` arrives over one HTTP/1.1 connection with pipelining, and over one HTTP/2 connection.",
          answer:
            "With HTTP/1.1, even pipelined, the server must send responses in the order the requests arrived, and the client matches responses to requests only by order. `/status` arrives after the 2-second report. With HTTP/2, each request is its own stream, so the server can send `/status` on its stream immediately while `/report` is still being generated. The fix for HTTP/1.1 is a second connection, which is what clients do.",
        },
        {
          id: "loss-h2-h3",
          prompt:
            "On a link with 2% packet loss, a page loads 40 resources over one HTTP/2 connection. Explain why it can be slower than expected, and what HTTP/3 changes and does not change.",
          answer:
            "All 40 streams share one TCP byte stream. Each lost segment makes TCP hold every later byte, from every stream, in the receive buffer until the retransmission arrives, so unrelated resources stall for a round trip or more. Each loss also cuts the one shared congestion window. HTTP/3 puts each request on its own QUIC stream, and QUIC delivers each stream in order independently, so a loss delays only the streams whose data was in that packet. It does not remove the congestion-window reduction, and a packet carrying several streams' data still stalls all of them.",
        },
        {
          id: "goaway-retry",
          prompt:
            "A server restarting for a deploy sends an HTTP/2 GOAWAY with Last-Stream-ID 41. The client had already sent POST requests on streams 39, 43, and 45. Which can it retry automatically?",
          answer:
            "Streams 43 and 45: their identifiers are above the Last-Stream-ID, so the server guarantees it did not process them, and RFC 9113 allows the client to retry them on a new connection even though POST is not idempotent. Stream 39 might have been processed. The server should finish it; if the connection closes before its response arrives, the client is back to the ordinary non-idempotent retry problem and needs an application-level way to detect a duplicate.",
        },
        {
          id: "udp-blocked",
          prompt:
            "Users on a corporate network report that a site works but never uses HTTP/3, while at home it does. Nothing is broken. What is the most likely explanation, and how did the client learn about HTTP/3 in the first place?",
          answer:
            "The site advertised HTTP/3 with an `Alt-Svc: h3=\":443\"` header (or an ALTSVC frame) on an HTTP/1.1 or HTTP/2 response. The client then tried a QUIC connection on UDP 443. On the corporate network a firewall most likely blocks or drops UDP to port 443, so the QUIC handshake fails, and RFC 9114 says the client should fall back to TCP-based HTTP. At home UDP passes. Requests succeed either way because the semantics are identical.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "h2-intro-hol",
      statement:
        "HTTP/1.0 allowed one outstanding request per TCP connection. HTTP/1.1 pipelining only partially addressed concurrency and still suffers application-layer head-of-line blocking, so clients use multiple connections. HTTP/2 interleaves messages on one connection and compresses fields, so fewer TCP connections are needed, but TCP head-of-line blocking is not addressed.",
      sourceId: "rfc9113",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-overview-streams",
      statement:
        "HTTP/2 runs over TCP. Each request/response exchange has its own stream; streams are largely independent, so a blocked or stalled request or response does not prevent progress on other streams. Multiplexing depends on flow control and prioritization, and this revision deprecates the RFC 7540 priority scheme.",
      sourceId: "rfc9113",
      locator: "Section 2, HTTP/2 Protocol Overview",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-negotiation",
      statement:
        "HTTP/2 over TLS uses ALPN with the identifier h2. Cleartext HTTP/2 can be started with prior knowledge by sending the connection preface; HTTP/2 over TLS must use ALPN.",
      sourceId: "rfc9113",
      locator: "Section 3.2, Starting HTTP/2 for https URIs; Section 3.3, Starting HTTP/2 with Prior Knowledge",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-stream-ids",
      statement:
        "Stream identifiers are unsigned 31-bit integers; client streams are odd, server streams even, and stream 0 is for connection control. Identifiers cannot be reused, and an endpoint that runs out opens a new connection.",
      sourceId: "rfc9113",
      locator: "Section 5, Streams and Multiplexing; Section 5.1.1, Stream Identifiers",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-concurrency",
      statement:
        "SETTINGS_MAX_CONCURRENT_STREAMS limits the streams the peer may open. Exceeding it is a stream error of type PROTOCOL_ERROR or REFUSED_STREAM, and the choice determines whether automatic retry is possible.",
      sourceId: "rfc9113",
      locator: "Section 5.1.2, Stream Concurrency",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-flow-control",
      statement:
        "HTTP/2 flow control applies to streams and the whole connection, is hop-by-hop, credit-based through WINDOW_UPDATE, applies only to DATA frames, and starts at 65,535 octets. A window smaller than the bandwidth-delay product limits throughput, and endpoints must read frames promptly to avoid deadlock.",
      sourceId: "rfc9113",
      locator: "Sections 5.2, 5.2.1, 5.2.2, and 5.2.3",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-priority-deprecated",
      statement:
        "The RFC 7540 priority signaling proved complex and unevenly implemented and is deprecated. Servers should use other contextual information in the absence of priority signals.",
      sourceId: "rfc9113",
      locator: "Sections 5.3.1 and 5.3.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "h2-hpack-state",
      statement:
        "HTTP/2 field compression is stateful: each endpoint has one HPACK encoder and decoder context for all field blocks on a connection, with a dynamic table that starts at 4,096 bytes.",
      sourceId: "rfc9113",
      locator: "Section 4.3, Field Section Compression; Section 4.3.1, Compression State",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-goaway",
      statement:
        "GOAWAY lets an endpoint stop accepting new streams while finishing earlier ones. It carries the last peer-initiated stream that was or might be processed; higher streams were not processed and can be retried on a new connection. Endpoints should send GOAWAY before closing so the peer knows whether, for example, a POST was processed.",
      sourceId: "rfc9113",
      locator: "Section 6.8, GOAWAY",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-request-reliability",
      statement:
        "GOAWAY and REFUSED_STREAM guarantee a request was not processed, and clients may retry such requests automatically even with non-idempotent methods. PING lets a client test an idle connection, because middleboxes such as NATs and load balancers may silently discard connection bindings.",
      sourceId: "rfc9113",
      locator: "Section 8.7, Request Reliability",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-connection-specific",
      statement:
        "HTTP/2 messages must not contain connection-specific fields such as Connection, Keep-Alive, Proxy-Connection, Transfer-Encoding, and Upgrade; an intermediary converting from HTTP/1.x must remove them. HTTP/2 does not support Upgrade to another protocol.",
      sourceId: "rfc9113",
      locator: "Section 8.2.2, Connection-Specific Header Fields",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-one-connection",
      statement:
        "HTTP/2 connections are persistent. Clients should not open more than one HTTP/2 connection to a given host and port pair, though they may replace connections, and the terminating endpoint should send GOAWAY before closing. A connection may be reused for other origins when the server is authoritative, which for https requires a certificate valid for the new host.",
      sourceId: "rfc9113",
      locator: "Section 9.1, Connection Management; Section 9.1.1, Connection Reuse",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h11-persistence",
      statement:
        "HTTP/1.1 defaults to persistent connections; the close option ends persistence. Every message must have a self-defined length to remain persistent; a server must read the whole request body or close the connection, and a client must read the whole response body to reuse it. A proxy must not keep a persistent connection with an HTTP/1.0 client.",
      sourceId: "rfc9112",
      locator: "Section 9.3, Persistence",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h11-order",
      statement:
        "HTTP/1.1 has no request identifier, so responses are matched to requests by order on the connection. Data received with no outstanding request must not be treated as a response, and the client should close the connection. A server may process pipelined safe requests in parallel but must send responses in the order received.",
      sourceId: "rfc9112",
      locator: "Section 9.2, Associating a Response to a Request; Section 9.3.2, Pipelining",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h11-concurrency",
      statement:
        "Clients ought to limit simultaneous connections to a server. Multiple connections are used to avoid head-of-line blocking from a slow or large response, but each consumes server resources and many connections can cause congestion.",
      sourceId: "rfc9112",
      locator: "Section 9.4, Concurrency",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h11-close-race",
      statement:
        "A client, server, or proxy may close the transport connection at any time, so a client can start a request just as the server closes the idle connection.",
      sourceId: "rfc9112",
      locator: "Section 9.5, Failures and Timeouts",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h11-chunked",
      statement:
        "Chunked transfer coding sends content of unknown size as length-delimited chunks ending with a zero-size chunk, so the sender keeps the connection persistent and the recipient knows when the message is complete.",
      sourceId: "rfc9112",
      locator: "Section 7.1, Chunked Transfer Coding",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "h3-prior-versions",
      statement:
        "Because HTTP/1.1 has no multiplexing layer, multiple TCP connections are often used, which hurts congestion control because TCP does not share it across connections. HTTP/2's multiplexing is not visible to TCP's loss recovery, so a lost or reordered packet stalls all active transactions.",
      sourceId: "rfc9114",
      locator: "Section 1.1, Prior Versions of HTTP",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-delegation",
      statement:
        "QUIC provides stream multiplexing, per-stream flow control, stream-level reliability, and connection-wide congestion control, and incorporates TLS 1.3. HTTP/3 maps HTTP semantics onto QUIC with HTTP/2-like framing on each stream.",
      sourceId: "rfc9114",
      locator: "Section 1.2, Delegation to QUIC",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-overview",
      statement:
        "Each HTTP/3 request-response pair consumes one QUIC stream; a stream that is blocked or loses packets does not prevent progress on others. HPACK relies on in-order transmission, which QUIC does not provide across streams, so HTTP/3 uses QPACK.",
      sourceId: "rfc9114",
      locator: "Section 2, HTTP/3 Protocol Overview",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-discovery",
      statement:
        "Unless another mechanism selects HTTP/3, the ALPN token h3 is used. If UDP is blocked and QUIC fails, clients should use TCP-based HTTP. An origin can advertise HTTP/3 with Alt-Svc, for example `h3=\":50781\"`. HTTP/3 cannot be used directly for http URIs.",
      sourceId: "rfc9114",
      locator: "Section 3.1, Discovering an HTTP/3 Endpoint; Sections 3.1.1 and 3.1.2",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-reuse",
      statement:
        "HTTP/3 connections are persistent. Clients should not open more than one HTTP/3 connection to a given IP address and UDP port, though they may use different transport or TLS configurations, and the closing endpoint should first send GOAWAY.",
      sourceId: "rfc9114",
      locator: "Section 3.3, Connection Reuse",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-request-stream",
      statement:
        "A request is sent on a client-initiated bidirectional QUIC stream, one request per stream, followed by zero or more interim and one final response. The first request uses stream 0, then 4, 8, and so on, and servers should permit at least 100 request streams at a time.",
      sourceId: "rfc9114",
      locator: "Section 4.1, HTTP Message Framing; Section 6.1, Bidirectional Streams",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-qpack",
      statement:
        "QPACK gives an encoder control over how much head-of-line blocking compression can cause, trading compression efficiency against latency.",
      sourceId: "rfc9114",
      locator: "Section 4.2.1, Field Compression",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-goaway",
      statement:
        "An HTTP/3 server's GOAWAY carries a client bidirectional stream ID; requests with that ID or greater were not processed and can be retried safely on another connection. A client must not start new requests after receiving GOAWAY.",
      sourceId: "rfc9114",
      locator: "Section 5.2, Connection Shutdown",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-stream-ordering",
      statement:
        "QUIC provides no ordering between bytes on different streams. Each stream is delivered as an ordered byte stream, with out-of-order data buffered up to the flow-control limit.",
      sourceId: "rfc9000",
      locator: "Section 2, Streams; Section 2.2, Sending and Receiving Data",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-over-udp",
      statement:
        "QUIC packets are carried in UDP datagrams to better facilitate deployment in existing systems and networks, and the QUIC handshake integrates TLS.",
      sourceId: "rfc9000",
      locator: "Section 1, Overview",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc9113",
      note: "Sections 1, 2, 3.2, 3.3, 4.3, 4.3.1, 5, 5.1.1, 5.1.2, 5.2–5.2.3, 5.3.1, 5.3.2, 6.8, 8.2.2, 8.7, 9.1, and 9.1.1.",
    },
    {
      sourceId: "rfc9112",
      note: "Sections 7.1, 9.2, 9.3, 9.3.2, 9.4, and 9.5: persistence, ordering, and connections.",
    },
    {
      sourceId: "rfc9114",
      note: "Sections 1.1, 1.2, 2, 3.1, 3.1.1, 3.1.2, 3.3, 4.1, 4.2.1, 5.2, and 6.1.",
    },
    { sourceId: "rfc9000", note: "Sections 1, 2, and 2.2: why UDP, and per-stream ordering." },
  ],
} satisfies LessonBody;
