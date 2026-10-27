import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m02.same-subnet",
  status: "complete",
  objectives: [
    "Trace every step from `connect()` to the frame on the wire when the destination is on the same subnet.",
    "Explain how a host decides whether a destination is on-link or must go through a router.",
    "List which header fields a router changes and which it leaves alone, and explain why.",
    "Use `ip route get` and `ip neigh` to predict the next hop and the destination MAC for any address.",
  ],
  prerequisites: ["m02.arp"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "A host has an IP datagram for some destination address. It must decide whether it can hand the datagram straight to the destination over its own link, or has to give it to a router. Then it must build a frame for whichever neighbour it chose.",
      whyItMatters:
        "Most 'can reach A but not B' problems are a wrong answer to this decision: a bad mask, a missing gateway, or an ARP failure for the right next hop. Knowing which neighbour a packet goes to tells you where to capture, which ARP entry to check, and whether a firewall or router is even involved.",
      idea: "Split delivery into two questions. The IP layer picks the next hop: if the destination's prefix matches a connected subnet, the next hop is the destination itself; otherwise it is a router from the routing table. The link layer then resolves the next hop's IP to a MAC and sends the frame. The IP header always names the final destination. The frame names only the next neighbour.",
      howItWorks:
        "Same subnet: 192.0.2.10/24 sends to 192.0.2.20. Masking both with /24 gives 192.0.2.0, so the kernel's connected route `192.0.2.0/24 dev eth0` matches. The next hop is 192.0.2.20, ARP resolves its MAC, and the frame goes out with source MAC A and destination MAC B. The switch forwards it on the destination MAC. No router touches it, and the TTL arrives unchanged. Off subnet: for 198.51.100.7 only the default route matches, so the next hop is the gateway 192.0.2.1. ARP resolves the gateway's MAC, and the frame is addressed to the router, while the IP destination is still 198.51.100.7. The router strips the frame, decrements the TTL, recomputes the IPv4 header checksum, looks up its own next hop, and builds a new frame from its own MAC to that next hop's MAC.",
      whyItWorks:
        "A MAC address is meaningful only on one link, so each link's frame has to be addressed with that link's addresses. An IP address is meaningful everywhere, so the end-to-end destination can ride unchanged across every link. Separating the two lets each link use whatever technology it has, and lets routers work only on IP.",
      limits:
        "The on-link test is only as good as the configuration: a wrong mask makes a host ARP for off-link addresses or send local traffic to the router. The destination IP is not always preserved: destination NAT, load balancers, and proxies rewrite it, or terminate the connection and open a new one. IPv6 makes the same decision using on-link prefixes from Router Advertisements and Neighbor Discovery instead of ARP. Cloud networks often route even same-subnet traffic through a virtual router while presenting it as one link.",
      connections:
        "Module 3 covers prefix arithmetic and the default gateway in depth. Module 4 covers routing tables, longest-prefix match, and what routers do with TTL (why.06 also appears there). Module 10 covers address rewriting by NAT.",
    },
    {
      type: "example",
      id: "same-subnet-walkthrough",
      title: "From `connect()` to frame, on one subnet",
      setup:
        "Host A is 192.0.2.10/24 with MAC 00:00:5e:00:53:0a. Host B is 192.0.2.20/24 with MAC 00:00:5e:00:53:14. Both are on one switch in one VLAN. A runs `curl http://192.0.2.20/`. A's ARP cache is empty.",
      walkthrough: [
        "`connect()` asks TCP to send a SYN. TCP builds the segment with a local ephemeral port and destination port 80, and IP adds source 192.0.2.10, destination 192.0.2.20, TTL 64.",
        "Route lookup: `ip route get 192.0.2.20` prints `192.0.2.20 dev eth0 src 192.0.2.10`. There is no `via`, so the destination itself is the next hop.",
        "Neighbour lookup: no entry for 192.0.2.20, so the SYN waits while A broadcasts an ARP request and B answers with 00:00:5e:00:53:14.",
        "Frame: destination MAC 00:00:5e:00:53:14, source MAC 00:00:5e:00:53:0a, EtherType 0x0800, then the IP datagram. The switch looks up B's MAC and sends it out B's port only.",
        "B's interface accepts the frame because the destination MAC is its own. Its kernel checks that 192.0.2.20 is a local address, delivers the SYN to the listening socket, and replies. The reply needs no ARP, because B cached A from the request.",
        "On B, `tcpdump -e` shows the SYN with TTL 64, exactly as A sent it. No router decremented it.",
      ],
      result:
        "On one subnet, the frame's destination MAC and the packet's destination IP name the same host. The only devices in the path are the two hosts and the switch.",
    },
    {
      type: "example",
      id: "off-subnet-walkthrough",
      title: "The same request, one subnet away",
      setup:
        "Now A sends to 198.51.100.7. A's routing table has `default via 192.0.2.1 dev eth0`. The router R has 192.0.2.1 (MAC 00:00:5e:00:53:01) on A's side and 198.51.100.1 (MAC 00:00:5e:00:53:02) on the far side.",
      walkthrough: [
        "`ip route get 198.51.100.7` prints `198.51.100.7 via 192.0.2.1 dev eth0 src 192.0.2.10`. The `via` names the next hop.",
        "A ARPs for 192.0.2.1, not for 198.51.100.7. The frame carries destination MAC 00:00:5e:00:53:01 and destination IP 198.51.100.7.",
        "R accepts the frame because it is addressed to R's MAC, discards the Ethernet header, sees that 198.51.100.7 is not its own address, and looks it up: connected on its far interface.",
        "R decrements the TTL from 64 to 63, recomputes the header checksum, ARPs for 198.51.100.7 on the far link, and sends a new frame from 00:00:5e:00:53:02 to the server's MAC.",
        "The server sees source IP 192.0.2.10 and TTL 63, and source MAC 00:00:5e:00:53:02, the router's.",
      ],
      result:
        "Across a routed hop, the IP addresses stay the same and the MAC addresses are replaced. The TTL drop from 64 to 63 is the hop's fingerprint.",
    },
    {
      type: "comparison",
      id: "what-changes",
      title: "What a router changes on each hop",
      caption: "Plain IPv4 forwarding with no NAT, tunnel, or options.",
      columns: ["Changed by the router?", "Why"],
      rows: [
        {
          label: "Destination MAC",
          cells: [
            "Yes. Set to the next neighbour.",
            "The old one named the router itself, and it means nothing on the next link.",
          ],
        },
        {
          label: "Source MAC",
          cells: [
            "Yes. Set to the router's outgoing interface.",
            "Replies and switches on the next link need a local sender.",
          ],
        },
        {
          label: "Source and destination IP",
          cells: [
            "No.",
            "They name the endpoints, and every later hop still needs the destination.",
          ],
        },
        {
          label: "TTL (IPv6: Hop Limit)",
          cells: ["Yes. Decremented by one.", "It bounds how long a looping packet can live."],
        },
        {
          label: "IPv4 header checksum",
          cells: [
            "Yes. Recomputed.",
            "The TTL changed. IPv6 has no header checksum, so there is nothing to recompute.",
          ],
        },
        {
          label: "Ports and payload",
          cells: ["No.", "A plain router does not read past the IP header."],
        },
      ],
    },
    {
      type: "why",
      id: "why-mac-changes",
      questionId: "why.06",
    },
    {
      type: "callout",
      id: "wrong-mask",
      tone: "caution",
      title: "A wrong mask breaks one direction",
      scope: "common-implementation",
      body: "If A is misconfigured as 192.0.2.10/16, it treats 192.0.3.5 as on-link and ARPs for it. No host on A's VLAN owns 192.0.3.5, so the entry goes FAILED, unless the router answers with proxy ARP, which makes it work by accident. Meanwhile hosts with the right /24 send replies to A through the router. Check `ip -4 addr show` and the connected route in `ip route` first.",
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "When the destination IP does change, and other edge cases",
      paragraphs: [
        "Destination NAT, used by Kubernetes Services, port forwarding, and many load balancers, rewrites the destination IP on the way in and restores the source on the way back. A reverse proxy goes further: it terminates the TCP connection and opens a new one, so the server sees the proxy's address as the source. Neither contradicts the rule. They are devices that deliberately act as an endpoint or rewrite the header.",
        "Two IP subnets on one VLAN work for hosts in the same subnet, but traffic between the subnets goes to the router and comes back on the same wire, even though the two hosts share a switch.",
        "Linux adds a connected route for every address it configures: `ip route` shows `192.0.2.0/24 dev eth0 proto kernel scope link src 192.0.2.10`. `scope link` means destinations under that prefix are reached directly. Deleting that route makes the subnet unreachable even though the address is still configured.",
        "Traffic to your own address never reaches the network at all. Linux routes it over `lo` through the `local` routing table, which `ip route show table local` displays.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "capture-gateway",
          prompt:
            "You capture on a server and see a packet from 203.0.113.50 whose source MAC is 00:00:5e:00:53:01. Is 203.0.113.50 on the server's subnet?",
          answer:
            "Probably not. A source MAC that belongs to the gateway, which `ip neigh` shows for the gateway's IP, means the packet came through the router, so the sender is off-subnet. Its own MAC stayed on its own link. A TTL lower than the sender's usual initial value, such as 63 instead of 64, points the same way.",
        },
        {
          id: "next-hop-decision",
          prompt:
            "Host 10.1.2.3/24 has a default route via 10.1.2.1. For each of 10.1.2.200 and 10.1.3.4, which IP does it ARP for?",
          answer:
            "10.1.2.200 shares the 10.1.2.0/24 prefix, so the host ARPs for 10.1.2.200 itself. 10.1.3.4 does not, so it uses the default route and ARPs for 10.1.2.1.",
        },
        {
          id: "ttl-same-subnet",
          prompt:
            "A packet from a host on the same subnet arrives with TTL 64, the sender's default. What does that tell you about the path?",
          answer:
            "No router forwarded it, because each router decrements the TTL. Only switches, which do not touch the IP header, were in the path.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "local-remote-decision",
      statement:
        "A host decides whether a destination is on a connected network by comparing the destination and source address under the address mask; if they match, it sends directly to the destination, and otherwise through a gateway.",
      sourceId: "rfc1122",
      locator: "Section 3.3.1 and 3.3.1.1, Local/Remote Decision",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "gateway-new-local-header",
      statement:
        "A gateway strips the incoming local network header, decides from the internet address where to forward, determines a local net address for the next host, and has the outgoing interface create a new local network header.",
      sourceId: "rfc791",
      locator: "Section 2.2, Model of Operation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ttl-checksum-per-hop",
      statement:
        "Every module that processes a datagram must decrease the TTL by at least one, and because the TTL changes, the header checksum is recomputed and verified at each point the header is processed.",
      sourceId: "rfc791",
      locator: "Section 3.1, Time to Live and Header Checksum",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-hop-limit",
      statement: "The IPv6 Hop Limit is decremented by 1 by each node that forwards the packet.",
      sourceId: "rfc8200",
      locator: "Section 3, IPv6 Header Format",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "arp-resolves-next-hop",
      statement:
        "Routing determines the protocol address of the next hop and the hardware on which to find it; ARP then resolves that next-hop address, not the final destination.",
      sourceId: "rfc826",
      locator: "Packet Generation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-next-hop-determination",
      statement:
        "IPv6 next-hop determination uses longest-prefix match against the on-link prefixes: an on-link destination is its own next hop, and otherwise a router is chosen. The neighbor cache then supplies the link-layer address.",
      sourceId: "rfc4861",
      locator: "Section 5.2, Conceptual Sending Algorithm",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc1122",
      note: "Section 3.3.1: the local/remote decision and gateway selection.",
    },
    { sourceId: "rfc791", note: "Section 2.2 gateway operation; Section 3.1 TTL and checksum." },
    { sourceId: "rfc8200", note: "IPv6 Hop Limit." },
    { sourceId: "rfc826", note: "ARP resolves the next hop chosen by routing." },
    { sourceId: "rfc4861", note: "Section 5.2: IPv6 next-hop determination." },
  ],
} satisfies LessonBody;
