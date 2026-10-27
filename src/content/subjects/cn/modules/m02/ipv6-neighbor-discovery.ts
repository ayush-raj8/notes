import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m02.ipv6-nd",
  status: "complete",
  objectives: [
    "Walk through IPv6 address resolution with Neighbor Solicitation and Neighbor Advertisement, including the multicast addresses used.",
    "Compute the solicited-node multicast address and its Ethernet MAC for an IPv6 address.",
    "List what Neighbor Discovery does besides address resolution, and compare it with ARP.",
    "Explain why blocking ICMPv6 breaks IPv6 on a link, and where Neighbor Discovery is still vulnerable.",
  ],
  prerequisites: ["m02.same-subnet"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "An IPv6 host has the same problem as an IPv4 host: it knows the next hop's IP address and needs its MAC. But IPv6 has no broadcast, and an IPv6 host also has to find its routers, learn which prefixes are on-link, make sure nobody else already uses its address, and notice when a neighbour stops responding. IPv4 solved those with ARP, DHCP, ICMP Router Discovery, and timeouts, or not at all.",
      whyItMatters:
        "Every IPv6 packet on Ethernet depends on Neighbor Discovery, including traffic to the default router. Dual-stack services fail in IPv6-only ways when ICMPv6 is filtered, when a switch drops multicast, or when a rogue Router Advertisement appears. `ip -6 neigh` and the ICMPv6 messages in a capture are the first things to read.",
      idea: "Move neighbour resolution into IP itself, as ICMPv6 messages that are valid only on the local link. A host asks for a neighbour's MAC with a Neighbor Solicitation sent to a multicast group that only the target, and the few hosts sharing the target's last 24 address bits, listen to. The target answers with a unicast Neighbor Advertisement. The same message family also discovers routers and prefixes, detects duplicate addresses, and confirms that a neighbour is still reachable.",
      howItWorks:
        "To resolve 2001:db8::20, the host creates an INCOMPLETE neighbour entry and queues the packet. It sends an ICMPv6 Neighbor Solicitation (type 135) with Hop Limit 255 and its own MAC in a Source Link-Layer Address option, to the solicited-node address ff02::1:ff00:20, which maps to Ethernet multicast 33:33:ff:00:00:20. The target joined that group when it configured its address. It caches the sender's MAC and replies with a Neighbor Advertisement (type 136), unicast, with the Solicited flag set and its MAC in a Target Link-Layer Address option. The sender's entry becomes REACHABLE, the queued packet is sent, and the entry then cycles through the same STALE, DELAY, and PROBE states that Linux uses for ARP.",
      whyItWorks:
        "A solicited-node group contains only addresses that share the target's low 24 bits. A network card that filters multicast in hardware drops the solicitation without waking the CPU on almost every other host, unlike an ARP broadcast. Requiring Hop Limit 255 on receipt means a message must have come from the link, since any router on the way would have decremented it. Carrying the sender's MAC in the solicitation lets one exchange teach both sides. Using upper-layer confirmations such as new TCP ACKs avoids most probe traffic.",
      limits:
        "Neighbor Discovery is no more authenticated than ARP: any host on the link can send false advertisements or a rogue Router Advertisement that makes itself the default router. SEND exists but is rarely deployed. RA Guard on switches is the common defence. Because ND depends on multicast, switches with MLD snooping must see hosts join their solicited-node groups, or resolution fails for some hosts. Filtering ICMPv6 on a host or a link breaks resolution entirely. Large IPv6 subnets also let a remote scanner fill a router's neighbour cache with INCOMPLETE entries for addresses that do not exist.",
      connections:
        "Module 3 covers IPv6 addresses, link-local addresses, and prefixes, which Router Advertisements announce. Module 4 covers how the default router is used. Module 10 covers firewalls, where the ICMPv6 types that ND needs must be allowed.",
    },
    {
      type: "comparison",
      id: "arp-vs-nd",
      title: "ARP and Neighbor Discovery",
      columns: ["ARP (IPv4)", "Neighbor Discovery (IPv6)"],
      rows: [
        {
          label: "Carried in",
          cells: ["Its own EtherType, 0x0806, beside IP.", "ICMPv6 inside IPv6, EtherType 0x86DD."],
        },
        {
          label: "Request goes to",
          cells: [
            "Broadcast, ff:ff:ff:ff:ff:ff. Every host processes it.",
            "The target's solicited-node multicast group, 33:33:ff:xx:xx:xx.",
          ],
        },
        {
          label: "Off-link protection",
          cells: [
            "Not needed by design, since ARP cannot be routed. There is also no authentication.",
            "Hop Limit must be 255 on receipt. There is still no authentication by default.",
          ],
        },
        {
          label: "Router discovery",
          cells: [
            "Separate: DHCP or static configuration.",
            "Router Solicitation and Router Advertisement, which also carry prefixes and MTU.",
          ],
        },
        {
          label: "Duplicate address check",
          cells: [
            "Optional ARP probes (RFC 5227).",
            "Duplicate Address Detection is required before using a unicast address.",
          ],
        },
        {
          label: "Reachability",
          cells: [
            "Not in the protocol. Linux adds it in its neighbour subsystem.",
            "Neighbor Unreachability Detection is part of the protocol.",
          ],
        },
        {
          label: "Linux view",
          cells: ["`ip -4 neigh`", "`ip -6 neigh`. Routers are marked `router`."],
        },
      ],
    },
    {
      type: "steps",
      id: "resolution",
      title: "Resolving 2001:db8::20",
      steps: [
        {
          title: "Compute the group",
          body: "Take the low 24 bits of 2001:db8::20, which are 00:00:20, and append them to ff02::1:ff00:0/104, giving ff02::1:ff00:20. The Ethernet MAC for an IPv6 multicast address is 33:33 followed by its last 32 bits, so 33:33:ff:00:00:20.",
        },
        {
          title: "Send the solicitation",
          body: "Host A (2001:db8::10, 00:00:5e:00:53:0a) sends a Neighbor Solicitation to ff02::1:ff00:20 with Hop Limit 255, target 2001:db8::20, and its own MAC as an option. `tcpdump` prints `ICMP6, neighbor solicitation, who has 2001:db8::20`.",
        },
        {
          title: "Only the group listens",
          body: "The switch delivers the frame to ports that joined the group, or floods it if it does not snoop MLD. Interfaces not in 33:33:ff:00:00:20 drop it in hardware.",
        },
        {
          title: "The target answers",
          body: "Host B checks Hop Limit 255 and that the target is its own address, caches A's MAC as STALE, and sends a Neighbor Advertisement by unicast to 2001:db8::10 with the Solicited and Override flags set and its MAC as an option. `tcpdump` prints `neighbor advertisement, tgt is 2001:db8::20`.",
        },
        {
          title: "A sends the queued packet",
          body: "A's entry becomes REACHABLE with 00:00:5e:00:53:14 and the queued packet goes out. If no advertisement arrives after three solicitations a second apart, resolution fails and the queued packets get ICMPv6 'address unreachable'.",
        },
      ],
    },
    {
      type: "example",
      id: "linux-nd",
      title: "Neighbor Discovery on a Linux host",
      setup:
        "A Linux host on an IPv6 LAN. Capture with `sudo tcpdump -n -i eth0 icmp6` while running the commands.",
      walkthrough: [
        "`ip -6 neigh` lists entries like `fe80::1 dev eth0 lladdr 00:00:5e:00:53:01 router REACHABLE`. The default router is usually known by its link-local address, learned from its Router Advertisement.",
        "`ip -6 route` shows `default via fe80::1 dev eth0 proto ra`. `proto ra` means the route came from a Router Advertisement, not from static configuration or DHCPv6.",
        "`ip maddr show dev eth0` lists the multicast groups the interface joined, including ff02::1 (all nodes) and one ff02::1:ffxx:xxxx entry per distinct low 24 bits among its addresses.",
        "Add an address with `ip -6 addr add 2001:db8::99/64 dev eth0`. For about a second it shows `tentative` while Duplicate Address Detection sends a solicitation from the unspecified address ::. If another host answers, the address is marked `dadfailed` and is never used.",
      ],
      result:
        "The neighbour table, the multicast group list, and the `proto ra` route together show what Neighbor Discovery has learned on the link.",
    },
    {
      type: "callout",
      id: "icmpv6-filter",
      tone: "caution",
      title: "Never drop all ICMPv6",
      scope: "common-implementation",
      body: "A firewall rule copied from IPv4 habits that drops ICMPv6 drops Neighbor Solicitation and Advertisement (types 135 and 136) and Router Solicitation and Advertisement (133 and 134). IPv6 then fails on the link while IPv4 works, which looks like a routing problem. Allow at least these types with Hop Limit 255, plus Packet Too Big (type 2) for Path MTU Discovery.",
    },
    {
      type: "expandable",
      id: "more-nd",
      title: "Router Advertisements, Duplicate Address Detection, and unreachability",
      paragraphs: [
        "Routers multicast Router Advertisements periodically and in reply to a host's Router Solicitation. An advertisement carries the router's link-local address and MAC, the on-link prefixes, flags that say whether to use SLAAC or DHCPv6, a suggested hop limit, and optionally the link MTU. Hosts build their default router list and on-link prefix list from them.",
        "Duplicate Address Detection sends a Neighbor Solicitation for the tentative address, from the unspecified address ::, to its solicited-node group. A host already using the address answers with an advertisement, and the new address is not assigned. The unspecified source keeps the probe from polluting other caches, as an ARP probe's 0.0.0.0 does.",
        "Neighbor Unreachability Detection treats forward progress, such as new TCP ACKs, as confirmation that the neighbour is reachable. Without it, a used entry goes STALE, then DELAY for 5 seconds, then PROBE with unicast solicitations. If the neighbour was a router and stops answering, the host deletes the entry and tries another default router.",
        "The Override flag decides whether an advertisement replaces a cached MAC. Anycast and proxy advertisements leave it clear so that they do not overwrite an existing entry.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "solicited-node",
          prompt:
            "What are the solicited-node multicast address and the destination MAC for a Neighbor Solicitation targeting 2001:db8:1::abcd:1234?",
          answer:
            "The low 24 bits are cd:12:34, so the group is ff02::1:ffcd:1234 and the MAC is 33:33:ff:cd:12:34.",
        },
        {
          id: "hop-limit-255",
          prompt:
            "Why does a host discard a Neighbor Advertisement that arrives with Hop Limit 254?",
          answer:
            "A router on the way would have decremented it from 255. A value below 255 means the message may have come from off the link, and Neighbor Discovery messages must come from a direct neighbour, so the check stops a remote attacker from forging them.",
        },
        {
          id: "v6-fails-v4-works",
          prompt:
            "On a dual-stack host, IPv4 to the gateway works, but `ip -6 neigh` shows the IPv6 gateway as FAILED and IPv6 traffic times out. Name two likely causes.",
          answer:
            "ICMPv6 Neighbor Discovery is being filtered by a host or network firewall, or a switch with MLD snooping is not delivering the solicited-node multicast to the router. A third possibility is that the router has stopped answering. Capture `icmp6` to see whether solicitations leave and advertisements return.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "nd-purpose",
      statement:
        "Nodes use Neighbor Discovery to determine the link-layer addresses of on-link neighbours, purge invalid cached values, find routers, and track which neighbours are reachable.",
      sourceId: "rfc4861",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "nd-replaces-ipv4",
      statement:
        "Neighbor Discovery corresponds to the IPv4 combination of ARP, ICMP Router Discovery, and ICMP Redirect. It spreads address-resolution multicasts over 2^24 groups, carries link-layer addresses in Router Advertisements, and uses Hop Limit 255 so off-link senders cannot inject messages.",
      sourceId: "rfc4861",
      locator: "Section 3.1, Comparison with IPv4",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "nd-one-exchange",
      statement:
        "Address resolution multicasts a Neighbor Solicitation to the target's solicited-node address; the target returns its link-layer address in a unicast Neighbor Advertisement, and one exchange resolves both sides because the solicitation includes the initiator's link-layer address.",
      sourceId: "rfc4861",
      locator: "Section 3, Protocol Overview",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ns-na-types",
      statement:
        "A Neighbor Solicitation is ICMPv6 type 135 and a Neighbor Advertisement is type 136, both sent with Hop Limit 255. The advertisement carries the Router, Solicited, and Override flags.",
      sourceId: "rfc4861",
      locator: "Sections 4.3 and 4.4, message formats",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "nd-hop-limit-check",
      statement:
        "A node must silently discard a Neighbor Solicitation or Advertisement whose IP Hop Limit is not 255.",
      sourceId: "rfc4861",
      locator: "Sections 7.1.1 and 7.1.2, Message Validation",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "nd-sending-ns",
      statement:
        "To resolve a neighbour, a node creates an INCOMPLETE entry, sends a solicitation to the solicited-node multicast address with its link-layer address as an option, queues at least one packet, and after MAX_MULTICAST_SOLICIT solicitations without an answer returns ICMP address-unreachable for the queued packets.",
      sourceId: "rfc4861",
      locator: "Section 7.2.2, Sending Neighbor Solicitations",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "nd-states",
      statement:
        "Neighbor cache entries are INCOMPLETE, REACHABLE, STALE, DELAY, or PROBE. Upper-layer forward progress or a solicited advertisement confirms reachability; unsolicited messages do not.",
      sourceId: "rfc4861",
      locator: "Sections 7.3.1 and 7.3.2",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "nd-constants",
      statement:
        "MAX_MULTICAST_SOLICIT and MAX_UNICAST_SOLICIT are 3, REACHABLE_TIME is 30,000 ms, RETRANS_TIMER is 1,000 ms, and DELAY_FIRST_PROBE_TIME is 5 s.",
      sourceId: "rfc4861",
      locator: "Section 10, Protocol Constants",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "nd-threats",
      statement:
        "Neighbor Discovery is vulnerable to denial of service, address spoofing, and router spoofing; a rogue router can advertise itself and time out legitimate routers.",
      sourceId: "rfc4861",
      locator: "Section 11.1, Threat Analysis",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "solicited-node-format",
      statement:
        "A solicited-node multicast address is the low-order 24 bits of a unicast or anycast address appended to ff02:0:0:0:0:1:ff00::/104, and a node must join it for every address it configures.",
      sourceId: "rfc4291",
      locator: "Section 2.7.1, Pre-Defined Multicast Addresses",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-multicast-mac",
      statement:
        "An IPv6 multicast packet is sent to the Ethernet address 33:33 followed by the last four octets of the IPv6 destination.",
      sourceId: "rfc2464",
      locator: "Section 7, Address Mapping -- Multicast",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "dad",
      statement:
        "Duplicate Address Detection must run on unicast addresses before assignment. It sends Neighbor Solicitations from the unspecified address to the tentative address's solicited-node group.",
      sourceId: "rfc4862",
      locator: "Sections 5.4 and 5.4.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-shared-neigh",
      statement:
        "Linux keeps IPv4 and IPv6 neighbours in neighbour tables managed with `ip neigh`, using the same NUD states.",
      sourceId: "man-ip-neighbour",
      locator: "DESCRIPTION; ip neighbour add, to ADDRESS and nud STATE",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc4861", note: "Neighbor Discovery: messages, validation, resolution, NUD." },
    { sourceId: "rfc4291", note: "Section 2.7.1: solicited-node multicast addresses." },
    { sourceId: "rfc2464", note: "Section 7: mapping IPv6 multicast to Ethernet MACs." },
    { sourceId: "rfc4862", note: "Section 5.4: Duplicate Address Detection." },
    { sourceId: "man-ip-neighbour", note: "Linux neighbour states for IPv4 and IPv6." },
  ],
} satisfies LessonBody;
