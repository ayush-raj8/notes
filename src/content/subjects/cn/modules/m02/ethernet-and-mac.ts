import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m02.ethernet-and-mac",
  status: "complete",
  objectives: [
    "Read an Ethernet frame field by field and say what each field is for.",
    "Explain why a frame carries MAC addresses when the packet inside already carries IP addresses.",
    "Tell unicast, broadcast, and multicast MAC addresses apart from the first octet.",
    "Predict which frames a Linux network interface accepts, and what `tcpdump -e` shows.",
  ],
  prerequisites: ["m01.planes-and-connections"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "IP has picked a next hop for a datagram, but the datagram still has to cross one physical link to get there. That link connects many interfaces, carries more than one protocol, and flips bits now and then. Every interface on it needs to know, cheaply and without understanding IP, whether a frame is for it, which protocol is inside, and whether it arrived intact.",
      whyItMatters:
        "Every packet a service sends or receives on a LAN, a cloud VPC, or a container bridge is inside a link-layer frame. Switches forward on the frame alone. Reading `tcpdump -e`, explaining the 1500-byte MTU, and debugging a host that answers ping but not from another subnet all depend on knowing what the frame carries and how far its addresses reach.",
      idea: "Wrap each datagram in a frame that names the sending and receiving interface on this one link, says which protocol is inside, and carries a checksum over the whole frame. Link addresses (MAC addresses) are flat 48-bit names for interfaces. They mean something only on the local link. IP addresses name hosts across the whole internetwork and are structured for routing.",
      howItWorks:
        "An Ethernet frame starts with a 6-byte destination MAC, a 6-byte source MAC, and a 2-byte EtherType: 0x0800 for IPv4, 0x86DD for IPv6, 0x0806 for ARP. The payload follows, 46 to 1500 bytes, padded with zeros if shorter. The interface hardware appends a 4-byte frame check sequence. The receiving interface drops a frame whose check fails, accepts one addressed to its own MAC, to broadcast ff:ff:ff:ff:ff:ff, or to a multicast group it has joined, and ignores the rest unless it is in promiscuous mode. The kernel then hands the payload to IPv4, IPv6, or ARP according to the EtherType.",
      whyItWorks:
        "The interface can filter in hardware by comparing six bytes, so a busy LAN does not interrupt every host for every frame. The EtherType lets IPv4, IPv6, ARP, and other protocols share one cable. Because IP never relies on a particular link address format, the same datagram can cross Ethernet, Wi-Fi, a tunnel, and a point-to-point link with no MAC at all, and a host can be renumbered without touching its hardware.",
      limits:
        "A MAC address means nothing beyond the local link: a router discards the incoming frame and builds a new one, so a remote server never sees the client's MAC. The checksum detects corruption, but Ethernet only drops a bad frame. It never retransmits, so recovery is TCP's job. A MAC address is not a trustworthy identity: any host can send frames with any source MAC, and phones and laptops randomize theirs per network. Frames larger than 1500 bytes of payload (jumbo frames) work only where every device on the link is configured for them.",
      connections:
        "The next lesson shows how a switch forwards frames by destination MAC. The ARP lesson shows how a host finds the MAC for an IP address, and the same-subnet lesson shows why the destination MAC changes at each routed hop (why.06). Module 1 covered encapsulation and how the MTU limits the TCP payload.",
    },
    {
      type: "comparison",
      id: "frame-layout",
      title: "An Ethernet II frame",
      caption:
        "Ethernet II (DIX) framing, which IPv4 and IPv6 use. The preamble and frame check sequence are added and removed by the interface hardware, so `tcpdump` usually shows neither. The 4-byte check sequence and 64-byte minimum frame come from IEEE 802.3, which is not in this course's source catalog.",
      columns: ["Size", "Purpose"],
      rows: [
        {
          label: "Destination MAC",
          cells: [
            "6 bytes",
            "The interface on this link that should take the frame, or a broadcast or multicast group. It comes first so hardware can decide early.",
          ],
        },
        {
          label: "Source MAC",
          cells: [
            "6 bytes",
            "The interface that put the frame on this link. Switches learn from it. Replies go back to it.",
          ],
        },
        {
          label: "EtherType",
          cells: [
            "2 bytes",
            "What the payload is: 0x0800 IPv4, 0x86DD IPv6, 0x0806 ARP, 0x8100 a VLAN tag followed by another EtherType.",
          ],
        },
        {
          label: "Payload",
          cells: [
            "46–1500 bytes",
            "The IP datagram or ARP message. Short payloads are zero-padded. The padding is not part of the IP packet.",
          ],
        },
        {
          label: "Frame check sequence",
          cells: [
            "4 bytes",
            "A CRC over the frame. A mismatch means the frame is dropped silently.",
          ],
        },
      ],
    },
    {
      type: "comparison",
      id: "mac-vs-ip",
      title: "MAC addresses and IP addresses",
      columns: ["MAC address", "IP address"],
      rows: [
        {
          label: "Names",
          cells: ["One network interface on one link.", "One host interface in the internetwork."],
        },
        {
          label: "Structure",
          cells: [
            "Flat, 48 bits. The first 24 or more bits identify the manufacturer's block, not a location.",
            "Hierarchical. A prefix identifies the network, so routers can aggregate millions of hosts into one route.",
          ],
        },
        {
          label: "Assigned by",
          cells: [
            "The manufacturer, or randomly or locally by the OS or hypervisor.",
            "The network: static configuration, DHCP, or IPv6 autoconfiguration.",
          ],
        },
        {
          label: "Read by",
          cells: [
            "Interfaces and switches on the same link.",
            "Every router on the path, and the destination host.",
          ],
        },
        {
          label: "Across a routed hop",
          cells: [
            "Replaced. Each link gets a new frame.",
            "Kept. The destination stays the same end to end unless NAT rewrites it.",
          ],
        },
        {
          label: "On Linux",
          cells: ["`ip link show` (`link/ether`).", "`ip addr show` (`inet`, `inet6`)."],
        },
      ],
    },
    {
      type: "why",
      id: "why-mac-and-ip",
      questionId: "why.05",
    },
    {
      type: "expandable",
      id: "mac-bits",
      title: "What the first octet of a MAC address says",
      paragraphs: [
        "The lowest bit of the first octet is the group bit. If it is 0, the address is unicast. If it is 1, the address is multicast or broadcast. 00:00:5e:00:53:01 is unicast. 01:00:5e:00:00:fb, used for IPv4 multicast, and 33:33:00:00:00:01, used for IPv6 multicast, are group addresses. ff:ff:ff:ff:ff:ff, all ones, is broadcast.",
        "The next bit is the universal/local bit. If it is 0, the address comes from a block the IEEE assigned to a vendor. If it is 1, the address was assigned locally, for example by a hypervisor, by Docker for a container's veth interface, or by a phone that randomizes its MAC per Wi-Fi network. A second hex digit of 2, 6, A, or E in the first octet means a locally administered unicast address.",
        "This course uses 00:00:5e:00:53:00 through 00:00:5e:00:53:ff in examples. IANA reserves that range for documentation, so it never collides with a real device.",
      ],
    },
    {
      type: "example",
      id: "tcpdump-e",
      title: "Reading a frame with `tcpdump -e`",
      setup:
        "On a Linux host with MAC 00:00:5e:00:53:0a and address 192.0.2.10, run `sudo tcpdump -e -n -i eth0` and open a TCP connection to 192.0.2.20 on the same subnet. One line of output looks like `00:00:5e:00:53:0a > 00:00:5e:00:53:14, ethertype IPv4 (0x0800), length 74: 192.0.2.10.51514 > 192.0.2.20.443: Flags [S] ...`.",
      walkthrough: [
        "`00:00:5e:00:53:0a > 00:00:5e:00:53:14` is the frame's source and destination MAC. Without `-e`, tcpdump hides them.",
        "`ethertype IPv4 (0x0800)` is the EtherType. It tells the receiving kernel to hand the payload to IPv4.",
        "`length 74` is the frame length as captured: 14 bytes of Ethernet header, 20 of IPv4, and 40 of TCP with SYN options. The check sequence is not included.",
        "Everything after the colon is the IP datagram inside the frame. The IP addresses and ports belong to the packet, not the frame.",
        "Ping 192.0.2.20 from a host on another subnet and capture on the same host. The source MAC is now the router's, while the source IP is still the remote host's.",
      ],
      result:
        "The frame header is 14 bytes that only this link cares about. The datagram inside is what the endpoints care about.",
    },
    {
      type: "callout",
      id: "capture-caveat",
      tone: "caution",
      title: "Captures can show frames larger than the MTU",
      scope: "linux",
      body: "With segmentation and receive offloads enabled, Linux hands tcpdump one large frame that the interface splits into MTU-sized frames on the wire, or merges from several received frames. A 64 KB 'frame' in a capture is the kernel's view, not a jumbo frame on the cable. `ethtool -k eth0` lists the offloads.",
    },
    {
      type: "expandable",
      id: "nic-filtering",
      title: "Which frames a Linux interface accepts",
      paragraphs: [
        "The interface accepts frames for its own MAC, for broadcast, and for multicast groups the kernel has asked it to join, such as the IPv6 all-nodes group. It drops everything else in hardware.",
        "A raw packet socket on Linux labels each received frame as for this host, broadcast, multicast, or for another host. The last kind appears only when the driver is in promiscuous mode, which tcpdump turns on by default. A switch rarely sends a host frames for other hosts, so promiscuous mode on a switched port usually shows only your own traffic plus broadcast and flooded frames.",
        "Two framings share the same two bytes after the source MAC. If the value is 0x0600 or more, it is an EtherType (Ethernet II). If it is 1500 or less, it is an IEEE 802.3 length, and an LLC header follows. IP uses Ethernet II. LLC framing survives mainly in protocols such as spanning tree.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "remote-mac",
          prompt:
            "A web server logs client IP addresses. Could it also log each client's MAC address? What would it actually see?",
          answer:
            "It sees a MAC address only on the frame that reached it, and for a client on another network that is the MAC of the last router or load balancer. The client's own MAC never leaves the client's link, because every router builds a new frame.",
        },
        {
          id: "ethertype-role",
          prompt:
            "Two frames arrive with the same destination MAC. One has EtherType 0x0806 and the other 0x86DD. What does the kernel do differently?",
          answer:
            "It hands the first to ARP and the second to IPv6. The EtherType is the only thing that tells the receiver how to interpret the payload, so one interface can carry several network protocols at once.",
        },
        {
          id: "group-bit",
          prompt: "Is 33:33:ff:00:00:20 a unicast address? How can you tell without looking it up?",
          answer:
            "No. 0x33 is 0011 0011 in binary, so the lowest bit of the first octet, the group bit, is 1. It is a multicast address, in this case one that IPv6 Neighbor Discovery uses.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ethernet-header-fields",
      statement:
        "The Ethernet header carries a 48-bit destination address, a 48-bit sender address, and a 16-bit protocol type, and is 14 bytes long.",
      sourceId: "rfc826",
      locator: "Packet format; 'Why is it done this way??' (the 14.byte ethernet header)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "mac-ip-unrelated",
      statement:
        "Ethernet requires 48-bit addresses on the cable, while protocol addresses have other lengths and need not have any relationship to the hardware address, so a mapping has to be distributed.",
      sourceId: "rfc826",
      locator: "The Problem",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv4-ethertype-padding",
      statement:
        "IPv4 datagrams use EtherType 0x0800. The data field is at least 46 octets, padded with zeros that are not part of the IP packet, and an IP datagram on Ethernet is at most 1500 octets.",
      sourceId: "rfc894",
      locator: "Frame Format",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-ethertype",
      statement: "IPv6 packets use EtherType 0x86DD and may be padded to the minimum frame size.",
      sourceId: "rfc2464",
      locator: "Section 3, Frame Format",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ethertype-values",
      statement: "EtherType 0x0806 is ARP and 0x8100 is the IEEE 802.1Q customer VLAN tag (C-Tag).",
      sourceId: "iana-ieee-802-numbers",
      locator: "EtherType table, rows 0806 and 8100",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ethertype-threshold",
      statement:
        "EtherTypes are 16-bit values of 0x0600 or more; a smaller value in that position is an 802.3 length followed by LLC protocol identifiers.",
      sourceId: "rfc9542",
      locator: "Section 3, Ethernet Protocol Parameters",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "length-vs-type",
      statement:
        "A receiver tells Ethernet and 802.3 framing apart by the two-octet field after the addresses: an 802.3 length is at most 1500, and valid EtherTypes are larger. The Ethernet MTU is 1500.",
      sourceId: "rfc1122",
      locator: "Section 2.3.3, Ethernet and IEEE 802 Encapsulation, DISCUSSION",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "mac-group-local-bits",
      statement:
        "In the first octet of a MAC address, the lowest bit is the group bit (0 unicast, 1 multicast or broadcast) and the next bit is the universal/local bit (0 globally assigned, 1 locally administered).",
      sourceId: "rfc9542",
      locator: "Section 2.1.1, Special First Octet Bits",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "mac-documentation-range",
      statement:
        "00-00-5E-00-53-00 through 00-00-5E-00-53-FF are reserved for unicast MAC addresses in documentation.",
      sourceId: "rfc9542",
      locator: "Section 2.1.4, 48-Bit MAC Documentation Values",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ip-maps-to-local-net",
      statement: "The internet module maps internet addresses to local net addresses.",
      sourceId: "rfc791",
      locator: "Section 2.3, Function Description, Addressing",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-packet-types",
      statement:
        "Linux packet sockets label received frames as PACKET_HOST, PACKET_BROADCAST, PACKET_MULTICAST, or PACKET_OTHERHOST, the last being a frame for another host caught because the driver is in promiscuous mode.",
      sourceId: "man-packet",
      locator: "Address types, sll_pkttype",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc826", note: "The Ethernet header layout and why a mapping to MAC is needed." },
    {
      sourceId: "rfc894",
      note: "IPv4 over Ethernet: EtherType, padding, and the 1500-byte limit.",
    },
    { sourceId: "rfc2464", note: "IPv6 over Ethernet." },
    { sourceId: "rfc1122", note: "Section 2.3.3 on Ethernet and 802.3 framing." },
    { sourceId: "rfc9542", note: "MAC address bits, EtherType rules, documentation MACs." },
    { sourceId: "iana-ieee-802-numbers", note: "EtherType values." },
    { sourceId: "rfc791", note: "IP maps its addresses to local network addresses." },
    { sourceId: "man-packet", note: "Linux packet types and promiscuous mode." },
  ],
} satisfies LessonBody;
