import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m04.algorithms",
  status: "complete",
  objectives: [
    "Contrast static routes with dynamic routing by who notices a failure and who repairs the table.",
    "Compute a distance-vector route from neighbours' claims, and a link-state route from a shared map.",
    "Trace count-to-infinity step by step, and explain what split horizon, poisoned reverse, and triggered updates fix and do not fix.",
    "Explain why every dynamic protocol has a convergence window, and what packets experience during it.",
  ],
  prerequisites: ["m04.routing-vs-forwarding"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Each router needs a next hop for every destination, but it can only see its own links. A set of routers, each with local knowledge, has to produce tables that agree with each other and contain no loops. When a link fails, they must notice and agree again, while packets keep arriving.",
      whyItMatters:
        "Convergence time is outage time. 'The link flapped and we lost a few seconds of traffic', 'packets looped until the TTL expired', and 'the static route kept pointing at a dead box' are all properties of how the table is built. Cloud route tables and many small networks are static, while data centres and ISPs run dynamic protocols. Knowing which kind you are on tells you what will happen when something breaks.",
      idea: "With static routing, people write the routes and nothing changes until they edit them. With dynamic routing, routers exchange information and recompute. There are two classic designs. In distance-vector routing, each router tells its neighbours how far it is from every destination, and picks the neighbour that offers the shortest total distance. In link-state routing, each router tells every other router what links it has, so every router holds the whole map and computes shortest paths itself.",
      howItWorks:
        "Distance-vector, as in RIP: a router's table lists each destination's next hop and metric. Each router periodically sends its table to its neighbours, every 30 seconds in RIP. On receiving a neighbour's claim, it adds the cost of the link to that neighbour and keeps the smallest total: D(me, dest) = min over neighbours k of [cost(me, k) + D(k, dest)]. A route not refreshed for 180 seconds is invalid, and a metric of 16 means unreachable. Link-state, as in OSPF: routers discover neighbours with Hello packets. Each router floods a description of its own links and their costs to every router in the area, and reliable flooding gives every router the same database. Each router then runs Dijkstra's shortest-path algorithm with itself as the root, and uses every equal-cost path it finds.",
      whyItWorks:
        "Distance-vector needs no map. Bellman-Ford converges to correct distances in finite time from any starting state, provided routers keep exchanging updates. That is why it tolerates lost messages and asynchronous timers. Link-state replaces rumours with facts: a failure becomes one new link description, flooded everywhere, and every router recomputes from the same data with the same algorithm, so they reach the same answer quickly.",
      limits:
        "Distance-vector can count to infinity. After a failure, neighbours keep advertising stale routes that lead back through each other, metrics climb one exchange at a time, and packets loop meanwhile. Split horizon stops loops between two routers, but not loops among three or more. Link-state costs memory and CPU on every router and flooding traffic that grows with the network, which is why OSPF splits large networks into areas. Its routers also update their FIBs at slightly different moments, which can cause short micro-loops. Static routes have no failure detection at all. In every case, the TTL is what keeps a transient loop from lasting forever.",
      connections:
        "The previous lesson separated building the table from using it. The next lesson covers OSPF as a link-state protocol, and BGP as a path-vector protocol, a distance-vector variant that carries the whole AS path so it can reject loops. Module 3 explained how the TTL bounds a loop. Module 11 covers timeouts and retries, which hide most convergence loss from applications.",
    },
    {
      type: "example",
      id: "count-to-infinity",
      title: "Counting to infinity on three routers",
      setup:
        "Routers A, B, and C are in a line, A—B—C, and every link costs 1. Network N is attached to C. After convergence, C has N directly at metric 1, B has N via C at 2, and A has N via B at 3. The routers run a simple distance-vector protocol without split horizon, and 16 means unreachable. Then the B—C link fails.",
      walkthrough: [
        "B notices that its link to C is down and marks N unreachable. Before B can tell anyone, A's regular update arrives saying 'N, metric 3'.",
        "B has no other route, so it accepts: N via A, metric 3 + 1 = 4. A's route actually goes through B, but nothing in the update says so. B now advertises 'N, 4'.",
        "A's route to N goes through B, and a router must believe updates from its current next hop, even when they get worse. A updates to N via B, metric 5.",
        "The two routers take turns: B 6, A 7, B 8, and so on, one exchange at a time. Meanwhile a packet for N bounces between A and B until its TTL runs out.",
        "When the metric reaches 16, both routers declare N unreachable. With 30-second updates, that can take minutes. The small value of infinity is a deliberate trade-off between network diameter and convergence time.",
        "With split horizon, A never advertises N to B, because it learned N from B. B has no other source, marks N unreachable, and tells A, and the table converges after one update. Poisoned reverse makes this explicit: A advertises N to B with metric 16. Triggered updates send changes immediately instead of waiting for the next period. A loop through three routers, such as A to B to C to A, can still count to infinity, because no router is repeating a route to the neighbour it learned it from.",
        "Link-state contrast: B and C each flood a new link description without the B—C link. A receives them, reruns Dijkstra, finds no path to N, and removes the route. There is no exchange of opinions to unwind.",
      ],
      result:
        "Distance-vector routers learned the failure only through each other's stale claims, so bad news travelled slowly and looped while it did. Split horizon removed the two-router echo, but not every loop. Link-state routers learned the failure itself.",
    },
    {
      type: "comparison",
      id: "approaches",
      title: "Static, distance-vector, and link-state routing",
      columns: ["Static", "Distance-vector", "Link-state"],
      rows: [
        {
          label: "What is exchanged",
          cells: [
            "Nothing. A person or an API writes the routes.",
            "Each router's distances to every destination, sent only to neighbours.",
            "Each router's own links and costs, flooded to every router in the area.",
          ],
        },
        {
          label: "What a router knows",
          cells: [
            "Only what it was told.",
            "Each neighbour's claimed distance, not the topology.",
            "The whole area's topology.",
          ],
        },
        {
          label: "Computation",
          cells: [
            "None.",
            "Bellman-Ford: min of link cost + neighbour's claim.",
            "Dijkstra from itself.",
          ],
        },
        {
          label: "After a failure",
          cells: [
            "Traffic follows the stale route until someone edits it.",
            "Bad news spreads hop by hop. It can count to infinity.",
            "Fast, after flooding and recomputation. Brief micro-loops are possible.",
          ],
        },
        {
          label: "Cost",
          cells: [
            "Operator effort, and a risk of human error.",
            "Small messages and little state. Diameter is limited (15 hops in RIP).",
            "Memory and CPU on every router. Large networks need areas.",
          ],
        },
        {
          label: "Examples",
          cells: [
            "`ip route add`, cloud VPC route tables, a host's default route.",
            "RIP. BGP is a path-vector refinement.",
            "OSPF, IS-IS.",
          ],
        },
      ],
    },
    {
      type: "expandable",
      id: "convergence",
      title: "Convergence in practice, and static routes on Linux",
      paragraphs: [
        "Convergence has three parts: detecting the failure, spreading the news, and updating every FIB. Detection is often the slowest. A RIP router waits 180 seconds without updates before invalidating a route, because single updates are sometimes lost. An OSPF router waits for the RouterDeadInterval, a sample of 10-second Hellos and a dead interval of about four times that. A link that goes down is usually reported by the interface itself, but a neighbour that hangs while its link stays up is noticed only when its Hellos stop.",
        "While the routers converge, packets meet two kinds of trouble: they are sent toward the failed component, or they loop between routers whose FIBs disagree. RFC 5715 calls the second kind micro-loops. Fast-reroute repairs exist, but micro-loops can still keep packets from reaching them. Retransmission hides most of this from applications, which see a stall rather than an error.",
        "A distance-vector router must believe its current next hop even when the news is worse. That rule is what lets bad news spread at all, and it is also what makes the count climb. Routes from other neighbours are accepted only if they are better.",
        "On Linux, `ip route add 10.20.0.0/16 via 10.0.0.3` creates a route with `proto boot`, which a routing daemon purges when it starts. Add `proto static` if the route should survive. Static routes are the right tool when there is one exit, such as a host's default route, or when the next hop is a virtual router that the platform keeps alive, as in many clouds. Anywhere a next hop can die quietly, a static route becomes a black hole.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "dv-compute",
          prompt:
            "Router A has two neighbours: B over a link of cost 1, and C over a link of cost 4. B advertises network N at distance 5, and C advertises N at distance 1. Which next hop and metric does A choose?",
          answer:
            "Via B costs 1 + 5 = 6, and via C costs 4 + 1 = 5. A picks C with metric 5, even though the link to C is more expensive. Distance-vector compares totals: the cost to the neighbour plus the neighbour's claim.",
        },
        {
          id: "split-horizon-limit",
          prompt:
            "Split horizon is on. Routers A, B, and C form a triangle, and network N hangs off C. A and B both reach N via C at metric 2. C loses N. Can the routers still count to infinity?",
          answer:
            "Yes. Split horizon only stops a router from advertising a route back to the neighbour it learned it from. If B's old 'N, 2' reaches A before C's bad news does, A switches to N via B and may advertise that to C, because A did not learn it from C. C then advertises its new route to B, and the claims go around the triangle with the metric rising each time until it reaches infinity. Triggered updates make this race less likely but do not rule it out.",
        },
        {
          id: "static-dead-next-hop",
          prompt:
            "A server has `10.50.0.0/16 via 10.0.0.9`. The router at 10.0.0.9 hangs, but its switch port stays up. What happens to traffic for 10.50.0.0/16, and what would have changed with OSPF?",
          answer:
            "Nothing removes the static route, so the server keeps sending to 10.0.0.9. Once its neighbour entry fails, ARP gets no answer, and the traffic is lost until someone changes the route. With OSPF, neighbours would stop hearing the router's Hellos, declare it down after the dead interval, flood the change, and reroute around it if another path exists.",
        },
        {
          id: "ls-microloop",
          prompt:
            "Why can't link-state routers count to infinity, and why can they still loop briefly after a failure?",
          answer:
            "They do not compute from each other's distance claims. Each one computes from a flooded map of actual links, so a failure removes the link from every router's map. They still update their FIBs at slightly different moments, and while one router uses the new map and its neighbour still uses the old one, they can send a packet back and forth until the second updates or the TTL expires.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "dv-basics",
      statement:
        "In distance-vector algorithms, entities exchange only distances, and only with adjacent neighbours; each routing entry holds the next gateway and a metric. The best metric satisfies D(i,j) = min over neighbours k of [d(i,k) + D(k,j)], and this converges to correct estimates in finite time without topology changes, from any non-negative starting estimates.",
      sourceId: "rfc1058",
      locator: "Section 2, Distance Vector Algorithms",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rip-timers-infinity",
      statement:
        "In RIP, each gateway sends an update every 30 seconds; a route whose gateway is not heard from for 180 seconds is marked invalid; 16 is used as 'infinity' to mark an unreachable destination. RIP is limited to networks whose longest path is 15 hops.",
      sourceId: "rfc1058",
      locator:
        "Section 1.1, Limitations of the protocol; Section 2.1, Dealing with changes in topology",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "count-to-infinity",
      statement:
        "After a failure, gateways can keep claiming routes through each other, so metrics increase slowly until they reach infinity; this is 'counting to infinity', and infinity is kept small as a trade-off between network size and convergence speed. A gateway must believe an update from its current next hop whether the metric is higher or lower.",
      sourceId: "rfc1058",
      locator: "Section 2.2, Preventing instability; Section 2.2.2, Triggered updates",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "split-horizon",
      statement:
        "Simple split horizon omits routes learned from a neighbour in updates sent to that neighbour; poisoned reverse includes them with metric 16. Split horizon with poisoned reverse prevents loops involving only two gateways, but not loops among three. Triggered updates send changes almost immediately and speed convergence, but counting to infinity is still possible.",
      sourceId: "rfc1058",
      locator: "Section 2.2.1, Split horizon; Section 2.2.2, Triggered updates",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "link-state-basics",
      statement:
        "In a link-state protocol, each router maintains an identical database of the topology and distributes its local state by flooding. All routers run the same algorithm and build a shortest-path tree rooted at themselves. When several equal-cost routes exist, traffic is distributed among them. OSPF uses Hellos to acquire neighbours, floods LSAs reliably within an area, and computes the tree with the Dijkstra algorithm.",
      sourceId: "rfc2328",
      locator:
        "Section 1.1, Protocol overview; Section 4, Functional Summary; Section 16.1, Calculating the shortest-path tree for an area",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ospf-dead-interval",
      statement:
        "OSPF's HelloInterval has a sample value of 10 seconds on a LAN; RouterDeadInterval, after which neighbours declare a silent router down, should be a multiple of the HelloInterval, such as 4. Smaller intervals detect changes faster at the cost of more protocol traffic.",
      sourceId: "rfc2328",
      locator: "Appendix C.3, Router interface parameters",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "microloops",
      statement:
        "During a routing transition, packets can be lost because routers keep using the failed component, and because routers update their FIBs at different times, the inconsistent FIBs form forwarding loops, called micro-loops, which can also stop packets from reaching a fast-reroute repair.",
      sourceId: "rfc5715",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-static-boot",
      statement:
        "When `ip route add` is given no protocol, it assumes `boot`; boot routes are purged when a routing daemon starts, while `static` routes are installed by the administrator to override dynamic routing and are respected by routing daemons.",
      sourceId: "man-ip-route",
      locator: "ip route add, protocol RTPROTO",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    {
      sourceId: "rfc1058",
      note: "Sections 1.1 and 2: the distance-vector algorithm, RIP's timers, counting to infinity, split horizon, and triggered updates.",
    },
    {
      sourceId: "rfc2328",
      note: "Sections 1.1, 4, 16.1, and Appendix C.3: link-state operation, Dijkstra, and Hello timers.",
    },
    { sourceId: "rfc5715", note: "Section 1: why loops form while routers converge." },
    { sourceId: "man-ip-route", note: "`proto boot` and `proto static`." },
  ],
} satisfies LessonBody;
