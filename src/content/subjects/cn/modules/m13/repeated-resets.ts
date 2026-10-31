import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.repeated-resets",
  status: "complete",
  objectives: [
    "Interpret RST as an active rejection, not silence.",
    "Find which side sends the RST and why.",
    "Separate listen absence, policy REJECT, and application abort.",
  ],
  prerequisites: ["m13.ip-ok-name-fails"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Connections die with 'connection reset by peer' again and again.\n• Timeout debugging does not apply—something answers with RST.",
      whyItMatters:
        "• RST names a sender; that address is the first clue.\n• Repeated RST often means a policy or a crash loop, not random loss.",
      idea: "• Capture who sends RST (sequence, TTL, MAC/IP).\n• Map to: no listener, REJECT, abortive close, or middlebox.",
      howItWorks:
        "• SYN → RST: nothing listening or REJECT tcp-reset.\n• During data: peer called close with SO_LINGER 0, crashed, or policy killed the flow.\n• After idle: NAT/firewall may RST packets with no state.",
      whyItWorks:
        "• TCP uses RST to abort; the segment's source address is evidence of which hop reacted.",
      limits:
        "• Spoofed RSTs exist on the open Internet; inside a VPC they are rarer.\n• Some proxies translate upstream reset into HTTP 502.",
      connections: "• Module 5 termination; Module 10 REJECT vs DROP; Module 12 SO_LINGER.",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "Nothing listening on the port (kernel RST to SYN).",
        "Firewall REJECT with tcp-reset.",
        "Application crash or abortive close on each request.",
        "Load balancer or mesh killing unhealthy upstream connections.",
        "Idle timeout on NAT/firewall sending RST on next packet.",
        "Sequence/ACK invalid to a middlebox that RSTs (less common on Linux servers).",
      ],
      order: [
        "Reproduce; note whether RST is on SYN or mid-stream.",
        "`tcpdump` both ends; read RST source IP and TTL.",
        "`ss -ltnp` at the moment of SYN if handshake never completes.",
        "Check firewall REJECT rules and proxy upstream logs.",
        "If mid-stream: app logs, OOM kills, and SO_LINGER settings.",
      ],
      commands: [
        {
          command: "tcpdump -ni any 'tcp[tcpflags] & tcp-rst != 0'",
          shows: "RST sender address, ports, and timing relative to SYN/FIN.",
        },
        {
          command: "ss -ltnp 'sport = :8080'",
          shows: "Listener presence when SYN-RST loops happen.",
        },
        {
          command: "iptables -L -n -v | head",
          shows: "REJECT counters climbing with each attempt.",
        },
        {
          command: "curl -v http://api.example.com/",
          shows: "Client-facing 'Connection reset' vs HTTP 502 from a proxy.",
        },
      ],
      implications: [
        "RST from server IP on SYN: no listener or host REJECT.",
        "RST from a different hop IP: middlebox policy.",
        "Mid-stream RST after request: app abort or proxy killing upstream.",
        "Only after idle: state expiry, not startup config.",
      ],
      distinguishing: [
        "DROP vs RST: silence/timeout vs immediate reset.",
        "App vs kernel: RST after HTTP bytes vs on bare SYN.",
        "Proxy 502 vs raw RST: HTTP layer saw the reset first.",
      ],
      mistakes: [
        "Chasing packet loss when every attempt gets an RST.",
        "Ignoring TTL/source IP of the RST.",
        "Restarting clients instead of the crashing server process.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "syn-rst",
          prompt: "Every SYN gets RST from the server IP. First checks?",
          answer: "`ss -ltnp` for a listener; then host REJECT rules. Not MTU or DNS.",
        },
        {
          id: "idle-rst",
          prompt: "Long-idle connections die with RST on next write. Suspect?",
          answer:
            "NAT/firewall dropped state and rejects the next packet; lower idle timeouts or send keepalives.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "tcp-rst",
      statement:
        "TCP uses the RST control bit to abort a connection; a reset segment indicates the sender is unwilling to continue the connection.",
      sourceId: "rfc9293",
      locator: "Section 3.5.1; Section 3.10.7.4",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "reject-rst",
      statement: "iptables REJECT can send a TCP RST (tcp-reset) instead of an ICMP error.",
      sourceId: "man-iptables-extensions",
      locator: "TARGET EXTENSIONS: REJECT (IPv4-specific)",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc9293", note: "RST semantics." },
    { sourceId: "man-iptables-extensions", note: "REJECT tcp-reset." },
    { sourceId: "man-tcpdump", note: "Who sent the RST." },
    { sourceId: "man-ss", note: "Listener state." },
  ],
} satisfies LessonBody;
