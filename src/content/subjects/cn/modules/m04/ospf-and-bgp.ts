import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m04.ospf-bgp",
  status: "complete",
  objectives: [
    "Say what OSPF decides inside one network and what BGP decides between networks, and why one protocol cannot do both.",
    "Follow a BGP route from a neighbour's announcement through policy, best-path selection, and next-hop resolution through the IGP.",
    "Predict which route a router uses when BGP holds overlapping prefixes, by longest-prefix match.",
    "Explain how a more-specific announcement can redirect traffic, and what origin validation does and does not prevent.",
  ],
  prerequisites: ["m04.algorithms"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "The Internet is made of many independently run networks, called autonomous systems (ASes). Inside one AS, the operator wants the shortest paths and fast failover, and shares a full map with its own routers. Between ASes, nobody shares a map. Each network chooses routes by its own business policy, for example preferring a route through a paying customer to one through a provider it pays, and must still avoid loops across networks it cannot see.",
      whyItMatters:
        "Which provider your requests leave through, why traffic to a SaaS region takes a long detour, and why a stranger's announcement can take a service offline are all BGP decisions. Failover inside a data centre is an OSPF-style decision. Kubernetes and load-balancer setups that 'advertise service IPs with BGP' rely on the same selection rules. A software engineer does not configure these protocols every day, but needs to know what they decide and what they cannot know.",
      idea: "Route on two levels. An interior gateway protocol (IGP) such as OSPF computes shortest paths inside an AS from a shared link-state map, using link costs. BGP, between ASes, exchanges per-prefix reachability with the list of ASes the route has passed through. Each AS picks among those routes by its own policy, and uses the AS list to reject loops. The router's forwarding table then combines both kinds of route, and longest-prefix match decides which one applies to each packet.",
      howItWorks:
        "OSPF runs directly over IP (protocol 89). Routers send Hellos to find neighbours and confirm two-way communication, flood link-state advertisements describing their links and interface costs, and run Dijkstra to get the lowest-cost path to every prefix. Areas hide one part of the network's detail from the rest. BGP runs over a TCP session, on port 179, between configured peers. A peer first sends every route its export policy allows, then only incremental UPDATE messages that announce or withdraw prefixes, with KEEPALIVEs to show the session is alive. Each route is a prefix plus path attributes: AS_PATH, NEXT_HOP, and others. Received routes sit in Adj-RIB-In. Import policy assigns a degree of preference, LOCAL_PREF. The best route per prefix goes into Loc-RIB, and export policy decides what each peer is told. Routes containing the router's own AS are discarded as loops. Ties are broken by the shortest AS_PATH, then origin, MED, eBGP over iBGP, and the lowest IGP cost to the NEXT_HOP. That NEXT_HOP must be resolvable through the routing table, so inside an AS BGP relies on the IGP to reach the exit router.",
      whyItWorks:
        "OSPF converges quickly because every router in an area computes from the same map. BGP scales to the whole Internet because it shares only 'this prefix, through these ASes', never internal topology, and the AS list is enough to detect loops without a global map. Policy is local, so each AS stays autonomous. Longest-prefix match lets the two protocols coexist without agreeing on prefix boundaries: a BGP aggregate, an OSPF subnet, and a static /32 can all sit in one table, and each packet uses the most specific one.",
      limits:
        "Base BGP believes what its peers announce. Nothing in RFC 4271 checks that the origin AS is entitled to a prefix, so mistakes and hijacks propagate, and a more-specific bogus prefix wins by longest match wherever it is accepted. RPKI origin validation checks only the origin AS, not the whole path. BGP chooses by policy and AS-path length, not by latency or load, and supports only destination-based forwarding. OSPF detects a silent neighbour only after its dead interval. Neither protocol knows whether your application works.",
      connections:
        "The previous lesson covered distance-vector and link-state algorithms. Module 3 explained why longest-prefix matching is necessary (why.35 also appears here). The next lesson shows how ICMP and traceroute reveal the result of all this. Module 10 covers load balancing, where anycast and BGP-advertised service addresses appear.",
    },
    {
      type: "example",
      id: "bgp-exit-choice",
      title: "Which provider a company's traffic leaves through",
      setup:
        "A company runs AS 64500 with border routers B1 and B2 and OSPF inside. B1 peers with ISP-A (AS 64497), and B2 peers with ISP-B (AS 64498). A SaaS provider, AS 64496, originates 203.0.113.0/24. B1 hears it from ISP-A with AS_PATH `64497 64496`, and B2 hears it from ISP-B with AS_PATH `64498 64499 64496`. All addresses and AS numbers are documentation values.",
      walkthrough: [
        "Without policy, every router in AS 64500 compares the two routes. Neither AS_PATH contains 64500, so both are loop-free. LOCAL_PREF is equal, so the shorter AS_PATH wins: ISP-A, through B1.",
        "The company pays less for ISP-B, so B2's import policy sets LOCAL_PREF 200 on routes from ISP-B and B1 leaves the default. LOCAL_PREF is compared before AS_PATH length, so every router now prefers ISP-B for 203.0.113.0/24, even though that path crosses one more AS.",
        "B2 tells the other routers about the route over iBGP. An internal router R5 receives NEXT_HOP 198.51.100.1, ISP-B's address on the B2 link, and resolves it through OSPF to 'via B2'. Packets from R5 follow OSPF's shortest path to B2 and then leave for ISP-B. BGP chose the exit, and OSPF chose the path to it.",
        "The SaaS provider also announces 203.0.113.128/25, but only to ISP-A. The routers now hold two prefixes. LOCAL_PREF compares routes for the same prefix only, so the /25 has one candidate, via ISP-A. A packet to 203.0.113.200 matches both the /24 and the /25, and longest match picks the /25, so it leaves via ISP-A. A packet to 203.0.113.10 matches only the /24 and leaves via ISP-B.",
        "If another network mistakenly announces 203.0.113.0/25 and 203.0.113.128/25 and its neighbours accept them, those more-specific routes beat the real /24 in every router that hears them, and traffic to the SaaS goes to the wrong network. If the SaaS provider had published a Route Origin Authorization for the /24 with maximum length 24, origin validation would mark both /25s Invalid. They would be dropped only by networks configured to reject Invalid routes.",
      ],
      result:
        "Policy picked among routes for the same prefix. Longest-prefix match then picked among prefixes for each packet, regardless of policy. OSPF delivered packets to whichever border router BGP had chosen.",
    },
    {
      type: "comparison",
      id: "ospf-vs-bgp",
      title: "OSPF and BGP at a glance",
      columns: ["OSPF", "BGP"],
      rows: [
        {
          label: "Scope",
          cells: ["Inside one AS (an IGP).", "Between ASes, and across one AS over iBGP."],
        },
        {
          label: "What is shared",
          cells: [
            "Every router's links and costs, flooded within an area.",
            "Per-prefix reachability with AS_PATH, NEXT_HOP, and policy attributes. No topology.",
          ],
        },
        {
          label: "Neighbours",
          cells: [
            "Discovered by Hello packets on each link.",
            "Configured peers over TCP port 179.",
          ],
        },
        {
          label: "Chooses by",
          cells: [
            "Lowest total interface cost (Dijkstra). Equal-cost paths are all used.",
            "Local policy (LOCAL_PREF), then shortest AS_PATH, and further tie-breakers.",
          ],
        },
        {
          label: "Loop prevention",
          cells: [
            "Every router computes from the same map.",
            "Reject routes whose AS_PATH contains your own AS.",
          ],
        },
        {
          label: "Failure detection",
          cells: [
            "Interface down, or Hellos missing for the dead interval.",
            "A withdrawal, or the session's hold timer expiring (suggested 90 seconds).",
          ],
        },
        {
          label: "What a software engineer sees",
          cells: [
            "Seconds of loss when a data-centre link fails, and ECMP spreading flows.",
            "Which provider or region traffic exits through, route leaks, hijacks, and anycast.",
          ],
        },
      ],
    },
    {
      type: "expandable",
      id: "details",
      title: "Details that explain common surprises",
      paragraphs: [
        "BGP sends no periodic refresh. A route stays until it is withdrawn, replaced by a new announcement of the same prefix, or removed because the session closed. Closing a session removes every route learned over it at once, which is why one flapping peering session can cause a burst of changes far away.",
        "LOCAL_PREF is sent only to internal peers, so it is a decision for your own AS. MED, by contrast, distinguishes several links to the same neighbouring AS, and a lower value is preferred. It is compared only between routes from that neighbour. When every other tie-breaker is equal, each router prefers the exit with the lowest IGP cost to the NEXT_HOP, so different routers in one AS can pick different exits for the same prefix.",
        "BGP keeps overlapping routes separately. If it accepts both a /24 and a /25 inside it, it installs both, or aggregates them only when they share a NEXT_HOP. The /25 then decides traffic for its half, and if it is withdrawn, that half falls back to the /24. A forwarding router applies the same pruning as a host: matching prefixes, then the longest, then the best metric.",
        "OSPF areas: each area has its own database and runs its own Dijkstra. Area border routers summarise an area's prefixes into the backbone, so a link flap inside one area does not trigger a recomputation everywhere. Routes learned from outside, such as BGP or static routes redistributed into OSPF, are flooded as external routes, except into stub areas.",
        "RPKI origin validation lets a prefix holder sign which AS may originate the prefix, and up to what prefix length. A receiver labels each route Valid, Invalid, or NotFound. NotFound means no authorization covers the prefix. The label does not remove a route unless the receiver is configured to act on it. The origin is the last AS in the AS_PATH, so an announcement that forges the right origin at the end of a false path is still Valid.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "ospf-cost",
          prompt:
            "OSPF interface costs: R1–R2 is 10, R2–R4 is 10, R1–R3 is 5, and R3–R4 is 30. Which path does R1 use to reach a prefix on R4, and what happens when R1–R2 fails?",
          answer:
            "Via R2 costs 10 + 10 = 20, and via R3 costs 5 + 30 = 35, so R1 uses R2. When R1–R2 fails, R1 and R2 flood new link-state advertisements without that link, every router reruns Dijkstra, and R1 switches to R3 at cost 35. OSPF picks the lowest total cost, not the fewest hops or the cheapest first link.",
        },
        {
          id: "local-pref-vs-path",
          prompt:
            "Your AS hears 192.0.2.0/24 from provider X with AS_PATH `65010 65020` and from provider Y with AS_PATH `65030 65040 65050 65020`. Your import policy sets LOCAL_PREF 300 on routes from Y and 100 on routes from X. Which exit does traffic to 192.0.2.7 use?",
          answer:
            "Y. LOCAL_PREF is compared before AS_PATH length, and a higher value wins, so Y's route is best even though its AS_PATH is twice as long. AS_PATH length breaks ties only between routes with equal preference.",
        },
        {
          id: "more-specific-wins",
          prompt:
            "Your service is at 198.51.100.0/22, announced by your AS. Another network announces 198.51.101.0/24, and some providers accept it. What happens to traffic for 198.51.101.5 and for 198.51.100.5 in those providers' networks?",
          answer:
            "Traffic for 198.51.101.5 matches both the /22 and the /24, and longest match picks the /24, so it goes to the other network. Traffic for 198.51.100.5 is outside the /24 and still follows your /22. BGP policy cannot override this, because the two routes are for different prefixes. If you published a Route Origin Authorization for the /22 with maximum length 22, origin validation would label the /24 Invalid, and providers that reject Invalid routes would drop it.",
        },
        {
          id: "next-hop-igp",
          prompt:
            "An internal router learns a BGP route over iBGP with a NEXT_HOP of another border router's address, but OSPF has no route to that address. What does the router do with the BGP route, and why?",
          answer:
            "It does not use it. A BGP route whose NEXT_HOP cannot be resolved through the routing table is excluded from selection and kept out of the forwarding table, because the router would not know which neighbour to send the packet to. BGP chooses the exit, but the IGP has to provide the path to it.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ospf-overview",
      statement:
        "OSPF is an interior gateway protocol that distributes routing information within one AS using link-state technology: each router keeps an identical database, floods its local state, and builds a shortest-path tree rooted at itself. Equal-cost routes share traffic, the cost is a single dimensionless metric, areas hide their topology from the rest of the AS, and a packet is routed to the longest (most specific) match.",
      sourceId: "rfc2328",
      locator: "Section 1, Introduction; Section 1.1, Protocol overview",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ospf-operation",
      statement:
        "OSPF runs directly over IP protocol 89. The Hello Protocol establishes neighbour relationships and confirms bidirectional communication; link-state advertisements are flooded reliably so all routers in an area hold the same database. Each area has its own database, and area border routers summarise an area's topology for the backbone; external routes are not flooded into stub areas. An interface's output cost is advertised as its link cost.",
      sourceId: "rfc2328",
      locator:
        "Section 3, Splitting the AS into Areas; Section 4, Functional Summary, 4.1, 4.2, and 4.3; Section 7.1, The Hello Protocol; Appendix C.3, Interface output cost",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "bgp-summary",
      statement:
        "BGP is an inter-AS routing protocol whose reachability information includes the list of ASes traversed, enough to prune routing loops and enforce some policy. It supports only destination-based forwarding. It runs over TCP port 179, sends the routes its export policy allows and then incremental updates, needs no periodic refresh, and uses KEEPALIVE messages. Peers in another AS are external (EBGP); peers in the same AS are internal (IBGP).",
      sourceId: "rfc4271",
      locator: "Section 3, Summary of Operation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "bgp-routes-ribs",
      statement:
        "A BGP route pairs the destinations in one prefix with path attributes. A route is withdrawn by listing it in WITHDRAWN ROUTES, by advertising a replacement with the same prefix, or by closing the session, which removes every route the two speakers had exchanged. Received routes are held in Adj-RIBs-In, selected routes in Loc-RIB, and routes to advertise in Adj-RIBs-Out.",
      sourceId: "rfc4271",
      locator:
        "Section 3.1, Routes: Advertisement and Storage; Section 3.2, Routing Information Base",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "bgp-decision",
      statement:
        "A BGP speaker assigns each route a degree of preference from local policy, carried to internal peers as LOCAL_PREF, where higher is preferred, and never sent to external peers. Routes whose AS_PATH contains the local AS, or whose NEXT_HOP cannot be resolved, are excluded. Ties are broken in order by fewest ASes in AS_PATH, lowest Origin, MED (lower preferred, discriminating links to the same neighbouring AS and compared only between its routes), EBGP over IBGP, and lowest interior cost to the NEXT_HOP.",
      sourceId: "rfc4271",
      locator:
        "Section 5.1.4, MULTI_EXIT_DISC; Section 5.1.5, LOCAL_PREF; Section 9.1.1, Phase 1; Section 9.1.2, Phase 2; Section 9.1.2.2, Breaking Ties",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "bgp-next-hop-resolution",
      statement:
        "A speaker advertising a learned route to an internal peer should not change its NEXT_HOP unless configured to announce itself. Before packets are forwarded along a BGP route, its NEXT_HOP must be resolved to a directly connected next hop through the routing table; unresolvable routes are removed from Loc-RIB and the routing table, and a change in the IGP cost to the NEXT_HOP triggers route selection again.",
      sourceId: "rfc4271",
      locator:
        "Section 5.1.3, NEXT_HOP, rule 1; Section 9.1.2, Phase 2: Route Selection; Section 9.1.2.1",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "bgp-overlapping-routes",
      statement:
        "When a BGP speaker accepts both a less specific and a more specific route, it must install both in Loc-RIB, or aggregate them if they share a NEXT_HOP. The overlap is decided by the more specific route, and if that route is withdrawn, its destinations remain reachable through the less specific one.",
      sourceId: "rfc4271",
      locator: "Section 9.1.4, Overlapping Routes",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "bgp-hold-time",
      statement: "The suggested default value for the BGP HoldTime is 90 seconds.",
      sourceId: "rfc4271",
      locator: "Section 10, BGP Timers",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "router-longest-match",
      statement:
        "A router selecting a next hop keeps the routes whose prefix matches the destination, then only those with the longest prefix length, then the best metric.",
      sourceId: "rfc1812",
      locator: "Section 5.2.4.3, Next Hop Address, rules 1, 2, and 4",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rpki-origin-validation",
      statement:
        "RPKI-based origin validation checks that the AS originating a prefix, the rightmost AS in the AS_PATH, is authorized by the prefix holder through a Route Origin Authorization with a maximum prefix length, and only partially addresses mis-announcement. A route is Valid if an authorization matches it, Invalid if one covers its prefix but none matches, and NotFound otherwise; validation state must not exclude a route from the decision process unless explicitly configured.",
      sourceId: "rfc6811",
      locator: "Section 1, Introduction; Section 2, Prefix-to-AS Mapping Database",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc2328",
      note: "Sections 1, 1.1, 3, 4, 7.1, and Appendix C.3: what OSPF computes and how it finds neighbours.",
    },
    {
      sourceId: "rfc4271",
      note: "Sections 3, 3.1, 3.2, 5.1.3 to 5.1.5, 9.1, 9.1.4, and 10: what BGP exchanges and how it chooses.",
    },
    {
      sourceId: "rfc1812",
      note: "Section 5.2.4.3: longest match in a router's forwarding decision.",
    },
    {
      sourceId: "rfc6811",
      note: "Sections 1 and 2: what origin validation checks, and its states.",
    },
  ],
} satisfies LessonBody;
