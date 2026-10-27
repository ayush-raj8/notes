import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m08.reuse-and-cache",
  status: "complete",
  objectives: [
    "Explain what a pooled connection saves, and why a pooled connection can fail on first use after sitting idle.",
    "Tell content coding from transfer coding, and say why compressed responses need Vary and change the ETag.",
    "Decide whether a cache may reuse a response, from its Cache-Control, its age, and Vary.",
    "Follow a conditional request from a stale cache entry to a 304, and say what no-cache, no-store, private, and s-maxage each change.",
  ],
  prerequisites: ["m08.http-versions"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Most HTTP traffic repeats itself. A service calls the same backend thousands of times a second, a browser asks for the same stylesheet on every page, and many clients ask for the same product list. Doing the full work each time means a new connection with its handshakes and a cold congestion window, the full response bytes on the wire, and the origin regenerating an answer it gave a second ago.",
      whyItMatters:
        "Reuse is most of the difference between a fast service and a slow one, and most of what a CDN does. It is also a common source of production bugs: a request fails with a connection reset because the pool handed out a connection the server had closed, a user sees another user's page because a shared cache stored a private response, or a deploy is invisible for an hour because a response said `max-age=3600`.",
      idea: "Reuse three things, each under rules that keep reuse correct. Reuse the connection, as long as each message's framing is intact and both sides still agree the connection is open. Reuse fewer bytes, by sending a compressed representation the client said it accepts. Reuse the whole response, as long as the origin said it stays fresh for that long, or the origin confirms with a cheap conditional request that the stored copy is still current.",
      howItWorks:
        "A client library keeps idle connections in a pool per origin and takes one for the next request instead of connecting. HTTP/1.1 needs one connection per in-flight request; HTTP/2 and HTTP/3 multiplex, so one connection per origin is normal. For bytes, the client sends `Accept-Encoding: gzip, br`, the server compresses the representation and labels it `Content-Encoding: gzip`, and adds `Vary: Accept-Encoding` so caches keep compressed and uncompressed copies apart. For responses, the origin sends `Cache-Control: max-age=N` (or `s-maxage` for shared caches) and a validator, an ETag or Last-Modified. A cache serves the stored response without contacting the origin while its age is below its freshness lifetime. After that it sends a conditional request with `If-None-Match` carrying the ETag; the origin answers 304 Not Modified with no content if nothing changed, or 200 with the new representation if it did.",
      whyItWorks:
        "A pooled connection skips the TCP and TLS handshakes, and a connection that has been busy recently keeps the congestion window it has already grown, so the request does not start from slow start. Content codings are a property of the representation, not of the hop, so a cache can store the compressed bytes and serve them to any client that accepts that coding. Freshness lets the origin trade staleness for load explicitly, and validators make the stale case cheap: a 304 costs a round trip and a few header bytes instead of the whole body. Because only the origin sets these values, every cache along the path can make the same decision without coordinating.",
      limits:
        "A pooled connection may be dead: the server, a NAT, or a load balancer can close an idle connection at any time, and the client often learns only when it writes. Retrying then is safe only for idempotent requests. A connection that sat idle longer than one retransmission timeout should restart from a small congestion window, so reuse after a long pause saves the handshakes but not slow start. Caching never pushes updates: a fresh response stays in use until it expires, unsafe requests invalidate only the caches they pass through, and there is no way to recall a copy from every browser. Compression of secrets alongside attacker-controlled text can leak them through response size. Caches are optional and differ; a response's directives are the only thing you control.",
      connections:
        "DNS caching in Module 7 is the same trade, with TTL instead of max-age and no conditional request (why.22). The previous lesson explained why HTTP/2 wants one connection per origin. The intermediaries lesson covers the shared caches, CDNs, and reverse proxies that apply these rules. Module 11 returns to pools under partial failure, including timeouts and retry budgets.",
    },
    {
      type: "comparison",
      id: "cache-directives",
      title: "Response directives that decide reuse",
      caption:
        "A shared cache serves many users, such as a CDN or a reverse proxy. A private cache serves one user agent, such as a browser cache.",
      columns: ["Who may store it", "When it may be reused without asking", "Typical use"],
      rows: [
        {
          label: "max-age=N",
          cells: [
            "Any cache, subject to the other rules.",
            "Until the response's age exceeds N seconds.",
            "Versioned static assets with a long N; API reads with a short N.",
          ],
        },
        {
          label: "s-maxage=N",
          cells: [
            "Shared caches use this instead of max-age.",
            "Until age exceeds N, and then a shared cache must revalidate before reuse.",
            "Let a CDN keep a response longer or shorter than browsers.",
          ],
        },
        {
          label: "no-cache",
          cells: [
            "Any cache may store it.",
            "Never. Every reuse must be validated with the origin first.",
            "Content that must be current but is often unchanged, so 304s save bytes.",
          ],
        },
        {
          label: "no-store",
          cells: [
            "No cache may store the request or response.",
            "Never.",
            "Sensitive responses. Not a privacy guarantee against a non-compliant cache.",
          ],
        },
        {
          label: "private",
          cells: [
            "Only a private cache, such as the browser.",
            "Per its freshness, in that private cache only.",
            "Per-user pages and API responses.",
          ],
        },
        {
          label: "must-revalidate",
          cells: [
            "Any cache, subject to the other rules.",
            "While fresh. Once stale, never without validation, even if the origin is unreachable.",
            "Where serving stale data could cause incorrect operation.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "catalog-cache",
      title: "A product list behind a CDN, over ten minutes",
      setup:
        "`GET /products` on `api.example.com` returns JSON. The origin sends `Cache-Control: max-age=60, s-maxage=300`, `ETag: \"v17\"`, `Content-Encoding: gzip`, and `Vary: Accept-Encoding`. A browser talks to a CDN, a shared cache, which talks to the origin. Times are in seconds and the names are documentation values.",
      walkthrough: [
        "t=0. The browser's cache and the CDN are empty. The CDN forwards the request with the browser's `Accept-Encoding: gzip, br`, stores the gzip response keyed by method, URI, and the Accept-Encoding value that Vary names, and returns it. The browser stores it too.",
        "t=30. The browser needs `/products` again. Its copy has age 30, below max-age 60, so it is fresh and the browser uses it without any request. No server sees this.",
        "t=90. The browser's copy is stale (90 > 60). It sends `GET /products` with `If-None-Match: \"v17\"`. At the CDN the stored response has age 90, below s-maxage 300, so it is still fresh there. The CDN evaluates the If-None-Match against its stored ETag, which matches, and answers 304 itself. The browser updates its stored headers and reuses the body. The origin sees nothing.",
        "t=200. A client using `Accept-Encoding: identity` asks the CDN. Vary says the stored gzip response was selected by Accept-Encoding, and the values differ, so the CDN cannot use it. It forwards the request and stores a second, uncompressed response.",
        "t=320. Another browser's request reaches the CDN. The gzip copy has age 320 > 300, and s-maxage requires a shared cache to revalidate. The CDN sends `If-None-Match: \"v17\"` to the origin. The product list has not changed, so the origin answers 304 with fresh Cache-Control and ETag fields, and the CDN's copy is fresh again for another 300 seconds.",
        "t=400. An admin sends `PUT /products/42` through the same CDN and gets 200. A cache must invalidate the target URI of a successful unsafe request, but that target is `/products/42`, not `/products`. The CDN may invalidate other URIs, but is not required to, and the browsers' own caches never saw the PUT. Until their copies expire, clients can see the old list.",
        "t=401. The origin now has `ETag: \"v18\"`. The next conditional request with `\"v17\"` that reaches it gets 200 with the new representation.",
      ],
      result:
        "Freshness lets each cache decide alone, so most requests never reach the origin; validation makes the stale case cost a 304 instead of a body. The cost is staleness bounded by max-age and s-maxage, not by when the data changes. If clients must see a change at once, the response must not be reusable without validation (`no-cache`), or its URL must change when the content does.",
    },
    {
      type: "steps",
      id: "observed-304",
      title: "A conditional request, observed",
      steps: [
        {
          title: "The first response",
          body: "`curl --compressed -D - https://www.rfc-editor.org/rfc/rfc9111.txt` returned `HTTP/2 200`, `cache-control: public, max-age=86400`, `content-encoding: gzip`, and `etag: \"f8907618635703edd228aee4aa834eee\"` (observed in October 2026).",
        },
        {
          title: "The same URL without compression",
          body: "Without `--compressed`, the response carried `W/\"f8907618635703edd228aee4aa834eee\"`, the same tag marked weak. RFC 9110 explains why such a pairing must be weak: if a server uses one validator for the gzip and the uncompressed representation, that validator is not byte-exact for either, so it is weak.",
        },
        {
          title: "The conditional request",
          body: "Sending that weak tag in `If-None-Match` returned `HTTP/2 304` with zero body bytes. If-None-Match uses the weak comparison, so a weak tag is enough to validate a cache entry, though not for operations such as a range request that need exact bytes.",
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
      body: "A pool keeps an idle connection for, say, 90 seconds, but the server closes idle connections after 60. At second 61 the client takes the connection from the pool and writes a request just as the server's close arrives, and the request fails with a reset or an empty response. RFC 9112 describes exactly this race. A GET can be retried on a new connection. A POST cannot be retried blindly, because the client cannot tell whether the server read it. Keep the client's idle timeout shorter than every server and proxy on the path, and let the library retry only idempotent requests.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "compression",
      title: "Compression details",
      paragraphs: [
        "Content-Encoding names codings applied to the representation, such as gzip, deflate, or compress, in the order they were applied. The representation is defined in its coded form, so its length, its ETag, and any digest describe the compressed bytes. Transfer-Encoding, in HTTP/1.1 only, is a property of one hop and is removed by the recipient; chunked is the common one.",
        "A client lists what it accepts in Accept-Encoding, optionally with weights. `identity` means no coding. RFC 9110 says an origin should send Vary on a cacheable response it wants reused selectively, so a server that chooses a coding from Accept-Encoding sends `Vary: Accept-Encoding`; without it, a cache could give gzip bytes to a client that cannot decode them.",
        "When a media type already includes compression, that encoding is not restated in Content-Encoding. Formats such as JPEG or video are already compressed, so applying gzip again costs CPU for little saving.",
        "Compression can leak secrets. If a response contains both a secret, such as a CSRF token, and text the attacker controls, the compressed size shrinks when the attacker's guess matches part of the secret, even over TLS. RFC 9110 cites the BREACH attack and recommends disabling compression for sensitive data or keeping it apart from attacker-controlled data.",
        "HTTP/2's HPACK and HTTP/3's QPACK compress header fields, not content. RFC 9110 names HPACK as an example of a scheme designed so that it is not considered exploitable in its limited use.",
      ],
    },
    {
      type: "expandable",
      id: "cache-edges",
      title: "Caching edge cases",
      paragraphs: [
        "A cache may store a response only if it has some explicit permission or a heuristically cacheable status. Without max-age or Expires, a cache may guess a freshness lifetime, typically a fraction such as 10% of the time since Last-Modified. Send explicit directives if you care.",
        "If both max-age and Expires are present, max-age wins; a shared cache uses s-maxage over both. When directives conflict, such as max-age and no-cache, the most restrictive should be honoured.",
        "Caches collapse requests: on a miss, several identical requests can wait on one forward request. That protects the origin during a burst of misses.",
        "A cache may serve a stale response only when it is disconnected from the origin or explicitly allowed to, and never when no-cache, must-revalidate, or s-maxage forbids it. With must-revalidate, a disconnected cache must return an error, usually 504.",
        "If a cache gets a 5xx while revalidating, it may forward the error or act as if the origin did not answer, which can mean serving its stored response if staleness is allowed.",
        "`Vary: *` means the response can never be reused without asking the origin. A response that varies on a field the cache does not see in the request, such as Cookie, can multiply stored copies.",
        "The presence of an Age header means a cache answered, not the origin. Its absence does not prove the origin was contacted.",
        "A shared cache must not reuse a response to a request with Authorization unless the response explicitly allows it with public, s-maxage, or must-revalidate.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "nocache-vs-nostore",
          prompt:
            "A team sets `Cache-Control: no-cache` on account balances, believing it keeps them out of caches. Is that right? What would keep them out, and what would `private` do?",
          answer:
            "No. no-cache allows any cache to store the response; it only forbids reusing it without a successful validation with the origin. To prevent storage, use no-store, which forbids caches from storing the request or response, while remembering it is not a privacy guarantee against a non-compliant cache. private forbids shared caches such as CDNs from storing it but allows the user's own browser cache to keep it. For a balance, `private, no-cache` (or no-store) is the usual choice.",
        },
        {
          id: "deploy-not-visible",
          prompt:
            "After a deploy, users still get the old `app.js` for up to an hour. It was served with `Cache-Control: max-age=3600`. Why, and what change makes future deploys visible immediately without losing caching?",
          answer:
            "A fresh response can be reused without contacting the origin until its age exceeds max-age, and nothing pushes an update to browsers or CDNs. Each copy stays in use for up to 3600 seconds after it was fetched. Changing the URL on each deploy, for example `app.3f9c.js` referenced from an HTML page served with `no-cache`, makes the new file a different cache entry, so the asset can keep a very long max-age while the page that names it is always revalidated.",
        },
        {
          id: "gzip-vary",
          prompt:
            "A server compresses responses when the client sends `Accept-Encoding: gzip` and omits Vary. An old client that cannot decode gzip starts getting garbage through the company proxy cache. Explain.",
          answer:
            "The proxy stored the gzip response keyed only by method and URI, because Vary did not say that Accept-Encoding influenced the choice. It then served the stored gzip bytes to the old client, which sent no Accept-Encoding for gzip. With `Vary: Accept-Encoding`, the proxy may reuse a stored response only for requests whose Accept-Encoding matches, and must otherwise forward the request.",
        },
        {
          id: "pool-reset",
          prompt:
            "A service sees occasional `connection reset by peer` errors on the first request after a quiet minute, always to one backend behind a load balancer whose idle timeout is 60 seconds. The client's pool keeps idle connections for 120 seconds. What is happening, and what are the two fixes?",
          answer:
            "The load balancer closes connections idle for 60 seconds. The client's pool still holds them and hands one out; the request is written into a connection the other end has already closed, and the client gets a reset. Fix it by making the pool's idle timeout shorter than the shortest idle timeout on the path, or by validating or replacing idle connections before use. Separately, allow automatic retry on a new connection only for idempotent methods, because a POST that hit the reset may or may not have been read.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "persistent-default",
      statement:
        "HTTP/1.1 defaults to persistent connections, and every message on one must have a self-defined length. A server must read the whole request body or close the connection, and a client must read the whole response body to reuse the connection.",
      sourceId: "rfc9112",
      locator: "Section 9.3, Persistence",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "connections-limit",
      statement:
        "Clients ought to limit simultaneous connections to a server; each consumes server resources and many connections can cause congestion.",
      sourceId: "rfc9112",
      locator: "Section 9.4, Concurrency",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "idle-close-race",
      statement:
        "Servers usually time out inactive connections. Any side may close the connection at any time, so a client can start a request just as the server closes the idle connection. Implementations should monitor open connections for a received close signal. Retry conditions are those of RFC 9110 Section 9.2.2.",
      sourceId: "rfc9112",
      locator: "Section 9.3.1, Retrying Requests; Section 9.5, Failures and Timeouts",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "retry-idempotent",
      statement:
        "Idempotent requests can be repeated automatically after a communication failure before the response is read; a client should not automatically retry a non-idempotent request unless it knows it is idempotent or that the original was never applied.",
      sourceId: "rfc9110",
      locator: "Section 9.2.2, Idempotent Methods",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "h2-single-connection",
      statement:
        "Clients should not open more than one HTTP/2 connection to a given host and port pair, and HTTP/2 connections are expected to stay open until no further communication is needed.",
      sourceId: "rfc9113",
      locator: "Section 9.1, Connection Management",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "content-encoding",
      statement:
        "Content-Encoding lists the codings applied to the representation in order, primarily to compress it without losing its media type. Unlike Transfer-Encoding, the codings are a characteristic of the representation, and its metadata describes the coded form. Defined codings include compress, deflate, and gzip.",
      sourceId: "rfc9110",
      locator: "Section 8.4, Content-Encoding; Section 8.4.1, Content Codings",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "accept-encoding",
      statement:
        "Accept-Encoding in a request lists the content codings acceptable in the response, with optional weights; identity means no encoding.",
      sourceId: "rfc9110",
      locator: "Section 12.5.3, Accept-Encoding",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "vary",
      statement:
        "Vary lists the request fields, besides method and URI, that may have influenced selection of the response; `Vary: *` means a recipient cannot tell whether the response fits a later request without forwarding it. A cache must not use a stored response with Vary without revalidation unless the nominated request fields match.",
      sourceId: "rfc9110",
      locator: "Section 12.5.5, Vary",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "vary-cache-key",
      statement:
        "A cache must not use a stored response with Vary without revalidation unless all nominated request fields match the original request; a field absent in one request matches only if absent in the other, and `Vary: *` always fails to match.",
      sourceId: "rfc9111",
      locator: "Section 4.1, Calculating Cache Keys with the Vary Header Field",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "weak-validators",
      statement:
        "A strong validator changes whenever the representation data observable in a 200 response changes. A validator shared by a gzip-coded and an uncoded representation is weak. Weak validators are usable only when exact equality is not required, such as cache validation.",
      sourceId: "rfc9110",
      locator: "Section 8.8.1, Weak versus Strong",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "etag",
      statement:
        "ETag carries an opaque entity tag, optionally marked weak with W/, for the selected representation. It can be more reliable than a modification date, whose one-second resolution may be insufficient.",
      sourceId: "rfc9110",
      locator: "Section 8.8.3, ETag",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "if-none-match",
      statement:
        "If-None-Match uses the weak comparison. A client updating stored responses that have entity tags should send them in If-None-Match; if one matches, the origin must not perform the method and responds 304 for GET or HEAD, 412 otherwise. A recipient must ignore If-Modified-Since when If-None-Match is present.",
      sourceId: "rfc9110",
      locator: "Section 13.1.2, If-None-Match; Section 13.1.3, If-Modified-Since",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "not-modified",
      statement:
        "304 means a conditional GET or HEAD would have produced 200 except that the condition was false, so the client should use its stored representation. It must include the ETag, Date, Vary, Cache-Control, and Expires fields a 200 would have had, and cannot contain content.",
      sourceId: "rfc9110",
      locator: "Section 15.4.5, 304 Not Modified",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "breach",
      statement:
        "Dynamic compression can reveal confidential information through size differences when attacker-controlled content shares a dictionary with secrets, as in BREACH. The mitigation is to disable compression on sensitive data or separate it from attacker-controlled data.",
      sourceId: "rfc9110",
      locator: "Section 17.6, Attacks Using Shared-Dictionary Compression",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cache-overview",
      statement:
        "Caching is optional, but reuse is the default when nothing prevents it; the rules focus on preventing inappropriate storage or reuse. The cache key is at least the method and target URI, extended by fields that Vary names.",
      sourceId: "rfc9111",
      locator: "Section 2, Overview of Cache Operation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "storing",
      statement:
        "A cache must not store a response unless, among other conditions, no-store is absent, a shared cache sees no private directive and no Authorization without explicit permission, and the response has explicit freshness, a public or private directive, or a heuristically cacheable status.",
      sourceId: "rfc9111",
      locator: "Section 3, Storing Responses in Caches",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "reuse-rules",
      statement:
        "A cache must not reuse a stored response unless the URI and method match, Vary-nominated fields match, no-cache is absent or the response is validated, and the response is fresh, allowed stale, or validated. It must add an Age field when serving without validation, must forward unsafe requests to the origin, and may collapse concurrent requests on a miss.",
      sourceId: "rfc9111",
      locator: "Section 4, Constructing Responses from Caches",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "freshness",
      statement:
        "A response is fresh while its age is below its freshness lifetime. The lifetime comes from s-maxage for a shared cache, else max-age, else Expires minus Date, else a heuristic. With conflicting directives the most restrictive should be honoured. A heuristic may use a fraction of the time since Last-Modified, typically 10%.",
      sourceId: "rfc9111",
      locator: "Sections 4.2, 4.2.1, and 4.2.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "serving-stale",
      statement:
        "A cache must not serve a stale response when no-cache, must-revalidate, s-maxage, or proxy-revalidate forbids it, and otherwise only when disconnected or explicitly permitted.",
      sourceId: "rfc9111",
      locator: "Section 4.2.4, Serving Stale Responses",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "validation",
      statement:
        "A cache validates a stored response by adding preconditions from its validators: it must send entity tags if the stored response had them, and should send Last-Modified in If-Modified-Since. A 304 lets it update and reuse the stored response; a full response replaces it; on a 5xx it may forward the error or act as if the server did not respond.",
      sourceId: "rfc9111",
      locator: "Section 4.3.1, Sending a Validation Request; Section 4.3.3, Handling a Validation Response",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "invalidation",
      statement:
        "A cache must invalidate the target URI when it gets a 2xx or 3xx response to an unsafe request, and may invalidate the Location and Content-Location URIs. This only affects caches the request passes through.",
      sourceId: "rfc9111",
      locator: "Section 4.4, Invalidating Stored Responses",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "age-field",
      statement:
        "Age is a cache's estimate of the seconds since the response was generated or validated at the origin. Its presence implies the origin did not generate or validate the response for this request; its absence does not imply the origin was contacted.",
      sourceId: "rfc9111",
      locator: "Section 5.1, Age",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "directives",
      statement:
        "max-age makes a response stale after N seconds. no-cache forbids reuse without successful validation. no-store forbids storing the request or response and is not a reliable privacy mechanism. private forbids shared caches from storing the response. must-revalidate forbids reusing a stale response without validation, even when disconnected, when the cache should return 504. s-maxage overrides max-age for shared caches and requires revalidation once stale.",
      sourceId: "rfc9111",
      locator: "Sections 5.2.2.1, 5.2.2.2, 5.2.2.4, 5.2.2.5, 5.2.2.7, and 5.2.2.10",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cache-answers-conditional",
      statement:
        "A cache that can satisfy a request by reusing a stored 200 response should evaluate the request's preconditions against that stored response's validators, with If-None-Match taking precedence over If-Modified-Since. When a 304 arrives, the cache must update the matching stored responses' header fields from it.",
      sourceId: "rfc9111",
      locator: "Section 4.3.2, Handling a Received Validation Request; Section 4.3.4, Freshening Stored Responses upon Validation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "vary-should",
      statement:
        "An origin server should generate Vary on a cacheable response when it wants the response selectively reused, which is generally when the content was tailored to the selecting fields.",
      sourceId: "rfc9110",
      locator: "Section 12.5.5, Vary",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "idle-restart-window",
      statement:
        "A TCP should set cwnd to no more than the restart window, min(IW, cwnd), before transmitting if it has not sent data for longer than the retransmission timeout. Testing only the last receive time fails to deflate cwnd on persistent HTTP connections.",
      sourceId: "rfc5681",
      locator: "Section 4.1, Restarting Idle Connections",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "shared-cache-auth",
      statement:
        "A shared cache must not reuse a response to a request with Authorization for another request unless the response has must-revalidate, public, or s-maxage.",
      sourceId: "rfc9111",
      locator: "Section 3.5, Storing Responses to Authenticated Requests",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc9111",
      note: "Sections 2, 3, 3.5, 4, 4.1, 4.2–4.2.2, 4.2.4, 4.3.1–4.3.4, 4.4, 5.1, and 5.2.2.1–5.2.2.10.",
    },
    {
      sourceId: "rfc9110",
      note: "Sections 8.4, 8.4.1, 8.8.1, 8.8.3, 9.2.2, 12.5.3, 12.5.5, 13.1.2, 13.1.3, 15.4.5, and 17.6.",
    },
    { sourceId: "rfc9112", note: "Sections 9.3, 9.3.1, 9.4, and 9.5: persistence and idle closes." },
    { sourceId: "rfc9113", note: "Section 9.1: one HTTP/2 connection per origin." },
    { sourceId: "rfc5681", note: "Section 4.1: slow start after an idle period." },
  ],
} satisfies LessonBody;
