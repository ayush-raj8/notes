import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m10.firewalls",
  status: "complete",
  objectives: [
    "Contrast stateless and stateful packet filters and say what each must allow for a reply to get back.",
    "Define inbound and outbound relative to a filter, and map them to Linux INPUT, OUTPUT, and FORWARD.",
    "Read conntrack states NEW, ESTABLISHED, RELATED, and INVALID in a rule set.",
    "Predict what a client sees for DROP versus REJECT, and why blocking all ICMP breaks things.",
  ],
  prerequisites: ["m10.nat"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• A network border must pass legitimate traffic and refuse the rest.\n• Each packet alone does not say whether it answers something the inside asked for.\n• Rules written per packet must still let whole conversations work in both directions.",
      whyItMatters:
        "• Most 'connection timed out' tickets end at a filter: host iptables/nftables, a cloud security group, a corporate edge.\n• A filter that is too loose exposes services; one that is too strict breaks PMTU discovery, DNS, or replies.",
      idea: "• A firewall screens traffic: pass, drop, or handle it itself (RFC 2979).\n• Stateless: each packet matched alone on addresses, ports, protocol, flags.\n• Stateful: tracks flows, so 'reply to something we allowed' is a rule.\n• Direction is relative to the filter and to who started the flow, not to which way one packet travels.",
      howItWorks:
        "• Stateless: allowing a TCP client out means also allowing inbound packets from port 443 to the client's ephemeral ports.\n• Stateful (Linux): conntrack labels each packet NEW, ESTABLISHED, RELATED, or INVALID. Rule: accept ESTABLISHED,RELATED; decide only on NEW.\n• Linux chains: INPUT (to local sockets), OUTPUT (from local sockets), FORWARD (routed through).\n• Verdicts: ACCEPT, DROP (silent), REJECT (ICMP error or TCP RST back).",
      whyItWorks:
        "• Conversations, not packets, are what policy is about. Tracking state lets one NEW decision cover every later packet.\n• RELATED lets ICMP errors and helper-tracked data connections in without opening ports for them.\n• Deciding once per flow is also cheaper than evaluating the whole rule list per packet.",
      limits:
        "• State costs memory; a full table drops new flows.\n• Asymmetric routing: a stateful filter that sees only one direction marks the other INVALID.\n• Port-based rules do not know what protocol really runs on a port, or what is inside TLS.\n• A filter does not stop attacks from inside or in allowed traffic.",
      connections:
        "• NAT lesson: same conntrack table, and why NAT is not a filter (why.38).\n• Module 4: ICMP errors and PMTU. Module 3: PMTU black holes (why.39).\n• Connectivity-failures lesson: DROP vs REJECT symptoms.\n• Module 13: DNS resolves, TCP fails.",
    },
    {
      type: "comparison",
      id: "stateless-vs-stateful",
      title: "Stateless vs stateful",
      columns: ["Stateless", "Stateful"],
      rows: [
        {
          label: "Decision unit",
          cells: ["Each packet, alone.", "Each flow; later packets follow the flow's verdict."],
        },
        {
          label: "Allow a client out to :443",
          cells: [
            "Out: dst port 443. In: src port 443 to dst ports 1024–65535 (and hope).",
            "Out: NEW to dst port 443. In: ESTABLISHED,RELATED.",
          ],
        },
        {
          label: "Unsolicited inbound",
          cells: [
            "Blocked only if no rule happens to match its ports.",
            "No flow state, so it is NEW and must match an explicit inbound rule.",
          ],
        },
        {
          label: "Cost",
          cells: ["No per-flow memory. Easy in hardware.", "Table per flow; timers; can fill up."],
        },
        {
          label: "Examples",
          cells: [
            "Router ACLs, Linux rules without `-m conntrack`, RFC 6092 sanity filters.",
            "iptables/nftables with conntrack, home gateways, most host firewalls.",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "direction",
      title: "Inbound vs outbound",
      steps: [
        {
          title: "Relative to the filter",
          body: "• Inbound: from the outside interface toward the protected side. Outbound: the reverse.\n• On a host, inbound = INPUT, outbound = OUTPUT. On a router, both are FORWARD; match `-i`/`-o` to tell them apart.",
        },
        {
          title: "Relative to who started the flow",
          body: "• A reply to an outbound request travels inbound, but it belongs to an outbound flow.\n• Stateful rules key on the first packet: an outbound NEW flow lets its inbound replies through as ESTABLISHED.",
        },
        {
          title: "Default policy",
          body: "• Typical edge: outbound NEW allowed, inbound NEW denied except listed services (RFC 6092's home default).\n• Egress filtering (restricting outbound NEW) limits what a compromised inside host can reach.",
        },
        {
          title: "Errors and control traffic",
          body: "• ICMP 'fragmentation needed' arrives inbound for an outbound flow. RFC 2979: do not block it, or PMTU discovery black-holes.\n• On Linux these arrive as RELATED.",
        },
      ],
    },
    {
      type: "example",
      id: "host-ruleset",
      title: "A minimal stateful host firewall",
      setup:
        "A Linux API server must accept SSH from 10.0.0.0/8 and HTTPS from anywhere, and make outbound calls freely. iptables syntax; nftables is equivalent.",
      walkthrough: [
        "• `iptables -P INPUT DROP`: inbound default is drop.",
        "• `iptables -A INPUT -i lo -j ACCEPT`: loopback traffic.",
        "• `iptables -A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT`: replies to the server's own outbound calls, and ICMP errors for its flows.",
        "• `iptables -A INPUT -m conntrack --ctstate INVALID -j DROP`: packets matching no known flow.",
        "• `iptables -A INPUT -p tcp --dport 22 -s 10.0.0.0/8 -m conntrack --ctstate NEW -j ACCEPT` and the same for `--dport 443` without `-s`.",
        "• OUTPUT left at ACCEPT, so outbound NEW is allowed. Each outbound flow's replies come back via the ESTABLISHED rule.",
        "• A probe to port 5432 is NEW, matches nothing, hits the DROP policy. The scanner sees a timeout, not a refusal.",
      ],
      result:
        "• Four rules give 'outbound free, inbound only 22 and 443'. Without the ESTABLISHED rule, every outbound call would time out waiting for its replies.",
    },
    {
      type: "comparison",
      id: "drop-vs-reject",
      title: "What the client sees",
      columns: ["Client symptom", "Use when"],
      rows: [
        {
          label: "DROP",
          cells: [
            "SYN retransmits until connect timeout (seconds to minutes).",
            "Unwanted traffic from untrusted sources; INVALID packets.",
          ],
        },
        {
          label: "REJECT (icmp-port-unreachable, default)",
          cells: [
            "Immediate error. TCP: connection refused or host unreachable, depending on the ICMP type and stack.",
            "Internal networks where fast failure beats stealth.",
          ],
        },
        {
          label: "REJECT --reject-with tcp-reset",
          cells: ["Immediate 'connection refused', like a closed port.", "TCP-only rules."],
        },
      ],
    },
    {
      type: "callout",
      id: "reject-invalid",
      tone: "caution",
      title: "Do not REJECT INVALID packets",
      body: "• A delayed original segment can arrive after its retransmission already advanced the flow, and be classified INVALID.\n• Answering it with a RST kills a healthy connection. iptables-extensions(8): DROP INVALID, do not REJECT it.",
      scope: "linux",
    },
    {
      type: "expandable",
      id: "firewall-details",
      title: "Details: UDP state, timeouts, cloud filters",
      paragraphs: [
        "UDP has no handshake. A stateful filter creates state on the first outbound datagram and keeps it on a timer; RFC 6092 requires outbound packets to refresh it. Linux defaults: 30 s, or 120 s once conntrack detects a UDP stream.",
        "TCP idle state: RFC 6092 says a filter that expires established flows must wait at least 2 h 4 min, matching RFC 5382 for NAT. Many middleboxes use less.",
        "Unsolicited inbound SYN (RFC 6092, RFC 5382): wait at least 6 seconds before answering with an ICMP error, so simultaneous open can still work.",
        "Stateless sanity filters still apply on stateful devices: drop spoofed source addresses from outside, multicast sources, and reserved destinations (RFC 6092 Section 3.1).",
        "Cloud filters follow the same split: per-instance security groups usually behave statefully, subnet ACLs often statelessly, so ACLs need explicit return-port rules. Check your provider's documentation; no provider page is cited here.",
        "The transparency rule (RFC 2979): adding a firewall must not break legitimate, standards-compliant use. Blocking all ICMP violates it by breaking PMTU discovery.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "stateless-return",
          prompt:
            "A stateless ACL allows outbound TCP to port 443 and nothing inbound. HTTPS from inside times out. Why, and the stateless fix?",
          answer:
            "SYN-ACKs and data come back inbound from port 443 and are dropped. Allow inbound TCP from source port 443 to the ephemeral range, or move to a stateful filter.",
        },
        {
          id: "forward-chain",
          prompt:
            "Rules in INPUT on a Linux router allow port 80, but traffic to a server behind the router is still dropped. Where should the rule go?",
          answer:
            "FORWARD. INPUT only sees packets for the router's own sockets; routed traffic traverses FORWARD.",
        },
        {
          id: "drop-or-reject",
          prompt:
            "Clients report connects hanging 2 minutes before failing on a blocked internal port. What changed in the firewall, and what would make them fail fast?",
          answer:
            "The rule DROPs, so SYNs are silently discarded and the client waits for its connect timeout. REJECT (ICMP or tcp-reset) makes connect fail immediately.",
        },
        {
          id: "related",
          prompt: "Why does a stateful rule set accept RELATED, not just ESTABLISHED?",
          answer:
            "ICMP errors for an existing flow, such as fragmentation needed, are new packets tied to that flow. Without RELATED they are dropped and PMTU discovery breaks.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "firewall-definition",
      statement:
        "A firewall screens traffic, blocking what it considers inappropriate or dangerous. Firewall functions are disjoint from NAT functions. A packet filter passes a packet unchanged, drops it, or handles it itself, typically deciding on IP addresses and ports.",
      sourceId: "rfc2979",
      locator: "Section 1, Introduction; Section 2, Characteristics",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "firewall-icmp",
      statement:
        "A firewall must not cause unintended failures of legitimate standards-compliant usage. A packet filter that permits outbound packets with DF set must not block the inbound ICMP Fragmentation Needed errors for them, or PMTU discovery black-holes.",
      sourceId: "rfc2979",
      locator: "Section 3; Section 3.1.1, Path MTU Discovery and ICMP",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "conntrack-states",
      statement:
        "The conntrack match exposes states: INVALID (no known connection), NEW (started a connection or not yet seen both directions), ESTABLISHED (seen both directions), RELATED (a new connection tied to an existing one, such as an FTP data transfer or an ICMP error), and UNTRACKED.",
      sourceId: "man-iptables-extensions",
      locator: "MATCH EXTENSIONS: conntrack, States for --ctstate",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "reject-target",
      statement:
        "REJECT sends back an error packet and otherwise acts like DROP. It is valid in INPUT, FORWARD, and OUTPUT. The default is icmp-port-unreachable; tcp-reset sends a TCP RST. INVALID packets should be dropped, not rejected, because a late original segment may be INVALID and a reject would kill the healthy connection.",
      sourceId: "man-iptables-extensions",
      locator: "TARGET EXTENSIONS: REJECT (IPv4-specific)",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "filter-chains",
      statement:
        "The filter table is the default and has INPUT (packets destined to local sockets), FORWARD (packets routed through the box), and OUTPUT (locally generated packets).",
      sourceId: "man-iptables",
      locator: "TABLES, filter",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "rfc6092-stateful",
      statement:
        "Residential IPv6 gateways should require filter state to be activated by traffic from interior nodes, with manual exceptions. Stateless filters drop packets such as multicast sources regardless of state. Connection-free (UDP) state is created on initiation and abandoned after inactivity, and must be refreshed by outbound packets.",
      sourceId: "rfc6092",
      locator: "Section 2, Overview; Sections 3.1, 3.2, and REC-16",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rfc6092-tcp",
      statement:
        "A stateful TCP filter must wait at least 6 seconds before answering an unsolicited inbound SYN with an error, and if it expires idle established flows, the idle-timeout must not be less than two hours four minutes.",
      sourceId: "rfc6092",
      locator: "Section 3.3.1, TCP Filters",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "conntrack-udp-timeouts",
      statement:
        "Linux conntrack UDP timeouts default to 30 seconds, and 120 seconds once a UDP stream is detected.",
      sourceId: "kernel-nf-conntrack-sysctl",
      locator: "nf_conntrack_udp_timeout, nf_conntrack_udp_timeout_stream",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc2979", note: "Sections 1, 2, 3, and 3.1.1." },
    { sourceId: "rfc6092", note: "Sections 2, 3.1, 3.2, and 3.3.1." },
    { sourceId: "man-iptables", note: "The filter table and its chains." },
    { sourceId: "man-iptables-extensions", note: "conntrack match states; REJECT target." },
    { sourceId: "kernel-nf-conntrack-sysctl", note: "UDP timeouts." },
  ],
} satisfies LessonBody;
