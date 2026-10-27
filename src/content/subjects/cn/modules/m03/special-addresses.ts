import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m03.special-addresses",
  status: "complete",
  objectives: [
    "Name the private, shared, loopback, link-local, broadcast, multicast, and documentation ranges in IPv4, and their IPv6 counterparts.",
    "Say for each range whether a packet using it can leave the host, leave the link, or cross the public Internet.",
    "Explain why private addresses need NAT or a proxy to reach the Internet, and what a private source address in a log implies.",
    "Choose between binding a service to 127.0.0.1, a specific address, and 0.0.0.0 on Linux.",
  ],
  prerequisites: ["m03.ipv4-cidr"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Most addresses mean 'one interface somewhere on the Internet'. But a host also needs a name for itself that never touches the network, a way to talk on a link before it has been configured, address space that any organisation can use without asking a registry, and examples that will never collide with a real host. If those uses borrowed ordinary addresses, routers would forward packets that should never leave a machine.",
      whyItMatters:
        "These ranges explain everyday symptoms: a service that works locally but not from another host, an instance with a 169.254 address, a log full of 10.x client addresses, or a VPN that breaks access to the office printer. They also decide which addresses a firewall should never see arriving from the Internet.",
      idea: "Reserve blocks of the address space and give each block a fixed scope: valid only inside one host, only on one link, only inside one organisation, or nowhere at all. Routers and hosts enforce the scope, so the address itself says how far a packet may travel.",
      howItWorks:
        "Loopback (127.0.0.0/8) is delivered inside the host and must never appear on a wire. Link-local (169.254.0.0/16) is used for talking to neighbours, and routers must not forward it. Private-use blocks (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) are routed normally inside an organisation, but are not announced or forwarded between organisations, so many networks can reuse them. To reach the Internet, a private host goes through a device that substitutes a public address, such as a NAT or a proxy. The IANA special-purpose registry records, for each reserved block, whether it is valid as a source, as a destination, forwardable by a router, and forwardable beyond its own domain.",
      whyItWorks:
        "Scope is decided by prefix, so the check costs a routing lookup that already happens. Because private blocks are never globally routed, the same 10.0.0.0/8 can exist in millions of networks without conflict. Loopback and link-local work before any configuration or route exists, which is when a host most needs them.",
      limits:
        "Private does not mean secure. It only means not routed on the public Internet. A host with a private address behind a NAT is still reachable by anything inside the same network or through a port forward. Private ranges collide when two networks join: VPN users at home on 192.168.1.0/24 cannot reach an office subnet with the same prefix. The registry records intent. Linux, for example, now accepts some historically reserved addresses as ordinary unicast, and older equipment may still reject them.",
      connections:
        "The previous lesson covered prefix arithmetic, which is how a host classifies an address into one of these ranges. Lesson 4 covers IPv6, where link-local addresses are always present. Module 10 covers NAT, which lets private hosts use public addresses.",
    },
    {
      type: "comparison",
      id: "ipv4-special-ranges",
      title: "IPv4 ranges with a fixed meaning",
      caption:
        "Scope as recorded in RFC 6890 and the RFCs it cites. The live IANA registry may list newer blocks.",
      columns: ["Meaning", "How far a packet can go"],
      rows: [
        {
          label: "10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16",
          cells: [
            "Private use. Any organisation may use them without asking a registry.",
            "Routed inside the organisation. Not forwarded across the public Internet.",
          ],
        },
        {
          label: "100.64.0.0/10",
          cells: [
            "Shared address space between an ISP's carrier-grade NAT and its customers' routers.",
            "Inside the provider's network only.",
          ],
        },
        {
          label: "127.0.0.0/8",
          cells: ["Loopback. Usually written as 127.0.0.1.", "Never leaves the host."],
        },
        {
          label: "169.254.0.0/16",
          cells: [
            "IPv4 link-local, self-assigned when nothing else configured the interface.",
            "Only the local link. Routers must not forward it.",
          ],
        },
        {
          label: "0.0.0.0/8",
          cells: [
            "'This host on this network'. 0.0.0.0 is a source while a host has no address, and a wildcard for bind().",
            "Not valid as a destination.",
          ],
        },
        {
          label: "255.255.255.255",
          cells: ["Limited broadcast.", "Every host on the link. Never forwarded."],
        },
        {
          label: "224.0.0.0/4",
          cells: ["Multicast groups.", "Depends on the group and on multicast routing."],
        },
        {
          label: "192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24",
          cells: [
            "Documentation (TEST-NET-1, -2, -3). These notes use them.",
            "Nowhere. They should never appear in real traffic.",
          ],
        },
        {
          label: "240.0.0.0/4",
          cells: ["Reserved.", "Not routed on the Internet. Linux treats it as unicast."],
        },
      ],
    },
    {
      type: "comparison",
      id: "ipv6-counterparts",
      title: "IPv6 counterparts",
      columns: ["IPv4", "IPv6"],
      rows: [
        { label: "Loopback", cells: ["127.0.0.0/8", "::1/128, a single address"] },
        {
          label: "Link-local",
          cells: [
            "169.254.0.0/16, only when nothing else is configured",
            "fe80::/10, on every IPv6 interface",
          ],
        },
        {
          label: "Locally assigned, not globally routed",
          cells: ["RFC 1918 private blocks", "fc00::/7, Unique Local Addresses"],
        },
        { label: "Documentation", cells: ["The three TEST-NET /24s", "2001:db8::/32"] },
        { label: "Unspecified", cells: ["0.0.0.0", "::"] },
        {
          label: "Broadcast",
          cells: ["255.255.255.255 and each subnet's last address", "None. Multicast replaces it."],
        },
      ],
    },
    {
      type: "callout",
      id: "bind-address",
      tone: "caution",
      title: "127.0.0.1 versus 0.0.0.0 in a service",
      scope: "linux",
      body: "A server bound to 127.0.0.1 accepts only connections whose destination is the loopback address, which means connections from the same host, or the same network namespace. Binding to 0.0.0.0 (INADDR_ANY) accepts connections to any local address. Inside a container, 127.0.0.1 is the container's own loopback, so a port published from the host reaches the container's eth0 address and finds nothing listening. `ss -ltn` shows the bound address in the Local Address column: `127.0.0.1:8080` is local only, `0.0.0.0:8080` or `*:8080` is every address.",
    },
    {
      type: "prose",
      id: "reading-addresses",
      title: "What an address in a log tells you",
      paragraphs: [
        "A private source address on traffic that came from the Internet was not sent that way. Private blocks are not forwarded between organisations, so something inside your network rewrote it: a load balancer, a reverse proxy, or a NAT. The real client address is in a header such as X-Forwarded-For or in the PROXY protocol, if the proxy adds one. A private source arriving on an Internet-facing interface without such a device is spoofed or misrouted, and an edge firewall should drop it.",
        "A host whose only IPv4 address is in 169.254.0.0/16 usually gave up waiting for DHCP and self-assigned an address. It can reach neighbours that did the same, but nothing through a router. Cloud platforms are an exception that proves the rule: they serve instance metadata on a link-local address, 169.254.169.254, precisely because it needs no routing and every instance can reach it.",
        "Linux treats the whole of 127.0.0.0/8 as loopback, so `curl http://127.0.0.2/` reaches a server bound to 0.0.0.0, and test suites can give each fake service its own 127.x address. `ip route show table local` lists the loopback range and every local address as `local` routes.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "private-client-ip",
          prompt:
            "Your public web service logs every client as 10.0.3.17 or 10.0.4.22. What does that tell you, and where is the real client address?",
          answer:
            "Packets from the Internet should not arrive with a private source, because private blocks are not forwarded between networks, and your edge should drop any that do. Those are the addresses of your own load balancers or proxies, which opened new connections to the service. The client address is only available if the proxy passes it on, for example in X-Forwarded-For or with the PROXY protocol.",
        },
        {
          id: "link-local-only",
          prompt:
            "A VM's only IPv4 address is 169.254.40.7/16. Can it reach a database at 10.1.2.3 through the subnet router?",
          answer:
            "No. A packet with a link-local source must not be sent to a router for forwarding, and routers must not forward it. The address means the interface was not configured, usually because DHCP failed. Fix DHCP or configure an address first.",
        },
        {
          id: "container-loopback",
          prompt:
            "A container runs a server on 127.0.0.1:8080 and the host publishes port 8080 to it. Requests from the host time out or are refused. Why?",
          answer:
            "Inside the container, 127.0.0.1 is the container's own loopback. Published traffic arrives at the container's eth0 address, and no socket is bound to that address. Bind the server to 0.0.0.0, or to the eth0 address.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "rfc1918-blocks",
      statement:
        "IANA reserved 10.0.0.0/8, 172.16.0.0/12, and 192.168.0.0/16 for private internets; any enterprise may use them without coordinating with a registry.",
      sourceId: "rfc1918",
      locator: "Section 3, Private Address Space",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rfc1918-not-forwarded",
      statement:
        "Routing information about private networks shall not be propagated on inter-enterprise links, and packets with private source or destination addresses should not be forwarded across them; private hosts reach outside services only through mediating gateways.",
      sourceId: "rfc1918",
      locator: "Section 3, Private Address Space",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "special-purpose-attributes",
      statement:
        "The special-purpose address registries record, for each block, whether it is valid as a source, valid as a destination, forwardable by a router, and forwardable beyond its administrative domain (Global).",
      sourceId: "rfc6890",
      locator: "Section 2.2.1, Information Requirements",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv4-special-entries",
      statement:
        "The IPv4 registry includes 0.0.0.0/8 (this host on this network), 100.64.0.0/10 (Shared Address Space, not global), 127.0.0.0/8 (loopback), 169.254.0.0/16 (link local, not forwardable), 192.0.2.0/24, 198.51.100.0/24 and 203.0.113.0/24 (documentation), 240.0.0.0/4 (reserved), and 255.255.255.255/32 (limited broadcast, not forwardable).",
      sourceId: "rfc6890",
      locator: "Section 2.2.2, IPv4 Special-Purpose Address Registry Entries",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "loopback-and-broadcast",
      statement:
        "Addresses of the form {127, any} are internal host loopback and must not appear outside a host; the limited broadcast address is received by every host on the connected network and is not forwarded.",
      sourceId: "rfc1122",
      locator: "Section 3.2.1.3, Addressing, cases (c) and (g)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "link-local-not-forwarded",
      statement:
        "A packet with an IPv4 link-local source or destination must not be sent to a router for forwarding, and a router must not forward it, regardless of TTL or routes.",
      sourceId: "rfc3927",
      locator:
        "Section 2.7, Link-Local Packets Are Not Forwarded; Section 7, Router Considerations",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "link-local-range",
      statement:
        "A host self-assigning an IPv4 link-local address picks pseudo-randomly from 169.254.1.0 to 169.254.254.255; the first and last 256 addresses of 169.254/16 are reserved.",
      sourceId: "rfc3927",
      locator: "Section 2.1, Link-Local Address Selection",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-special-addresses",
      statement:
        "On Linux, INADDR_ANY (0.0.0.0) means any address for binding, all of 127/8 is treated as loopback, 224/4 is multicast, and 0/8 and 240/4 (other than 0.0.0.0 and 255.255.255.255) are treated as ordinary unicast.",
      sourceId: "man-ip",
      locator: "Special and reserved addresses",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ipv6-special-prefixes",
      statement:
        "IPv6 identifies the loopback ::1/128, the unspecified ::/128, multicast ff00::/8, and link-local fe80::/10 by their leading bits; routers must not forward packets with link-local source or destination addresses to other links.",
      sourceId: "rfc4291",
      locator: "Section 2.4, Address Type Identification; Section 2.5.6",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-ula",
      statement:
        "Unique Local IPv6 unicast addresses use the prefix fc00::/7 followed by a pseudo-random 40-bit Global ID.",
      sourceId: "rfc4193",
      locator: "Section 3.1, Format",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc1918",
      note: "Section 3: the private blocks and how they are kept off the Internet.",
    },
    {
      sourceId: "rfc6890",
      note: "Sections 2.2.1 to 2.2.3: the registry fields and the IPv4 and IPv6 special blocks.",
    },
    { sourceId: "rfc1122", note: "Section 3.2.1.3: loopback and broadcast forms." },
    { sourceId: "rfc3927", note: "Sections 2.1, 2.7, and 7: IPv4 link-local." },
    { sourceId: "rfc4291", note: "Sections 2.4, 2.5.3, and 2.5.6: IPv6 special addresses." },
    { sourceId: "rfc4193", note: "Unique Local Addresses." },
    { sourceId: "man-ip", note: "How Linux treats special and reserved IPv4 addresses." },
  ],
} satisfies LessonBody;
