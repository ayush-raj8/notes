import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m08.request-response",
  status: "complete",
  objectives: [
    "Say what HTTP adds to a byte stream: framing, method, target, fields, and status.",
    "Classify methods as safe or idempotent, and say what each lets a client, cache, or proxy do.",
    "Read a status code by class, and tell a client error from a server or gateway error.",
    "Explain how cookies carry sessions over a stateless protocol, and what Secure and HttpOnly protect.",
  ],
  prerequisites: ["m07.transport-and-failures"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• After DNS and TCP, the two programs share only a reliable, ordered byte stream.\n• The stream does not say where a request ends, what is asked, or which site on the address is meant.\n• It does not say whether the server did it, or whether asking again is safe.\n• A private protocol per application would be opaque to every library, cache, and proxy.",
      whyItMatters:
        "• Browsers, REST APIs, CDNs, gateways, and gRPC all act on HTTP's shared meaning.\n• Retry libraries retry idempotent methods. Caches reuse safe, fresh responses.\n• A 502, 503, or 504 names the failing hop before you open a log.",
      idea: "• One self-describing message format for everyone.\n• Request: method, target, header fields, optional content.\n• Response: status code, header fields, optional content.\n• Each message is understood on its own, without the connection's history.",
      howItWorks:
        "• Request: control data (method, target, version), then fields such as Host, then content.\n• Framing marks the end: Content-Length or chunked in HTTP/1.1, frames in HTTP/2 and HTTP/3.\n• Response: zero or more interim 1xx, then exactly one final status (2xx, 3xx, 4xx, 5xx).\n• Safe methods: GET, HEAD, OPTIONS, TRACE. Idempotent: those plus PUT and DELETE.\n• State across requests travels in each message: a Cookie or an Authorization field.",
      whyItWorks:
        "• Meaning is in the message, not the connection. Balancers, proxies, and caches can split, share, and replay requests.\n• Shared methods and status classes let generic code decide: retry, cache, or blame which side.\n• Explicit framing lets messages share a connection and exposes a truncated response.",
      limits:
        "• Method semantics are promises. HTTP cannot stop a GET that deletes.\n• Idempotent means same intended effect, not same response.\n• A timed-out POST is not safe to retry without a duplicate check.\n• 2xx is what the server reports. 202 means not finished. A 5xx may come from a proxy.\n• Stateless means sessions live in cookies or tokens, which need their own protection.",
      connections:
        "• Module 5: an ACK is not processing (why.09), TCP cannot make retries safe (why.10), connect can succeed while the request fails (why.20). HTTP status and methods are the answer.\n• Module 7 resolved the name. Host tells the server which name.\n• Next lesson: how HTTP/1.1, HTTP/2, and HTTP/3 carry these messages. Module 9: TLS. Module 11: retries.",
    },
    {
      type: "steps",
      id: "one-exchange",
      title: "One exchange, read from `curl -v`",
      steps: [
        {
          title: "Request line and Host",
          body: "`curl -v --http1.1 https://www.wikipedia.org/` sent `GET / HTTP/1.1` and `Host: www.wikipedia.org` (observed October 2026). Method, target, version. Host lets one IP serve many sites. No Host, or two, gets 400.",
        },
        {
          title: "Fields, then an empty line",
          body: "One `name: value` per line. An empty line ends the header section. A request with neither Content-Length nor Transfer-Encoding has no body, so the GET is complete there.",
        },
        {
          title: "Status line",
          body: "`HTTP/1.1 200 OK`. Clients act on the number. The reason phrase is optional text.",
        },
        {
          title: "Content framing",
          body: "`content-type: text/html` and `content-length: 122421`. The length says where the body ends. Without it (or chunked), only a close ends the response, and truncation is undetectable.",
        },
        {
          title: "State for later requests",
          body: "`set-cookie: WMF-Last-Access=...;Path=/;HttpOnly;secure;Expires=...`. The browser returns it as `Cookie:` on later requests, so the server recognises the client without the connection.",
        },
        {
          title: "Same exchange over HTTP/2",
          body: "Without `--http1.1`, the reply starts `HTTP/2 200`. Control data moves into pseudo-header fields (`:method`, `:path`, `:authority`, `:status`). The meaning is unchanged.",
        },
      ],
    },
    {
      type: "comparison",
      id: "methods",
      title: "Methods and what they promise",
      caption:
        "Safe and idempotent describe what the client asked for. Logging a GET is still safe. PATCH is defined outside RFC 9110 and is not covered.",
      columns: ["Safe", "Idempotent", "Means", "Consequence"],
      rows: [
        {
          label: "GET",
          cells: [
            "Yes",
            "Yes",
            "Retrieve the target.",
            "Cacheable. Crawlers and prefetchers send it freely.",
          ],
        },
        {
          label: "HEAD",
          cells: ["Yes", "Yes", "GET without content.", "Check size or ETag without the body."],
        },
        {
          label: "POST",
          cells: [
            "No",
            "No",
            "Target processes the data by its own rules.",
            "No automatic retry. Caches forward it.",
          ],
        },
        {
          label: "PUT",
          cells: [
            "No",
            "Yes",
            "Create or replace the target's state.",
            "Safe to repeat after a lost response.",
          ],
        },
        {
          label: "DELETE",
          cells: [
            "No",
            "Yes",
            "Remove the target's association.",
            "Repeat has same effect; response may differ. Not cacheable.",
          ],
        },
        {
          label: "OPTIONS",
          cells: [
            "Yes",
            "Yes",
            "Ask what the target supports.",
            "`OPTIONS *` asks about the whole server.",
          ],
        },
      ],
    },
    {
      type: "comparison",
      id: "status-classes",
      title: "Status classes",
      columns: ["Means", "Examples", "Client action"],
      rows: [
        {
          label: "1xx",
          cells: [
            "Interim. Never final.",
            "100 Continue, 101 Switching Protocols",
            "Keep waiting.",
          ],
        },
        {
          label: "2xx",
          cells: [
            "Received, understood, accepted.",
            "200, 201 + Location, 202, 204",
            "Success. 202 = not done yet.",
          ],
        },
        {
          label: "3xx",
          cells: [
            "More action needed.",
            "301, 308, 303, 304 Not Modified",
            "Follow Location, or use cache on 304.",
          ],
        },
        {
          label: "4xx",
          cells: [
            "Request is wrong or refused.",
            "400, 401, 403, 404",
            "Fix the request. Do not retry unchanged.",
          ],
        },
        {
          label: "5xx",
          cells: [
            "Server failed a valid request.",
            "500 from app. 502, 503, 504 often from a gateway.",
            "Maybe retry later, only if safe.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "post-times-out",
      title: "An order POST that times out behind a load balancer",
      setup:
        "App sends `POST /orders` (JSON) to `api.example.com`. A load balancer forwards it to one of several backends. App timeout 10 s. Names and timings are illustrative.",
      walkthrough: [
        "`Content-Length: 182` frames the body. The balancer reads 182 bytes and forwards on its own backend connection.",
        "Backend inserts the order, then stalls on payment. Balancer gives up and answers 504 Gateway Timeout.",
        "504 says nothing about whether the backend acted. Here it did. POST is not idempotent, so a blind retry creates a second order.",
        "Fix: a client-generated order id stored with the order. A retry with the same id returns the existing order. Or query `GET /orders?client_ref=...` first.",
        "Other gateway codes: 502 if the backend reply was invalid; 503 if no backend could take it (maybe with Retry-After). The app's TCP connection was fine every time (why.20).",
        "`PUT /orders/7f3a` with the full order would have been safe to retry at the HTTP level.",
      ],
      result:
        "Status says which hop answered. Method says whether retry is safe. Neither says whether a POST ran after the client stopped waiting. That needs an application-level id or an outcome check.",
    },
    {
      type: "why",
      id: "why-http-if-tcp-exists",
      questionId: "why.01",
    },
    {
      type: "why",
      id: "why-http-if-tcp-is-reliable",
      questionId: "why.23",
    },
    {
      type: "why",
      id: "why-raw-text-is-not-a-web-platform",
      questionId: "why.24",
    },
    {
      type: "callout",
      id: "who-sent-the-5xx",
      tone: "caution",
      title: "A 5xx may not come from your code",
      body: "502 and 504 are defined for a server acting as a gateway or proxy. First suspect: the proxy-to-backend hop. Look for a crashed or restarting backend, a closed connection, or a proxy timeout shorter than the work. Backend logs may show nothing, or show the request finishing after the proxy gave up.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "cookies-and-sessions",
      title: "Cookies and sessions",
      paragraphs: [
        "Stateless: each request stands alone. A server must not assume two requests on one connection share a user agent, unless the connection is secured and specific to it.",
        "`Set-Cookie: name=value` may appear on any response, including 4xx and 5xx. The browser returns matching cookies in one `Cookie:` field. Path and Domain set scope. Expires or Max-Age sets lifetime. Delete by re-setting with a past expiry and the same Path and Domain.",
        "Usual design: the cookie holds only a random session id; data stays on the server. Issue a new id at login to prevent session fixation.",
        "Secure: sent only over a secure channel. Protects confidentiality, not integrity; an active attacker can still overwrite it over plain HTTP. HttpOnly: hidden from page scripts. SameSite is from a later cookie spec, not cited here.",
        "APIs usually send a token in Authorization instead. A shared cache must not reuse such a response for others unless it says public, s-maxage, or must-revalidate.",
        "Set-Cookie may repeat and must not be merged into one comma-separated line, because cookie dates contain commas.",
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Edge cases",
      paragraphs: [
        "Unknown code: treat as the x00 of its class (471 acts like 400). Outside 100–599: treat as 5xx.",
        "HEAD responses and 1xx, 204, 304 never have content. The client must remember which request a response answers.",
        "A server must not act before it has the whole header section. A later field may be a precondition or credential.",
        "`Expect: 100-continue`: the client waits for 100 before sending a large body. The final status comes later.",
        "401: no valid credentials, with a WWW-Authenticate challenge. 403: understood and refused; do not repeat with the same credentials. A server may send 404 to hide that the resource exists.",
        "A cache never answers a POST from storage. A cached POST response can only serve a later GET or HEAD.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "retry-which",
          prompt:
            "The connection breaks after the request is sent, before any response. May a library auto-retry GET, PUT, DELETE, and POST?",
          answer:
            "GET, PUT, and DELETE: yes, they are idempotent. POST: no, unless the app marks it safe or the server can detect a duplicate. A proxy must never auto-retry a non-idempotent request.",
        },
        {
          id: "get-delete",
          prompt:
            "An admin page deletes via a link to `GET /records/42?action=delete`. Records vanish with no clicks. Why, and what is the fix?",
          answer:
            "GET is safe by definition, so crawlers and prefetchers follow it. RFC 9110 makes the resource owner disable unsafe actions reached by safe methods. Use POST or DELETE behind authentication.",
        },
        {
          id: "virtual-hosting",
          prompt:
            "`shop.example.com` and `blog.example.com` share one IP and port. How does the server pick the site? What if an HTTP/1.1 request omits it?",
          answer:
            "From Host in HTTP/1.1, or `:authority` in HTTP/2 and HTTP/3. A missing or duplicate Host in HTTP/1.1 must get 400.",
        },
        {
          id: "which-hop",
          prompt:
            "An API behind a reverse proxy returns 502, 503, and 504 on different days. Where do you look first for each?",
          answer:
            "502: the backend sent an invalid or cut-off reply; check crashes and restarts. 504: the backend was too slow; compare the proxy timeout with backend latency. 503: overload or maintenance; check capacity, health, and Retry-After.",
        },
        {
          id: "cookie-attrs",
          prompt: "A session cookie has `Secure; HttpOnly`. What does that stop, and what not?",
          answer:
            "Secure stops it leaking over plain HTTP. HttpOnly stops scripts reading it. Neither stops a script sending requests that carry the cookie, or an active attacker overwriting it.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "http-over-connection-stateless",
      statement:
        "HTTP is a client/server protocol over a reliable transport or session-layer connection. It is stateless: each request's semantics can be understood in isolation, and connections do not affect message interpretation. Implementations rely on this to reuse proxied connections and load balance requests. A server must not assume two requests on one connection are from the same user agent unless the connection is secured and specific to that agent.",
      sourceId: "rfc9110",
      locator: "Section 3.3, Connections, Clients, and Servers",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "message-abstraction",
      statement:
        "Request control data is a method, target, and version; response control data is a status code, optional reason phrase, and version. HTTP/1.1 sends control data as the first line; HTTP/2 and HTTP/3 send it as pseudo-header fields.",
      sourceId: "rfc9110",
      locator: "Section 6.2, Control Data",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "framing-completeness",
      statement:
        "Each HTTP version defines its own framing. Ending a response by closing the connection, allowed in HTTP/1.1 for compatibility, can fail to distinguish an incomplete response, so almost all modern implementations use explicit length-delimited framing.",
      sourceId: "rfc9110",
      locator: "Section 6.1, Framing and Completeness",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "http11-message-and-host",
      statement:
        "An HTTP/1.1 message is a start-line, field lines, an empty line, and an optional body. A client must send Host in every HTTP/1.1 request, and a server must respond 400 to a request that lacks Host or has more than one Host line.",
      sourceId: "rfc9112",
      locator: "Section 2.1, Message Format; Section 3.2, Request Target",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "request-body-zero",
      statement:
        "A request without Transfer-Encoding or Content-Length has a body of length zero. A response without a declared length ends when the server closes the connection.",
      sourceId: "rfc9112",
      locator: "Section 6.3, Message Body Length (items 7 and 8)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "host-authority",
      statement:
        "Host carries the host and port from the target URI so one server can serve many host names. In HTTP/2 and HTTP/3 it may be supplanted by :authority. Host acts as an application-level routing mechanism.",
      sourceId: "rfc9110",
      locator: "Section 7.2, Host and :authority",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "safe-methods",
      statement:
        "GET, HEAD, OPTIONS, and TRACE are safe. The distinction lets crawlers and prefetching work without harm. A resource owner must disable an unsafe action reachable through a safe method.",
      sourceId: "rfc9110",
      locator: "Section 9.2.1, Safe Methods",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "idempotent-methods",
      statement:
        "PUT, DELETE, and safe methods are idempotent: repeats have the same intended effect, though responses may differ. They can be retried automatically after a communication failure. A client should not auto-retry a non-idempotent request unless it knows the request is idempotent or can detect the original was never applied. A proxy must not auto-retry non-idempotent requests.",
      sourceId: "rfc9110",
      locator: "Section 9.2.2, Idempotent Methods",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "post-and-put",
      statement:
        "POST asks the target to process the enclosed representation by its own semantics. PUT creates or replaces the target's state. DELETE removes the target's association with its current functionality, and its responses are not cacheable. OPTIONS asks about communication options; `OPTIONS *` applies to the server.",
      sourceId: "rfc9110",
      locator: "Sections 9.3.3 POST, 9.3.4 PUT, 9.3.5 DELETE, and 9.3.7 OPTIONS",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "status-classes",
      statement:
        "Valid status codes are 100 to 599, and the first digit is the class. A client must understand the class and treat an unknown code as the x00 of that class; an invalid code should be treated as 5xx. A request has zero or more interim 1xx responses and exactly one final response. Reason phrases are only recommendations.",
      sourceId: "rfc9110",
      locator: "Section 15, Status Codes; Section 15.1, Overview of Status Codes",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "accepted-not-done",
      statement:
        "202 Accepted means accepted for processing, not completed. HTTP has no way to re-send a status code from an asynchronous operation.",
      sourceId: "rfc9110",
      locator: "Section 15.3.3, 202 Accepted",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "auth-status-codes",
      statement:
        "401 means the request lacks valid credentials and carries a WWW-Authenticate challenge. 403 means understood and refused; the client should not repeat it with the same credentials, and a server may answer 404 to hide a forbidden resource.",
      sourceId: "rfc9110",
      locator: "Sections 15.5.2, 15.5.4, and 15.5.5",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "gateway-status-codes",
      statement:
        "502: a gateway or proxy got an invalid response from an inbound server. 503: temporarily unable to handle the request because of overload or maintenance, optionally with Retry-After. 504: a gateway or proxy did not get a timely response from upstream.",
      sourceId: "rfc9110",
      locator: "Sections 15.6.3, 15.6.4, and 15.6.5",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "no-content-responses",
      statement: "Responses to HEAD, and all 1xx, 204, and 304 responses, have no content.",
      sourceId: "rfc9110",
      locator: "Section 6.4.1, Content Semantics",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "field-order-set-cookie",
      statement:
        "Set-Cookie often appears on several field lines and cannot be combined into one value. A server must not apply a request until it has the entire request header section.",
      sourceId: "rfc9110",
      locator: "Section 5.3, Field Order",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cookie-mechanism",
      statement:
        "A server stores state with Set-Cookie, and the user agent returns it in Cookie. User agents must process Set-Cookie in all non-1xx responses, including 4xx and 5xx. Path and Domain set scope, Expires sets lifetime, and a past expiry with the same Path and Domain removes the cookie.",
      sourceId: "rfc6265",
      locator: "Section 3, Overview; Section 3.1, Examples",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cookie-secure-httponly",
      statement:
        "Secure limits a cookie to secure channels and protects only its confidentiality; an active attacker can overwrite Secure cookies from an insecure channel. HttpOnly omits the cookie from non-HTTP APIs such as script access.",
      sourceId: "rfc6265",
      locator: "Section 4.1.2.5, The Secure Attribute; Section 4.1.2.6, The HttpOnly Attribute",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "session-identifiers",
      statement:
        "Servers commonly store a nonce (session identifier) in a cookie and look up state by it. Servers should avoid session fixation.",
      sourceId: "rfc6265",
      locator: "Section 8.4, Session Identifiers",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "shared-cache-authorization",
      statement:
        "A shared cache must not reuse a response to a request with Authorization for another request unless the response carries must-revalidate, public, or s-maxage.",
      sourceId: "rfc9111",
      locator: "Section 3.5, Storing Responses to Authenticated Requests",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc9110",
      note: "Sections 3.3, 5.3, 6.1, 6.2, 6.4.1, 7.2, 9.2.1, 9.2.2, 9.3.3–9.3.5, 9.3.7, 15, 15.1, 15.3.3, 15.5.2–15.5.5, and 15.6.3–15.6.5.",
    },
    { sourceId: "rfc9112", note: "Sections 2.1, 3.2, and 6.3: message format, Host, body length." },
    { sourceId: "rfc9111", note: "Section 3.5: caching and Authorization." },
    { sourceId: "rfc6265", note: "Sections 3, 4.1.2.5, 4.1.2.6, and 8.4: cookies and sessions." },
  ],
} satisfies LessonBody;
