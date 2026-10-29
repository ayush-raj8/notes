import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m08.http-versions",
  status: "complete",
  objectives: [
    "Explain why HTTP/1.1 reuses connections yet returns one response at a time.",
    "Describe HTTP/2 streams, flow control, GOAWAY, and REFUSED_STREAM on one TCP connection.",
    "Explain why one lost TCP segment stalls every HTTP/2 stream, and what HTTP/3 over QUIC removes.",
    "Say how a client negotiates each version, and what it does when QUIC is blocked.",
  ],
  prerequisites: ["m08.request-response"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Clients send many requests to one server, often at once.\n• One connection per request pays TCP + TLS handshakes and slow start every time.\n• One reused connection in strict order lets a slow response block everything behind it.",
      whyItMatters:
        "• Explains most HTTP latency: slow endpoints delaying unrelated calls, connection counts, lossy mobile links.\n• Explains why proxies and load balancers behave differently for HTTP/1.1 and HTTP/2 clients.",
      idea: "• Keep the semantics. Change only the wire mapping.\n• HTTP/1.1: reuse the connection, answer strictly in order.\n• HTTP/2: frames tagged with stream IDs, interleaved on one TCP connection.\n• HTTP/3: one QUIC stream per exchange, so the transport keeps them apart.",
      howItWorks:
        "• HTTP/1.1: persistent by default until `Connection: close`. Each message self-delimits. No request ID, so responses return in request order, even when pipelined.\n• HTTP/2: over TCP, ALPN `h2`. HEADERS + DATA frames per stream; client streams odd. HPACK field compression. Per-stream and connection flow-control windows.\n• HTTP/2 shutdown: GOAWAY or RST_STREAM(REFUSED_STREAM) marks requests that were never processed.\n• HTTP/3: over QUIC, ALPN `h3`. One client bidirectional stream per request. QUIC does ordering, flow control, and encryption per stream. QPACK replaces HPACK.",
      whyItWorks:
        "• Explicit framing makes reuse possible: the receiver knows where each message ends.\n• Stream IDs remove HTTP/1.1's ordering dependency. One connection means one shared congestion controller.\n• QUIC orders each stream separately and retransmits stream data without blocking other streams.",
      limits:
        "• HTTP/2 does not fix TCP head-of-line blocking (RFC 9113 says so). One lost segment stalls all streams.\n• HTTP/3: a lost packet carrying several streams stalls all of them. Loss still shrinks the shared congestion window.\n• QUIC needs UDP. Clients must fall back to TCP.\n• One connection carries everything: its failure fails every in-flight request, and an L4 balancer sees one client as one unit.",
      connections:
        "• Module 5: slow start, retransmission, in-order delivery (why.17).\n• Module 6: QUIC streams, packet numbers, fallback.\n• Next: pooling and caching. Then intermediaries: gRPC on HTTP/2 and balancing long-lived connections.\n• Module 9: the TLS handshake that carries ALPN.",
    },
    {
      type: "comparison",
      id: "versions",
      title: "Same semantics, three wire mappings",
      caption:
        "What each protocol specifies. Real performance also depends on loss, RTT, implementation, and server prioritisation.",
      columns: ["HTTP/1.1", "HTTP/2", "HTTP/3"],
      rows: [
        {
          label: "Transport",
          cells: [
            "TCP, usually + TLS.",
            "TCP. `h2` via ALPN; cleartext needs prior knowledge.",
            "QUIC over UDP, TLS 1.3 inside. `h3` via ALPN.",
          ],
        },
        {
          label: "Format",
          cells: [
            "Text start-line + field lines.",
            "Binary frames. Pseudo-headers (`:method`, `:status`).",
            "Binary frames on QUIC streams.",
          ],
        },
        {
          label: "Concurrency",
          cells: [
            "One response at a time, in order. Parallelism = more connections.",
            "Many streams, up to the peer's SETTINGS_MAX_CONCURRENT_STREAMS.",
            "One stream per request. Servers should allow ≥100.",
          ],
        },
        {
          label: "Field compression",
          cells: ["None.", "HPACK. Needs in-order delivery.", "QPACK. Encoder limits blocking."],
        },
        {
          label: "HOL blocking left",
          cells: [
            "HTTP layer (slow response) plus TCP.",
            "TCP layer only: one loss stalls all streams.",
            "Only streams whose data was lost, plus allowed QPACK blocking.",
          ],
        },
        {
          label: "Graceful close",
          cells: [
            "`Connection: close`. Idle close any time.",
            "GOAWAY with last stream maybe processed.",
            "GOAWAY with a stream ID; that ID and above not processed.",
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
          title: "HTTP/1.1, one connection",
          body: "`GET /a`, wait, `GET /b`, wait... If `/a` takes 800 ms, `/b`–`/f` wait. Pipelining sends all six at once, but responses still return in order. Same wait.",
        },
        {
          title: "HTTP/1.1, six connections",
          body: "Clients open parallel connections. Each pays TCP + TLS and its own slow start, and they compete. RFC 9112 asks clients to be conservative.",
        },
        {
          title: "HTTP/2, six streams",
          body: "One handshake. HEADERS on streams 1, 3, 5, 7, 9, 11 without waiting. Server interleaves DATA as each is ready, so `/b`–`/f` arrive while `/a` is generated. HPACK makes repeated fields cheap.",
        },
        {
          title: "HTTP/2, one loss",
          body: "A segment carrying part of `/c` is lost. Later segments with `/d`–`/f` arrive but wait in the kernel: TCP delivers in order. Every stream waits about one RTT.",
        },
        {
          title: "HTTP/3, six QUIC streams",
          body: "Streams 0, 4, 8, 12, 16, 20. A lost packet with only `/c` data delays only `/c`. If the packet carried `/c` and `/d`, both wait.",
        },
        {
          title: "What did not change",
          body: "Methods, targets, status codes, fields, cache and retry rules. A handler cannot tell the version unless it asks.",
        },
      ],
    },
    {
      type: "example",
      id: "discover-h3",
      title: "Which version did curl get, and how would a client find HTTP/3?",
      setup:
        "`curl` 8.7.1 on macOS, with nghttp2, without HTTP/3, fetched `https://www.cloudflare.com/` (observed October 2026). Header lines are observed; the next-step reasoning is what RFC 9114 allows.",
      walkthrough: [
        "`ALPN: curl offers h2,http/1.1` then `ALPN: server accepted h2`. Version chosen inside TLS. No Upgrade exchange.",
        "Reply: `HTTP/2 103`, then `HTTP/2 200`. 103 is an interim 1xx on the same stream, so a stream can carry several HEADERS.",
        'The response had `alt-svc: h3=":443"; ma=86400`. The origin advertises HTTP/3 on UDP 443 of the same host.',
        "An HTTP/3 client may now try QUIC on UDP 443 with ALPN `h3`. Until that works it keeps using HTTP/2.",
        "If a firewall blocks UDP 443, QUIC never completes. RFC 9114: fall back to TCP-based HTTP. The request does not fail.",
        "An `http://` URI cannot go straight to HTTP/3: that scheme names a TCP port.",
      ],
      result:
        "ALPN picks HTTP/1.1 or HTTP/2. Alt-Svc advertises HTTP/3 for a later QUIC connection. Because UDP may be blocked, HTTP/3 is always an upgrade a client tries, never the only path.",
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
      title: "One connection is a single point of failure",
      body: "If the HTTP/2 or HTTP/3 connection dies, every in-flight request fails together. With GOAWAY first, requests above the last stream ID were not processed and may be retried, even POSTs. Without GOAWAY the client cannot know. A server about to restart should send GOAWAY and finish accepted streams.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "http2-mechanics",
      title: "HTTP/2 production details",
      paragraphs: [
        "Stream IDs: 31-bit, never reused. Client odd, server even, 0 for connection control (SETTINGS, PING, GOAWAY). Exhausted IDs mean a new connection.",
        "SETTINGS_MAX_CONCURRENT_STREAMS is set by each receiver. Exceeding it gets PROTOCOL_ERROR or REFUSED_STREAM; only REFUSED_STREAM allows automatic retry.",
        "Flow control: per stream and per connection, DATA frames only, initial 65,535 bytes. Hop-by-hop, not end to end through a proxy. A window below the bandwidth-delay product caps throughput. Endpoints must read frames promptly or risk deadlock.",
        "HPACK: one dynamic table per direction per connection, starting at 4,096 bytes. Field blocks must decode in send order, which needs TCP's ordering. Hence QPACK for HTTP/3.",
        "RFC 7540's priority tree is deprecated: complex and unevenly implemented.",
        "Connection-specific fields (Connection, Keep-Alive, Proxy-Connection, Transfer-Encoding, Upgrade) make an HTTP/2 message malformed. A 1.1-to-2 proxy must strip them. HTTP/2 has no Upgrade.",
        "PING tests an idle connection without a request. NATs and load balancers may silently drop idle bindings.",
      ],
    },
    {
      type: "expandable",
      id: "http11-edges",
      title: "HTTP/1.1 edge cases",
      paragraphs: [
        "Reuse needs every message self-delimited. A server must read the whole request body or close after responding. A client must read the whole response body to reuse the connection. Abandoning a download often costs the connection.",
        "Bytes arriving with no outstanding request must not be treated as a response. Close the connection.",
        "Either side may close an idle connection any time. A client can send just as the server closes. Retry rules are RFC 9110's (next lesson).",
        "HTTP/1.0 closes after each response unless both use `keep-alive`. A proxy must not keep a persistent connection with an HTTP/1.0 client.",
        "Chunked coding streams unknown-length content and keeps the connection. HTTP/1.1 only; HTTP/2 and HTTP/3 frames do this job.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "slow-endpoint",
          prompt:
            "`GET /report` takes 2 s; `GET /status` is instant; same server. When does `/status` arrive over pipelined HTTP/1.1, and over HTTP/2?",
          answer:
            "HTTP/1.1: after the report, because responses must return in request order. HTTP/2: immediately, on its own stream.",
        },
        {
          id: "loss-h2-h3",
          prompt:
            "40 resources over one HTTP/2 connection on a 2% loss link. Why slow, and what does HTTP/3 change?",
          answer:
            "Each loss makes TCP hold every stream's later bytes until the retransmit, and cuts the shared congestion window. HTTP/3 delays only streams whose data was in the lost packet. The congestion-window cut remains.",
        },
        {
          id: "goaway-retry",
          prompt:
            "Server sends HTTP/2 GOAWAY with Last-Stream-ID 41. The client had POSTs on streams 39, 43, 45. Which may it auto-retry?",
          answer:
            "43 and 45: above 41, so guaranteed unprocessed, and RFC 9113 allows retry even for POST. 39 might have run, so it needs an application-level duplicate check.",
        },
        {
          id: "udp-blocked",
          prompt:
            "A site never uses HTTP/3 on the corporate network but does at home. Nothing breaks. Why, and how did the client learn of HTTP/3?",
          answer:
            'An `Alt-Svc: h3=":443"` header (or ALTSVC frame) advertised it. The corporate firewall likely blocks UDP 443, so QUIC fails and the client stays on TCP, as RFC 9114 says it should.',
        },
      ],
    },
  ],
  claims: [
    {
      id: "h2-intro-hol",
      statement:
        "HTTP/1.1 pipelining only partially addressed concurrency and still suffers application-layer head-of-line blocking, so clients use multiple connections. HTTP/2 interleaves messages on one connection and compresses fields, but TCP head-of-line blocking is not addressed.",
      sourceId: "rfc9113",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-overview-streams",
      statement:
        "HTTP/2 runs over TCP. Each request/response exchange has its own stream; streams are largely independent, so a stalled one does not block others. This revision deprecates the RFC 7540 priority scheme.",
      sourceId: "rfc9113",
      locator: "Section 2, HTTP/2 Protocol Overview",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-negotiation",
      statement:
        "HTTP/2 for https URIs uses TLS with ALPN and the identifier h2. Cleartext HTTP/2 can start with prior knowledge by sending the connection preface.",
      sourceId: "rfc9113",
      locator:
        "Section 3.2, Starting HTTP/2 for https URIs; Section 3.3, Starting HTTP/2 with Prior Knowledge",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-stream-ids",
      statement:
        "Stream IDs are unsigned 31-bit integers; client streams are odd, server streams even, and stream 0 is for connection control. IDs cannot be reused; an endpoint that runs out opens a new connection.",
      sourceId: "rfc9113",
      locator: "Section 5.1.1, Stream Identifiers",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-concurrency",
      statement:
        "SETTINGS_MAX_CONCURRENT_STREAMS limits the streams the peer may open. Exceeding it is a stream error of PROTOCOL_ERROR or REFUSED_STREAM, and the choice decides whether automatic retry is possible.",
      sourceId: "rfc9113",
      locator: "Section 5.1.2, Stream Concurrency",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-flow-control",
      statement:
        "HTTP/2 flow control covers streams and the connection, is hop-by-hop and credit-based through WINDOW_UPDATE, applies only to DATA frames, and starts at 65,535 octets. Without bandwidth-delay awareness it can underuse the network, and endpoints must read frames promptly to avoid deadlock.",
      sourceId: "rfc9113",
      locator: "Sections 5.2, 5.2.1, and 5.2.2",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-hpack-state",
      statement:
        "HTTP/2 field compression is stateful: one HPACK encoder and decoder context per endpoint for all field blocks on a connection, with a dynamic table starting at 4,096 bytes.",
      sourceId: "rfc9113",
      locator: "Section 4.3.1, Compression State",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-goaway",
      statement:
        "GOAWAY stops new streams while finishing earlier ones. It carries the last peer-initiated stream that was or might be processed; higher streams were not processed and can be retried on a new connection. Endpoints should send GOAWAY before closing so the peer knows whether, for example, a POST was processed.",
      sourceId: "rfc9113",
      locator: "Section 6.8, GOAWAY",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-request-reliability",
      statement:
        "GOAWAY and REFUSED_STREAM guarantee a request was not processed; clients may retry such requests automatically, even non-idempotent ones. PING tests an idle connection, because NATs and load balancers may silently discard bindings.",
      sourceId: "rfc9113",
      locator: "Section 8.7, Request Reliability",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-connection-specific",
      statement:
        "HTTP/2 messages must not contain connection-specific fields (Connection, Proxy-Connection, Keep-Alive, Transfer-Encoding, Upgrade); an intermediary converting from HTTP/1.x must remove them. HTTP/2 does not support Upgrade.",
      sourceId: "rfc9113",
      locator: "Section 8.2.2, Connection-Specific Header Fields",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h2-one-connection",
      statement:
        "HTTP/2 connections are persistent. Clients should not open more than one HTTP/2 connection per host and port pair, though they may replace connections; the closing endpoint should send GOAWAY first.",
      sourceId: "rfc9113",
      locator: "Section 9.1, Connection Management",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h11-persistence",
      statement:
        "HTTP/1.1 defaults to persistent connections; the close option ends persistence. Every message must self-delimit. A server must read the whole request body or close; a client must read the whole response body to reuse the connection. A proxy must not keep a persistent connection with an HTTP/1.0 client.",
      sourceId: "rfc9112",
      locator: "Section 9.3, Persistence",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h11-order",
      statement:
        "HTTP/1.1 has no request identifier; responses match requests by order. Data with no outstanding request must not be treated as a response, and the client should close. A server may process pipelined safe requests in parallel but must respond in the order received.",
      sourceId: "rfc9112",
      locator: "Section 9.2, Associating a Response to a Request; Section 9.3.2, Pipelining",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h11-concurrency",
      statement:
        "Clients ought to limit simultaneous connections to a server. Multiple connections avoid head-of-line blocking from slow or large responses, but each costs server resources and many can cause congestion.",
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
        "Chunked coding sends unknown-size content as length-delimited chunks, so the sender keeps the connection persistent and the recipient knows when the message is complete.",
      sourceId: "rfc9112",
      locator: "Section 7.1, Chunked Transfer Coding",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "h3-prior-versions",
      statement:
        "Multiple HTTP/1.1 connections hurt congestion control, which TCP does not share across connections. HTTP/2 multiplexing is invisible to TCP loss recovery, so a lost or reordered packet stalls all active transactions.",
      sourceId: "rfc9114",
      locator: "Section 1.1, Prior Versions of HTTP",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-delegation",
      statement:
        "QUIC provides stream multiplexing, per-stream flow control, stream-level reliability, and connection-wide congestion control, and incorporates TLS 1.3. HTTP/3 uses HTTP/2-like framing on each stream.",
      sourceId: "rfc9114",
      locator: "Section 1.2, Delegation to QUIC",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-overview",
      statement:
        "Each HTTP/3 request-response pair uses one QUIC stream; a blocked or lossy stream does not stop others. HPACK needs in-order delivery, which QUIC does not give across streams, so HTTP/3 uses QPACK.",
      sourceId: "rfc9114",
      locator: "Section 2, HTTP/3 Protocol Overview",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-discovery",
      statement:
        'Without another selection mechanism, ALPN h3 is used. If UDP is blocked and QUIC fails, clients should use TCP-based HTTP. An origin advertises HTTP/3 with Alt-Svc, e.g. `h3=":50781"`. HTTP/3 cannot be used directly for http URIs.',
      sourceId: "rfc9114",
      locator: "Sections 3.1, 3.1.1, and 3.1.2",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-reuse",
      statement:
        "HTTP/3 connections are persistent. Clients should not open more than one HTTP/3 connection to a given IP address and UDP port with the same configuration.",
      sourceId: "rfc9114",
      locator: "Section 3.3, Connection Reuse",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-request-stream",
      statement:
        "Each request uses one client-initiated bidirectional QUIC stream, followed by zero or more interim and one final response. The first request is on stream 0, then 4, 8, and so on; servers should permit at least 100 request streams.",
      sourceId: "rfc9114",
      locator: "Section 4.1, HTTP Message Framing; Section 6.1, Bidirectional Streams",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-qpack",
      statement:
        "QPACK gives the encoder control over how much head-of-line blocking compression can cause.",
      sourceId: "rfc9114",
      locator: "Section 4.2.1, Field Compression",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "h3-goaway",
      statement:
        "An HTTP/3 server's GOAWAY carries a client bidirectional stream ID; requests with that ID or greater were not processed and can be retried on another connection. No new requests after GOAWAY.",
      sourceId: "rfc9114",
      locator: "Section 5.2, Connection Shutdown",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-stream-ordering",
      statement:
        "QUIC does not order bytes across streams. Each stream is delivered as an ordered byte stream, buffering out-of-order data up to the flow-control limit.",
      sourceId: "rfc9000",
      locator: "Section 2, Streams; Section 2.2, Sending and Receiving Data",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-over-udp",
      statement:
        "QUIC packets are carried in UDP datagrams to ease deployment in existing systems and networks, and the QUIC handshake integrates TLS.",
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
      note: "Sections 1, 2, 3.2, 3.3, 4.3.1, 5.1.1, 5.1.2, 5.2–5.2.2, 5.3.1, 6.8, 8.2.2, 8.7, and 9.1.",
    },
    {
      sourceId: "rfc9112",
      note: "Sections 7.1, 9.2, 9.3, 9.3.2, 9.4, and 9.5: persistence, ordering, connections.",
    },
    {
      sourceId: "rfc9114",
      note: "Sections 1.1, 1.2, 2, 3.1–3.1.2, 3.3, 4.1, 4.2.1, 5.2, and 6.1.",
    },
    { sourceId: "rfc9000", note: "Sections 1, 2, and 2.2: UDP and per-stream ordering." },
  ],
} satisfies LessonBody;
