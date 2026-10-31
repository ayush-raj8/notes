import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m10",
  status: "complete",
  readingMinutes: { min: 12, max: 18 },
  thesis:
    "Devices at the network edge rewrite addresses, filter flows, and spread requests across servers, and each of those jobs keeps per-connection state you must be able to reason about.",
  outcomes: [
    "Explain SNAT, DNAT, and PAT, and say what a connection-tracking entry stores.",
    "Say why NAT is not a firewall and how stateful filtering differs from stateless filtering.",
    "Compare layer 4 and layer 7 load balancing, including health checks and persistence.",
    "Describe what a reverse proxy terminates and what it forwards.",
    "Sort an edge connectivity failure into translation, filtering, or backend selection.",
  ],
  coverage: [
    {
      bullet: "SNAT and DNAT.",
      notes:
        "• SNAT rewrites the source address on the way out (private host → public address).\n• DNAT rewrites the destination on the way in (public VIP:port → internal host:port).\n• Rewrites touch IP (and often port) headers and force checksum updates.\n• Return traffic is un-rewritten from saved state, not from a fresh routing decision.\n• Detail: why.37.",
    },
    {
      bullet: "Port address translation.",
      notes:
        "• PAT (NAPT) lets many private hosts share one public IP by also rewriting the source port.\n• The translator picks a unique public port per flow and records the mapping.\n• Capacity is bounded by ports per public IP per destination tuple.\n• Inbound packets with no mapping have nowhere to go, so unsolicited inbound is dropped.",
    },
    {
      bullet: "NAT tables and connection tracking.",
      notes:
        "• Entry holds roughly: protocol, original tuple, translated tuple, state, timeout.\n• Created on the first outbound packet; both directions match the same entry.\n• TCP entries follow handshake/teardown; UDP/ICMP use idle timers.\n• Table full or timeout too short → drops or silently broken long-idle connections.\n• Detail: why.38.",
    },
    {
      bullet: "Stateful vs stateless firewalls.",
      notes:
        "• Stateless: judges each packet alone against rules (src, dst, port, flags).\n• Stateful: remembers flows, so a reply to an allowed outbound connection is allowed back automatically.\n• Stateful needs a table (memory, timeouts); stateless needs explicit rules for both directions.\n• Stateful state is the same kind of table that NAT keeps, often shared in one component.",
    },
    {
      bullet: "Inbound vs outbound traffic.",
      notes:
        "• Direction is defined by who opened the connection, not by packet direction.\n• Typical policy: allow outbound-initiated flows and their replies; deny unsolicited inbound.\n• Servers exposed to the internet need an explicit inbound rule (and a DNAT or public address).\n• Egress rules matter too: they limit exfiltration and cap what a compromised host can reach.",
    },
    {
      bullet: "Layer 4 vs Layer 7 load balancing.",
      notes:
        "• L4: picks a backend per connection from IP/port; bytes pass through untouched (TLS and HTTP opaque).\n• L7: parses the protocol (HTTP, TLS SNI) and routes per request by host, path, or header.\n• L7 can retry, rewrite, and cache; costs more CPU and holds two connections per client.\n• L4 is cheaper and protocol-agnostic; it cannot see request-level failures.\n• Detail: why.53.",
    },
    {
      bullet: "Health checks.",
      notes:
        "• Active: balancer probes a TCP port or HTTP path on an interval.\n• Passive: balancer watches real traffic for errors and timeouts.\n• Thresholds (fail N, pass M) trade detection speed against flapping.\n• A shallow check (port open) can pass while the app is broken; a deep check can take a healthy node down because of a dependency.",
    },
    {
      bullet: "Connection persistence.",
      notes:
        "• Sticky routing keeps one client on one backend (source IP hash, cookie, session id).\n• Needed when backend holds local session state; avoid it by moving state out.\n• Source-IP stickiness behaves badly behind a shared NAT: many users look like one client.\n• Stickiness skews load and complicates draining a backend.",
    },
    {
      bullet: "Reverse proxies.",
      notes:
        "• Accepts client connections, then opens separate connections to backends.\n• Common jobs: TLS termination, routing by host/path, compression, caching, rate limiting.\n• Backend sees the proxy as the client; the real address travels in headers (X-Forwarded-For) or PROXY protocol.\n• Adds a hop: another timeout, another place to return 502/503/504.",
    },
    {
      bullet: "Common causes of connectivity failures.",
      notes:
        "• Missing or wrong DNAT / port-forward; backend not listening on the translated port.\n• Firewall or security-group rule missing, or asymmetric return path bypassing the stateful device.\n• NAT table exhausted or mapping expired during idle.\n• All backends marked unhealthy, or health check path differs from real traffic.\n• Sticky or hashed routing sending everyone to one dead node.\n• First commands: `ss -ltn` on the backend, `curl -v` through the VIP, `tcpdump` on both sides of the translator.",
    },
    {
      bullet: "Why NAT is not equivalent to a firewall.",
      notes:
        "• NAT changes addresses; a firewall decides allow/deny. Different purposes.\n• Unsolicited inbound is dropped because no mapping exists, a side effect, not a policy.\n• A static DNAT or port forward exposes the host with no filtering at all.\n• Hosts behind NAT still need a host firewall; IPv6 usually has no NAT yet is still protected by firewalling.\n• Detail: why.38.",
    },
  ],
  mentalModel:
    "• Picture an edge box with a table: [original tuple] ↔ [translated tuple] + state + timer.\n• Outbound packet creates the entry and is rewritten (SNAT/PAT); the reply matches the entry and is un-rewritten.\n• Inbound new flow: DNAT or load-balancer rule picks the backend; the filter decides whether it is allowed at all.\n• Three separate questions on every flow: translate? (NAT), permit? (firewall), which backend? (load balancer).\n• L4 balancers forward connections; L7 proxies terminate and re-originate them.",
  failuresAndSignals: [
    "NAT table exhaustion or port exhaustion: new outbound connections fail or time out while old ones work. Look for conntrack-full logs and drops that correlate with connection rate.",
    "Idle mapping expired: a long-lived idle TCP connection looks open on both ends but the next packet gets RST or is dropped. Fix with keepalives shorter than the NAT idle timeout.",
    "Health check passes but users get errors: check hits `/` or a port, real traffic hits a broken dependency. Signal is 5xx at the proxy with healthy backends in the pool.",
    "Source-IP stickiness behind a shared NAT or CGNAT: one backend overloaded, others idle. Signal is skewed per-backend request counts.",
    "Port forward added but nothing listens or a firewall rule is missing: SYN gets RST (nothing listening) or silence (filtered). Distinguish with `curl -v` and a capture.",
  ],
  whyAnchors: ["why.37", "why.38", "why.53"],
  connections: {
    before:
      "• Module 9: TLS and HTTPS; the L7 and termination choices here decide where TLS ends.\n• Modules 3 and 5: IP addressing and TCP state; NAT and firewalls key on those tuples and flags.",
    after:
      "• Module 11: how latency, loss, and retries behave across the proxies and balancers described here.\n• Module 13: 502/503 and 'works from one machine but not another' investigations start at these edge devices.",
  },
  selfCheck: [
    {
      id: "pat-reply",
      prompt: "How does a PAT device deliver a reply to the right private host?",
      answer:
        "It stored a mapping at the first outbound packet: private IP:port ↔ public IP:unique port. The reply's destination port selects that entry, and the device rewrites the destination back.",
    },
    {
      id: "nat-vs-firewall",
      prompt: "A host behind NAT cannot be reached from outside. Is that proof it is protected?",
      answer:
        "No. The block is a side effect of having no mapping. A static port forward removes it, and nothing in NAT inspects or filters the traffic (why.38).",
    },
    {
      id: "l4-l7",
      prompt: "Why can an L7 balancer retry a failed request but an L4 balancer cannot?",
      answer:
        "L7 parses requests, so it knows request boundaries and can resend one to another backend. L4 only moves bytes on a connection and cannot tell where a request starts or ends (why.53).",
    },
  ],
} satisfies ChapterSummary;
