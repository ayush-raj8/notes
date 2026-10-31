import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.large-response-hangs",
  status: "complete",
  objectives: [
    "Link size-dependent hangs to Path MTU black holes and window limits.",
    "Test with controlled payload sizes.",
    "Check ICMP 'too big' filtering and MSS clamping.",
  ],
  prerequisites: ["m13.repeated-resets"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Small requests succeed; large responses stall or time out.\n• Handshake and tiny replies look healthy.",
      whyItMatters:
        "• Classic PMTU black hole: DF set, ICMP filtered, large segments vanish (why.40).\n• Also socket/window limits on long fat paths.",
      idea: "• Vary size deliberately. If only large transfers hang, suspect MTU or window—not auth.\n• Capture: small packets ACKed, full-sized retransmitted forever.",
      howItWorks:
        "• Path MTU Discovery sends DF; without ICMP too-big, TCP keeps large segments.\n• Fix: allow ICMP, clamp MSS, or enable TCP MTU probing.\n• Separately: tiny windows/buffers cap throughput so large bodies look hung.",
      whyItWorks: "• Control packets and short payloads fit a reduced MTU; bulk data does not.",
      limits:
        "• Application bugs that buffer whole bodies can mimic network hangs.\n• Middleboxes that clamp only some paths cause intermittent size bugs.",
      connections: "• Module 3 fragmentation; Module 11 BDP and why.40.",
    },
    {
      type: "why",
      id: "why-pmtu",
      questionId: "why.40",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "PMTU black hole: large DF packets dropped; ICMP Fragmentation Needed / Packet Too Big filtered.",
        "Tunnel/VPN lowers MTU below what endpoints assume.",
        "Send/receive buffers or TCP window too small for the transfer to finish in time.",
        "Proxy or LB buffering limits / timeouts on large bodies.",
        "Application deadlock only when serialising large payloads.",
      ],
      order: [
        "Reproduce with two sizes (e.g. 1 KiB vs 1 MiB) on the same path.",
        "tcpdump: do full-sized segments retransmit without ICMP?",
        "tracepath/traceroute --mtu or check cached PMTU.",
        "Try MSS clamp or lower MTU on a test interface.",
        "Rule out app: same size via a known-good path or localhost.",
      ],
      commands: [
        {
          command: "curl -v -o /dev/null https://api.example.com/small vs /large",
          shows: "Whether failure tracks response size.",
        },
        {
          command: "tcpdump -ni eth0 host 198.51.100.10",
          shows: "Retransmits of large segments; presence/absence of ICMP too-big.",
        },
        {
          command: "tracepath 198.51.100.10",
          shows: "Estimated path MTU along probes.",
        },
        {
          command: "ss -ti dst 198.51.100.10:443",
          shows: "snd_wscale, cwnd, and whether the sender is window-limited.",
        },
      ],
      implications: [
        "Small OK, large retransmits forever: PMTU black hole until timeout.",
        "ICMP too-big arrives: PMTUD can shrink; if still stuck, other causes.",
        "Window stuck tiny: buffer/BDP tuning, not MTU.",
        "Only through VPN: tunnel MTU—clamp MSS on the tunnel.",
      ],
      distinguishing: [
        "MTU vs app: localhost large download works; remote large fails.",
        "MTU vs timeout budget: raising client timeout only delays failure while retransmits continue.",
        "One path only: compare cellular vs office; different middleboxes.",
      ],
      mistakes: [
        "Blaming the application serializer first without a size A/B test.",
        "Disabling DF globally as a permanent fix.",
        "Ignoring tunnels and overlays when the underlay MTU is 1500.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "blackhole",
          prompt: "Handshake works; 20-byte replies work; 64 KiB responses hang. Likely?",
          answer:
            "Path MTU black hole (or similar size filter). Large DF segments dropped; ICMP too-big not reaching the sender (why.40).",
        },
        {
          id: "ab",
          prompt: "First experiment to run?",
          answer:
            "Same client and path, two payload sizes. If only the large one fails, investigate MTU/window—not auth.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "pmtud-blackhole",
      statement:
        "When Path MTU Discovery black holes occur, TCP connections can hang because large packets are dropped and ICMP Destination Unreachable (Fragmentation Needed) never reaches the sender.",
      sourceId: "rfc2923",
      locator: "Section 2.1; Section 2.3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "df-bit",
      statement:
        "IPv4 Path MTU Discovery relies on sending packets with Don't Fragment set and learning from ICMP Fragmentation Needed messages.",
      sourceId: "rfc1191",
      locator: "Section 2; Section 3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc2923", note: "PMTU black holes." },
    { sourceId: "rfc1191", note: "IPv4 PMTUD." },
    { sourceId: "rfc8201", note: "IPv6 PMTUD." },
    { sourceId: "man-tracepath", note: "Path MTU probing." },
    { sourceId: "curl-manpage", note: "Size A/B transfers." },
  ],
} satisfies LessonBody;
