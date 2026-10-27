import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m03.ipv6",
  status: "complete",
  objectives: [
    "Read, expand, and canonicalise IPv6 addresses and prefixes, and decide whether two of them share a /64.",
    "Explain why an IPv6 interface has several addresses at once, and where each one comes from.",
    "Compare what a host does differently in IPv4 and IPv6: configuration, next-hop resolution, broadcast, and fragmentation.",
    "Recognise IPv4-mapped addresses and link-local zone indexes in Linux tools and application logs.",
  ],
  prerequisites: ["m03.gateway-and-lpm"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "CIDR slowed the use of IPv4 addresses but was never meant to solve their exhaustion. Thirty-two bits cannot give every device a globally unique address. A larger address is the obvious fix, but once the space is huge, a host can do more than receive an address: it can build its own, keep several, and find its router without a configuration server.",
      whyItMatters:
        "Most clients and many clouds are dual-stack, so the same service can be reached two ways that fail separately. Logs, allowlists, and firewall rules have to handle 128-bit addresses written in several text forms, addresses that change for privacy, and IPv4 clients that show up as IPv6 addresses.",
      idea: "An IPv6 address is 128 bits: a routing prefix and subnet ID, usually 64 bits together, followed by a 64-bit interface identifier. The network announces the prefix, and the host supplies the interface identifier. Every interface always has a link-local address, so neighbour and router discovery work before anything else is configured.",
      howItWorks:
        "When an interface comes up, the host forms a link-local address from fe80::/64 and its interface ID, checks that nobody else uses it, and sends a Router Solicitation. A Router Advertisement, sent from the router's link-local address, gives a default router and one or more prefixes. For a prefix with the autonomous flag set, the host appends an interface ID to form a global address. It may also generate temporary addresses with random interface IDs that it rotates. A prefix with the on-link flag set is added to the on-link list, so destinations under it are resolved with Neighbor Discovery instead of being sent to the router. The routing table then works exactly as in IPv4, with longest-prefix match over IPv6 prefixes.",
      whyItWorks:
        "With 64 bits of interface ID per subnet, hosts can pick identifiers locally with almost no chance of collision, and Duplicate Address Detection catches the rest. That removes the need for a DHCP lease for basic connectivity. Multicast with per-address solicited-node groups replaces broadcast, so a large subnet does not wake every host on each resolution. Routers never fragment, so they do less work per packet.",
      limits:
        "Dual stack doubles everything: two sets of addresses, routes, DNS records, and firewall rules, and a break in one family can be hidden by clients falling back to the other. Link-local addresses are ambiguous on a host with several interfaces, so tools need a zone index such as `%eth0`. Temporary addresses rotate, so allowing a client by its IPv6 address is fragile, and allowing its /64 is usually closer to what you mean. The same address can be written many ways, so compare parsed values, not strings.",
      connections:
        "Module 2 covered Neighbor Discovery, which replaces ARP. The previous lesson covered default routers and longest match, which work the same way here. The next lesson covers fragmentation, which IPv6 moves to the source.",
    },
    {
      type: "comparison",
      id: "v4-vs-v6",
      title: "The host's view of IPv4 and IPv6",
      columns: ["IPv4", "IPv6"],
      rows: [
        {
          label: "Address size",
          cells: ["32 bits, dotted decimal", "128 bits, hexadecimal groups"],
        },
        {
          label: "Typical subnet",
          cells: ["Any length, often /24 or smaller", "/64, leaving a 64-bit interface ID"],
        },
        {
          label: "Addresses per interface",
          cells: [
            "Usually one",
            "Always a link-local, plus one or more global and temporary addresses",
          ],
        },
        {
          label: "Configuration",
          cells: ["DHCP or static", "Router Advertisements and SLAAC, with DHCPv6 optional"],
        },
        {
          label: "Next-hop resolution",
          cells: ["ARP, broadcast", "Neighbor Discovery, multicast"],
        },
        { label: "Broadcast", cells: ["Yes", "No. Multicast replaces it."] },
        {
          label: "Default router learned from",
          cells: ["DHCP or static configuration", "Router Advertisements, as a link-local address"],
        },
        {
          label: "Fragmentation",
          cells: ["Source or any router, unless DF is set", "Source only"],
        },
        {
          label: "Smallest packet every hop must carry whole",
          cells: ["68 bytes", "1280 bytes, the minimum link MTU"],
        },
        { label: "Loopback", cells: ["127.0.0.0/8", "::1"] },
      ],
    },
    {
      type: "example",
      id: "reading-ipv6",
      title: "Reading an interface's IPv6 addresses",
      setup:
        "`ip -6 addr show dev eth0` prints three addresses: `2001:db8:a:1:5054:ff:fe12:3456/64 scope global dynamic mngtmpaddr`, `2001:db8:a:1:9c3e:41ff:2b07:d1a8/64 scope global temporary dynamic`, and `fe80::5054:ff:fe12:3456/64 scope link`. `ip -6 route` shows `default via fe80::1 dev eth0 proto ra`.",
      walkthrough: [
        "Expand the first address: 2001:0db8:000a:0001:5054:00ff:fe12:3456. The first four groups, 2001:db8:a:1, are the /64 prefix that a router advertised. The last four are the interface ID.",
        "The interface ID 5054:ff:fe12:3456 contains ff:fe in the middle, which marks one derived from the MAC 52:54:00:12:34:56, with the universal/local bit flipped (0x52 becomes 0x50). Many distributions now use a stable random ID instead, so that the MAC does not leak into every global address.",
        "The `temporary` address has the same prefix and a random interface ID. `dynamic` means it came from autoconfiguration and has a lifetime. The kernel picks which of the global addresses to use as the source for each new connection, and `ip route get` shows the choice.",
        "Same-subnet test: 2001:db8:a:1:ffff::3 has the prefix 2001:db8:a:1, so it is on-link and resolved with Neighbor Discovery. 2001:db8:a:2::10 differs in the fourth group, so it goes to the default router.",
        "The default router is fe80::1, a link-local address. To ping it, name the link: `ping fe80::1%eth0`. Without the zone index, the kernel cannot tell which interface's fe80::/64 is meant.",
        "Canonical text form: 2001:0DB8:0000:0000:0000:0000:0002:0001 is written 2001:db8::2:1. Drop leading zeros, replace the longest run of zero groups with `::` once, and use lowercase. When two runs tie, compress the first.",
      ],
      result:
        "One interface holds a link-local address for the link, a stable global address, and a rotating temporary one, all on the same /64. Routing uses them exactly like IPv4 prefixes, but the prefix and router came from an advertisement, not from DHCP.",
    },
    {
      type: "callout",
      id: "v4-mapped",
      tone: "note",
      title: "IPv4 clients that look like IPv6",
      scope: "linux",
      body: "A server that listens on an AF_INET6 socket bound to `::` also accepts IPv4 connections, unless IPV6_V6ONLY is set, and the default on Linux is to accept them. An IPv4 client then appears as an IPv4-mapped address such as `::ffff:198.51.100.7`. Code that parses client addresses, applies allowlists, or rate-limits by address has to normalise these, or one client becomes two keys. `ss -ltn` shows such a listener as `[::]:443` or `*:443`.",
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Dual stack, text forms, and other details",
      paragraphs: [
        "When a name has both A and AAAA records, the default address-selection policy prefers IPv6 if the host has a suitable IPv6 source address. A broken IPv6 path then shows up as a delay before clients fall back to IPv4, or as outright failure in clients that do not fall back. Test each family on its own with `curl -4` and `curl -6`.",
        "`::` may appear only once in an address, because two would make the number of missing groups ambiguous. Leading zeros may be dropped within a group, but trailing zeros may not: 2001:db8:0:cd30::/60 is valid, and 2001:db8:0:cd3/60 is not the same prefix.",
        "Unique Local Addresses (fc00::/7) play the role of RFC 1918 space, but with a random 40-bit Global ID, so two networks that merge are unlikely to collide.",
        "Prefix length still decides containment, exactly as in IPv4. A /48 site holds 65,536 /64 subnets. Subnets longer than /64 work for routing, but they break SLAAC, which needs a 64-bit interface ID on Ethernet.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "v6-same-subnet",
          prompt: "Are 2001:db8:5:10::1/64 and 2001:db8:5:11::1 on the same subnet?",
          answer:
            "No. A /64 is the first four 16-bit groups. They are 2001:db8:5:10 and 2001:db8:5:11, which differ in the fourth group, so the second address is off-link and goes to the router.",
        },
        {
          id: "mapped-log",
          prompt: "An access log shows a client as ::ffff:203.0.113.5. Did that client use IPv6?",
          answer:
            "No. It connected over IPv4 to a dual-stack AF_INET6 listener, and the kernel reported its IPv4 address 203.0.113.5 in IPv4-mapped form. Normalise it to 203.0.113.5 before comparing it with IPv4 allowlists.",
        },
        {
          id: "zone-index",
          prompt:
            "Why does `ping fe80::1` fail on a host with eth0 and wlan0, while `ping fe80::1%wlan0` works?",
          answer:
            "Every IPv6 interface has an address and a route in fe80::/64, so the destination does not say which link it is on, and the routes tie. The zone index names the interface to send on.",
        },
        {
          id: "canonical-form",
          prompt: "Write 2001:0db8:0000:0000:0001:0000:0000:0001 in canonical form.",
          answer:
            "2001:db8::1:0:0:1. Leading zeros are dropped and there are two runs of two zero groups. When runs tie, the first is replaced with `::`, and the other is written as 0:0.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "cidr-not-exhaustion",
      statement:
        "CIDR was designed to slow routing table growth and IPv4 address consumption; it did not attempt to solve the eventual exhaustion of the 32-bit address space.",
      sourceId: "rfc4632",
      locator: "Section 2, History and Problem Description",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-128-no-broadcast",
      statement:
        "IPv6 addresses are 128-bit identifiers for interfaces, of type unicast, anycast, or multicast; there are no broadcast addresses, their function being superseded by multicast.",
      sourceId: "rfc4291",
      locator: "Section 2, IPv6 Addressing",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-many-addresses",
      statement:
        "Every interface must have at least one link-local unicast address and may have several addresses of any type and scope.",
      sourceId: "rfc4291",
      locator: "Section 2.1, Addressing Model; Section 2.8",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-text-and-iid",
      statement:
        "IPv6 text form uses eight 16-bit hexadecimal pieces, '::' may compress one run of zero groups and may appear only once, and prefixes are written address/prefix-length. Unicast interface IDs, except for addresses starting with binary 000, are 64 bits.",
      sourceId: "rfc4291",
      locator: "Sections 2.2, 2.3, and 2.5.1",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-canonical-text",
      statement:
        "When generating text, leading zeros must be suppressed, '::' must shorten as much as possible but not a single zero group, the longest run (or the first of equal runs) is compressed, and hex digits are lowercase.",
      sourceId: "rfc5952",
      locator: "Section 4, A Recommendation for IPv6 Text Representation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "slaac-address-formation",
      statement:
        "A node forms a link-local address when an interface is enabled, and for each Router Advertisement prefix with the autonomous flag set it forms an address by combining the prefix with an interface identifier.",
      sourceId: "rfc4862",
      locator: "Section 5.3, Creation of Link-Local Addresses; Section 5.5.3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ra-on-link-flag",
      statement:
        "The on-link (L) flag in a Prefix Information option says the prefix can be used for on-link determination; the autonomous (A) flag says it can be used for stateless address configuration.",
      sourceId: "rfc4861",
      locator: "Section 4.6.2, Prefix Information",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "temporary-addresses",
      statement:
        "Temporary address extensions to SLAAC generate additional temporary addresses to mitigate the privacy issues of stable addresses.",
      sourceId: "rfc8981",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "prefer-ipv6",
      statement:
        "One effect of the default policy table is to prefer IPv6 addresses over IPv4 addresses when matching source addresses are available.",
      sourceId: "rfc6724",
      locator: "Section 2.1, Policy Table",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-source-fragmentation-and-mtu",
      statement:
        "IPv6 fragmentation is performed only by source nodes, not by routers, and every link must have an MTU of at least 1280 octets.",
      sourceId: "rfc8200",
      locator: "Section 4.5, Fragment Header; Section 5, Packet Size Issues",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv4-minimum-forward",
      statement:
        "Every IPv4 module must be able to forward a datagram of 68 octets without further fragmentation.",
      sourceId: "rfc791",
      locator: "Section 3.2, Discussion, Fragmentation and Reassembly",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "v4-mapped-sockets",
      statement:
        "An IPv6 socket handles IPv4 connections using IPv4-mapped addresses (::ffff:a.b.c.d) unless IPV6_V6ONLY is set; the default comes from /proc/sys/net/ipv6/bindv6only, which defaults to 0.",
      sourceId: "man-ipv6-v6only",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "v4-mapped-ipv6-7",
      statement:
        "IPv4 and IPv6 share the local port space, and an IPv4 connection to an IPv6 socket has its source address mapped to v6.",
      sourceId: "man-ipv6",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ip-addr-scopes",
      statement:
        "`ip address` scopes are global, link (valid only on this device), and host (valid only inside this host); `dynamic` lists IPv6 addresses installed by stateless autoconfiguration, and `temporary` lists temporary IPv6 addresses.",
      sourceId: "man-ip-address",
      locator: "ip address add, scope; ip address show, dynamic and temporary",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc4291", note: "Sections 2 to 2.5 and 2.8: the address architecture." },
    { sourceId: "rfc5952", note: "Section 4: canonical text form." },
    { sourceId: "rfc4862", note: "Sections 5.3 and 5.5.3: SLAAC." },
    { sourceId: "rfc4861", note: "Section 4.6.2: on-link and autonomous flags." },
    { sourceId: "rfc8981", note: "Temporary addresses." },
    { sourceId: "rfc6724", note: "Section 2.1: default address selection prefers IPv6." },
    {
      sourceId: "rfc8200",
      note: "Sections 4.5 and 5: source-only fragmentation and the 1280-byte minimum.",
    },
    { sourceId: "rfc791", note: "Section 3.2: the 68-octet minimum." },
    { sourceId: "rfc4632", note: "Section 2: what CIDR did not solve." },
    { sourceId: "man-ipv6", note: "IPv6 sockets on Linux." },
    { sourceId: "man-ipv6-v6only", note: "IPV6_V6ONLY and bindv6only." },
    { sourceId: "man-ip-address", note: "Address scopes and flags." },
  ],
} satisfies LessonBody;
