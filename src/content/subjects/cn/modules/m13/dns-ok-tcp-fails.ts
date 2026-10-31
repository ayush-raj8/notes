import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.dns-ok-tcp-fails",
  status: "complete",
  objectives: [
    "Separate a good DNS answer from a failed TCP handshake to that address.",
    "Classify timeout, refused, and reset at connect time.",
    "Use ss and tcpdump to find where the SYN stops.",
  ],
  prerequisites: ["m13.method"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• The name resolves to an address, but `connect()` times out, is refused, or is reset.\n• Operators often blame DNS again or dig into TLS before TCP is proven.",
      whyItMatters:
        "• Wrong layer burns the incident. A correct A/AAAA record does not imply a listener or an open path.",
      idea: "• DNS answered; the failure is on the path to IP:port or at the listener.\n• Timeout ⇒ silence. Refused/RST ⇒ something answered. Capture both ends.",
      howItWorks:
        "• Confirm the address the client used matches `dig`/`getent`.\n• `ss -ltnp` on the server: listening address and port.\n• `tcpdump` for SYN in and SYN-ACK/RST out.\n• Check host firewall / security group / routing on the hop where packets stop.",
      whyItWorks:
        "• Resolution only maps names to addresses; TCP needs a route, an allow rule, and a listening socket.",
      limits:
        "• Happy Eyeballs can hide IPv6 path failure behind a slow IPv4 success.\n• Load balancers may accept TCP while backends are down (later scenarios).",
      connections:
        "• Modules 4–5: ICMP and handshake.\n• Module 10: DROP vs REJECT, bind to loopback.\n• Next: TCP works but TLS fails.",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "Nothing listens on that address:port, or the process listens only on 127.0.0.1.",
        "A filter drops SYNs (security group, iptables DROP, ACL) so the client times out.",
        "A filter or host rejects with RST / ICMP, so the client sees refused.",
        "Routing or asymmetric return path: SYN arrives, SYN-ACK never reaches the client.",
        "Wrong address from DNS (stale, split-horizon, or typo) that is reachable for ICMP but not that port.",
        "SYN backlog / listen queue full so new handshakes stall or reset (see accept-stall).",
      ],
      order: [
        "Record the exact IP:port from the client error and from `getent`/`dig`.",
        "From a host just outside the server, try `curl -v --connect-timeout 3 telnet://IP:PORT` or a raw connect.",
        "On the server: `ss -ltnp` for the port and bound address.",
        "Capture both ends: does SYN arrive? Does SYN-ACK or RST leave?",
        "At the last hop that sees the SYN, check firewall counters and routes.",
      ],
      commands: [
        {
          command: "dig +short api.example.com A",
          shows: "Addresses DNS returns. Compare with the client's logged peer address.",
        },
        {
          command: "ss -ltnp 'sport = :443'",
          shows: "Whether anything listens and on which address (0.0.0.0 vs 127.0.0.1).",
        },
        {
          command: "tcpdump -ni eth0 'tcp port 443 and tcp[tcpflags] & (tcp-syn|tcp-rst) != 0'",
          shows: "SYNs, SYN-ACKs, and RSTs on the wire.",
        },
        {
          command: "ip route get 198.51.100.10",
          shows: "The route and egress interface the kernel would use toward that address.",
        },
        {
          command: "curl -v --connect-timeout 3 https://198.51.100.10/",
          shows: "Connect timing without relying on the name; TLS may still fail afterward.",
        },
      ],
      implications: [
        "SYN never reaches the server: drop or wrong route upstream.",
        "SYN arrives, no reply: host firewall DROP or no listener on that address.",
        "RST from the server address: nothing listening or REJECT tcp-reset.",
        "SYN-ACK leaves, client times out: return path or middlebox dropping the reply.",
      ],
      distinguishing: [
        "DNS wrong vs path: connect by the dig'd IP fails the same way → not DNS.",
        "DROP vs dead host: local ARP/neighbour fails for dead on-link host; DROP still increments counters.",
        "Refused vs timeout: refused means an answer; timeout means silence.",
      ],
      mistakes: [
        "Re-checking DNS forever because the ticket said 'can't resolve' when the client log shows an IP.",
        "Testing from the server itself, which skips security groups and NAT.",
        "Blaming TLS when `connect()` never returned success.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "refused",
          prompt: "`dig` is fine; client gets 'connection refused'. First command on the server?",
          answer: "`ss -ltnp` for that port—likely nothing listening or bound to loopback only.",
        },
        {
          id: "timeout-path",
          prompt: "SYN arrives on the server NIC; no SYN-ACK leaves. Where is the problem?",
          answer:
            "On the host: firewall DROP toward that socket, or no listener on the destination address.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ss-listening",
      statement: "ss lists listening sockets; -p can show the owning process.",
      sourceId: "man-ss",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "tcpdump-filter",
      statement: "tcpdump can capture packets matching an expression on an interface.",
      sourceId: "man-tcpdump",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "man-ss", note: "Listener address and port." },
    { sourceId: "man-tcpdump", note: "SYN/RST evidence." },
    { sourceId: "bind-dig", note: "Confirm resolved addresses." },
    { sourceId: "man-ip-route", note: "ip route get." },
    { sourceId: "curl-manpage", note: "Connect timeout testing." },
  ],
} satisfies LessonBody;
