import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m04.routing-vs-forwarding",
  status: "complete",
  objectives: [
    "Separate routing, which builds the table, from forwarding, which applies it to each packet.",
    "Walk through what a router does to one packet: local-delivery check, longest-prefix lookup, TTL, next hop.",
    "Explain how connected, static, and protocol routes compete for one prefix and which one is installed.",
    "Tell a route that is known from a route that is installed, using FRR's RIB and the kernel's table on Linux.",
  ],
  prerequisites: ["m03.ttl-and-fragmentation"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "A router receives a packet for 198.51.100.7 and must choose an output link right away. The facts that decide the right link are spread across the network and keep changing: which links are up, which neighbours can reach which prefixes, and which paths the operator prefers. The router cannot ask anyone about each packet. Copying the packet onto every link would reach the destination without that knowledge, but the copies multiply at every router and circle forever in any topology with a loop.",
      whyItMatters:
        "Many routing incidents come down to the gap between 'the router knows a route' and 'the router forwards on it'. A BGP session can be up while traffic is black-holed. A static route can quietly beat the dynamic route you expected. A Linux VM with two interfaces can refuse to pass traffic between them. Each of these is easy to diagnose once you know which table you are looking at.",
      idea: "Split the job in two. Routing is a slow, distributed computation: routers exchange reachability, apply policy, and choose one best next hop per prefix. Forwarding is the fast, local step: for each packet, look up the destination in the table routing produced and send the packet one hop. The table is the only interface between them.",
      howItWorks:
        "Routes come from three kinds of source. Connected routes come from the router's own interface addresses. Static routes are written by an operator. Dynamic routes are learned by protocols such as OSPF and BGP. In a routing suite such as FRR, each protocol offers its best routes to a central routing information base (RIB). For each prefix, the RIB picks the candidate with the lowest administrative distance (connected 0, static 1, eBGP 20, OSPF 110, iBGP 200) and installs it in the forwarding information base (FIB). On Linux, the FIB is the kernel's routing table. For each packet, the router validates the header and decides whether the packet is for itself. If not, it looks up the next hop: it keeps only routes whose prefix matches the destination, then the longest, then the best metric. If nothing matches, it drops the packet as unreachable. It decrements the TTL, discarding the packet with ICMP Time Exceeded at zero, resolves the next hop's MAC, and sends a new frame.",
      whyItWorks:
        "Forwarding needs only the destination address and a local table. It keeps no per-flow state and has no conversation with other routers, so it can run in the kernel or in hardware at line rate. Routing can afford timers, retries, and graph algorithms, because it runs when the topology or policy changes, not once per packet. Every router fills its table from the same shared information, so the independent decisions at each hop add up to a path, as long as the tables agree.",
      limits:
        "The tables do not always agree. After a failure, routers update at different moments, and packets can loop or be dropped until they converge. The TTL bounds the damage, but it does not prevent it. A route promises only a next hop, not a working path beyond it. Forwarding by destination alone cannot express 'send this customer's traffic another way' without policy routing. RFC 1812 calls the same structure both the routing table and the forwarding table. The RIB and FIB split is how implementations such as FRR and the Linux kernel divide the work, not a protocol requirement.",
      connections:
        "Module 1 introduced the control plane and the data plane (why.04 also appears here). Module 2 showed the per-hop frame rewrite that forwarding performs (why.06 also appears here). Module 3 covered longest-prefix match on a host and the TTL. The next lesson shows how routing protocols compute the routes, and lesson 3 covers OSPF and BGP.",
    },
    {
      type: "example",
      id: "rib-to-fib",
      title: "One Linux router, three sources of routes",
      setup:
        "A Linux router runs FRR. Its LAN interface `eth0` is 10.0.0.1/24, and its uplink `eth1` is 192.0.2.2/30, facing an ISP router at 192.0.2.1. An operator added a static default route via 192.0.2.1 and a static route for 10.20.0.0/16 via 10.0.0.3, meant as a backup. OSPF has also learned 10.20.0.0/16 via 10.0.0.2. `net.ipv4.ip_forward` is 1.",
      walkthrough: [
        "Connected: the addresses on `eth0` and `eth1` produce 10.0.0.0/24 and 192.0.2.0/30 with `proto kernel scope link`. No protocol has to learn these.",
        "Competing candidates: the RIB has two routes for 10.20.0.0/16. The static route has distance 1, and the OSPF route has distance 110. The lower distance wins, so the kernel gets `10.20.0.0/16 via 10.0.0.3`. The 'backup' is now the primary, and OSPF's route is known but not installed. To make a static route a real backup, give it a distance above 110.",
        "A packet arrives on `eth1` for 10.20.5.9 with TTL 57. It is not addressed to the router, so the kernel looks up 10.20.5.9. It matches the default (/0) and 10.20.0.0/16, and the /16 is longer. The TTL becomes 56, the router resolves 10.0.0.3 with ARP, and it sends a new frame out of `eth0`. The IP destination does not change.",
        "A packet for 203.0.113.50 matches only the default and leaves through `eth1` via 192.0.2.1. A packet for 10.0.0.1 is the router's own address, so it is delivered locally rather than forwarded.",
        "10.0.0.3 dies, but its address is still inside the connected 10.0.0.0/24. Nothing withdraws the static route, so the kernel keeps sending 10.20.0.0/16 to a dead neighbour, while OSPF's working route waits unused. `ip route get 10.20.5.9` shows the kernel's choice. FRR's `show ip route` marks the selected route with `>` and the installed route with `*`.",
      ],
      result:
        "Three sources offered routes. The RIB picked one per prefix by distance, and the kernel forwarded each packet using only that pick. Every surprise in the example came from the selection step, not from forwarding.",
    },
    {
      type: "why",
      id: "why-routing-tables",
      questionId: "why.34",
    },
    {
      type: "why",
      id: "why-protocols-and-tables",
      questionId: "why.36",
    },
    {
      type: "comparison",
      id: "route-sources",
      title: "Where a route comes from",
      caption:
        "Distances are FRR's defaults. Other routing suites use similar orderings, but the numbers are local configuration, not part of any protocol.",
      columns: ["Who installs it", "FRR distance", "What removes it"],
      rows: [
        {
          label: "Connected",
          cells: [
            "The kernel, from an address on an interface (`proto kernel`).",
            "0",
            "Removing the address.",
          ],
        },
        {
          label: "Static",
          cells: [
            "An operator, with `ip route add` or the routing suite's configuration.",
            "1",
            "Only an operator or a script. It does not notice that the next-hop router died.",
          ],
        },
        {
          label: "eBGP",
          cells: [
            "The BGP daemon, from a peer in another AS.",
            "20",
            "A withdrawal from the peer, or the BGP session closing.",
          ],
        },
        {
          label: "OSPF",
          cells: [
            "The OSPF daemon, from its shortest-path computation.",
            "110",
            "A topology change, such as a neighbour's Hellos stopping.",
          ],
        },
        {
          label: "iBGP",
          cells: [
            "The BGP daemon, from a peer in the same AS.",
            "200",
            "A withdrawal, or the next hop becoming unresolvable.",
          ],
        },
      ],
    },
    {
      type: "expandable",
      id: "linux-details",
      title: "Linux details: forwarding switch, route protocols, and multipath",
      paragraphs: [
        "A Linux host does not forward by default. `net.ipv4.ip_forward` is 0, so a packet that arrives for some other address is dropped even if the routing table has a route for it. Setting it to 1 turns the host into a router, and changing it resets other per-interface settings to host or router defaults. Container hosts, VPN gateways, and Kubernetes nodes set it.",
        "Every kernel route records which protocol installed it. `proto kernel` routes come from interface addresses. `proto boot`, which is the default when `ip route add` is given no protocol, marks routes that a routing daemon will purge when it starts. `proto static` asks daemons to respect the route and possibly advertise it. `redirect` comes from ICMP redirects, and `ra` from IPv6 Router Advertisements. `ip route show proto static` filters by protocol.",
        "A route can have several next hops: `ip route add 10.20.0.0/16 nexthop via 10.0.0.2 weight 1 nexthop via 10.0.0.3 weight 1`. The kernel picks one per flow by hashing. `fib_multipath_hash_policy` defaults to 0, which hashes on addresses only, and 1 adds the ports. `ip route get` accepts `ipproto`, `sport`, and `dport`, so you can ask which next hop one specific flow will use.",
        "FRR's zebra daemon holds the RIB. It computes the best route across protocols per prefix and pushes the result into the kernel over Netlink. Once a route is in the kernel, FRR has little control over which multipath next hop the kernel's hash picks for a given flow.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "host-not-router",
          prompt:
            "A Linux VM has `eth0` on 10.0.0.0/24 and `eth1` on 10.1.0.0/24, and `net.ipv4.ip_forward` is 0. A machine on 10.0.0.0/24 uses the VM as its gateway to reach 10.1.0.9. What happens, and why does the routing table not help?",
          answer:
            "The VM drops the packet. The routing table has a connected route for 10.1.0.0/24, but the packet is not addressed to the VM, and with forwarding disabled the VM only delivers packets addressed to itself. Routing knowledge is there, but forwarding is switched off. Setting `ip_forward` to 1 fixes it.",
        },
        {
          id: "static-beats-ospf",
          prompt:
            "FRR has 10.20.0.0/16 from OSPF via 10.0.0.2 and a static route via 10.0.0.3. Which one does `ip route` show, and how would you make the static route a backup?",
          answer:
            "The static route, because its administrative distance (1) is lower than OSPF's (110). The kernel receives only that one. To make it a backup, configure it with a distance above 110. It will then be installed only when OSPF has no route for the prefix.",
        },
        {
          id: "known-not-installed",
          prompt:
            "A BGP session is established, and the daemon lists a route for 198.51.100.0/24, but traffic to 198.51.100.7 still leaves through the default route. What do you check first?",
          answer:
            "Check whether the route reached the FIB, not just the RIB. `ip route get 198.51.100.7` shows what the kernel will actually do. In FRR, `show ip route 198.51.100.0/24` shows whether the route is selected (`>`) and installed (`*`), or rejected. Common causes are a better route from another source, or a BGP next hop that no route can resolve.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "rfc1812-route-database",
      statement:
        "Forwarding requires a router to choose the address and interface of the next-hop router, or of the destination host on the final hop, using a route database that is also called a routing table or forwarding table. Building that database through routing protocols and configuration is called routing, and routers seek to keep minimal state.",
      sourceId: "rfc1812",
      locator: "Section 2.2.3, Routers",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rfc1812-forwarding-steps",
      statement:
        "A router validates the IP header, decides whether the datagram is for local delivery or forwarding, and selects a next hop from its FIB by pruning rules applied in order: basic match, longest match, then best metric. If the candidate set becomes empty, the packet is discarded as unreachable.",
      sourceId: "rfc1812",
      locator: "Section 5.2.1.1, General; Section 5.2.4.3, Next Hop Address",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rfc1812-ttl",
      statement:
        "A router forwarding a packet must decrement the TTL by at least one; if it reaches zero, the packet must be discarded and, for a non-multicast destination, an ICMP Time Exceeded code 0 sent to the source. The hop-count function keeps routing problems from making packets loop forever.",
      sourceId: "rfc1812",
      locator: "Section 5.3.1, Time to Live (TTL)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "frr-rib-fib",
      statement:
        "In FRR, routing protocols send their best routes to zebra, which holds the RIB, computes the best route across protocols for each prefix, and feeds that FIB to the kernel over Netlink on Linux. The route with the lowest administrative distance is selected; defaults include connected and kernel 0, static 1, eBGP 20, OSPF 110, RIP 120, and iBGP 200. In `show ip route`, `>` marks the selected route and `*` a FIB route.",
      sourceId: "frr-zebra",
      locator: "Administrative Distance; ECMP; zebra FIB push interface",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "linux-ip-forward",
      statement:
        "`net.ipv4.ip_forward` controls forwarding between interfaces and defaults to 0; changing it resets configuration to host (RFC 1122) or router (RFC 1812) defaults. `fib_multipath_hash_policy` defaults to 0, a Layer 3 hash, and 1 selects a Layer 4 hash.",
      sourceId: "kernel-ip-sysctl",
      locator: "/proc/sys/net/ipv4: ip_forward; fib_multipath_hash_policy",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-route-protocols",
      statement:
        "A Linux route's protocol records who installed it: `kernel` during autoconfiguration, `boot` during boot (purged when a routing daemon starts, and assumed when none is given), `static` by an administrator to override dynamic routing, `redirect` from an ICMP redirect, and `ra` from router discovery. A multipath route lists several `nexthop` entries with weights, and `ip route get` accepts `ipproto`, `sport`, and `dport` for the lookup.",
      sourceId: "man-ip-route",
      locator: "ip route add: protocol RTPROTO, nexthop NEXTHOP; ip route get",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    {
      sourceId: "rfc1812",
      note: "Sections 2.2.3, 5.2.1.1, 5.2.4.3, and 5.3.1: what a router is, the forwarding walk-through, and TTL.",
    },
    {
      sourceId: "frr-zebra",
      note: "RIB, FIB, and administrative distance in a Linux routing suite.",
    },
    { sourceId: "kernel-ip-sysctl", note: "`ip_forward` and the multipath hash policy." },
    { sourceId: "man-ip-route", note: "Route protocols, multipath next hops, and `ip route get`." },
  ],
} satisfies LessonBody;
