import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m08.request-response",
  status: "complete",
  objectives: [
    "Say what HTTP adds to a reliable byte stream: message framing, a method and target, header fields, and a status.",
    "Classify methods as safe or idempotent, and say what each property lets a client, cache, or proxy do on its own.",
    "Read a status code by its class, and tell a client error from a server error and from an error written by a gateway.",
    "Explain how cookies let a stateless protocol carry sessions, and what Secure and HttpOnly do and do not protect.",
  ],
  prerequisites: ["m07.transport-and-failures"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "After DNS gives a client an address and TCP connects to it, the two programs share a reliable, ordered stream of bytes and nothing else. The stream does not say where one request ends and the next begins, what the client is asking for, which of the many sites on that address it means, whether the server did it, or whether it is safe to ask again. Every application could invent its own answers, but then no browser, library, cache, proxy, or load balancer written by someone else could understand its traffic.",
      whyItMatters:
        "Every REST API, browser, CDN, API gateway, and gRPC service speaks HTTP, and the shared meaning is what the software in between acts on. Retry libraries retry because a method is idempotent, caches reuse because a method is safe and a response says it is fresh, and a 502, 503, or 504 tells you which hop failed before you read a single log line.",
      idea: "Agree on one self-describing message format. A request carries a method, a target, header fields, and optional content. A response carries a status code, header fields, and optional content. Each message can be understood on its own, without knowing what came before it on the connection, so any server or intermediary can handle any request.",
      howItWorks:
        "The client opens or reuses a connection and sends control data (the method, the target, and the protocol version), then header fields, including Host for the site it wants, then any content. The framing says exactly where the message ends: a Content-Length or chunked coding in HTTP/1.1, frames in HTTP/2 and HTTP/3. The server answers with zero or more interim 1xx responses and exactly one final response, whose three-digit status code starts with its class: 2xx success, 3xx redirection, 4xx client error, 5xx server error. Methods carry promises: GET, HEAD, OPTIONS, and TRACE are safe, meaning read-only as far as the client asked, and those plus PUT and DELETE are idempotent, meaning that repeating them has the same intended effect as sending them once. State that must last across requests travels in the messages themselves, as a cookie the server set or a credential in the Authorization header.",
      whyItWorks:
        "Because the meaning is in the message and not in the connection, a load balancer can send consecutive requests to different servers, a proxy can reuse its connections for many clients, and a cache can answer a request it saw an hour ago. Because every service uses the same methods and status classes, generic software can make correct decisions about traffic it has never seen: whether to retry, whether to cache, whether the client or the server was at fault. Explicit framing is what lets many requests share one connection and lets the receiver tell a complete response from one cut off by a failure.",
      limits:
        "The method semantics are promises the server is supposed to keep, and HTTP cannot enforce them. A GET that deletes data will be triggered by crawlers and prefetchers. Idempotent means the same intended effect, not the same response. A POST that timed out cannot be retried safely unless the application can detect a duplicate. A 2xx says the request was accepted as far as the server reports, and a 202 says only that processing has not finished. A 5xx may have been written by a proxy, not by your service. And because the protocol is stateless, sessions live in cookies and tokens that must themselves be protected.",
      connections:
        "Module 5 showed that a TCP acknowledgment does not mean the request was processed (why.09), that TCP cannot make retries safe (why.10), and that a connection can succeed while the request fails (why.20). This lesson supplies the application-level answers: status codes and method semantics. Module 7 resolved the name; the Host field is how the server learns which name was meant. The next lesson covers how HTTP/1.1, HTTP/2, and HTTP/3 put these messages on the wire. Module 9 adds TLS, and Module 11 returns to timeouts and retries.",
    },
    {
      type: "steps",
      id: "one-exchange",
      title: "One request and one response, read from `curl -v`",
      steps: [
        {
          title: "The request's control data and Host",
          body: "`curl -v --http1.1 https://www.wikipedia.org/` sent `GET / HTTP/1.1` and `Host: www.wikipedia.org` (observed in October 2026). The first line is the method, the target as a path, and the version. Host carries the authority from the URI, so one IP address can serve many sites. An HTTP/1.1 server must answer 400 to a request with no Host or with two of them.",
        },
        {
          title: "Header fields, then an empty line",
          body: "Each field is a name, a colon, and a value on its own line. An empty line ends the header section. A GET usually has no content, and a request with neither Content-Length nor Transfer-Encoding has a body of length zero, so the server knows the request is complete at the empty line.",
        },
        {
          title: "The status line",
          body: "The server replied `HTTP/1.1 200 OK`. The client acts on the number. The reason phrase after it is only a recommendation and may be replaced or left out.",
        },
        {
          title: "Response fields describe and frame the content",
          body: "`content-type: text/html` says what the content is, and `content-length: 122421` says exactly how many bytes follow the empty line. Without a length or chunked coding, a response can only end when the server closes the connection, and a client then cannot tell a complete response from one cut short by a failure.",
        },
        {
          title: "Fields that set up later requests",
          body: "The same response carried `set-cookie: WMF-Last-Access=...;Path=/;HttpOnly;secure;Expires=...`. The browser stores that cookie and sends it back as `Cookie:` on later requests to the same site, which is how a server recognizes a returning client without relying on the connection.",
        },
        {
          title: "The same exchange over HTTP/2",
          body: "Without `--http1.1`, curl and the server agreed on HTTP/2 during TLS, and the response began `HTTP/2 200` with lowercase field names. The method, target, and status now travel as pseudo-header fields such as `:method`, `:path`, `:authority`, and `:status`, but the meaning of every field is unchanged. That is the point of separating HTTP's semantics from each version's wire format.",
        },
      ],
    },
    {
      type: "comparison",
      id: "methods",
      title: "Methods and what they promise",
      caption:
        "Safe and idempotent are properties of what the client requested, not of everything the server does. Logging a GET is still safe. PATCH is defined outside RFC 9110 and is not covered here.",
      columns: ["Safe", "Idempotent", "Used for", "Consequence"],
      rows: [
        {
          label: "GET",
          cells: [
            "Yes",
            "Yes",
            "Retrieve a representation of the target.",
            "Caches may store and reuse the response. Crawlers and prefetchers may send it without asking anyone.",
          ],
        },
        {
          label: "HEAD",
          cells: [
            "Yes",
            "Yes",
            "Like GET, but the response has no content.",
            "Checks headers such as size or ETag without transferring the body.",
          ],
        },
        {
          label: "POST",
          cells: [
            "No",
            "No",
            "Have the target process the enclosed data by its own rules: submit a form, create an order.",
            "Clients should not retry it automatically. A cache must forward it to the origin.",
          ],
        },
        {
          label: "PUT",
          cells: [
            "No",
            "Yes",
            "Create or replace the target's state with the enclosed representation.",
            "Safe to repeat after a lost response: the second PUT leaves the same state.",
          ],
        },
        {
          label: "DELETE",
          cells: [
            "No",
            "Yes",
            "Remove the association between the target and its current functionality.",
            "Repeating it has the same intended effect, though the second response may differ. Its responses are not cacheable.",
          ],
        },
        {
          label: "OPTIONS",
          cells: [
            "Yes",
            "Yes",
            "Ask what communication options the target supports, without implying a resource action.",
            "`OPTIONS *` asks about the server as a whole and is useful only as a capability check.",
          ],
        },
      ],
    },
    {
      type: "comparison",
      id: "status-classes",
      title: "Status classes, and who usually sent them",
      columns: ["Means", "Common examples", "What a client does"],
      rows: [
        {
          label: "1xx",
          cells: [
            "Interim: received, keep going. Never the final answer.",
            "100 Continue, 101 Switching Protocols",
            "Waits for the final response on the same request.",
          ],
        },
        {
          label: "2xx",
          cells: [
            "The request was received, understood, and accepted.",
            "200 OK, 201 Created with a Location, 202 Accepted, 204 No Content",
            "Treats it as success, but reads 202 as 'not finished yet'.",
          ],
        },
        {
          label: "3xx",
          cells: [
            "More action is needed to complete the request.",
            "301 and 308 moved, 303 See Other, 304 Not Modified",
            "Follows the Location, or reuses its cached copy for 304.",
          ],
        },
        {
          label: "4xx",
          cells: [
            "The request has a problem or cannot be fulfilled.",
            "400 Bad Request, 401 needs credentials, 403 refused, 404 Not Found",
            "Does not retry unchanged. Fixes the request or the credentials.",
          ],
        },
        {
          label: "5xx",
          cells: [
            "The server failed to fulfil an apparently valid request.",
            "500 from the application. 502, 503, and 504 often from a gateway or proxy.",
            "May retry later, but only if the method or the application makes that safe.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "post-times-out",
      title: "An order request that times out behind a load balancer",
      setup:
        "A mobile app sends `POST /orders` with a JSON body to `api.example.com`. A load balancer terminates the client's connection and forwards the request to one of several application servers. The app waits up to 10 seconds. Names are documentation values; the timings are illustrative.",
      walkthrough: [
        "The request is framed by `Content-Length: 182`. The load balancer reads all 182 bytes, picks a backend, and forwards the request over its own connection to that server.",
        "The backend inserts the order into the database, then stalls on a slow payment call. The load balancer gives up waiting for the backend and answers the app with 504 Gateway Timeout: it was acting as a gateway and did not receive a timely response from the server it needed.",
        "The app sees a 5xx and is tempted to retry. But POST is not idempotent, and a 504 says nothing about whether the backend acted. Here it did, so a blind retry would create a second order. RFC 9110 says a client should not automatically retry a non-idempotent request unless it can know the request is idempotent or detect that the original was never applied.",
        "The app avoids the duplicate by design. Each order request carries a client-generated identifier, for example in an application-defined header or in the body, and the server stores it with the order. A retry with the same identifier returns the existing order instead of creating another. Alternatively, the app can ask `GET /orders?client_ref=...` before retrying.",
        "Compare the other gateway codes. If the backend had answered with a malformed response, the load balancer would send 502 Bad Gateway. If no backend could take the request because of overload or maintenance, a server could send 503 Service Unavailable, perhaps with Retry-After. In all three cases the TCP connection between the app and the load balancer worked perfectly, which is why.20 from Module 5.",
        "Had the app sent `PUT /orders/7f3a` with the full order instead, a retry after the timeout would have been safe at the HTTP level: a second PUT of the same representation leaves the same state.",
      ],
      result:
        "The status code says which hop answered and roughly what went wrong, and the method says whether a retry is safe. Neither says whether a non-idempotent request was carried out after the client stopped waiting. Making POST retries safe takes an application-level identifier or a way to check the outcome.",
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
      body: "502 Bad Gateway and 504 Gateway Timeout are defined for a server acting as a gateway or proxy. When an API behind a load balancer or reverse proxy returns them, the first suspect is the hop between that proxy and the backend: a crashed or restarting process, a closed connection, or a timeout shorter than the backend's work. The backend's own logs may show nothing, or may show the request completing after the proxy gave up. The intermediaries lesson follows a request through such a chain.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "cookies-and-sessions",
      title: "Cookies and sessions",
      paragraphs: [
        "HTTP is stateless: each request can be understood on its own, and RFC 9110 says a server must not assume two requests on the same connection come from the same user agent unless the connection is secured and specific to that agent. Anything a server needs to remember about a client across requests has to be sent again with each request.",
        "Cookies are one way to do that. The server sends `Set-Cookie: name=value` with attributes in any response, including 4xx and 5xx responses. The browser stores it and returns matching cookies in a single `Cookie:` header on later requests within the cookie's scope. Path and Domain set which URLs receive it, and Expires or Max-Age sets how long it lasts. To delete a cookie, the server sets it again with an expiry in the past, using the same Path and Domain.",
        "Servers commonly put only a random session identifier in the cookie and keep the session data on the server, keyed by it. An attacker who steals the cookie can still use the session, but cannot read sensitive data from it or splice parts of two sessions together. Servers should also avoid session fixation, where an attacker gets a victim to use an identifier the attacker already knows; issuing a new identifier at login prevents it.",
        "Secure means the browser sends the cookie only over a secure channel, typically HTTPS. It protects the cookie's confidentiality, not its integrity: an active attacker on an insecure connection can still overwrite it. HttpOnly keeps the cookie out of non-HTTP APIs such as scripts in the page, which limits theft through script injection. The SameSite attribute, which limits sending cookies on cross-site requests, comes from a later revision of the cookie specification that is not cited here.",
        "APIs used by programs rather than browsers usually carry a token in the Authorization header instead. That has a caching consequence: a shared cache must not reuse a response to a request with Authorization for another request, unless the response explicitly allows it with public, s-maxage, or must-revalidate.",
        "Set-Cookie is the one common field that may appear on several lines and cannot be combined into one comma-separated line, because commas appear inside cookie dates. Code that merges duplicate headers needs to treat it specially.",
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Edge cases and details",
      paragraphs: [
        "Unknown status codes are not errors in the client. A client must understand the class of every status code and treat an unrecognized one as the x00 of its class, so an unknown 471 is handled like 400. A value outside 100 to 599 should be treated as a 5xx.",
        "Responses to HEAD, and 1xx, 204, and 304 responses, never have content, whatever their headers say. A client therefore has to remember which request a response answers in order to parse it.",
        "A server must not act on a request until it has the whole header section, because a later field could be a precondition, a credential, or a misleading duplicate.",
        "When a client knows a large upload might be rejected, it can send `Expect: 100-continue` and wait for a 100 Continue before sending the content. The 100 is an interim response; the final status comes later on the same request.",
        "401 means the request lacked valid credentials and comes with a WWW-Authenticate challenge. 403 means the server understood the request and refuses it, and a client should not repeat it with the same credentials. A server that does not want to reveal that a resource exists may answer 404 instead of 403.",
        "POST responses are cacheable only in narrow cases, and a cached POST response can only answer a later GET or HEAD. A cache never answers a POST from storage.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "retry-which",
          prompt:
            "A client's connection breaks after it sent the request and before any response arrived. For each of GET, PUT, DELETE, and POST, may an HTTP library retry automatically on a new connection?",
          answer:
            "GET, PUT, and DELETE are idempotent, so repeating them has the same intended effect even if the first one was applied, and a library may retry them. POST is not idempotent, so the library should not retry it automatically unless the application has told it the request is safe to repeat or has a way to detect that the original was never applied, such as a request identifier the server checks. A proxy must never retry a non-idempotent request on its own.",
        },
        {
          id: "get-delete",
          prompt:
            "An admin page deletes a record with a link to `GET /records/42?action=delete`. Records start disappearing even though nobody clicked. What is happening, and what is the fix?",
          answer:
            "GET is defined as safe, so crawlers, link checkers, and browser prefetching may request any GET URL without user intent, and they are. The resource broke the method's promise. RFC 9110 puts the responsibility on the resource owner: an unsafe action must be disabled when reached through a safe method. Use POST or DELETE for the action, behind authentication.",
        },
        {
          id: "virtual-hosting",
          prompt:
            "`shop.example.com` and `blog.example.com` resolve to the same IP address and port. How does the server know which site a request is for, and what happens to an HTTP/1.1 request that omits that information?",
          answer:
            "The client sends the authority from the URI in the Host header field in HTTP/1.1, or in the `:authority` pseudo-header in HTTP/2 and HTTP/3, and the server picks the site from it. With HTTPS, the TLS layer also carries the name, which Module 9 covers. An HTTP/1.1 server must answer 400 Bad Request to a request with no Host field or with more than one.",
        },
        {
          id: "which-hop",
          prompt:
            "Users of an API behind a reverse proxy see three errors on different days: 502, 503, and 504. What does each suggest about where to look first?",
          answer:
            "502 means the proxy got an invalid response from the backend, for example a connection closed mid-response or a malformed reply, so look at backend crashes, restarts, and protocol mismatches. 504 means the proxy did not get a timely response, so compare the proxy's timeout with how long the backend takes, and check whether the backend finished the work after the proxy gave up. 503 means the server is temporarily unable to handle requests, from overload or maintenance; check capacity, backend health, and whether a Retry-After was sent. In every case the client's own connection to the proxy succeeded.",
        },
        {
          id: "cookie-attrs",
          prompt:
            "A session cookie is set with `Secure; HttpOnly`. Which attacks do these attributes limit, and which do they not?",
          answer:
            "Secure stops the browser from sending the cookie over plain HTTP, so a passive eavesdropper cannot read it from an unencrypted request. HttpOnly keeps page scripts from reading it, which limits theft through injected script. Neither stops a script from making requests that the browser sends with the cookie attached, and Secure does not stop an active attacker from overwriting the cookie over an insecure connection. The server still has to protect the session: random identifiers, a new identifier at login, and expiry.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "http-over-connection-stateless",
      statement:
        "HTTP is a client/server protocol over a reliable transport or session-layer connection. It is stateless: each request's semantics can be understood in isolation, and the relationship between connections and messages does not affect their interpretation. Many implementations depend on this to reuse proxied connections or dynamically load balance requests. A server must not assume two requests on the same connection are from the same user agent unless the connection is secured and specific to that agent.",
      sourceId: "rfc9110",
      locator: "Section 3.3, Connections, Clients, and Servers",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "message-abstraction",
      statement:
        "A message consists of control data, a headers table, potentially unbounded content, and a trailers table. Messages are self-descriptive: everything a recipient needs is in the message itself, though a client must retain knowledge of the request to interpret the response. Request control data is a method, target, and version; response control data is a status code, optional reason phrase, and version. In HTTP/2 and HTTP/3 control data is sent as pseudo-header fields.",
      sourceId: "rfc9110",
      locator: "Section 6, Message Abstraction; Section 6.2, Control Data",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "framing-completeness",
      statement:
        "Each HTTP version defines its own framing. Ending a response by closing the connection, allowed in HTTP/1.1 for compatibility, cannot distinguish an incomplete response if the connection closes early, so almost all modern implementations use explicit length-delimited framing.",
      sourceId: "rfc9110",
      locator: "Section 6.1, Framing and Completeness",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "http11-message-and-host",
      statement:
        "An HTTP/1.1 message is a start-line, header field lines, an empty line, and an optional body. A client must send Host in every HTTP/1.1 request, and a server must respond 400 to a request that lacks Host or has more than one Host line.",
      sourceId: "rfc9112",
      locator: "Section 2.1, Message Format; Section 3.2, Request Target",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "request-body-zero",
      statement:
        "A request without Transfer-Encoding or Content-Length has a message body of length zero. A response without a declared length is delimited by the server closing the connection.",
      sourceId: "rfc9112",
      locator: "Section 6.3, Message Body Length (items 7 and 8)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "host-authority",
      statement:
        "The Host field provides the host and port from the target URI, letting a server distinguish resources for multiple host names. In HTTP/2 and HTTP/3 it may be supplanted by the :authority pseudo-header field. Host acts as an application-level routing mechanism.",
      sourceId: "rfc9110",
      locator: "Section 7.2, Host and :authority",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "safe-methods",
      statement:
        "Safe methods are essentially read-only: the client does not request or expect a state change. GET, HEAD, OPTIONS, and TRACE are safe. The distinction lets crawlers and prefetching work without causing harm. A resource owner must disable an unsafe action reachable through a safe method.",
      sourceId: "rfc9110",
      locator: "Section 9.2.1, Safe Methods",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "idempotent-methods",
      statement:
        "A method is idempotent if multiple identical requests have the same intended effect as one. PUT, DELETE, and the safe methods are idempotent. Idempotent requests can be repeated automatically after a communication failure before the response is read. A client should not automatically retry a non-idempotent request unless it knows the request is idempotent or can detect that the original was never applied. A proxy must not automatically retry non-idempotent requests.",
      sourceId: "rfc9110",
      locator: "Section 9.2.2, Idempotent Methods",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "post-and-put",
      statement:
        "POST asks the target to process the enclosed representation by its own semantics; a cached POST response can satisfy a later GET or HEAD but never a POST. PUT asks that the target's state be created or replaced with the enclosed representation, answered with 201 if created and 200 or 204 if replaced. DELETE asks the origin to remove the association between the target and its current functionality; its responses are not cacheable. OPTIONS asks about communication options without implying a resource action, and `OPTIONS *` applies to the server in general.",
      sourceId: "rfc9110",
      locator: "Sections 9.3.3 POST, 9.3.4 PUT, 9.3.5 DELETE, and 9.3.7 OPTIONS",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "status-classes",
      statement:
        "Status codes run from 100 to 599 and the first digit is the class: informational, successful, redirection, client error, server error. A client must understand the class and treat an unrecognized code as the x00 of that class; an invalid code should be treated as 5xx. A request has zero or more interim 1xx responses followed by exactly one final response. Reason phrases are only recommendations.",
      sourceId: "rfc9110",
      locator: "Section 15, Status Codes; Section 15.1, Overview of Status Codes",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "accepted-not-done",
      statement:
        "202 Accepted means the request was accepted for processing but processing has not completed, and HTTP has no facility for re-sending a status code from an asynchronous operation.",
      sourceId: "rfc9110",
      locator: "Section 15.3.3, 202 Accepted",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "auth-status-codes",
      statement:
        "401 means the request lacks valid credentials and must carry a WWW-Authenticate challenge. 403 means the server understood and refuses; the client should not repeat the request with the same credentials, and a server may answer 404 to hide a forbidden resource.",
      sourceId: "rfc9110",
      locator: "Sections 15.5.2, 15.5.4, and 15.5.5",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "gateway-status-codes",
      statement:
        "502 means a server acting as a gateway or proxy received an invalid response from an inbound server. 503 means the server is temporarily unable to handle the request because of overload or maintenance, and may carry Retry-After. 504 means a gateway or proxy did not receive a timely response from an upstream server.",
      sourceId: "rfc9110",
      locator: "Sections 15.6.3, 15.6.4, and 15.6.5",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "no-content-responses",
      statement:
        "Responses to HEAD, and all 1xx, 204, and 304 responses, do not include content.",
      sourceId: "rfc9110",
      locator: "Section 6.4.1, Content Semantics",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "field-order-set-cookie",
      statement:
        "Set-Cookie often appears on multiple field lines and cannot be combined into one value, so recipients handle it as a special case. A server must not apply a request until it has received the entire request header section.",
      sourceId: "rfc9110",
      locator: "Section 5.3, Field Order",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cookie-mechanism",
      statement:
        "A server stores state with Set-Cookie in any response, and the user agent returns it in the Cookie header on later requests. User agents must process Set-Cookie in responses other than 1xx, including 4xx and 5xx. Path and Domain control scope, Expires controls lifetime, and a cookie is removed by setting it with a past expiry and the same Path and Domain.",
      sourceId: "rfc6265",
      locator: "Section 3, Overview; Section 3.1, Examples",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cookie-secure-httponly",
      statement:
        "Secure limits a cookie to secure channels and protects only its confidentiality; an active network attacker can overwrite Secure cookies from an insecure channel. HttpOnly omits the cookie from non-HTTP APIs such as script access.",
      sourceId: "rfc6265",
      locator: "Section 4.1.2.5, The Secure Attribute; Section 4.1.2.6, The HttpOnly Attribute",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "session-identifiers",
      statement:
        "Servers commonly store a nonce, a session identifier, in a cookie and look up session state by it, which limits the damage if the cookie leaks. Servers should take care to avoid session fixation.",
      sourceId: "rfc6265",
      locator: "Section 8.4, Session Identifiers",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "shared-cache-authorization",
      statement:
        "A shared cache must not use a cached response to a request with Authorization to satisfy another request unless the response carries must-revalidate, public, or s-maxage.",
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
      note: "Sections 3.3, 5.3, 6, 6.1, 6.2, 6.4.1, 7.2, 9.2.1, 9.2.2, 9.3.3–9.3.5, 9.3.7, 15, 15.2.1, 15.3.3, 15.5.2–15.5.5, and 15.6.3–15.6.5: semantics, methods, and status codes.",
    },
    { sourceId: "rfc9112", note: "Sections 2.1, 3.2, and 6.3: HTTP/1.1 message format and Host." },
    { sourceId: "rfc9111", note: "Section 3.5: caching and Authorization." },
    { sourceId: "rfc6265", note: "Sections 3, 4.1.2.5, 4.1.2.6, and 8.4: cookies and sessions." },
  ],
} satisfies LessonBody;
