import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m03.ipv4-cidr",
  status: "complete",
  objectives: [
    "Read an IPv4 address with a prefix length as a network part and a host part, and convert between prefix lengths and dotted masks.",
    "Compute the network address, broadcast address, and usable host range of any IPv4 prefix.",
    "Decide whether two addresses are on the same subnet by masking, and show the bitwise reasoning.",
    "Explain why CIDR replaced classes, and what a mismatched mask does to the on-link decision.",
  ],
  prerequisites: ["m02.same-subnet"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "An IPv4 address is 32 bits. A router cannot hold a route for each of the four billion addresses, and a host has to decide, for every packet, whether the destination is on its own link or behind a router. Both need a way to say 'these addresses belong together' without listing them.",
      whyItMatters:
        "Every subnet in a VPC, every Kubernetes pod range, every firewall rule, and every route is written as a prefix such as 10.20.32.0/21. Reading prefixes wrong produces overlapping ranges, security groups that allow too much, and hosts that ARP for addresses behind a router. The same-subnet test is the first branch in every send.",
      idea: "Split the address into a network part (the leading bits) and a host part (the rest). A prefix length /n says the first n bits name the network. All addresses that agree in those n bits are one block of 2^(32−n) addresses, and one route or one subnet can describe the whole block.",
      howItWorks:
        "Write the prefix as a mask: n one-bits followed by zeros. /21 is 11111111.11111111.11111000.00000000, or 255.255.248.0. ANDing any address with the mask clears the host bits and leaves the network address: 10.20.33.14 AND 255.255.248.0 = 10.20.32.0. Setting all host bits to one gives the last address, 10.20.39.255, which is the subnet's broadcast address. The addresses between them are the hosts. Two addresses are on the same subnet of length n exactly when they AND to the same network address under that mask. A host makes that test with its own interface's prefix: if the destination masks to its own network, it sends directly; otherwise, through a router.",
      whyItWorks:
        "Masking is one AND and one compare, so it is cheap enough to do per packet. Because a prefix is a contiguous, bit-aligned block, a block of size 2^k can be split into two blocks of 2^(k−1) or merged with its neighbour into one of 2^(k+1). That lets an ISP announce one /16 instead of 256 /24s, and lets you carve a /16 into whatever subnet sizes you need without wasting a class-sized block.",
      limits:
        "A prefix only says which addresses share a network number. It does not say they are on the same wire. Two hosts with the same /24 on different VLANs cannot reach each other directly, and a cloud network may route between hosts in one subnet. The decision is made by the sender with the sender's mask, so mismatched masks make the two directions disagree. Block sizes are powers of two, so a 300-host subnet needs a /23 (510 usable addresses).",
      connections:
        "Module 2 used this decision to choose between ARP for the destination and ARP for the gateway. The next lesson covers the address ranges that have a fixed meaning. Lesson 3 of this module covers how a host chooses between overlapping prefixes in its routing table. Module 4 covers how routers aggregate prefixes.",
    },
    {
      type: "comparison",
      id: "prefix-table",
      title: "Common prefix lengths",
      caption:
        "Usable hosts excludes the all-zeros and all-ones host values. /31 and /32 are the exceptions noted in the text.",
      columns: ["Mask", "Addresses", "Usable hosts", "Typical use"],
      rows: [
        {
          label: "/8",
          cells: ["255.0.0.0", "16,777,216", "16,777,214", "10.0.0.0/8 private block"],
        },
        { label: "/16", cells: ["255.255.0.0", "65,536", "65,534", "A VPC or campus"] },
        { label: "/20", cells: ["255.255.240.0", "4,096", "4,094", "A large cloud subnet"] },
        {
          label: "/22",
          cells: ["255.255.252.0", "1,024", "1,022", "A subnet for about a thousand hosts"],
        },
        { label: "/24", cells: ["255.255.255.0", "256", "254", "An office LAN"] },
        { label: "/26", cells: ["255.255.255.192", "64", "62", "A small server subnet"] },
        { label: "/28", cells: ["255.255.255.240", "16", "14", "A handful of appliances"] },
        { label: "/30", cells: ["255.255.255.252", "4", "2", "A router-to-router link"] },
        { label: "/31", cells: ["255.255.255.254", "2", "2", "A point-to-point link"] },
        {
          label: "/32",
          cells: ["255.255.255.255", "1", "1", "A host route or a loopback service IP"],
        },
      ],
    },
    {
      type: "example",
      id: "same-subnet-decisions",
      title: "Worked same-subnet decisions",
      setup:
        "Host A is 10.20.33.14/21. For each destination, decide whether A sends directly or through its gateway. Then check a /25 case where the addresses look close but are not, and a case where two hosts disagree.",
      walkthrough: [
        "Mask for /21: 21 ones are 8 + 8 + 5, so the mask is 255.255.248.0. Only the third octet is partly masked. Its mask is 11111000, which keeps multiples of 8.",
        "A's network: third octet 33 = 00100001. AND 11111000 = 00100000 = 32. So A's network is 10.20.32.0/21, and the block runs from 10.20.32.0 to 10.20.39.255 (32 + 8 − 1 = 39).",
        "Destination 10.20.37.200: 37 = 00100101, AND 11111000 = 00100000 = 32. Network 10.20.32.0 matches, so 37.200 is on-link. A ARPs for 10.20.37.200 directly, even though the third octet differs. `ip route get 10.20.37.200` prints no `via`.",
        "Destination 10.20.40.5: 40 = 00101000, AND 11111000 = 00101000 = 40. Network 10.20.40.0 does not match, so A sends to its gateway and ARPs for the gateway's address. `ip route get` prints `via` and the gateway.",
        "Close but not the same: 192.168.1.126/25 and 192.168.1.130/25. The /25 mask on the last octet is 10000000. 126 = 01111110 gives 0; 130 = 10000010 gives 128. The networks are 192.168.1.0/25 and 192.168.1.128/25, so the two hosts four addresses apart must go through a router.",
        "Disagreeing masks: B is 10.1.5.20/24 and C is 10.1.0.10/16, on the same VLAN. C masks B with /16 and gets 10.1.0.0, its own network, so C ARPs for B directly. B masks C with /24 and gets 10.1.0.0, which is not B's 10.1.5.0, so B sends its replies to the gateway. Traffic works only if the gateway routes it back, and captures show different paths in each direction.",
        "Check with Python on any Linux box: `python3 -c \"import ipaddress as i; print(i.ip_address('10.20.37.200') in i.ip_interface('10.20.33.14/21').network)\"` prints `True`.",
      ],
      result:
        "Mask both addresses with the sender's prefix and compare. Octet boundaries mean nothing unless the prefix length is a multiple of 8. Addresses that look adjacent can be on different subnets, and addresses in different octets can be on the same one.",
    },
    {
      type: "steps",
      id: "compute-any-prefix",
      title: "Computing any prefix by hand",
      steps: [
        {
          title: "Find the interesting octet",
          body: "Divide the prefix length by 8. The quotient is how many octets are fully network; the remainder is how many bits of the next octet are. For /26 that is 3 full octets and 2 bits of the fourth.",
        },
        {
          title: "Find the block size",
          body: "The block size in that octet is 2^(8 − remainder): 2^6 = 64 for /26. The mask octet is 256 minus the block size, so 192.",
        },
        {
          title: "Round down to the block",
          body: "Round the address's interesting octet down to a multiple of the block size: 172.16.5.77/26 gives 64, so the network is 172.16.5.64. Zero the octets after it.",
        },
        {
          title: "Find the end",
          body: "Add the block size minus one: 64 + 63 = 127, and set later octets to 255. The broadcast is 172.16.5.127, and the hosts are .65 to .126.",
        },
      ],
    },
    {
      type: "callout",
      id: "cidr-not-classes",
      tone: "note",
      title: "Classes are history",
      scope: "rfc",
      body: "Before CIDR, the first bits of an address fixed its mask: 10.x was 'Class A' /8, 172.16.x was 'Class B' /16, 192.168.x was 'Class C' /24. Class C was too small for most organisations and Class B too large, so Class B space ran out and routing tables grew with every new network. CIDR made the prefix length explicit. You cannot infer the mask from the address, so every route, interface, and firewall rule has to carry its length. Tools that print 'Class C' are describing an old default, not a rule.",
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Edge cases: /31, /32, the first address, and non-contiguous masks",
      paragraphs: [
        "A /31 has no room for a network and a broadcast address, so both addresses are used for the two ends of a point-to-point link. RFC 4632's prefix table labels it 'p2p link'. A /32 is a single address. Routers use it as a host route, and Linux hosts often put a service IP on `lo` as a /32, because a /32 implies no on-link neighbours.",
        "Linux and the lowest address: `ip(7)` says the highest address of a broadcast-capable subnet is the broadcast address and cannot usefully be assigned to an interface. The lowest address, often called the network address, was historically reserved, but since Linux 5.14 it is an ordinary unicast address that can be assigned. Other systems and cloud providers may still refuse it, and many clouds reserve several more addresses at the start of each subnet.",
        "Every address you add gets a connected route: `ip addr add 10.20.33.14/21 dev eth0` also installs `10.20.32.0/21 dev eth0 proto kernel scope link`. That route, not the address itself, is what makes 10.20.37.200 on-link. The `noprefixroute` flag suppresses it.",
        "A mask must be contiguous: all ones, then all zeros. 255.255.0.255 is not a valid CIDR mask, and modern routing code cannot represent it.",
        "Containment is the same test applied to prefixes: 10.200.0.0/16 is inside 10.0.0.0/8 because 16 ≥ 8 and 10.200.0.0 masked to /8 is 10.0.0.0. Two prefixes overlap only if one contains the other, so to check two VPC ranges for overlap, mask the longer one to the shorter one's length.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "slash27-decision",
          prompt:
            "Host 192.168.10.200/27 sends to 192.168.10.190. Does it ARP for 192.168.10.190 or for its gateway?",
          answer:
            "/27 leaves 5 host bits, so blocks are 32 wide and the last-octet mask is 224. 200 rounds down to 192, and 190 rounds down to 160. The networks are 192.168.10.192/27 and 192.168.10.160/27, so the destination is off-link and the host ARPs for its gateway.",
        },
        {
          id: "slash22-range",
          prompt:
            "A node is 10.4.6.9/22. What are its network address, broadcast address, and number of usable hosts?",
          answer:
            "/22 is 2 full octets plus 6 bits of the third, so the third-octet block is 4 and the mask is 255.255.252.0. 6 rounds down to 4: network 10.4.4.0, broadcast 10.4.7.255, and 2^10 − 2 = 1022 usable hosts.",
        },
        {
          id: "mismatched-mask",
          prompt:
            "Host X is 10.0.1.5/16 and host Y is 10.0.2.7/24, both on one VLAN. X can send to Y, but Y's replies arrive through the router. Explain why.",
          answer:
            "Each host decides with its own mask. X masks 10.0.2.7 with /16 and gets its own 10.0.0.0, so it ARPs for Y directly. Y masks 10.0.1.5 with /24 and gets 10.0.1.0, not its own 10.0.2.0, so it sends replies to its gateway. The path differs by direction, and a stateful firewall on the router may drop the replies because it never saw the requests.",
        },
        {
          id: "vpc-overlap",
          prompt: "One VPC uses 10.8.0.0/14 and another uses 10.10.0.0/16. Do they overlap?",
          answer:
            "Yes. /14 covers 2 bits of the second octet, so its block is 4 wide: 10.8.0.0 to 10.11.255.255. Masking 10.10.0.0 to /14 gives 10.8.0.0, so the /16 lies inside the /14, and routes between the two VPCs would be ambiguous.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "cidr-prefix-notation",
      statement:
        "In CIDR notation, a prefix is a 4-octet address followed by '/' and a decimal value from 0 to 32 giving the number of significant bits; the mask is that many leading ones followed by zeros.",
      sourceId: "rfc4632",
      locator: "Section 3.1, Basic Concept and Prefix Notation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "classful-problems",
      statement:
        "Under the class A/B/C plan, Class C was too small and Class B too large for most mid-sized organisations, which led to Class B exhaustion and routing table growth; CIDR replaced classes with explicit prefixes.",
      sourceId: "rfc4632",
      locator: "Section 2, History and Problem Description; Section 3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "mask-must-be-explicit",
      statement:
        "With classless prefixes the mask cannot be inferred from the leading bits of an address, so routing protocols and forwarding tables must carry the prefix length explicitly, and the mask must be contiguous.",
      sourceId: "rfc4632",
      locator: "Section 5, Routing Implementation Considerations; Section 5.1",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "host-field-reserved-values",
      statement:
        "Outside the listed special cases, an IPv4 host number may not be all zeros or all ones; all ones in the host part is the directed broadcast for that subnet.",
      sourceId: "rfc1122",
      locator: "Section 3.2.1.3, Addressing",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "local-remote-by-mask",
      statement:
        "A host decides whether a destination is on a connected network by comparing the destination and its own address under the address mask.",
      sourceId: "rfc1122",
      locator: "Section 3.3.1.1, Local/Remote Decision",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-lowest-address-unicast",
      statement:
        "On Linux, the highest address of a broadcast-capable subnet is its broadcast address; since Linux 5.14 the lowest address is treated as an ordinary unicast address that can be assigned to an interface.",
      sourceId: "man-ip",
      locator: "Special and reserved addresses, Highest-numbered address / Lowest-numbered address",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-prefix-route",
      statement:
        "Adding an address with a prefix length creates a route for that network prefix automatically, unless the address is added with `noprefixroute`.",
      sourceId: "man-ip-address",
      locator: "ip address add, noprefixroute",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    {
      sourceId: "rfc4632",
      note: "Sections 2, 3.1, and 5: why classes failed, prefix notation, and explicit prefix lengths.",
    },
    {
      sourceId: "rfc1122",
      note: "Section 3.2.1.3 reserved host values; Section 3.3.1.1 the local/remote decision.",
    },
    { sourceId: "man-ip", note: "Special and reserved addresses on Linux." },
    { sourceId: "man-ip-address", note: "Connected prefix routes and `noprefixroute`." },
  ],
} satisfies LessonBody;
