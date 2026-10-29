import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m10.load-balancing",
  status: "complete",
  objectives: [
    "Compare layer-4 and layer-7 load balancing by what each sees, decides, and terminates.",
    "Estimate how long a failed backend keeps receiving traffic from active-check parameters.",
    "Choose a persistence method and name how NAT and backend changes break it.",
    "Explain what a reverse proxy is and why a passing health check does not prove user success.",
  ],
  prerequisites: ["m10.firewalls"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• One address must front many servers, some of which are failing at any moment.\n• Each new connection or request needs a backend that is alive and not overloaded.\n• Some clients need to return to the same backend (session state, caches).",
      whyItMatters:
        "• Every production service sits behind at least one balancer: cloud LB, ingress, sidecar, or client library.\n• Most 502/503s, uneven load after scale-out, and 'one user always fails' bugs come from balancing choices.",
      idea: "• A load balancer is an intermediary that picks a backend per connection (L4) or per request (L7).\n• Health checks keep a live set; the algorithm picks from it.\n• Persistence overrides the algorithm when a client must stick.\n• An L7 balancer is a reverse proxy: an HTTP gateway acting as the origin.",
      howItWorks:
        "• L4: sees addresses and ports. Forwards bytes (proxy) or rewrites headers (DNAT). One connection → one backend for life.\n• L7: terminates TCP and usually TLS, parses HTTP, routes by host, path, header; can retry, rewrite, cache.\n• Algorithms: round robin, least connections, hash of source IP or key.\n• Active checks: probe each backend every interval; mark down after N failures, up after M successes. Passive checks: count real request failures.",
      whyItWorks:
        "• Spreading work keeps each server under its saturation point.\n• Removing dead backends quickly turns hard failures into a few slow or retried requests.\n• Per-request L7 choice balances even when clients hold one long connection (HTTP/2, gRPC).",
      limits:
        "• Detection takes time: requests hit a dead backend until checks fail enough times.\n• A check proves the probe works, not the user's path (why.53).\n• Source-IP persistence collapses clients behind one NAT onto one backend.\n• Balancer is itself a hop: its own timeouts, connection limits, and 502/504s.\n• L7 needs the TLS keys; L4 cannot see HTTP errors.",
      connections:
        "• Module 8: per-connection vs per-request balancing for HTTP/2 and gRPC (why.54), gateway status codes.\n• NAT lesson: DNAT is the simplest L4 balancer.\n• Connectivity-failures lesson: 'no healthy upstream'.\n• Module 13: TLS succeeds, HTTP returns 502 or 503.",
    },
    {
      type: "comparison",
      id: "l4-vs-l7",
      title: "Layer 4 vs layer 7",
      caption:
        "'Layer 4' and 'layer 7' are industry terms. HAProxy names them `mode tcp` and `mode http`.",
      columns: ["Layer 4", "Layer 7"],
      rows: [
        {
          label: "Sees",
          cells: [
            "IP addresses, ports, protocol. Payload is opaque (including TLS).",
            "Full HTTP request: method, host, path, headers, status.",
          ],
        },
        {
          label: "Choice made",
          cells: ["Once per connection.", "Per request; one client connection can fan out."],
        },
        {
          label: "TLS",
          cells: [
            "Passed through; backend holds the certificate.",
            "Usually terminated at the balancer; may re-encrypt to backends.",
          ],
        },
        {
          label: "Health signal",
          cells: ["TCP connect succeeds or not.", "HTTP status, response time, parse errors."],
        },
        {
          label: "Backend sees client IP",
          cells: [
            "DNAT style: yes, but replies must return through the balancer. Proxy style: no.",
            "No; the balancer's address. Client IP travels in a header such as X-Forwarded-For.",
          ],
        },
        {
          label: "Good for",
          cells: [
            "Non-HTTP (databases, SMTP, SSH), raw throughput, TLS passthrough.",
            "HTTP routing, retries, canaries, gRPC per-call balancing.",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "algorithms",
      title: "Picking a backend",
      steps: [
        {
          title: "Round robin",
          body: "• Each server in turn, by weight. HAProxy `roundrobin`, nginx default.\n• Fair when requests cost about the same.",
        },
        {
          title: "Least connections",
          body: "• Server with fewest active connections. HAProxy `leastconn`, nginx `least_conn`.\n• HAProxy recommends it for long sessions (SQL, LDAP), not short HTTP.",
        },
        {
          title: "Hash",
          body: "• Hash source IP, URI, or a key. Same key → same server while the set is stable.\n• Plain hash: adding or removing a server remaps most keys. Consistent hash (nginx `hash … consistent`): only a few.",
        },
        {
          title: "Then the live set",
          body: "• Any algorithm picks only from servers currently marked up.\n• nginx: on an error it passes the request to the next server, until all are tried.",
        },
      ],
    },
    {
      type: "example",
      id: "detection-window",
      title: "How long a dead backend keeps getting traffic",
      setup:
        "HAProxy backend with `option httpchk GET /healthz` and HAProxy 2.8 defaults: `inter 2000` (2 s), `fall 3`, `rise 2`. Backend B's process hangs at t = 0. Traffic is 300 requests/s spread over 3 backends.",
      walkthrough: [
        "• HTTP checks pass on 2xx or 3xx. Without `option httpchk`, a check is only a TCP connect, which a hung process with an open listen socket may still pass.",
        "• Checks at about t = 0–2, 2–4, 4–6 s fail. With no `timeout check`, `inter` is also the check timeout.",
        "• B is marked down after 3 consecutive failures: roughly 6 s, up to about 8 s depending on where the first check fell.",
        "• Meanwhile B gets about 100 requests/s: ~600–800 requests stall or fail before it leaves the pool.",
        "• Shorten with `fastinter` (faster checks while transitioning), `observe layer7` (count real response errors), or retries on another server.",
        "• Recovery: B returns after `rise 2` passing checks, about 4 s, then gets full traffic at once unless slow-start is configured.",
      ],
      result:
        "• Detection time ≈ interval × fall. Size it against how many requests you can afford to fail, and pair it with passive checks and safe retries.",
    },
    {
      type: "why",
      id: "why-health-check",
      questionId: "why.53",
    },
    {
      type: "comparison",
      id: "persistence",
      title: "Connection persistence",
      columns: ["How it sticks", "Breaks when"],
      rows: [
        {
          label: "Source-IP hash (nginx `ip_hash`, HAProxy `balance source`)",
          cells: [
            "Hash of client address. nginx uses the first three IPv4 octets.",
            "Many users share one NAT address (office, CGN), so they all land on one server. Server set changes remap clients. Mobile clients change IP.",
          ],
        },
        {
          label: "Cookie (HAProxy `cookie … insert`)",
          cells: [
            "Balancer sets a cookie naming the server; client sends it back.",
            "Client drops cookies; non-browser clients; the named server is down (then redispatch).",
          ],
        },
        {
          label: "Long-lived connection",
          cells: [
            "L4: the connection itself is the stickiness.",
            "Connection resets, or scale-out leaves new servers idle until clients reconnect.",
          ],
        },
        {
          label: "None (stateless backends)",
          cells: [
            "Session state in a shared store or a signed token.",
            "Nothing to break. Preferred when you control the app.",
          ],
        },
      ],
    },
    {
      type: "callout",
      id: "reverse-proxy",
      tone: "note",
      title: "Reverse proxy = HTTP gateway",
      body: "• RFC 9110: a gateway ('reverse proxy') acts as the origin server to the client and forwards requests inbound to one or more servers, often for caching or load balancing.\n• So every L7 balancer is a reverse proxy, and every reverse proxy with more than one upstream is a balancer.\n• It ends the client's TCP and TLS. Its own timeouts and limits apply, and it writes the 502/504 when an upstream fails.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "lb-details",
      title: "Details: passive checks, draining, L4 by DNAT",
      paragraphs: [
        "nginx open source has passive checks only: `max_fails` failures within `fail_timeout` mark a server unavailable for `fail_timeout`. Defaults: 1 failure, 10 s. What counts as a failure is set by `proxy_next_upstream`. Active `health_check` is in the commercial version.",
        "HAProxy `observe layer7` adjusts health from real responses; `on-marked-down shutdown-sessions` kills existing connections to a server marked down instead of letting them hang.",
        "Draining: before removing a server, stop sending it new connections and let in-flight ones finish. For HTTP/2 and gRPC, GOAWAY does this (Module 8).",
        "L4 by DNAT: a Linux box DNATs a virtual IP to backends. Replies must pass back through it to be un-DNATed, so the balancer must be the backends' route, or it must also SNAT (then backends lose the client IP).",
        "Health endpoint design: a liveness check should be cheap and local. A readiness check may include critical dependencies, but if every backend shares one failing dependency, all go down together and the balancer has nothing left.",
        "Client-side balancing (gRPC `round_robin`, service meshes) moves the choice into the caller; health then comes from the client's own connection state.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "l4-or-l7",
          prompt:
            "You need to route `/api` and `/static` to different pools, and retry idempotent requests on another backend. L4 or L7, and why?",
          answer:
            "L7. Only a balancer that parses HTTP sees the path and knows request boundaries, so it can route per request and retry a whole request elsewhere.",
        },
        {
          id: "detection",
          prompt:
            "Checks every 5 s, fall 3. A backend dies. Roughly how long until it stops receiving traffic, and two ways to shorten it?",
          answer:
            "About 15 s (up to ~20 s). Shorten interval or fall, use faster checks while transitioning, add passive/observed checks, and retry failed idempotent requests on another server.",
        },
        {
          id: "ip-hash-hotspot",
          prompt:
            "With source-IP persistence, one backend is overloaded during a customer's all-hands. Why?",
          answer:
            "The whole office reaches you through one NAT address (or one /24 with nginx `ip_hash`), so every employee hashes to the same backend. Use cookie persistence or stateless backends.",
        },
        {
          id: "tcp-check-pass",
          prompt:
            "A TCP-connect health check passes, but users get timeouts from that backend. How can both be true?",
          answer:
            "The kernel completes the handshake from the listen backlog even if the application is hung. A TCP check proves only that something accepts connections; use an HTTP check that the app must answer.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "haproxy-modes",
      statement:
        "In HAProxy, mode tcp establishes a full-duplex connection between client and server with no layer-7 examination; it is the default and suits SSL, SSH, SMTP. Mode http analyses each request before connecting to a server, rejects non-compliant requests, and allows layer-7 filtering, processing, and switching.",
      sourceId: "haproxy-config-2-8",
      locator: "Section 4.2, mode",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "haproxy-balance",
      statement:
        "HAProxy roundrobin uses each server in turn by weight. leastconn sends to the server with the fewest connections and is recommended for very long sessions, not short HTTP. source hashes the client IP so the same client reaches the same server while no server goes up or down.",
      sourceId: "haproxy-config-2-8",
      locator: "Section 4.2, balance",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "haproxy-checks",
      statement:
        "HAProxy health-check defaults: inter 2000 ms between checks, also used as the check timeout if timeout check is unset; fall 3 consecutive failures to mark a server dead; rise 2 consecutive successes to mark it operational. Without option httpchk, a check is only a TCP connection; with it, 2xx and 3xx responses pass. observe layer4/layer7 adjusts health from real traffic.",
      sourceId: "haproxy-config-2-8",
      locator: "Section 5.2: inter, fall, rise, observe; Section 4.2: option httpchk",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "haproxy-cookie",
      statement:
        "HAProxy cookie enables cookie-based persistence: the cookie is sent in Set-Cookie and returned by the client. In insert mode HAProxy inserts it in responses when the client has none for an accessible server.",
      sourceId: "haproxy-config-2-8",
      locator: "Section 4.2, cookie",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "haproxy-marked-down",
      statement:
        "on-marked-down shutdown-sessions terminates all connections to a server immediately when it is marked down; actions are disabled by default.",
      sourceId: "haproxy-config-2-8",
      locator: "Section 5.2, on-marked-down",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "nginx-balancing",
      statement:
        "nginx distributes requests by weighted round robin by default and, on an error, passes the request to the next server until all functioning servers are tried. least_conn picks the server with the fewest active connections. ip_hash hashes the first three octets of an IPv4 address or the whole IPv6 address. hash with consistent uses ketama so few keys remap when servers change; without it, most keys may remap.",
      sourceId: "nginx-upstream",
      locator: "Directives: upstream, least_conn, ip_hash, hash",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "nginx-passive",
      statement:
        "nginx max_fails (default 1) failures within fail_timeout (default 10 s) mark a server unavailable for fail_timeout. What counts as a failure is defined by proxy_next_upstream and similar directives. The health_check directive belongs to the commercial subscription.",
      sourceId: "nginx-upstream",
      locator: "Directive server: max_fails, fail_timeout",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "gateway-reverse-proxy",
      statement:
        "A gateway (a.k.a. reverse proxy) acts as an origin server for the outbound connection but forwards received requests inbound to other servers. Gateways are used for caching acceleration and for partitioning or load balancing HTTP services.",
      sourceId: "rfc9110",
      locator: "Section 3.7, Intermediaries",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "haproxy-config-2-8",
      note: "mode, balance, cookie, option httpchk, and server check parameters.",
    },
    {
      sourceId: "nginx-upstream",
      note: "upstream, server parameters, least_conn, ip_hash, hash.",
    },
    { sourceId: "rfc9110", note: "Section 3.7: gateways." },
  ],
} satisfies LessonBody;
