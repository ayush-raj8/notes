import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m01.layering",
  status: "complete",
  objectives: [
    "Explain why the jobs of a network are split across separate protocols instead of one.",
    "Name the four TCP/IP layers, map them onto the seven OSI layers, and say which devices implement which layers.",
    "Trace how each header names the protocol above it so a receiver can hand data upward.",
    "Recognize where real systems break strict layering, and what that costs.",
  ],
  prerequisites: ["m01.what-a-network-must-do"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "The jobs from the previous lesson need different knowledge. Only the network card can put bits on a particular wire or radio. Only routers know the topology. Only the end hosts know whether data arrived. Only the application knows what a message means. One protocol that did all of this would have to be implemented by every device and upgraded everywhere at once.",
      whyItMatters:
        "The Internet runs over Ethernet, Wi-Fi, fibre, and cellular links, and carries HTTP, DNS, SSH, and protocols nobody has written yet. A new application must not need new routers, and a new link type must not need new applications.",
      idea: "Split the work into layers. Each layer gives a service to the layer above, uses the service of the layer below, and talks to the same layer on the other machine through its own header. A device implements only the layers its job needs.",
      howItWorks:
        "TCP/IP has four layers. The link layer moves a frame across one link to the next device. The internet layer, IP, moves a datagram from source host to destination host across many links. The transport layer delivers data between programs: UDP adds ports and a checksum, and TCP adds ports, sequence numbers, acknowledgments, retransmission, and flow control. The application layer gives the bytes meaning. On send, each layer prepends its header and passes the result down. Every header carries a field naming the protocol it contains (the Ethernet type, the IP protocol number, the destination port), so the receiver strips headers in reverse and hands each payload to the right protocol above.",
      whyItWorks:
        "Every layer above IP depends only on IP's service, and IP depends only on being able to send a datagram across one link. That narrow middle lets anything above change without touching anything below. Routers implement link and IP only, so they forward without understanding transports or applications.",
      limits:
        "Layers hide information. TCP does not see the MTU of a distant link and has to discover it. Real devices also break the rules: NAT rewrites ports, which belong to the transport layer; load balancers and firewalls read transport and application headers. Those middleboxes make new transport protocols hard to deploy, which is one reason QUIC runs over UDP. Every layer also costs header bytes in every packet.",
      connections:
        "The next lesson shows the headers byte by byte. Module 2 covers the link layer, Modules 3 and 4 the internet layer, Modules 5 and 6 the transport layer, and Module 8 explains why HTTP still exists on top of TCP (why.01).",
    },
    {
      type: "why",
      id: "why-layers",
      questionId: "why.03",
    },
    {
      type: "comparison",
      id: "osi-tcpip",
      title: "OSI and TCP/IP side by side",
      caption:
        "Each row is an OSI layer. TCP/IP is the protocol suite that runs. OSI is the vocabulary engineers still use: an L4 load balancer works on transport headers, an L7 one on HTTP. The session and presentation rows are a loose fit.",
      columns: ["TCP/IP layer", "Job", "Examples", "Where it runs on Linux"],
      rows: [
        {
          label: "7 Application",
          cells: [
            "Application",
            "What a message means.",
            "HTTP, DNS, SSH, SMTP",
            "User-space process",
          ],
        },
        {
          label: "6 Presentation",
          cells: [
            "Application",
            "Encoding and encryption of the data.",
            "TLS, JSON, Protocol Buffers",
            "Libraries inside the process",
          ],
        },
        {
          label: "5 Session",
          cells: [
            "Application",
            "Dialogs that outlive one exchange.",
            "HTTP/2 streams, RPC sessions",
            "Libraries inside the process",
          ],
        },
        {
          label: "4 Transport",
          cells: [
            "Transport",
            "Delivery between programs; reliability in TCP.",
            "TCP, UDP; QUIC on top of UDP",
            "Kernel for TCP and UDP; QUIC in user space",
          ],
        },
        {
          label: "3 Network",
          cells: [
            "Internet",
            "Delivery between hosts across networks.",
            "IPv4, IPv6, ICMP",
            "Kernel; also every router",
          ],
        },
        {
          label: "2 Data link",
          cells: [
            "Link",
            "Delivery to the next device on one link.",
            "Ethernet, Wi-Fi",
            "Kernel driver and network card",
          ],
        },
        {
          label: "1 Physical",
          cells: [
            "Link",
            "Bits on copper, fibre, or radio.",
            "1000BASE-T, 802.11 radio",
            "Network card hardware",
          ],
        },
      ],
    },
    {
      type: "why",
      id: "why-transport",
      questionId: "why.02",
    },
    {
      type: "steps",
      id: "who-reads-what",
      title: "Which headers each device reads",
      steps: [
        {
          title: "Switch",
          body: "Reads the Ethernet header, looks up the destination MAC address, and sends the frame out one port. It does not need IP.",
        },
        {
          title: "Router",
          body: "Removes the Ethernet header, reads the IP header, looks up the destination address in its forwarding table, decrements the TTL, and wraps the datagram in a new frame for the next link. The transport header and application data pass through unread.",
        },
        {
          title: "Destination kernel",
          body: "Accepts the frame for its MAC address, uses the Ethernet type to pick IPv4 or IPv6, uses the IP protocol number to pick TCP or UDP, and uses the addresses and ports to pick one socket.",
        },
        {
          title: "Application",
          body: "Reads bytes from the socket and parses its own protocol, such as an HTTP request line and headers.",
        },
      ],
    },
    {
      type: "why",
      id: "why-routers",
      questionId: "why.04",
    },
    {
      type: "example",
      id: "curl-through-home-router",
      title: "`curl https://example.com` from a laptop",
      setup:
        "A Linux laptop on home Wi-Fi fetches a page. DNS has already returned the server's address.",
      walkthrough: [
        "curl and its TLS library produce encrypted HTTP bytes and write them to a TCP socket. That is the application layer.",
        "The kernel's TCP adds a header with source port, for example 51514, chosen from `ip_local_port_range`, destination port 443, and sequence numbers.",
        "IP adds the laptop's address as source, the server's address as destination, a TTL, and protocol number 6 for TCP.",
        "The link layer adds a Wi-Fi header addressed to the home router's MAC address, because the server is not on the local network.",
        "The home router removes that link header, rewrites the source address and port for NAT, decrements the TTL, and sends a new frame toward the ISP. Routers after it touch only link and IP headers.",
        "The server's kernel reverses the steps: frame type to IP, protocol 6 to TCP, address and port pair to the socket owned by the web server process.",
      ],
      result:
        "Each layer read only its own header, except the NAT step, which reached into the transport header. Module 10 covers why that works and when it breaks.",
    },
    {
      type: "why",
      id: "why-ports",
      questionId: "why.07",
    },
    {
      type: "callout",
      id: "models-are-maps",
      tone: "note",
      title: "Layer models are maps, not laws",
      scope: "teaching-simplification",
      body: "Some protocols sit between layers. ARP carries IP addresses inside link-layer frames. ICMP is part of IP but is carried inside IP datagrams. TLS sits on top of TCP yet below HTTP. Use the layer numbers to say which header a device reads, not to settle where a protocol belongs.",
    },
    {
      type: "expandable",
      id: "layer-violations",
      title: "Layer crossings you will meet in production",
      paragraphs: [
        "NAT rewrites IP addresses and TCP or UDP ports, and keeps a table of active flows. A router doing NAT therefore holds per-connection state that plain IP forwarding avoids.",
        "Equal-cost multipath routing often hashes the transport ports along with the IP addresses so that one flow stays on one path and is not reordered.",
        "A layer-7 load balancer or TLS-terminating proxy is an endpoint: it ends the client's TCP connection, reads the HTTP request, and opens a separate connection to a backend.",
        "Firewalls and middleboxes that inspect transport headers tend to drop protocols they do not recognize. New transports such as QUIC are built on UDP so they pass through.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "router-layers",
          prompt:
            "A router forwards an HTTPS request. Which headers does it read, and which does it change?",
          answer:
            "It reads the link header and the IP header. It replaces the link header with one for the next link and decrements the IP TTL, which also changes the IPv4 header checksum. Without NAT it leaves the TCP header and the encrypted HTTP bytes untouched.",
        },
        {
          id: "demux-fields",
          prompt:
            "Which field in each header tells the receiving kernel where to hand the payload next?",
          answer:
            "The Ethernet type selects IPv4 or IPv6. The IP protocol number (Next Header in IPv6) selects TCP, UDP, or ICMP. The destination port, together with the addresses, selects the socket.",
        },
        {
          id: "osi-session",
          prompt: "An interviewer asks where TLS sits in the OSI model. What is a precise answer?",
          answer:
            "TLS runs on top of TCP and below the application protocol, so it is often called presentation-layer work. TCP/IP has no separate session or presentation layer; TLS is part of the application layer there. The useful fact is that routers and L4 load balancers cannot read the encrypted payload.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "tcpip-four-layers",
      statement:
        "The Internet protocol suite has application, transport, internet, and link layers. Its application layer combines the OSI presentation and application layers.",
      sourceId: "rfc1122",
      locator: "Section 1.1.3, Internet Protocol Suite",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tcp-udp-service",
      statement:
        "TCP is a reliable connection-oriented transport that provides end-to-end reliability, resequencing, and flow control. UDP is a connectionless datagram transport.",
      sourceId: "rfc1122",
      locator: "Section 1.1.3, Transport Layer",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ip-interfaces",
      statement:
        "TCP hands a segment, header and data, to the internet module as the data portion of a datagram. The internet module builds the datagram and calls the local network interface to transmit it.",
      sourceId: "rfc791",
      locator: "Section 1.3, Interfaces",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ip-protocol-field",
      statement: "The IPv4 Protocol field names the next-level protocol carried in the datagram.",
      sourceId: "rfc791",
      locator: "Section 3.1, Protocol",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-protocol-17",
      statement:
        "UDP is IP protocol 17. It sends messages with minimal protocol mechanism and does not guarantee delivery or protect against duplicates.",
      sourceId: "rfc768",
      locator: "Introduction; Protocol Number",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tcp-ports",
      statement:
        "TCP uses port numbers to identify application services and to multiplex distinct flows between hosts.",
      sourceId: "rfc9293",
      locator: "Section 2.2, Key TCP Concepts",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "connection-socket-pair",
      statement:
        "A TCP connection is identified by a pair of sockets, where a socket is an Internet address combined with a TCP port.",
      sourceId: "rfc9293",
      locator: "Section 4, Glossary: connection, socket",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "state-in-hosts",
      statement:
        "Gateways keep no connection state; reliability and flow-control state lives in the hosts, in the transport layer or in applications.",
      sourceId: "rfc1122",
      locator: "Section 1.1.2 (b)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "osi-seven-layers",
      statement:
        "The OSI reference model has seven layers: physical, data link, network, transport, session, presentation, and application.",
      sourceId: "book-kurose",
      locator: "Chapter 1, Section 1.5.1, Layered Architecture",
      verification: "unread",
      subtle: false,
      scope: "teaching-simplification",
    },
  ],
  references: [
    { sourceId: "rfc1122", note: "Section 1.1.3 defines the four layers." },
    { sourceId: "rfc791", note: "Section 1.3 describes the TCP-to-IP-to-link handoff." },
    { sourceId: "rfc768", note: "The UDP service in one page." },
    { sourceId: "rfc9293", note: "Section 2.2 summarizes the TCP service." },
    { sourceId: "book-kurose", note: "Teaching reference for the OSI model." },
  ],
} satisfies LessonBody;
