import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m03.gateway-and-lpm",
  status: "complete",
  objectives: [
    "Explain why a host hands every off-link packet to a gateway on its own link instead of addressing the remote host.",
    "Read a Linux routing table and pick the route for any destination by longest-prefix match, then by metric.",
    "Explain why routing tables overlap on purpose, and why the most specific route must win.",
    "Use `ip route get` to confirm the kernel's choice, and recognise route types that drop traffic.",
  ],
  prerequisites: ["m03.special-addresses"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "A host can deliver a frame only to a neighbour on one of its links. For any other destination it must pick a neighbour that will carry the packet further. A laptop may have several candidates: the Wi-Fi router, a VPN tunnel, a peering router for one internal network. The routing table lists them as prefixes that overlap, since the default route covers every address. The host needs one rule that picks exactly one route for every destination.",
      whyItMatters:
        "'Works from my machine but not the VPN', 'traffic to one subnet leaks around the tunnel', and 'the new route did nothing' are all route-selection questions. `ip route get <address>` answers them in one line, if you know the rule it applies.",
      idea: "Each route says 'for addresses under this prefix, send to this next hop on this interface'. Of all routes whose prefix contains the destination, use the one with the longest prefix: it describes the smallest set of addresses, so it knows the most about this destination. The default route, 0.0.0.0/0, matches everything and so always loses to anything more specific. The default gateway is just that route's next hop.",
      howItWorks:
        "For each route, mask the destination with the route's prefix length and compare it with the route's prefix. Keep the matches, take the longest, and on Linux break ties between equal prefixes by the lowest metric. The winner gives an output interface and either no gateway, meaning the destination is on that link, or a `via` address, which must itself be on-link. The host then resolves the chosen next hop, the destination or the gateway, to a MAC with ARP or Neighbor Discovery, and sends the frame there. The IP destination stays the remote address. The gateway runs the same lookup in its own table.",
      whyItWorks:
        "Longest match lets a table hold a broad route and narrow exceptions at the same time, without ordering the entries or deleting the broad one. A provider can announce one aggregate for all its customers, while a customer that is also reachable another way announces a more specific prefix that wins wherever it is heard. Because every implementation applies the same rule, adding a more-specific route has a predictable effect everywhere.",
      limits:
        "Longest match is the rule within one table. Linux first consults routing policy rules, which can choose a different table by source address, firewall mark, or interface, so `ip rule` matters as much as `ip route` on hosts with VPNs or several uplinks. A route is only as good as its next hop: if the gateway is down, the host keeps sending to it. A host also does not learn the path beyond its gateway, except through ICMP redirects. IPv6 hosts learn default routers from Router Advertisements rather than DHCP, and the next hop is usually the router's link-local address.",
      connections:
        "Module 2 showed the frame a host builds for an off-link destination. Lesson 1 of this module covered the prefix arithmetic behind each match. Module 4 covers how routers fill their tables with OSPF and BGP and apply the same match at scale (why.35 also appears there).",
    },
    {
      type: "example",
      id: "lpm-walkthrough",
      title: "Picking a route on a laptop with a VPN",
      setup:
        "`ip route` on a laptop prints four routes. `default via 10.0.0.1 dev eth0 proto dhcp metric 100`, `10.0.0.0/24 dev eth0 proto kernel scope link src 10.0.0.15`, `10.8.0.0/16 via 10.0.0.254 dev eth0`, and `10.8.4.0/24 dev wg0 proto kernel scope link src 10.8.4.2`.",
      walkthrough: [
        "10.0.0.77 matches the default (/0) and 10.0.0.0/24. The /24 is longer. It has no `via`, so 10.0.0.77 is on-link and the laptop ARPs for 10.0.0.77 on eth0.",
        "10.8.9.1 matches the default and 10.8.0.0/16, since 10.8.9.1 masked to /16 is 10.8.0.0. It does not match 10.8.4.0/24. The /16 wins, so the next hop is 10.0.0.254 and the laptop ARPs for 10.0.0.254, not for 10.8.9.1.",
        "10.8.4.9 matches the default, the /16, and the /24. The /24 wins, so the packet goes into the wg0 tunnel even though a broader route for the same network points elsewhere.",
        "203.0.113.10 matches only the default. The next hop is 10.0.0.1, the default gateway.",
        "`ip route get 10.8.4.9` prints `10.8.4.9 dev wg0 src 10.8.4.2`, and `ip route get 203.0.113.10` prints `203.0.113.10 via 10.0.0.1 dev eth0 src 10.0.0.15`. These are the kernel's actual choices, including policy rules, so check them rather than reading the table by eye.",
      ],
      result:
        "Every destination matched the default route, and none used it unless nothing more specific matched. Adding the /24 changed only the addresses inside it, and removing it would send them back to the /16.",
    },
    {
      type: "why",
      id: "why-default-gateway",
      questionId: "why.33",
    },
    {
      type: "why",
      id: "why-lpm",
      questionId: "why.35",
    },
    {
      type: "comparison",
      id: "route-fields",
      title: "Reading a Linux route",
      columns: ["Meaning", "What it decides"],
      rows: [
        {
          label: "default",
          cells: [
            "The prefix 0.0.0.0/0, or ::/0 for IPv6.",
            "Used only when nothing longer matches.",
          ],
        },
        {
          label: "via 10.0.0.1",
          cells: [
            "The next-hop router.",
            "The host ARPs for this address. Without `via`, it ARPs for the destination.",
          ],
        },
        {
          label: "dev eth0",
          cells: ["The output interface.", "Which link the frame goes out on."],
        },
        {
          label: "scope link",
          cells: [
            "Destinations under this prefix are directly reachable.",
            "No router is involved for these addresses.",
          ],
        },
        {
          label: "proto kernel / dhcp / ra",
          cells: [
            "Who installed it: the kernel for an address's own prefix, a DHCP client, or IPv6 Router Advertisements.",
            "Who will change or remove it later.",
          ],
        },
        {
          label: "metric 100",
          cells: ["Preference among routes with the same prefix.", "Lower wins a tie."],
        },
        {
          label: "src 10.0.0.15",
          cells: [
            "The preferred source address.",
            "Which of the host's addresses the peer sees, unless the socket chose one.",
          ],
        },
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Policy rules, route types, and other Linux details",
      paragraphs: [
        "The kernel's lookup starts with the routing policy database, not the main table. By default, `ip rule` has three rules: priority 0 looks up the `local` table, which holds the host's own and broadcast addresses, 32766 looks up `main`, which `ip route` shows, and 32767 looks up the empty `default` table. Longest match applies inside each table. WireGuard and many multi-uplink setups add rules that send some traffic to another table first.",
        "Not every route forwards. A `blackhole` route discards silently and the sender gets EINVAL. `unreachable` discards and returns ICMP host unreachable, so local senders get EHOSTUNREACH. `prohibit` returns 'administratively prohibited' and EACCES. Routers announcing an aggregate install a discard route for it, so that traffic for an unreachable more-specific is dropped instead of bouncing back up a default route.",
        "Two default routes can coexist with different metrics, for example wired at 100 and Wi-Fi at 600. The lower metric is used until its route disappears. A route with a `via` that is not inside any connected prefix is normally rejected, because the host could not resolve the gateway on any link. The `onlink` flag tells Linux to treat the next hop as directly attached anyway, which some cloud and container networks rely on.",
        "Some VPN clients capture all traffic by adding 0.0.0.0/1 and 128.0.0.0/1 through the tunnel. Each is longer than /0, and together they cover every address, so they beat the default route without deleting it. They also add a /32 route for the VPN server itself through the original gateway, or the tunnel's own packets would loop into the tunnel.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "pick-route",
          prompt:
            "A host's table is `default via 192.168.1.1 dev wlan0`, `192.168.1.0/24 dev wlan0`, `10.0.0.0/8 via 192.168.1.50 dev wlan0`, and `10.20.0.0/16 dev tun0`. Which next hop does each of 10.20.5.5, 10.30.1.1, and 198.51.100.9 use?",
          answer:
            "10.20.5.5 matches /0, /8, and /16, so it goes directly out tun0. 10.30.1.1 matches /0 and /8, so it goes via 192.168.1.50. 198.51.100.9 matches only the default, so it goes via 192.168.1.1. For the two `via` cases, the host ARPs for the gateway, not the destination.",
        },
        {
          id: "gateway-off-link",
          prompt:
            "A host is 10.0.0.15/24, and someone tries to configure its default gateway as 10.0.1.1. Why can't this work as written?",
          answer:
            "The host must deliver frames to the gateway directly, so it must be able to ARP for it on a link. 10.0.1.1 is not in 10.0.0.0/24, so no connected route covers it and the host would not know which link to ARP on. Linux normally rejects the route. The gateway has to be an address on the host's own subnet, unless the network is deliberately set up for `onlink`.",
        },
        {
          id: "split-default",
          prompt:
            "A VPN client adds `0.0.0.0/1 dev tun0` and `128.0.0.0/1 dev tun0` but leaves `default via 192.168.1.1` in place. Where does traffic to 198.51.100.9 go, and why didn't the VPN just replace the default route?",
          answer:
            "198.51.100.9 starts with a 1 bit, so it matches 128.0.0.0/1, which is longer than /0, and goes into the tunnel. The two /1 routes cover the whole space and beat the default by longest match. Leaving the default in place means that when the VPN goes away and removes its routes, the original path is still there.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "host-default-gateway",
      statement:
        "When a destination is not on a connected network and the host has no route cache entry for it, the IP layer must pick a gateway from its list of default gateways; hosts must support multiple default gateways, and a gateway that is not the best next hop forwards the datagram and returns an ICMP Redirect.",
      sourceId: "rfc1122",
      locator: "Section 3.3.1.2, Gateway Selection",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "longest-match-forwarding",
      statement:
        "Forwarding in the Internet is done on a longest-match basis, so a multihomed destination must be announced explicitly as a more-specific prefix rather than summarised inside an aggregate.",
      sourceId: "rfc4632",
      locator: "Section 5.1, Rules for Route Advertisement, rule 1; Section 5.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "default-route-prefix",
      statement:
        "The prefix 0.0.0.0/0 is the default route and must be accepted by all implementations.",
      sourceId: "rfc4632",
      locator: "Section 5.1, Rules for Route Advertisement",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "aggregate-discard",
      statement:
        "A router that generates an aggregate must discard packets that match the aggregate but none of its more-specific routes, to prevent forwarding loops; this is typically done with a null or discard route.",
      sourceId: "rfc4632",
      locator: "Section 5.1, rule 2; Section 5.2, How the Rules Work",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-route-get-and-types",
      statement:
        "`ip route get` prints a single route exactly as the kernel sees it. Route types include unreachable (ICMP host unreachable, EHOSTUNREACH), blackhole (silent discard, EINVAL), and prohibit (ICMP administratively prohibited, EACCES). `default` is equivalent to 0/0 or ::/0, lower metric values are preferred, and `onlink` treats a next hop as directly attached even if it matches no interface prefix.",
      sourceId: "man-ip-route",
      locator: "DESCRIPTION, Route types; ip route add, metric, onlink; ip route get",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-rpdb",
      statement:
        "Linux replaces the single longest-match table with a routing policy database. At startup it has three rules: priority 0 looks up table local, 32766 looks up table main, and 32767 looks up table default.",
      sourceId: "man-ip-rule",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ipv6-default-router-list",
      statement:
        "An IPv6 host adds the source address of a valid Router Advertisement with a non-zero Router Lifetime to its Default Router List, and that source address is the router's link-local address.",
      sourceId: "rfc4861",
      locator: "Section 6.3.4, Processing Received Router Advertisements; Section 4.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc1122", note: "Section 3.3.1.2: how a host chooses a gateway." },
    {
      sourceId: "rfc4632",
      note: "Sections 5.1 and 5.2: longest-match forwarding, the default route, and discard routes for aggregates.",
    },
    { sourceId: "rfc4861", note: "Sections 4.2 and 6.3.4: how IPv6 hosts learn default routers." },
    { sourceId: "man-ip-route", note: "Route fields, route types, and `ip route get`." },
    { sourceId: "man-ip-rule", note: "The routing policy database and its default rules." },
  ],
} satisfies LessonBody;
