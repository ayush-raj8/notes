import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m03.ttl-and-fragmentation",
  status: "complete",
  objectives: [
    "Explain what the TTL and Hop Limit fields bound, who decrements them, and what happens at zero.",
    "Fragment an IPv4 datagram by hand: identification, offsets in 8-byte units, and the More Fragments flag.",
    "Explain why fragmentation is fragile, and how Path MTU Discovery lets the sender avoid it in IPv4 and IPv6.",
    "Diagnose a Path MTU black hole on Linux with `ping -M do`, `tracepath`, and `ip route get`.",
  ],
  prerequisites: ["m03.ipv6"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Two things can go wrong on a path that neither endpoint can see. A routing mistake can send a packet around a loop forever, using bandwidth on every lap. And each link has a maximum packet size, its MTU, so a packet that fits the first link may be too large for a tunnel or a smaller link further on.",
      whyItMatters:
        "The TTL is why routing loops burn out instead of melting a network, and it is what traceroute uses. MTU problems cause some of the most confusing failures in production: SSH logs in and then freezes, a TLS handshake stalls over a VPN, or small API calls work while large responses hang.",
      idea: "Give every packet a hop budget that the sender sets and every router spends, so no packet can outlive its budget. For size, either cut an oversized packet into pieces along the way, which IPv4 allows, or have the sender learn the smallest MTU on the path and never send anything larger. That second method is Path MTU Discovery.",
      howItWorks:
        "TTL: the sender sets it (64 on Linux). Every router decrements it by at least one and discards the packet when it reaches zero, usually returning an ICMP Time Exceeded message to the source. IPv6 calls the same field Hop Limit. Fragmentation: an IPv4 router facing a smaller MTU may split a datagram into fragments that carry the same Identification, a Fragment Offset in 8-byte units, and a More Fragments flag set on all but the last. Only the destination reassembles them. If the Don't Fragment (DF) bit is set, the router drops the datagram instead. Path MTU Discovery: the sender sets DF on everything. A router that cannot forward a packet drops it and returns ICMP 'fragmentation needed' (IPv4) or Packet Too Big (IPv6), including the next hop's MTU. The sender lowers its estimate for that destination and resends smaller packets, and TCP lowers its segment size to match. IPv6 routers never fragment, so IPv6 always works this way.",
      whyItWorks:
        "A hop counter turns an unbounded loop into a bounded cost: at most TTL transmissions per packet. Path MTU Discovery moves the size problem to the one place that can fix it cheaply, the sender, once per destination, instead of making routers cut and receivers reassemble every packet. Routers stay stateless, and no packet depends on several fragments all arriving.",
      limits:
        "Path MTU Discovery works only if the ICMP error gets back to the sender. Firewalls that drop all ICMP, anycast addresses, and asymmetric routes can lose it, and then large packets vanish while small ones succeed. That is a PMTU black hole. Packetization-layer probing (Linux `tcp_mtu_probing`) and routers that clamp the TCP MSS work around it. The TTL counts hops, not time, so it does not bound how long a packet can sit in queues.",
      connections:
        "Module 1 covered MTU in encapsulation, and why.39 also appears there. Module 4 covers ICMP and traceroute, which is built on Time Exceeded. Module 11 explains why large responses hang while small ones succeed (why.40), and Module 13 has the investigation for it.",
    },
    {
      type: "example",
      id: "fragment-by-hand",
      title: "Fragmenting a 4000-byte datagram for a 1500-byte link",
      setup:
        "An IPv4 datagram has a 20-byte header and 3980 bytes of data, total length 4000, Identification 0x1c46, and DF clear. A router must forward it onto a link with MTU 1500.",
      walkthrough: [
        "Each fragment needs its own 20-byte header, so at most 1500 − 20 = 1480 bytes of data fit. 1480 is a multiple of 8, so every fragment except the last can carry 1480 bytes. Offsets count 8-byte blocks, so 1480 bytes is 185 blocks.",
        "Fragment 1: data bytes 0 to 1479, offset 0, More Fragments 1, total length 1500.",
        "Fragment 2: data bytes 1480 to 2959, offset 185, More Fragments 1, total length 1500.",
        "Fragment 3: the remaining 3980 − 2960 = 1020 bytes, offset 370, More Fragments 0, total length 1040.",
        "All three carry Identification 0x1c46 and the same source, destination, and protocol, which is how the receiver groups them. Only fragment 1 contains the TCP or UDP header, so only it shows ports.",
        "If fragment 2 is lost, the receiver holds fragments 1 and 3 until its reassembly timer expires, then discards both and may send ICMP Time Exceeded with code 1. Nothing is delivered, and the sender's transport must resend all 4000 bytes.",
      ],
      result:
        "Three packets replace one, each piece's loss destroys the whole datagram, and two of the three are invisible to anything that filters or balances on ports.",
    },
    {
      type: "example",
      id: "pmtud-black-hole",
      title: "Path MTU Discovery, and when it fails",
      setup:
        "A Linux server at 198.51.100.20 sends a large HTTP response to a client behind a VPN. The server's link MTU is 1500, and the tunnel along the path has MTU 1400. The server sends TCP with DF set, as Linux does by default.",
      walkthrough: [
        "The handshake and the request are small, so they succeed. The first full-size response segment is a 1500-byte packet.",
        "The router at the tunnel cannot forward 1500 bytes with DF set. It drops the packet and sends ICMP type 3, code 4, with Next-Hop MTU 1400. `tcpdump -n icmp` on the server shows it as `unreachable - need to frag (mtu 1400)`.",
        "The server's kernel records a path MTU of 1400 for the client's address. `ip route get <client>` now shows `cache expires ... mtu 1400`. TCP resends the data in segments of at most 1400 − 40 = 1360 bytes, and the response completes.",
        "Now a firewall drops all inbound ICMP. The 1500-byte packets are dropped silently, the server retransmits the same size, and the client sees the connection hang after the headers or the first bytes, while small requests on the same path keep working.",
        "From the client side, measure the path: `ping -M do -s 1372 198.51.100.20` fits exactly (1372 + 8 ICMP + 20 IP = 1400), and `-s 1373` fails. `tracepath -n 198.51.100.20` prints the `pmtu` it discovers at each hop.",
        "Fixes, in order of preference: allow ICMP 'fragmentation needed' and ICMPv6 Packet Too Big through the firewall; clamp the TCP MSS on the tunnel router; enable `net.ipv4.tcp_mtu_probing`; or lower the MTU on the interface that leads into the tunnel.",
      ],
      result:
        "Path MTU Discovery fixes the size problem with one ICMP message per destination. Without that message, nothing tells the sender, so the failure looks like a hang rather than an error.",
    },
    {
      type: "why",
      id: "why-mtu-matters",
      questionId: "why.39",
    },
    {
      type: "comparison",
      id: "v4-v6-size-handling",
      title: "Hop limits and packet size in IPv4 and IPv6",
      columns: ["IPv4", "IPv6"],
      rows: [
        { label: "Hop field", cells: ["TTL, 8 bits", "Hop Limit, 8 bits"] },
        {
          label: "Linux default",
          cells: ["64 (`net.ipv4.ip_default_ttl`)", "64 (`net.ipv6.conf.*.hop_limit`)"],
        },
        {
          label: "Who may fragment",
          cells: [
            "The source and any router, unless DF is set",
            "Only the source, with a Fragment header",
          ],
        },
        {
          label: "'Too big' signal",
          cells: [
            "ICMP type 3 code 4, with the next-hop MTU",
            "ICMPv6 Packet Too Big, with the MTU",
          ],
        },
        {
          label: "Smallest size guaranteed",
          cells: [
            "68 bytes forwarded whole; 576 bytes accepted after reassembly",
            "1280 bytes per link; 1500 bytes accepted after reassembly",
          ],
        },
        { label: "Path MTU Discovery", cells: ["RFC 1191", "RFC 8201"] },
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Why fragments are fragile, and Linux controls",
      paragraphs: [
        "Only the first fragment carries the ports. A stateless firewall filtering on ports must either let later fragments through or drop them. Load balancers and ECMP routers that hash the 5-tuple can send fragments of one datagram down different paths. NAT devices must reassemble virtually to translate them. Many networks drop fragments outright, and IPv6 fragments especially often fail to arrive.",
        "IPv4's Identification field is 16 bits. At high packet rates it wraps within the reassembly timeout, so fragments of two different datagrams can be glued together, and the TCP or UDP checksum may not catch the result. IPv6 uses a 32-bit Identification.",
        "A host must never lower its Path MTU estimate below 68 bytes in IPv4 or 1280 in IPv6, and must not raise it because of an ICMP message, which could be stale or forged. It may probe for an increase, but not sooner than 5 minutes after a Too Big message. Linux also refuses to cache a PMTU below `min_pmtu` (552 by default) and expires cached values after `mtu_expires` seconds.",
        "Per socket: TCP sockets do Path MTU Discovery by default. For UDP, `IP_MTU_DISCOVER` with `IP_PMTUDISC_DO` sets DF and makes `send()` fail with EMSGSIZE when a datagram exceeds the known path MTU, so the application must size its own datagrams. `IP_TTL` sets the TTL per socket. A route with `mtu lock` disables discovery for that destination.",
        "TTL as evidence: a packet from a Linux host that arrives with TTL 58 has crossed six routers, if the sender kept the default of 64. Other systems start at other values, so treat this as a hint.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "loop-ttl",
          prompt:
            "Two routers have routes pointing at each other for 203.0.113.0/24. A Linux host sends one packet there. What happens to it?",
          answer:
            "It bounces between the routers, losing at least one from its TTL on each hop. After about 64 hops a router decrements it to zero, discards it, and usually sends ICMP Time Exceeded back to the host. The loop costs 64 transmissions, not an infinite number.",
        },
        {
          id: "fragment-arithmetic",
          prompt:
            "An IPv4 datagram with a 20-byte header and 2980 bytes of data must cross a link with MTU 1000. List each fragment's data length, offset, and More Fragments flag.",
          answer:
            "Room for data is 1000 − 20 = 980, rounded down to a multiple of 8: 976 bytes, or 122 blocks. Fragments: 976 bytes at offset 0, 976 at 122, 976 at 244, all with MF 1, and the last 52 bytes at offset 366 with MF 0. Their total lengths are 996, 996, 996, and 72.",
        },
        {
          id: "ping-size",
          prompt:
            "You suspect a 1400-byte path MTU to a host. Which `ping` command tests it exactly, and what result confirms it?",
          answer:
            "`ping -M do -s 1372 host` sends a 1400-byte IPv4 packet (1372 data + 8 ICMP + 20 IP) with DF set, and `ping -M do -s 1373 host` sends 1401. If 1372 gets replies and 1373 gets 'message too long' or no reply, the path MTU is 1400.",
        },
        {
          id: "df-dropped",
          prompt:
            "Why does a router drop a 1500-byte TCP packet with DF set on a 1400-byte link, instead of fragmenting it, and how does the sender find out?",
          answer:
            "DF forbids fragmentation, so the router must discard the packet. It returns ICMP 'fragmentation needed' with the next-hop MTU, 1400. The sender lowers its path MTU estimate for that destination, and TCP resends in smaller segments. If that ICMP message is filtered, the sender never learns, and the connection stalls.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ttl-field",
      statement:
        "The TTL is set by the sender and reduced wherever the datagram is processed. Every module must decrease it by at least one, and a datagram whose TTL reaches zero must be destroyed. The TTL is an upper bound on lifetime, meant to discard undeliverable datagrams.",
      sourceId: "rfc791",
      locator: "Section 2.3, Function Description; Section 3.1, Time to Live",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ttl-two-functions",
      statement:
        "A host must not send a datagram with TTL zero and must provide a way for the transport to set the TTL. The TTL limits segment lifetime and terminates routing loops, and acts as a hop count because each gateway reduces it by at least one.",
      sourceId: "rfc1122",
      locator: "Section 3.2.1.7, Time-to-Live",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "time-exceeded",
      statement:
        "A gateway that finds the TTL is zero must discard the datagram and may send ICMP Time Exceeded (type 11) code 0; a host that cannot complete reassembly within its time limit discards the fragments and may send code 1.",
      sourceId: "rfc792",
      locator: "Time Exceeded Message",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "hop-limit",
      statement:
        "The IPv6 Hop Limit is decremented by 1 by each forwarding node, and the packet is discarded if it reaches zero; it was renamed from Time to Live because IPv6 nodes are not required to enforce a maximum packet lifetime.",
      sourceId: "rfc8200",
      locator: "Section 3, IPv6 Header Format; Section 8.2, Maximum Packet Lifetime",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv4-fragment-fields",
      statement:
        "IPv4 fragments of one datagram share an Identification value, carry a Fragment Offset in units of 8 octets, and set More Fragments on all but the last; data is split on 8-octet boundaries, and a datagram marked Don't Fragment that cannot be delivered whole is discarded.",
      sourceId: "rfc791",
      locator: "Section 2.3, Fragmentation; Section 3.1, Flags and Fragment Offset",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "reassembly-and-minimums",
      statement:
        "IPv4 fragments at gateways and reassembles at the destination. Every module must forward 68 octets without further fragmentation, and every destination must accept 576 octets, whole or reassembled.",
      sourceId: "rfc791",
      locator: "Section 3.2, Discussion, Fragmentation and Reassembly; Section 3.1, Total Length",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "pmtud-ipv4",
      statement:
        "Path MTU Discovery sends datagrams with DF set; a router that cannot forward one without fragmenting discards it and returns ICMP Destination Unreachable 'fragmentation needed and DF set', carrying the next-hop MTU in a formerly unused field, and the source reduces its path MTU estimate.",
      sourceId: "rfc1191",
      locator: "Section 2, Protocol overview; Section 4, Router specification",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "pmtud-host-rules",
      statement:
        "A host must never reduce its Path MTU estimate below 68 octets, must not increase it because of a Datagram Too Big message, and must not attempt an increase less than 5 minutes after receiving one. The TCP MSS is the IP datagram size minus 40.",
      sourceId: "rfc1191",
      locator: "Section 3, Host specification; Section 3.1, TCP MSS Option",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "pmtud-ipv6",
      statement:
        "In IPv6, a node that cannot forward a packet returns ICMPv6 Packet Too Big; the source must not reduce its Path MTU estimate below the 1280-octet minimum link MTU. If ICMPv6 filtering blocks Packet Too Big, the source never learns the path MTU, and packetization-layer PMTUD is more robust.",
      sourceId: "rfc8201",
      locator: "Section 3, Protocol Overview; Section 4, Protocol Requirements; Section 6",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-fragmentation",
      statement:
        "IPv6 fragmentation is done only by the source, using a Fragment header with a 13-bit offset in 8-octet units, an M flag, and a 32-bit Identification. Every link must carry 1280 octets, and a node must accept fragmented packets that reassemble to 1500 octets.",
      sourceId: "rfc8200",
      locator: "Section 4.5, Fragment Header; Section 5, Packet Size Issues",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "fragmentation-fragile",
      statement:
        "Fragmentation causes problems for NAT, which must virtually reassemble; for stateless firewalls, since ports appear only in the first fragment; and for ECMP and load balancers that hash on ports. At high data rates IPv4's 16-bit Identification can repeat, causing misassembly.",
      sourceId: "rfc8900",
      locator: "Sections 3.3, 3.4, 3.5, and 3.6",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "pmtu-black-holes",
      statement:
        "Path MTU Discovery depends on the network delivering ICMP Packet Too Big messages. Loss through rate limiting, misimplemented firewall policy, anycast, or unidirectional routing creates PMTU black holes, and many paths drop fragments outright.",
      sourceId: "rfc8900",
      locator: "Section 3.8, PMTU Black-Holing Due to ICMP Loss; Section 3.9",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-default-ttl",
      statement:
        "Linux's /proc/sys/net/ipv4/ip_default_ttl defaults to 64 and can be overridden per socket with IP_TTL; ip_no_pmtu_disc disables Path MTU Discovery for TCP by default, which the page advises against.",
      sourceId: "man-proc-sys-net-ipv4",
      locator: "ip_default_ttl; ip_no_pmtu_disc",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-pmtu-sysctls",
      statement:
        "Linux's min_pmtu defaults to 552 and bounds every cached PMTU; mtu_expires sets how long cached PMTU information is kept; tcp_mtu_probing controls TCP packetization-layer PMTUD, with mode 1 enabling it when an ICMP black hole is detected; the IPv6 hop_limit default is 64.",
      sourceId: "kernel-ip-sysctl",
      locator:
        "/proc/sys/net/ipv4: min_pmtu, mtu_expires, tcp_mtu_probing; /proc/sys/net/ipv6: hop_limit",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-ip-mtu-discover",
      statement:
        "Linux performs Path MTU Discovery on SOCK_STREAM sockets when enabled. For other sockets, IP_PMTUDISC_DO sets DF on all packets and the kernel rejects datagrams larger than the known path MTU with EMSGSIZE.",
      sourceId: "man-ip-mtu-discover",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-mtu-lock",
      statement:
        "A route's `mtu lock` disables Path MTU Discovery for its destinations; without `lock`, the kernel may update the route's MTU through Path MTU Discovery.",
      sourceId: "man-ip-route",
      locator: "ip route add, mtu lock MTU",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ping-pmtu-options",
      statement:
        "`ping -M do` sets DF and lets the kernel reject packets that are too large; `-s` sets the number of data bytes, to which ICMP adds an 8-byte header.",
      sourceId: "man-ping",
      locator: "OPTIONS, -M pmtudisc_opt and -s packetsize",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "tracepath-pmtu",
      statement:
        "tracepath traces the path to a host and reports the Path MTU it discovers along the way.",
      sourceId: "man-tracepath",
      locator: "DESCRIPTION; OUTPUT",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    {
      sourceId: "rfc791",
      note: "Sections 2.3, 3.1, and 3.2: TTL, fragmentation fields, and minimum sizes.",
    },
    { sourceId: "rfc1122", note: "Section 3.2.1.7: host TTL rules." },
    { sourceId: "rfc792", note: "Time Exceeded." },
    { sourceId: "rfc8200", note: "Sections 3, 4.5, 5, and 8.2: Hop Limit and IPv6 fragmentation." },
    { sourceId: "rfc1191", note: "Path MTU Discovery for IPv4." },
    { sourceId: "rfc8201", note: "Path MTU Discovery for IPv6." },
    { sourceId: "rfc8900", note: "Section 3: why fragmentation is fragile, and PMTU black holes." },
    { sourceId: "man-proc-sys-net-ipv4", note: "Default TTL and the PMTU discovery switch." },
    { sourceId: "kernel-ip-sysctl", note: "PMTU cache and TCP MTU probing sysctls." },
    { sourceId: "man-ip-mtu-discover", note: "Per-socket Path MTU Discovery." },
    { sourceId: "man-ip-route", note: "`mtu lock` on routes." },
    { sourceId: "man-ping", note: "`-M do` and `-s`." },
    { sourceId: "man-tracepath", note: "Discovering the path MTU." },
  ],
} satisfies LessonBody;
