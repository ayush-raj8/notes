import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.tls-ok-http-5xx",
  status: "complete",
  objectives: [
    "Treat 502/503/504 from a gateway as a hop past TLS, not a network-down signal.",
    "Separate empty pools, bad upstreams, and slow backends.",
    "Bypass the balancer to test a backend directly.",
  ],
  prerequisites: ["m13.tcp-ok-tls-fails"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• TLS to the edge succeeds; the response is 502, 503, or 504.\n• The secure channel is fine; the proxy could not get a usable upstream answer.",
      whyItMatters:
        "• Packet captures on the client prove nothing about backends.\n• Health checks can still be green while user requests fail (why.53).",
      idea: "• 502: invalid or reset upstream response. 503: unavailable / shedding. 504: upstream too slow for the proxy.\n• Prove which hop failed: edge logs and a direct backend hit.",
      howItWorks:
        "• Read status and proxy headers (`Server`, Via).\n• Check balancer upstream health and recent deploy.\n• `curl` a backend with `--resolve` or internal URL.\n• Compare health-check path vs the failing user path.",
      whyItWorks:
        "• The client already completed TCP+TLS to the gateway; remaining failure is between gateway and origin or in the origin app.",
      limits:
        "• Some CDNs map many failures to one status. Always read their error body/code.\n• 503 can also be the origin itself shedding load.",
      connections:
        "• Module 8 intermediaries; Module 10 load balancing.\n• why.20: handshake ≠ application success.",
    },
    {
      type: "why",
      id: "why-handshake",
      questionId: "why.20",
    },
    {
      type: "why",
      id: "why-health",
      questionId: "why.53",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "No healthy upstream in the pool (all marked down, empty pool, wrong target group).",
        "Upstream resets or closes during the request (crash, panic, connection limit).",
        "Upstream slower than the proxy's server timeout → 504.",
        "Upstream returns an invalid response the proxy will not forward → 502.",
        "TLS or HTTP mismatch between proxy and backend (HTTPS backend expected plain HTTP).",
        "Health check hits a cheap path that stays green while `/api` is broken.",
      ],
      order: [
        "Confirm client TLS to the edge succeeded and note 502 vs 503 vs 504.",
        "Read balancer/access logs for upstream status and chosen backend.",
        "Inspect pool health and last check failures.",
        "Hit one backend directly from a host that can reach it.",
        "Compare health-check URL and headers with the failing request.",
      ],
      commands: [
        {
          command: "curl -vI https://edge.example.com/api",
          shows: "TLS OK plus gateway status; Server/Via may name the proxy.",
        },
        {
          command: "curl -v --resolve api.internal:8080:10.0.2.5 http://api.internal:8080/api",
          shows: "Backend behaviour with the balancer bypassed.",
        },
        {
          command: "curl -v https://edge.example.com/healthz",
          shows: "Whether the edge health path still succeeds while /api returns 5xx.",
        },
      ],
      implications: [
        "502 with healthy pool: look at that request's upstream reset or protocol error.",
        "503 with empty/down pool: restore backends before tuning timeouts.",
        "504: backend or dependency latency vs proxy timeout—not client DNS.",
        "Direct backend OK, edge 5xx: proxy config, routing, or TLS to upstream.",
      ],
      distinguishing: [
        "502 vs 504: bad/reset response vs waited too long.",
        "Origin 503 vs proxy 503: body and Server header; hit origin directly.",
        "Health green vs user fail: different path, method, or dependency.",
      ],
      mistakes: [
        "Raising client timeouts when the gateway already returned 504.",
        "Trusting only the LB health dashboard without matching the user URL.",
        "tcpdump on the client for an error generated inside the data centre.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "which-status",
          prompt: "TLS works; every request gets 504. What does that say?",
          answer:
            "The gateway gave up waiting for an upstream. Investigate backend/dependency latency and proxy timeouts—not the client's TLS.",
        },
        {
          id: "health-pass",
          prompt: "Why can health checks pass while users get 502?",
          answer:
            "Checks use a different path/schedule; backends can fail between checks; user requests need dependencies the check skips (why.53).",
        },
      ],
    },
  ],
  claims: [
    {
      id: "http-502",
      statement:
        "502 Bad Gateway means a gateway or proxy received an invalid response from an inbound server.",
      sourceId: "rfc9110",
      locator: "Section 15.6.3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "http-504",
      statement:
        "504 Gateway Timeout means a gateway or proxy did not receive a timely response from an upstream server.",
      sourceId: "rfc9110",
      locator: "Section 15.6.5",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc9110", note: "502 and 504 meanings." },
    { sourceId: "haproxy-config-2-8", note: "Upstream and health-check behaviour." },
    { sourceId: "nginx-upstream", note: "Passive failure accounting." },
    { sourceId: "curl-manpage", note: "Direct backend tests." },
  ],
} satisfies LessonBody;
