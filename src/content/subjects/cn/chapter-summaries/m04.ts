import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m04",
  status: "complete",
  readingMinutes: { min: 12, max: 18 },
  thesis:
    "Routing protocols build the tables that forwarding consults packet by packet, and ICMP is how the network reports back when a packet cannot be delivered or when you probe the path.",
  outcomes: [
    "Separate routing (control plane, table building) from forwarding (per-packet lookup) and say why both exist.",
    "Compare static and dynamic routing, and distance-vector with link-state.",
    "Describe at a high level what OSPF decides inside an AS and what BGP decides between ASes.",
    "Explain how loops arise, how convergence is measured, and which mechanisms limit damage.",
    "Use ping and traceroute, read ICMP errors, and explain why traceroute can disagree with a real flow.",
  ],
  coverage: [
    {
      bullet: "Routing vs forwarding.",
      notes:
        "• Routing = control plane: learn the topology and compute best next hops per prefix.\n• Forwarding = data plane: for each packet, look up the destination in the forwarding table (FIB) and send it out one interface.\n• Routing runs slowly and globally (seconds); forwarding runs per packet in hardware (nanoseconds).\n• Tables replace flooding: a router sends each packet toward one next hop instead of everywhere (why.34).\n• Both are needed: protocols adapt to change, forwarding tables make decisions fast (why.36).\n• Each hop re-decides independently, so consistent tables on every router matter.\n• At every routed hop the dest MAC is rewritten; dest IP stays the same (why.06).",
    },
    {
      bullet: "Static vs dynamic routing.",
      notes:
        "• Static: administrator writes routes by hand; predictable, no protocol traffic, cheap on small or stub networks.\n• Dynamic: routers exchange information (RIP, OSPF, BGP) and recompute automatically on change.\n• Static does not react to failure unless paired with tracking or floating-static backups.\n• Dynamic scales and self-heals but adds configuration, CPU, and the risk of propagating mistakes.\n• Typical mix: default static route at the edge, dynamic IGP inside, BGP to other ASes.\n• Administrative distance ranks sources when several offer the same prefix (static usually beats dynamic).\n• Hosts nearly always use static: connected route plus default gateway.",
    },
    {
      bullet: "Distance-vector vs link-state routing.",
      notes:
        "• Distance-vector: each router tells neighbors its distances to destinations; neighbors add link cost (Bellman-Ford). Router knows 'via whom, how far', not the map.\n• Link-state: each router floods link information to all routers in the area, builds the same topology database, and runs Dijkstra locally for shortest paths.\n• DV: little state and simple, but slow convergence and count-to-infinity risk.\n• LS: faster, loop-free convergence and a full map, but more memory, CPU, and flooding.\n• Fixes for DV loops: split horizon, poison reverse, hold-down timers, maximum hop count (RIP uses 16 as infinity).\n• BGP is path-vector, a DV variant that carries the whole AS path.\n• Metric choice (hops, bandwidth-based cost) determines what 'best' means.",
    },
    {
      bullet: "High-level operation of OSPF and BGP.",
      notes:
        "• OSPF: link-state IGP inside one AS; neighbors form adjacencies with Hello packets, then flood LSAs so everyone holds the same link-state database.\n• Each router runs Dijkstra with link cost (often derived from bandwidth) to build its shortest-path tree and routes.\n• Areas (with area 0 as backbone) limit flooding and database size; a designated router reduces adjacency load on shared segments.\n• BGP: path-vector EGP between ASes (eBGP) and inside them (iBGP); runs over TCP port 179 with incremental updates.\n• BGP advertises prefixes with attributes (AS_PATH, NEXT_HOP, local preference, MED); policy picks the route, not just distance.\n• AS_PATH loop prevention: a router rejects any route that already contains its own AS number.\n• BGP trusts what neighbors announce, so leaks and hijacks propagate; RPKI and filtering are the defences.\n• NEXT_HOP must be resolvable via the IGP, so BGP and OSPF depend on each other.\n• Longest-prefix still decides forwarding across overlapping BGP prefixes (why.35).",
    },
    {
      bullet: "Routing tables.",
      notes:
        "• RIB holds every route learned from every source; the best per prefix is installed in the FIB used for forwarding.\n• Entry fields: prefix/length, next hop, interface, metric, source (connected, static, OSPF, BGP), age.\n• Route selection: longest prefix first, then lowest administrative distance, then lowest metric.\n• Connected routes appear automatically when an interface comes up; they are the root for everything else.\n• Default route 0.0.0.0/0 (::/0) catches anything unmatched.\n• Tables change when links fail, neighbors time out, or policy changes; the FIB is updated after the RIB.\n• View them with ip route, netstat -rn, show ip route.",
    },
    {
      bullet: "Longest-prefix matching.",
      notes:
        "• Among all routes that contain the destination, pick the one with the longest prefix length.\n• Enables aggregation plus exceptions: a /16 summary with a /24 carve-out for a special site.\n• Applies regardless of protocol: a /24 from BGP beats a /16 from OSPF for addresses in the /24.\n• Subtle trap: more specific beats lower metric, so a hijacker announcing a longer prefix wins traffic.\n• Hardware implements it in tries or TCAM so lookups stay fast with hundreds of thousands of entries.\n• Detail of the algorithm lives in Module 3; here it explains why a route's presence in the table may still not carry the traffic.",
    },
    {
      bullet: "Routing loops and convergence.",
      notes:
        "• Loop: routers' tables point at each other for a prefix, so packets bounce until TTL hits zero.\n• Cause: inconsistent views during a change (a link fails, some routers still use the old path).\n• Convergence = time until every router has a consistent, correct table after a change.\n• Meanwhile packets may blackhole, loop, or take suboptimal paths.\n• Mitigations: TTL (bounds the damage), split horizon, hold-downs, link-state flooding, fast reroute, BFD to detect failure sooner.\n• BGP convergence is slow (minutes) due to path exploration, MRAI timers, and route flap damping.\n• Count to infinity: DV routers keep raising a metric through each other until the max.\n• Loops drive up link utilisation and show as TTL exceeded messages.",
    },
    {
      bullet: "ICMP, ping, and traceroute.",
      notes:
        "• ICMP is the IP-layer control and error channel; carried inside IP, not TCP/UDP. IPv6 uses ICMPv6 and needs it for ND and PMTUD.\n• Echo Request/Reply = ping: tests reachability and measures round-trip time and loss.\n• Time Exceeded: router drops a packet at TTL 0; Destination Unreachable carries a code (net, host, port, admin prohibited, fragmentation needed).\n• Traceroute sends probes with TTL 1, 2, 3...; each router's Time Exceeded reveals one hop, final destination answers with Echo Reply or Port Unreachable.\n• Each reply names the router interface that replied, not the whole path.\n• Traceroute can differ from the real path (why.52): load balancing across ECMP paths, probe vs TCP flow hashing, asymmetric return paths, ICMP rate limiting, MPLS hiding hops.\n• Stars (* * *) mean no reply arrived (filtered or rate-limited), not necessarily failure.\n• ICMP messages quote the start of the offending packet so the sender can match it.",
    },
    {
      bullet: "What happens when a destination is unreachable.",
      notes:
        "• No route: router has no matching prefix or default → drops and sends ICMP Network Unreachable.\n• Route but no host: last-hop router's ARP/ND gets no answer → Host Unreachable (or silence).\n• Host up but port closed: TCP RST or ICMP Port Unreachable for UDP; that is an answer, not a network failure.\n• Filtered: firewall may drop silently (timeout) or send admin prohibited; silence is ambiguous.\n• TTL expired in a loop: Time Exceeded back to the source.\n• Too big with DF: Fragmentation Needed / Packet Too Big.\n• Diagnosis order: ping gateway → ping remote IP → traceroute for last responding hop → check routes on that hop.\n• ICMP errors are rate-limited and may be blocked, so absence of errors never proves a healthy path.",
    },
  ],
  mentalModel:
    "• Routing protocols are the road-map committee; forwarding is the driver at each junction reading the signpost.\n• OSPF = every router in an AS holds the same map and computes its own shortest path; BGP = ASes trade 'I can reach this prefix via this path' under policy.\n• The table's longest matching prefix chooses the next hop; each hop repeats this until the last router ARPs for the host.\n• ICMP is the feedback: Time Exceeded makes traceroute work, Unreachable explains drops.",
  failuresAndSignals: [
    "Routing loop after a link failure — TTL exceeded replies, traceroute bouncing between two routers, high link utilisation; check for inconsistent routes or missing split-horizon / summarisation.",
    "Blackhole route or stale static — pings time out with no ICMP error, traceroute dies at one hop; inspect the route at that hop and its next hop's reachability.",
    "OSPF neighbors stuck below Full — mismatched Hello/Dead timers, area ID, MTU, or authentication; show ip ospf neighbor reveals the state.",
    "BGP prefix leak or hijack — traffic suddenly goes through the wrong AS, a more-specific prefix appears in looking glasses; AS_PATH changes and latency shifts are the clue.",
    "Traceroute shows * * * or a path that differs from application latency — ICMP rate-limited or ECMP hashing; test with TCP-based traceroute and compare from both ends.",
  ],
  whyAnchors: ["why.34", "why.36", "why.52"],
  connections: {
    before:
      "• Module 3: addressing, longest-prefix lookup, TTL, and PMTUD; this module explains how the tables those lookups use are built and how ICMP reports problems.",
    after:
      "• Module 5: TCP in depth — how transport uses an end-to-end path, with loss, delay, and reordering that routing and forwarding can produce.",
  },
  selfCheck: [
    {
      id: "why-both",
      prompt: "Why do we need both routing protocols and forwarding tables?",
      answer:
        "Protocols discover and recompute paths when topology changes; forwarding tables hold the finished answer so each packet is handled with a fast lookup (why.36).",
    },
    {
      id: "dv-ls",
      prompt: "How do distance-vector and link-state routers differ in what they know?",
      answer:
        "Distance-vector routers know only neighbors' distance claims; link-state routers flood link data, hold the full topology, and run Dijkstra themselves, so they converge faster without count-to-infinity.",
    },
    {
      id: "ospf-bgp",
      prompt: "What does OSPF optimise, and what does BGP optimise?",
      answer:
        "OSPF picks lowest-cost paths inside one AS. BGP picks routes between ASes by policy and attributes such as AS_PATH, and uses AS_PATH to reject loops.",
    },
    {
      id: "traceroute",
      prompt: "Why can traceroute show a path different from the TCP connection's path?",
      answer:
        "Probes may hash differently under ECMP, return paths can be asymmetric, and routers may rate-limit or hide ICMP, so each hop reply reflects only that probe (why.52).",
    },
  ],
} satisfies ChapterSummary;
