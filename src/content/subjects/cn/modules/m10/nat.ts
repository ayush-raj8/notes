import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m10.nat",
  status: "complete",
  objectives: [
    "Say which address SNAT and DNAT rewrite, in which direction, and where Linux applies each.",
    "Explain how port translation lets many private hosts share one public address, and what limits it.",
    "Read a conntrack entry: original tuple, reply tuple, state, and remaining timeout.",
    "Explain why a NAT's mapping table is not a security policy.",
  ],
  prerequisites: ["m09.limits"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Far more IPv4 hosts than public addresses. Private ranges (RFC 1918) are not routable outside.\n• Inside hosts still need to reach the Internet, and some inside services must be reachable from outside.\n• Replies must find their way back to the one inside host that started the conversation.",
      whyItMatters:
        "• Almost every home, office, cloud VPC egress, and Kubernetes node SNATs traffic.\n• Mapping expiry, port exhaustion, and a full conntrack table cause timeouts that look like network flakiness.\n• Logs and rate limits upstream see the NAT's address, not the client's.",
      idea: "• Rewrite addresses (and ports) at the border, and remember the rewrite per session.\n• SNAT changes the source on the way out. DNAT changes the destination on the way in.\n• Port translation (NAPT/PAT) multiplexes many inside (IP, port) pairs onto one outside IP by giving each a distinct port.\n• The remembered mapping undoes the rewrite on every reply.",
      howItWorks:
        "• First packet of a flow: NAT picks an outside (IP, port), creates a binding, rewrites, and fixes IP and TCP/UDP checksums.\n• Later packets in either direction: look up the binding and apply it. No rule re-evaluation.\n• Linux: the `nat` table is consulted only for a packet that creates a new connection. DNAT in PREROUTING/OUTPUT, SNAT/MASQUERADE in POSTROUTING.\n• Bindings end on FIN/RST plus a wait, or on an idle timer. UDP has no end signal, so only the timer.",
      whyItWorks:
        "• TCP and UDP name a conversation by addresses and ports, so a unique outside port is enough to demultiplex replies.\n• Neither end needs changes: the inside host thinks it owns the outside address it never sees.\n• Per-flow state means rules run once per connection, not once per packet.",
      limits:
        "• Unsolicited inbound traffic has no binding, so inbound services need a static DNAT (port forward).\n• Payloads that carry addresses (FTP, SIP) break without an ALG.\n• A NAT cannot tell idle from dead, so it expires idle bindings; the next packet silently goes nowhere.\n• One outside IP has about 64k ports per protocol per destination; heavy egress runs out.\n• End-to-end IPsec does not survive address rewriting.",
      connections:
        "• Module 3: private address ranges. Module 5: TCP keepalive and idle connections (why.15).\n• Firewalls lesson: the same conntrack state drives stateful filtering.\n• Connectivity-failures lesson: expired mappings and port exhaustion.\n• why.55 (primary in Module 13): two healthy services failing across a NAT boundary.",
    },
    {
      type: "comparison",
      id: "nat-kinds",
      title: "SNAT, DNAT, and PAT",
      caption:
        "PAT is the industry name; RFC 2663 and RFC 3022 call it NAPT. Linux targets are from iptables-extensions(8); nftables has equivalents.",
      columns: ["Rewrites", "Typical use", "Linux"],
      rows: [
        {
          label: "SNAT",
          cells: [
            "Source address on the way out; destination on replies.",
            "Private hosts reaching the Internet through a static public IP.",
            "`-t nat -A POSTROUTING -j SNAT --to-source 203.0.113.7`",
          ],
        },
        {
          label: "MASQUERADE",
          cells: [
            "Like SNAT, to whatever address the outgoing interface has now.",
            "Dynamic (DHCP/PPP) uplinks. Forgets connections when the interface goes down.",
            "`-t nat -A POSTROUTING -o eth0 -j MASQUERADE`",
          ],
        },
        {
          label: "DNAT",
          cells: [
            "Destination address (and optionally port) on the way in; source on replies.",
            "Port forwarding, exposing one inside server, simple L4 load balancing.",
            "`-t nat -A PREROUTING -p tcp --dport 8080 -j DNAT --to-destination 192.168.1.50:80`",
          ],
        },
        {
          label: "PAT / NAPT",
          cells: [
            "Source IP and source port, so many hosts share one outside IP.",
            "Home routers, cloud NAT gateways, Kubernetes egress.",
            "SNAT or MASQUERADE with port mapping; `--random-fully` randomizes the port.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "conntrack-entry",
      title: "One HTTPS connection through a home NAT",
      setup:
        "Laptop 192.168.1.20 opens TCP to 198.51.100.10:443. The router's outside address is 203.0.113.7 and it MASQUERADEs 192.168.1.0/24. Addresses are documentation ranges; the entry is illustrative but uses the conntrack-tools output format.",
      walkthrough: [
        "• SYN leaves the laptop: `192.168.1.20:51000 → 198.51.100.10:443`.",
        "• Router: no entry for this tuple, so the `nat` table runs. MASQUERADE picks 203.0.113.7 and keeps port 51000 if free. SYN leaves as `203.0.113.7:51000 → 198.51.100.10:443`.",
        "• Conntrack stores both directions: `tcp 6 431999 ESTABLISHED src=192.168.1.20 dst=198.51.100.10 sport=51000 dport=443 src=198.51.100.10 dst=203.0.113.7 sport=443 dport=51000 [ASSURED]`.",
        "• The second tuple is what a reply looks like. SYN-ACK to `203.0.113.7:51000` matches it and is rewritten to `192.168.1.20:51000`.",
        "• `431999` is seconds left; Linux's established TCP timeout defaults to 432000 (5 days). UDP flows default to 30 s, or 120 s once seen as a stream.",
        "• A second laptop also using port 51000 to the same server gets a different outside port, say 51001. That is the port translation.",
      ],
      result:
        "• The NAT is a lookup table keyed by both tuples. Anything that breaks or expires that entry breaks the connection, even with both ends healthy.",
    },
    {
      type: "steps",
      id: "linux-path",
      title: "Where Linux applies NAT to a forwarded packet",
      steps: [
        {
          title: "PREROUTING",
          body: "• Conntrack looks up or creates the entry.\n• New flow: `nat` PREROUTING may DNAT. The routing decision then uses the new destination.",
        },
        {
          title: "Routing and FORWARD",
          body: "• Route lookup on the (possibly rewritten) destination.\n• `filter` FORWARD decides allow or drop. NAT does not filter.\n• Forwarding needs `net.ipv4.ip_forward=1`.",
        },
        {
          title: "POSTROUTING",
          body: "• New flow: `nat` POSTROUTING may SNAT/MASQUERADE. The entry is confirmed when the packet leaves.",
        },
        {
          title: "Every later packet",
          body: "• Skips the `nat` rules. Conntrack applies the stored rewrite in both directions.\n• So changing a NAT rule does not affect existing flows until their entries go away (`conntrack -D`).",
        },
      ],
    },
    {
      type: "why",
      id: "why-nat",
      questionId: "why.37",
    },
    {
      type: "why",
      id: "why-nat-not-security",
      questionId: "why.38",
    },
    {
      type: "callout",
      id: "idle-expiry",
      tone: "caution",
      title: "Idle mappings expire",
      body: "• RFC 5382: a NAT may drop an idle established TCP mapping, but not before 2 h 4 min. RFC 4787: UDP mappings not before 2 min (5 min recommended).\n• Real devices, especially cloud NATs and middleboxes, are often shorter. The connection then dies silently.\n• Fix: keepalives (TCP keepalive, HTTP/2 PING, app heartbeats) more often than the shortest idle timer on the path.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "nat-details",
      title: "Details: checksums, ICMP, ports, hairpinning",
      paragraphs: [
        "Checksums: TCP and UDP checksums cover a pseudo-header with both IP addresses, so NAT must patch them. RFC 3022 gives an incremental algorithm. ICMP query checksums do not cover addresses.",
        "ICMP errors: a 'fragmentation needed' or 'port unreachable' quotes the original header. NAT must rewrite that inner header too, or the inside host cannot match the error to its socket.",
        "Fragments: only the first fragment carries ports, so NAPT cannot place later fragments by port. RFC 3022 says outbound fragment translation in NAPT is 'doomed to fail'. Linux conntrack has its own reassembly (see the `nf_conntrack_frag6_*` sysctls for IPv6).",
        "Port choice on Linux SNAT without a range: ports below 512 map below 512, 512–1023 map below 1024, others map to 1024 and up, and the original port is kept when possible.",
        "Mapping behavior (RFC 4787 REQ-1): a NAT must reuse the same outside port for one inside (IP, port) to any destination. That is what lets ICE-style peer-to-peer connectivity work.",
        "Hairpinning (RFC 4787 REQ-9): an inside host that connects to the NAT's own outside address must be looped back to the inside server. Many 'works from outside, not from the office' bugs are a missing hairpin.",
        "Carrier-grade NAT (RFC 6888): ISPs share one address across many subscribers and must support per-subscriber port limits. A user behind CGN can exhaust their port quota.",
        "Application gateways: protocols that send addresses in the payload (FTP PORT, SIP, H.323) need an ALG that rewrites the payload. Encrypted payloads cannot be rewritten.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "snat-vs-dnat",
          prompt:
            "A web server at 10.0.0.5:80 must be reachable at 203.0.113.7:443. Which NAT do you configure, in which Linux chain, and what else must allow it?",
          answer:
            "DNAT in `nat` PREROUTING to 10.0.0.5:80. The `filter` FORWARD chain must also allow the flow, and forwarding must be on. Replies are un-DNATed automatically from the conntrack entry.",
        },
        {
          id: "reply-tuple",
          prompt:
            "In a conntrack entry, the reply tuple's destination is 203.0.113.7 but the original source was 192.168.1.20. What does that tell you?",
          answer:
            "The flow was source-NATed to 203.0.113.7. Replies arrive addressed to the public address and are rewritten back to 192.168.1.20.",
        },
        {
          id: "rule-change",
          prompt:
            "You change a DNAT rule to point at a new backend. Existing clients still hit the old one. Why?",
          answer:
            "Only the first packet of a connection consults the `nat` table. Established flows keep their stored mapping until their conntrack entries end or are deleted with `conntrack -D`.",
        },
        {
          id: "idle-ssh",
          prompt:
            "SSH sessions through a cloud NAT die after a few idle minutes, but busy sessions are fine. Cause and fix?",
          answer:
            "The NAT's idle timer expires the mapping; the next packet has no binding. Send keepalives (for example SSH ServerAliveInterval or TCP keepalive) more often than that timer.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "nat-flavors",
      statement:
        "Traditional NAT is outbound: sessions start from the private network. Basic NAT translates the source IP of outbound packets and the destination IP of inbound packets, plus checksums. NAPT also translates transport identifiers (TCP/UDP ports, ICMP query IDs) so many hosts share one external address.",
      sourceId: "rfc2663",
      locator: "Sections 4.1, 4.1.1, and 4.1.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "nat-session-end",
      statement:
        "A NAT cannot safely treat FIN or RST as the last packet, so it should wait about 4 minutes after detecting TCP termination. It cannot distinguish idle connections from dead ones in general, so it garbage-collects idle state, and timeouts must be configurable. UDP has no deterministic start or end.",
      sourceId: "rfc2663",
      locator: "Sections 2.5 and 2.6",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "napt-binding",
      statement:
        "NAPT binds (private address, private port) to (assigned address, assigned port) when the first outgoing session starts, keeps soft state per connection, and may remove the binding when the last session using it ends. Inbound access requires a static mapping per service port.",
      sourceId: "rfc3022",
      locator: "Sections 2.2, 3.1, 3.2, and 3.3",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "nat-header-rewrite",
      statement:
        "NAT must update TCP and UDP checksums because they cover a pseudo-header with the IP addresses; NAPT also rewrites the port. ICMP error messages need the embedded IP and transport header rewritten too. Outbound TCP/UDP fragment translation in NAPT fails because only the first fragment carries ports.",
      sourceId: "rfc3022",
      locator: "Sections 4.1, 4.2, 4.3, and 6.3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "nat-privacy-debugging",
      statement:
        "Traditional NAT hides private host addresses from external hosts, which also makes it harder to trace a misbehaving host.",
      sourceId: "rfc3022",
      locator: "Section 6.1, Privacy and Security",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "nat-alg",
      statement:
        "Applications that carry IP addresses or ports in their payload, or that use control sessions to set up data sessions, need an Application Level Gateway to work through NAT.",
      sourceId: "rfc2663",
      locator: "Sections 8.1 and 8.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-mapping-timer",
      statement:
        "A NAT must use endpoint-independent mapping. Its UDP mapping timer must not expire in less than two minutes (with narrow exceptions for well-known ports), five minutes or more is recommended, and outbound packets must refresh it. A NAT must support hairpinning using the external source address and port.",
      sourceId: "rfc4787",
      locator: "Sections 4.1 (REQ-1), 4.3 (REQ-5, REQ-6), and 6 (REQ-9)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "tcp-idle-timeout",
      statement:
        "If a NAT cannot tell whether TCP endpoints are active, it may abandon an idle session, but the established idle-timeout must not be less than 2 hours 4 minutes and the transitory one not less than 4 minutes. Handling of non-SYN packets with no mapping, drop or RST, is left to the implementation.",
      sourceId: "rfc5382",
      locator: "Section 5, NAT Session Refresh (REQ-5)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "cgn-port-limits",
      statement:
        "A carrier-grade NAT must support limiting the number of external ports per subscriber, and should support limiting state memory per subscriber.",
      sourceId: "rfc6888",
      locator: "Section 3 (REQ-4, REQ-5)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-nat-table",
      statement:
        "The iptables nat table is consulted when a packet that creates a new connection is encountered. Its built-in chains are PREROUTING, INPUT, OUTPUT, and POSTROUTING.",
      sourceId: "man-iptables",
      locator: "TABLES, nat",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-nat-targets",
      statement:
        "DNAT is valid in nat PREROUTING and OUTPUT and rewrites the destination for the packet and all future packets in the connection. SNAT is valid in nat POSTROUTING and INPUT. MASQUERADE maps to the outgoing interface's address and forgets connections when the interface goes down. Without a port range, SNAT keeps the source port when possible, mapping below 512, 512–1023, and 1024+ ranges separately.",
      sourceId: "man-iptables-extensions",
      locator: "TARGET EXTENSIONS: DNAT, MASQUERADE, SNAT",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "conntrack-timeouts",
      statement:
        "Linux conntrack defaults: established TCP 432000 s (5 days), TCP TIME_WAIT 120 s, UDP 30 s, UDP stream 120 s. nf_conntrack_max defaults to nf_conntrack_buckets, and each entry is hashed twice, once per direction.",
      sourceId: "kernel-nf-conntrack-sysctl",
      locator:
        "nf_conntrack_max, nf_conntrack_tcp_timeout_established, nf_conntrack_tcp_timeout_time_wait, nf_conntrack_udp_timeout, nf_conntrack_udp_timeout_stream",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "conntrack-tool",
      statement:
        "The conntrack utility lists, updates, and deletes flow entries and listens to flow events; each listed entry shows the original and the reply tuple.",
      sourceId: "conntrack-tools-manual",
      locator: "Chapter 2; Chapter 5, Using conntrack",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "nat-not-security",
      statement:
        "Translating address bits does not provide security in itself; a static NAT mapping all ports to one host exposes it as if no NAT were present. NAT's perceived protection comes from the lack of pre-established mapping state, which a basic firewall also provides. IPv4 NAT was not developed as a security mechanism.",
      sourceId: "rfc4864",
      locator: "Section 2.2; Section 4.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "firewall-disjoint",
      statement:
        "Firewall functions are disjoint from NAT functions: neither implies the other, although one device often provides both.",
      sourceId: "rfc2979",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc2663", note: "Sections 2.5, 2.6, 4.1, 4.1.1, 4.1.2, 8.1, 8.2, and 9." },
    { sourceId: "rfc3022", note: "Sections 2.2, 3.1–3.3, 4.1–4.3, 6.1, and 6.3." },
    { sourceId: "rfc4787", note: "REQ-1, REQ-5, REQ-6, and REQ-9: UDP mapping and hairpinning." },
    { sourceId: "rfc5382", note: "Section 5 (REQ-5): TCP idle timeouts." },
    { sourceId: "rfc6888", note: "Section 3: per-subscriber port and state limits." },
    { sourceId: "rfc4864", note: "Sections 2.2 and 4.2: NAT is not a security mechanism." },
    { sourceId: "rfc2979", note: "Section 1: firewall and NAT are separate functions." },
    { sourceId: "man-iptables", note: "The nat table." },
    { sourceId: "man-iptables-extensions", note: "SNAT, DNAT, and MASQUERADE targets." },
    { sourceId: "kernel-nf-conntrack-sysctl", note: "Conntrack table size and timeouts." },
    { sourceId: "conntrack-tools-manual", note: "conntrack -L, -D, -E." },
  ],
} satisfies LessonBody;
