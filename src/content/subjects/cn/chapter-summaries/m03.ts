import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m03",
  status: "complete",
  readingMinutes: { min: 12, max: 18 },
  thesis:
    "IP gives every interface a hierarchical address, and each host or router uses a prefix match against its routing table to decide the next hop, with TTL and MTU rules keeping that forwarding safe and workable.",
  outcomes: [
    "Read an address with a prefix length and compute its network, broadcast, and host range.",
    "Decide whether two addresses are on the same subnet and what that means for the next hop.",
    "Recognise private, loopback, link-local, and other special-purpose ranges and what they imply.",
    "Walk a lookup through a routing table with longest-prefix matching and a default gateway.",
    "Explain TTL/hop limit, fragmentation, and Path MTU Discovery, and why MTU still matters.",
  ],
  coverage: [
    {
      bullet: "IPv4 addressing and CIDR.",
      notes:
        "• IPv4 address = 32 bits, written as four octets; belongs to an interface, not a host.\n• CIDR notation a.b.c.d/n: first n bits are the network prefix, remaining bits identify hosts.\n• Classless: prefixes of any length replace the old class A/B/C boundaries and allow aggregation (supernetting).\n• Block of /n has 2^(32−n) addresses; in ordinary subnets the first (network) and last (broadcast) are not usable by hosts.\n• Aggregation lets one route cover many contiguous blocks, keeping the global table smaller.\n• Allocation is hierarchical: registries → ISPs → organisations → subnets.",
    },
    {
      bullet: "Subnet masks and network prefixes.",
      notes:
        "• Mask = n ones followed by zeros; /24 = 255.255.255.0, /25 = 255.255.255.128, /21 = 255.255.248.0.\n• Network address = address AND mask; broadcast = network with all host bits set.\n• Block size in the interesting octet = 256 − mask octet; networks start on multiples of that size.\n• Mask must be contiguous ones in CIDR; the prefix length is the same information in shorter form.\n• Mask is configured per interface: two hosts with different masks can disagree about who is local.\n• Subnetting borrows host bits to create smaller networks; each borrowed bit doubles networks and halves hosts.",
    },
    {
      bullet: "Determining whether two addresses belong to the same subnet.",
      notes:
        "• Method: AND each address with the same mask; equal results means same subnet.\n• Always use the sender's mask for its own decision — it does not consult the peer's mask.\n• Same subnet → deliver directly (ARP/ND for the destination's MAC). Different → send to default gateway.\n• Example: 10.0.7.200/21 and 10.0.12.5/21 have networks 10.0.0.0 and 10.0.8.0, so they differ.\n• Mismatched masks produce asymmetric behaviour: A thinks B is local while B sends replies via its gateway.\n• Symptom of a bad mask: some peers reachable, others not, or one-way traffic.",
    },
    {
      bullet: "Public vs private addresses.",
      notes:
        "• Public: globally unique and routable on the Internet; assigned through registries.\n• Private (RFC 1918): 10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16; reusable by anyone, never routed on the public Internet.\n• Private hosts reach the Internet through NAT at the edge (covered in Module 10).\n• Overlapping private ranges break when two networks are joined (VPN, mergers).\n• CGNAT range 100.64.0.0/10 is shared address space for carrier NAT.\n• Private is not 'secure': it only means 'not globally routed'.",
    },
    {
      bullet: "Loopback, link-local, and special-purpose addresses.",
      notes:
        "• Loopback 127.0.0.0/8 (::1 in IPv6): never leaves the host; used to test local stacks and services.\n• Link-local 169.254.0.0/16 (fe80::/10): valid only on one link; IPv4 form appears when DHCP fails (APIPA). Routers do not forward it.\n• 0.0.0.0 means 'this host / unspecified' as a source or 'any address' when binding; 0.0.0.0/0 is the default route.\n• Broadcast 255.255.255.255 limited to the local link; directed broadcast targets a subnet.\n• Multicast 224.0.0.0/4 (ff00::/8 in IPv6) addresses groups, not single hosts.\n• Documentation ranges (192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24) must not appear in real traffic.\n• Seeing a 169.254.x.x address on a host is a signal that addressing failed.",
    },
    {
      bullet: "Default gateways.",
      notes:
        "• Default gateway = router on the local subnet used for every destination not on-link (why.33).\n• Host sends the frame to the gateway's MAC, while the IP destination stays the remote server.\n• Host needs no full routing table: 'local or not' plus one next hop is enough.\n• Gateway must be on the host's own subnet or the host cannot ARP for it.\n• Host route table typically has: on-link prefix, default route 0.0.0.0/0 via gateway, loopback.\n• Wrong or missing gateway: local subnet works, everything remote fails.\n• Redundancy through shared virtual gateways (VRRP/HSRP) keeps the same IP and MAC identity.",
    },
    {
      bullet: "IPv4 vs IPv6.",
      notes:
        "• IPv6 = 128-bit addresses, written in hex with :: compression; solves IPv4 address exhaustion.\n• Fixed 40-byte header, no header checksum, no router fragmentation; extension headers instead of options.\n• Subnets are normally /64; hosts self-configure with SLAAC from router advertisements, or DHCPv6.\n• Hosts typically hold several addresses per interface: link-local, global, and often temporary privacy addresses.\n• No broadcast; multicast and anycast take over its jobs, and ND replaces ARP.\n• Minimum link MTU 1280 bytes; Path MTU Discovery is essential because routers will not fragment.\n• Dual-stack is the common transition: both families run side by side, each with its own routes and gateway.",
    },
    {
      bullet: "Routing tables and longest-prefix matching.",
      notes:
        "• Entry = destination prefix, next hop (or on-link), outgoing interface, metric.\n• Lookup: collect every entry whose prefix contains the destination; choose the longest prefix (most specific).\n• Why needed: overlapping prefixes are normal — aggregates plus exceptions — and need one deterministic winner (why.35).\n• Default route 0.0.0.0/0 matches all but is the shortest, so it wins only when nothing else matches.\n• Ties between equal-length prefixes are broken by administrative preference, then metric.\n• Example: 10.1.2.0/24 beats 10.1.0.0/16 for 10.1.2.9, but 10.1.9.9 uses the /16.\n• No match and no default → drop and send ICMP destination unreachable.\n• Hosts and routers use the same logic; routers just have far more entries.",
    },
    {
      bullet: "TTL and hop limits.",
      notes:
        "• TTL (IPv4) / Hop Limit (IPv6) is an 8-bit counter, decremented by every router that forwards the packet.\n• At zero the router discards the packet and sends ICMP Time Exceeded to the source.\n• Purpose: bound the life of packets caught in routing loops, so they cannot circulate forever.\n• Common starting values: 64, 128, 255; received value hints at the OS and hop distance.\n• Traceroute uses it deliberately: TTL 1, 2, 3... exposes each router in turn (see Module 4).\n• Layer-2 switching does not touch TTL; only routing hops do.\n• TTL expiry in a loop is a signal of a routing problem, not an application bug.",
    },
    {
      bullet: "IP fragmentation and Path MTU Discovery.",
      notes:
        "• MTU = largest IP packet a link carries; the path MTU is the smallest MTU along the way.\n• IPv4 routers may fragment unless the DF bit is set; fragments carry ID, offset, and More-Fragments flag, and only the destination reassembles.\n• Fragmentation costs: lose one fragment and the whole datagram is lost, and reassembly uses memory and can be abused; firewalls often drop fragments.\n• IPv6: routers never fragment; only the source can, via an extension header.\n• PMTUD: send with DF set; a router that cannot forward returns ICMP 'Fragmentation Needed' (IPv4) or 'Packet Too Big' (IPv6) with the next-hop MTU; the sender shrinks.\n• TCP avoids fragmenting by advertising MSS and honouring PMTUD.\n• ICMP filtering breaks PMTUD → MTU black hole: small packets pass, large ones hang (why.39).\n• Tunnels and VPNs reduce effective MTU, often the real culprit.",
    },
  ],
  mentalModel:
    "• Address = prefix (which network) + host bits (which interface); the mask tells the sender where the split is.\n• Every sender asks one question first: 'is the destination on my prefix?' Yes → ARP and deliver; no → hand to the gateway.\n• A routing table is an ordered list of prefixes; the most specific one wins, and the default is the last resort.\n• TTL stops loops; MTU/PMTUD stops oversized packets; both are enforced hop by hop but reported by ICMP.",
  failuresAndSignals: [
    "Wrong subnet mask or gateway — local peers work, remote hosts fail or one-way traffic; compare each side's address/prefix and route table (ip route, route print).",
    "169.254.x.x address on a host — DHCP failed and link-local fallback kicked in; check DHCP server, VLAN, and relay.",
    "MTU black hole — ping works, TLS or large uploads hang; DF packets are dropped and ICMP too-big never returns; test with ping -M do -s <size> and lower MSS or MTU.",
    "Overlapping private ranges across VPN or merged sites — some destinations resolve to the wrong network; the routing table's longest match silently prefers the local one.",
    "Routing loop — TTL exceeded messages and traceroute alternating between two routers; look for the conflicting routes or static default.",
  ],
  whyAnchors: ["why.33", "why.35", "why.39"],
  connections: {
    before:
      "• Module 2: ARP/ND resolve next-hop MACs; the same-subnet decision made here picks whose MAC that is.",
    after:
      "• Module 4: how routing tables get filled (static, OSPF, BGP), why loops and convergence matter, and how ICMP and traceroute observe the path.",
  },
  selfCheck: [
    {
      id: "same-subnet",
      prompt: "Are 192.168.10.130/25 and 192.168.10.20/25 on the same subnet?",
      answer:
        "No. /25 splits the last octet at 128: .130 is in 192.168.10.128/25, .20 is in 192.168.10.0/25. They need a router.",
    },
    {
      id: "gateway-mac",
      prompt: "Why does a host send traffic for a remote server to its gateway's MAC?",
      answer:
        "Remote hosts are not on the link, so only the next hop can be reached by a frame. The frame goes to the gateway's MAC while the IP destination stays the server (why.33).",
    },
    {
      id: "lpm",
      prompt:
        "Routes: 0.0.0.0/0 via A, 10.0.0.0/8 via B, 10.4.0.0/16 via C. Where does 10.4.9.1 go?",
      answer:
        "Via C: all three match, the /16 is the longest prefix. Longest-prefix matching makes specific routes override aggregates (why.35).",
    },
    {
      id: "mtu",
      prompt: "Why does MTU matter if IP can fragment?",
      answer:
        "Fragmentation is costly and fragile (loss of one piece loses all), IPv6 forbids router fragmentation, and DF plus filtered ICMP breaks PMTUD; sending within the path MTU avoids all of that (why.39).",
    },
  ],
} satisfies ChapterSummary;
