import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m08.reuse-and-cache",
  status: "complete",
  objectives: [
    "Say what a pooled connection saves, and why it can fail on first use after idling.",
    "Tell content coding from transfer coding, and say why compression needs Vary and changes the ETag.",
    "Decide whether a cache may reuse a response from Cache-Control, age, and Vary.",
    "Follow a stale entry to a 304, and say what no-cache, no-store, private, and s-maxage change.",
  ],
  prerequisites: ["m08.http-versions"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Most HTTP traffic repeats: same backend, same stylesheet, same product list.\n• Doing it all again costs a new connection, the full body, and the origin's work.",
      whyItMatters:
        "• Reuse is most of the gap between a fast and a slow service, and most of what a CDN does.\n• Common bugs: resets from a stale pooled connection, one user's page cached for another, a deploy hidden by `max-age=3600`.",
      idea: "• Reuse the connection, while framing is intact and both sides think it is open.\n• Reuse fewer bytes: send a compressed representation the client accepts.\n• Reuse the whole response while it is fresh, or after a cheap conditional check.",
      howItWorks:
        "• Pool: idle connections per origin, taken for the next request. HTTP/1.1 needs one per in-flight request; HTTP/2 and HTTP/3 need one per origin.\n• Compression: `Accept-Encoding: gzip, br` → `Content-Encoding: gzip` + `Vary: Accept-Encoding`.\n• Freshness: `Cache-Control: max-age=N` (or `s-maxage` for shared caches) plus a validator (ETag or Last-Modified).\n• Fresh: serve without asking. Stale: send `If-None-Match`. Origin answers 304 (no body) or 200 (new body).",
      whyItWorks:
        "• A reused connection skips TCP and TLS handshakes. If recently busy, it also keeps its grown congestion window.\n• Content coding belongs to the representation, so a cache can store and serve the compressed bytes.\n• Only the origin sets freshness and validators, so every cache decides alike without coordinating. A 304 costs a round trip, not a body.",
      limits:
        "• A pooled connection may already be closed by the server, a NAT, or a balancer. Only idempotent requests are safe to retry.\n• Idle longer than one RTO: TCP should restart from a small window. Handshakes saved, slow start not.\n• Caches are never pushed updates. Unsafe requests invalidate only caches they pass.\n• Compressing secrets next to attacker text can leak them by size.\n• Caches are optional and vary. Response directives are all you control.",
      connections:
        "• Module 7 DNS caching: same trade, TTL instead of max-age, no conditional request (why.22).\n• Previous lesson: why HTTP/2 wants one connection per origin.\n• Next: the proxies and CDNs that apply these rules. Module 11: pools under partial failure.",
    },
    {
      type: "comparison",
      id: "cache-directives",
      title: "Response directives that decide reuse",
      caption:
        "Shared cache: serves many users (CDN, reverse proxy). Private cache: one user agent (browser).",
      columns: ["Who may store", "Reuse without asking", "Typical use"],
      rows: [
        {
          label: "max-age=N",
          cells: [
            "Any cache.",
            "Until age > N.",
            "Versioned assets (long N); API reads (short N).",
          ],
        },
        {
          label: "s-maxage=N",
          cells: [
            "Shared caches use it over max-age.",
            "Until age > N; then must revalidate.",
            "CDN lifetime different from browsers.",
          ],
        },
        {
          label: "no-cache",
          cells: ["Any cache.", "Never. Validate first.", "Must be current; 304s save bytes."],
        },
        {
          label: "no-store",
          cells: ["None.", "Never.", "Sensitive data. Not a privacy guarantee against bad caches."],
        },
        {
          label: "private",
          cells: ["Private caches only.", "Per freshness, in that cache.", "Per-user pages."],
        },
        {
          label: "must-revalidate",
          cells: [
            "Any cache.",
            "While fresh. Stale: never without validation, even when disconnected.",
            "Where stale data breaks correctness.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "catalog-cache",
      title: "A product list behind a CDN, over ten minutes",
      setup:
        '`GET /products` on `api.example.com`. Origin sends `Cache-Control: max-age=60, s-maxage=300`, `ETag: "v17"`, `Content-Encoding: gzip`, `Vary: Accept-Encoding`. Browser → CDN (shared) → origin. Times in seconds; names are documentation values.',
      walkthrough: [
        "t=0. Both caches empty. CDN forwards with `Accept-Encoding: gzip, br`, stores the gzip response keyed by URI + that Accept-Encoding, returns it. Browser stores it too.",
        "t=30. Browser copy age 30 < 60: fresh. No request at all.",
        't=90. Browser copy stale. Sends `If-None-Match: "v17"`. CDN copy age 90 < 300: fresh there. ETag matches, so the CDN answers 304 itself. Origin sees nothing.',
        "t=200. A client sends `Accept-Encoding: identity`. Vary values differ, so the CDN forwards and stores a second, uncompressed copy.",
        't=320. CDN gzip copy age 320 > 300. s-maxage forces revalidation: `If-None-Match: "v17"` to origin. Unchanged, so 304. Fresh for another 300 s.',
        "t=400. Admin `PUT /products/42` through the CDN, 200. The CDN must invalidate `/products/42`, not `/products`. Browsers never saw the PUT. Clients may see the old list until expiry.",
        't=401. Origin now has `ETag: "v18"`. The next `"v17"` check that reaches it gets 200 with the new body.',
      ],
      result:
        "Freshness lets each cache decide alone. Validation makes stale cost a 304. Staleness is bounded by max-age and s-maxage, not by when data changes. For instant visibility, use `no-cache` or change the URL.",
    },
    {
      type: "steps",
      id: "observed-304",
      title: "A conditional request, observed",
      steps: [
        {
          title: "Compressed response",
          body: '`curl --compressed -D - https://www.rfc-editor.org/rfc/rfc9111.txt` → `HTTP/2 200`, `cache-control: public, max-age=86400`, `content-encoding: gzip`, `etag: "f8907618635703edd228aee4aa834eee"` (observed October 2026).',
        },
        {
          title: "Uncompressed response",
          body: 'Without `--compressed`: `W/"f8907618635703edd228aee4aa834eee"`. Same tag, marked weak. RFC 9110: one validator for gzip and uncoded forms is weak by definition.',
        },
        {
          title: "Conditional request",
          body: "That weak tag in `If-None-Match` → `HTTP/2 304`, zero body bytes. If-None-Match uses weak comparison, so weak tags validate caches.",
        },
      ],
    },
    {
      type: "why",
      id: "why-pooling",
      questionId: "why.30",
    },
    {
      type: "callout",
      id: "stale-pool",
      tone: "caution",
      title: "The idle-connection race",
      body: "Pool keeps idle connections 90 s; server closes them at 60 s. At 61 s the client writes into a connection the server is closing, and gets a reset or empty reply. RFC 9112 describes this race. Retry GET on a new connection. Do not blindly retry POST. Keep the client's idle timeout below every server and proxy on the path.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "compression",
      title: "Compression details",
      paragraphs: [
        "Content-Encoding: codings applied to the representation, in order (gzip, deflate, compress). Length, ETag, and digests describe the coded bytes.",
        "Transfer-Encoding (HTTP/1.1 only): a property of one hop, removed by the recipient. Usually chunked.",
        "Accept-Encoding lists acceptable codings, with optional weights. `identity` = no coding. A server that picks a coding by it should send `Vary: Accept-Encoding`, or a cache may hand gzip to a client that cannot decode it.",
        "Already-compressed media (JPEG, video) gain little from gzip.",
        "BREACH: when a secret and attacker text share a compression dictionary, the size shrinks on a correct guess, even over TLS. Disable compression for secrets or keep them apart.",
        "HPACK and QPACK compress fields, not content. RFC 9110 cites HPACK as designed to resist this attack in its limited use.",
      ],
    },
    {
      type: "expandable",
      id: "cache-edges",
      title: "Caching edge cases",
      paragraphs: [
        "Storage needs explicit permission or a heuristically cacheable status. With no max-age or Expires, caches may guess, typically 10% of the time since Last-Modified.",
        "Lifetime order for a shared cache: s-maxage, then max-age, then Expires minus Date. Conflicting directives: honour the most restrictive.",
        "Caches may collapse concurrent misses into one forward request.",
        "Stale responses only when disconnected or explicitly allowed. Never when no-cache, must-revalidate, or s-maxage forbids. must-revalidate + disconnected = error, usually 504.",
        "A 5xx during revalidation: forward it, or act as if the origin did not answer (possibly serving stale if allowed).",
        "`Vary: *` never matches. Varying on Cookie can multiply stored copies.",
        "An Age field means a cache answered. No Age does not prove the origin was contacted.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "nocache-vs-nostore",
          prompt:
            "A team sets `no-cache` on account balances to keep them out of caches. Right? What does, and what would `private` do?",
          answer:
            "No. no-cache allows storage but forces validation before reuse. no-store forbids storage. private keeps it out of shared caches but allows the browser cache.",
        },
        {
          id: "deploy-not-visible",
          prompt:
            "After a deploy users get the old `app.js` for up to an hour. It had `max-age=3600`. Why, and what fixes future deploys?",
          answer:
            "Fresh copies are reused without asking, and nothing pushes updates. Version the URL (`app.3f9c.js`) and serve the HTML that names it with `no-cache`.",
        },
        {
          id: "gzip-vary",
          prompt:
            "A server gzips when asked but omits Vary. An old client that cannot decode gzip gets garbage through a proxy cache. Why?",
          answer:
            "The proxy keyed the gzip response on method and URI only, then served it to the old client. `Vary: Accept-Encoding` makes it match Accept-Encoding before reuse.",
        },
        {
          id: "pool-reset",
          prompt:
            "Occasional `connection reset by peer` on the first request after a quiet minute. Balancer idle timeout 60 s; client pool keeps 120 s. Cause and fixes?",
          answer:
            "The balancer closed the idle connection; the pool handed it out anyway. Set the pool idle timeout below 60 s or validate idle connections. Auto-retry only idempotent requests.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "persistent-default",
      statement:
        "HTTP/1.1 defaults to persistent connections, and every message on one must self-delimit. A server must read the whole request body or close; a client must read the whole response body to reuse the connection.",
      sourceId: "rfc9112",
      locator: "Section 9.3, Persistence",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "idle-close-race",
      statement:
        "Servers usually time out inactive connections. Any side may close at any time, so a client can start a request as the server closes the idle connection. Implementations should monitor for a received close. Retry conditions are those of RFC 9110 Section 9.2.2.",
      sourceId: "rfc9112",
      locator: "Section 9.3.1, Retrying Requests; Section 9.5, Failures and Timeouts",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "retry-idempotent",
      statement:
        "Idempotent requests can be repeated automatically after a communication failure; a client should not auto-retry a non-idempotent request unless it knows it is idempotent or that the original was never applied.",
      sourceId: "rfc9110",
      locator: "Section 9.2.2, Idempotent Methods",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "h2-single-connection",
      statement:
        "Clients should not open more than one HTTP/2 connection per host and port pair; connections are expected to stay open until no further communication is needed.",
      sourceId: "rfc9113",
      locator: "Section 9.1, Connection Management",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "idle-restart-window",
      statement:
        "A TCP should set cwnd to no more than the restart window, min(IW, cwnd), before transmitting if it has not sent data for longer than the RTO. Testing only the last receive time fails to deflate cwnd on persistent HTTP connections.",
      sourceId: "rfc5681",
      locator: "Section 4.1, Restarting Idle Connections",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "content-encoding",
      statement:
        "Content-Encoding lists codings applied to the representation in order, mainly to compress it without losing its media type. Unlike Transfer-Encoding, the codings belong to the representation, and its metadata describes the coded form. Defined codings include compress, deflate, and gzip.",
      sourceId: "rfc9110",
      locator: "Section 8.4, Content-Encoding; Section 8.4.1, Content Codings",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "accept-encoding",
      statement:
        "Accept-Encoding lists the content codings acceptable in the response, with optional weights; identity means no encoding.",
      sourceId: "rfc9110",
      locator: "Section 12.5.3, Accept-Encoding",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "vary",
      statement:
        "Vary lists request fields, besides method and URI, that may have influenced the response; `Vary: *` means a later request cannot be matched without forwarding. An origin should send Vary on a cacheable response it wants selectively reused.",
      sourceId: "rfc9110",
      locator: "Section 12.5.5, Vary",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "vary-cache-key",
      statement:
        "A cache must not reuse a stored response with Vary without revalidation unless all nominated request fields match; a field absent in one request matches only if absent in the other; `Vary: *` always fails.",
      sourceId: "rfc9111",
      locator: "Section 4.1, Calculating Cache Keys with the Vary Header Field",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "weak-validators",
      statement: "A validator shared by a gzip-coded and an uncoded representation is weak.",
      sourceId: "rfc9110",
      locator: "Section 8.8.1, Weak versus Strong",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "etag",
      statement:
        "ETag carries an opaque entity tag for the selected representation, optionally prefixed W/ for weak.",
      sourceId: "rfc9110",
      locator: "Section 8.8.3, ETag",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "if-none-match",
      statement:
        "If-None-Match uses weak comparison, since weak tags can validate caches. Clients updating stored responses with entity tags should send them in If-None-Match. A recipient must ignore If-Modified-Since when If-None-Match is present.",
      sourceId: "rfc9110",
      locator: "Section 13.1.2, If-None-Match; Section 13.1.3, If-Modified-Since",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "not-modified",
      statement:
        "304 means a conditional GET or HEAD would have produced 200 but the condition was false, so the client uses its stored copy. It must carry the Content-Location, Date, ETag, Vary, Cache-Control, and Expires a 200 would have had.",
      sourceId: "rfc9110",
      locator: "Section 15.4.5, 304 Not Modified",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "breach",
      statement:
        "Dynamic compression can reveal secrets through size when attacker-controlled content shares a dictionary with them, as in BREACH. Mitigate by disabling compression on sensitive data or separating it. HPACK is cited as designed to be not exploitable in its limited use.",
      sourceId: "rfc9110",
      locator: "Section 17.6, Attacks Using Shared-Dictionary Compression",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cache-overview",
      statement:
        "Caching is optional, but reuse is the default when nothing prevents it. The cache key is at least method and target URI, extended by Vary.",
      sourceId: "rfc9111",
      locator: "Section 2, Overview of Cache Operation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "storing",
      statement:
        "A cache must not store a response if no-store is present, if it is shared and sees private or an Authorization request without explicit permission, or if the response lacks explicit freshness, public, private (for private caches), or a heuristically cacheable status.",
      sourceId: "rfc9111",
      locator: "Section 3, Storing Responses in Caches",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "reuse-rules",
      statement:
        "A cache must not reuse a stored response unless URI, method, and Vary fields match, no-cache is absent or the response is validated, and the response is fresh, allowed stale, or validated. It must add Age when serving without validation, must write through unsafe requests, and may collapse requests on a miss.",
      sourceId: "rfc9111",
      locator: "Section 4, Constructing Responses from Caches",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "freshness",
      statement:
        "Freshness lifetime comes from s-maxage (shared caches), else max-age, else Expires minus Date, else a heuristic. Conflicting directives: honour the most restrictive. A heuristic may use a fraction of the time since Last-Modified, typically 10%.",
      sourceId: "rfc9111",
      locator: "Sections 4.2.1 and 4.2.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "serving-stale",
      statement:
        "A cache must not serve stale when no-cache, must-revalidate, s-maxage, or proxy-revalidate forbids it, and otherwise only when disconnected or explicitly permitted.",
      sourceId: "rfc9111",
      locator: "Section 4.2.4, Serving Stale Responses",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "validation",
      statement:
        "A cache validates by adding preconditions from stored validators. A 304 lets it update and reuse the stored response; a full response replaces it; on a 5xx it may forward the error or act as if the server did not respond.",
      sourceId: "rfc9111",
      locator:
        "Section 4.3.1, Sending a Validation Request; Section 4.3.3, Handling a Validation Response",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cache-answers-conditional",
      statement:
        "A cache that can satisfy a request with a stored 200 should evaluate the request's preconditions against that stored response's validators.",
      sourceId: "rfc9111",
      locator: "Section 4.3.2, Handling a Received Validation Request",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "invalidation",
      statement:
        "A cache must invalidate the target URI on a 2xx or 3xx response to an unsafe request, and may invalidate Location and Content-Location URIs. This only affects caches the request passes through.",
      sourceId: "rfc9111",
      locator: "Section 4.4, Invalidating Stored Responses",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "age-field",
      statement:
        "An Age field implies the origin did not generate or validate the response for this request; its absence does not imply the origin was contacted.",
      sourceId: "rfc9111",
      locator: "Section 5.1, Age",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "directives",
      statement:
        "max-age: stale after N seconds. must-revalidate: no stale reuse without validation, even disconnected (should return 504). no-cache: no reuse without successful validation. no-store: do not store request or response; not a reliable privacy mechanism. private: shared caches must not store. s-maxage: overrides max-age for shared caches and requires revalidation once stale.",
      sourceId: "rfc9111",
      locator: "Sections 5.2.2.1, 5.2.2.2, 5.2.2.4, 5.2.2.5, 5.2.2.7, and 5.2.2.10",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc9111",
      note: "Sections 2, 3, 4, 4.1, 4.2.1, 4.2.2, 4.2.4, 4.3.1–4.3.3, 4.4, 5.1, and 5.2.2.1–5.2.2.10.",
    },
    {
      sourceId: "rfc9110",
      note: "Sections 8.4, 8.4.1, 8.8.1, 8.8.3, 9.2.2, 12.5.3, 12.5.5, 13.1.2, 13.1.3, 15.4.5, and 17.6.",
    },
    { sourceId: "rfc9112", note: "Sections 9.3, 9.3.1, and 9.5: persistence and idle closes." },
    { sourceId: "rfc9113", note: "Section 9.1: one HTTP/2 connection per origin." },
    { sourceId: "rfc5681", note: "Section 4.1: slow start after an idle period." },
  ],
} satisfies LessonBody;
