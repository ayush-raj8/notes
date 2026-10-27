import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m01.encapsulation",
  status: "complete",
  objectives: [
    "Trace application data through a TCP segment, an IP packet, and an Ethernet frame, and back up at the receiver.",
    "Use the right name for the unit at each layer: application data, segment or datagram, packet, frame.",
    "Compute the largest TCP and UDP payload that fits a given MTU, and the overhead per packet.",
    "Explain what happens when a packet is larger than a link's MTU, and how that fails in practice.",
  ],
  prerequisites: ["m01.layering"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Each layer needs its own control information to travel with the data: ports for the transport, addresses and a TTL for IP, next-hop addresses for the link. A lower layer must carry the upper layer's data without understanding it. And every link has a largest frame it will carry.",
      whyItMatters:
        "Encapsulation decides how many application bytes fit in a packet, why TCP sends 1448 or 1460 bytes at a time on Ethernet, what `tcpdump` shows, and why a VPN or a blocked ICMP message can make large transfers hang while small requests work.",
      idea: "Each layer treats whatever the layer above handed it as an opaque payload, prepends its own header, and passes the result down. The link layer may also append a trailer. The receiver removes headers in the reverse order. The MTU caps the size of the IP packet a link carries, so every header above the link eats into the space left for application data.",
      howItWorks:
        "The application writes bytes to a socket. TCP cuts the byte stream into segments, each a TCP header plus data; UDP wraps each message in a UDP datagram with an 8-byte header. IP prepends its header, at least 20 bytes for IPv4 or a fixed 40 for IPv6, to form a datagram. The unit handed to the link layer is called a packet: a whole datagram or one fragment of it. The link layer wraps the packet in a frame; Ethernet adds a 14-byte header and a 4-byte check sequence. The MTU counts the IP packet only, not the Ethernet header, so a standard Ethernet MTU of 1500 leaves 1460 bytes for TCP data over IPv4 without options, and TCP advertises that as its maximum segment size (MSS). The path MTU is the smallest MTU of any link on the path.",
      whyItWorks:
        "Because each layer only adds and removes its own header, a router can strip one link header, process the IP header, and add a different link header for the next link without touching the transport header or the data. The MSS lets TCP size segments so the resulting packet fits the link, which avoids fragmentation.",
      limits:
        "Headers are fixed cost per packet, so small messages waste a larger share of the link. TCP sizes segments to its own byte stream, not to the application's writes, so one `write` can become several segments and several writes can share one. The sender knows its own link's MTU but has to learn the path MTU, and that discovery depends on ICMP messages that some networks drop.",
      connections:
        "Module 2 covers the Ethernet frame and MAC addresses (why.05). Module 3 covers fragmentation and Path MTU Discovery in detail. Module 5 covers MSS negotiation during the handshake.",
    },
    {
      type: "steps",
      id: "send-path",
      title: "One HTTP request down the stack and back up",
      steps: [
        {
          title: "Application data",
          body: "The client writes `GET / HTTP/1.1` and its headers to a connected TCP socket. To the kernel these are bytes in the socket's send buffer.",
        },
        {
          title: "TCP segment",
          body: "TCP takes up to one MSS of bytes and prepends a 20-byte header plus options: ports, sequence and acknowledgment numbers, flags, window, and checksum.",
        },
        {
          title: "IP packet",
          body: "IP prepends a 20-byte IPv4 header with source and destination address, TTL, protocol number 6, and total length.",
        },
        {
          title: "Ethernet frame",
          body: "The link layer prepends destination MAC, source MAC, and type 0x0800 (IPv4), then appends a 4-byte frame check sequence. The card adds an 8-byte preamble and leaves a 12-byte gap before the next frame.",
        },
        {
          title: "Each router hop",
          body: "The router checks and removes the frame, decrements the TTL, updates the IPv4 header checksum, and builds a new frame for the next link with new MAC addresses. The segment inside is unchanged.",
        },
        {
          title: "Receiver",
          body: "The destination checks and strips the frame, uses type 0x0800 to pick IPv4, protocol 6 to pick TCP, and the addresses and ports to pick the socket. TCP places the data by sequence number and the server reads the bytes.",
        },
      ],
    },
    {
      type: "comparison",
      id: "pdu-names",
      title: "Names for the unit at each layer",
      caption:
        "These are the RFC 1122 terms. People often say packet for any of them; in an interview, use the precise name when the layer matters.",
      columns: ["Unit", "Consists of", "Who reads the header"],
      rows: [
        {
          label: "Application",
          cells: [
            "Message or data",
            "Bytes in the application's own format, such as an HTTP request.",
            "The peer application.",
          ],
        },
        {
          label: "Transport",
          cells: [
            "TCP segment, UDP datagram",
            "Transport header plus application data.",
            "The destination host's kernel; also NAT, firewalls, and L4 load balancers.",
          ],
        },
        {
          label: "Internet",
          cells: [
            "IP datagram; a packet when handed to the link (a whole datagram or a fragment)",
            "IP header plus a transport segment or datagram.",
            "Every router and the destination host.",
          ],
        },
        {
          label: "Link",
          cells: [
            "Frame",
            "Link header, the packet, and for Ethernet a trailing check sequence.",
            "Every device on that one link, such as switches and the next router.",
          ],
        },
      ],
    },
    {
      type: "comparison",
      id: "payload-sizes",
      title: "Largest payload in a 1500-byte MTU",
      caption:
        "Assumes no IP options or IPv6 extension headers. Linux enables TCP timestamps by default, which add 12 bytes to each segment.",
      columns: ["IP header", "Transport header", "Largest payload"],
      rows: [
        { label: "TCP over IPv4", cells: ["20", "20", "1460 (1448 with timestamps)"] },
        { label: "TCP over IPv6", cells: ["40", "20", "1440 (1428 with timestamps)"] },
        { label: "UDP over IPv4", cells: ["20", "8", "1472"] },
        { label: "UDP over IPv6", cells: ["40", "8", "1452"] },
      ],
    },
    {
      type: "example",
      id: "four-kilobytes",
      title: "How a 4000-byte response crosses Ethernet",
      setup:
        "A Linux server writes 4000 bytes of HTTP response to a TCP socket over IPv4. Both links have MTU 1500 and TCP timestamps are on, so each segment carries at most 1448 bytes of data.",
      walkthrough: [
        "TCP cuts the stream into segments of 1448, 1448, and 1104 bytes.",
        "Each segment gets 32 bytes of TCP header (20 fixed plus 12 for the timestamp option) and 20 bytes of IPv4 header. The two full packets are exactly 1500 bytes.",
        "Each frame adds 18 bytes of Ethernet header and check sequence, and the wire adds 20 more for preamble and inter-frame gap: 90 bytes of overhead per packet.",
        "Three packets cost 270 bytes of overhead for 4000 bytes of data, so about 93.7 percent of the wire time carries application bytes.",
        "Had the server written 4000 one-byte responses as separate packets, overhead would dominate: each packet would be 53 bytes, each frame 71, and the wire would carry 1 useful byte in every 91.",
      ],
      result:
        "Large writes amortize headers; tiny packets waste most of the link. On the receiver, `read` may return the 4000 bytes in one call or several, because TCP does not keep the sender's write boundaries.",
    },
    {
      type: "callout",
      id: "too-big",
      tone: "caution",
      title: "When a packet is larger than the next link's MTU",
      scope: "rfc",
      body: "An IPv4 router can fragment the packet unless the sender set the Don't Fragment bit; if the bit is set, it drops the packet and sends an ICMP error. IPv6 routers never fragment: only the source can, and a router that cannot forward sends ICMPv6 Packet Too Big. Linux sets Don't Fragment on TCP traffic and uses those ICMP errors for Path MTU Discovery. If a firewall drops them, the handshake and small requests succeed while full-size packets disappear, and the connection hangs.",
    },
    {
      type: "expandable",
      id: "linux-observation",
      title: "Seeing MTU and segment sizes on Linux",
      paragraphs: [
        "`ip link show dev eth0` prints the interface MTU. Loopback usually reports 65536, and tunnel interfaces such as WireGuard or other VPNs report less than 1500 because their own headers have to fit inside the outer packet.",
        "`ss -tni` shows per-connection `mss`, `pmtu`, and `advmss`. `tracepath host` reports the path MTU it discovers hop by hop.",
        "`ping -M do -s 1472 host` sends a 1500-byte IPv4 packet with Don't Fragment set: 1472 bytes of data, 8 of ICMP header, 20 of IP header. If it fails while `-s 1400` works, something on the path has a smaller MTU.",
        "`tcpdump` on the sending host can show TCP packets far larger than the MTU. The capture point sits above segmentation offload (TSO and GSO), where the kernel hands the card one large buffer and the card or driver cuts it into MTU-sized frames. On the receiver, GRO merges frames back before capture. `ethtool -k eth0` lists which offloads are on.",
      ],
    },
    {
      type: "expandable",
      id: "minimums",
      title: "The minimum sizes every host must handle",
      paragraphs: [
        "IPv4 hosts must accept datagrams of at least 576 bytes, whole or in fragments. That is why TCP assumes an MSS of 536 (576 minus 40) when the peer sends no MSS option.",
        "IPv6 requires every link to carry 1280-byte packets, so the default IPv6 MSS is 1220 (1280 minus 60). Links that cannot must fragment and reassemble below IPv6.",
        "The IPv4 Total Length field is 16 bits, so a datagram can be at most 65,535 bytes including the header, whatever the link supports.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "mtu-scope",
          prompt: "Does a 1500-byte Ethernet MTU include the Ethernet header?",
          answer:
            "No. The MTU is the largest IP packet the link carries. The 14-byte header and 4-byte check sequence come on top, so a full frame is 1518 bytes, or 1522 with a VLAN tag.",
        },
        {
          id: "vpn-mss",
          prompt:
            "A WireGuard interface has MTU 1420. What is the largest TCP payload per IPv4 packet without options, and why is it smaller than on Ethernet?",
          answer:
            "1380 bytes: 1420 minus 20 for IPv4 and 20 for TCP. The tunnel encrypts each inner packet and wraps it in its own outer UDP and IP headers, and the result still has to fit the physical link's 1500-byte MTU.",
        },
        {
          id: "router-rewrite",
          prompt: "Which parts of a packet does a plain IP router change on each hop?",
          answer:
            "It replaces the whole link-layer frame, decrements the TTL (Hop Limit in IPv6), and in IPv4 recomputes the header checksum. The transport segment and data pass through unchanged unless NAT is involved.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "rfc1122-terminology",
      statement:
        "A segment is a TCP header followed by application data, carried inside an IP datagram. A datagram is an IP header followed by a transport message. A packet is what crosses the interface between the internet and link layers: a whole datagram or a fragment. A frame is a link-layer header followed by a packet.",
      sourceId: "rfc1122",
      locator: "Section 1.3.3, Terminology",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv4-header-min",
      statement:
        "The IPv4 header length is counted in 32-bit words with a minimum of 5, so the smallest header is 20 bytes. Total Length allows datagrams up to 65,535 bytes, and every host must accept datagrams of up to 576 bytes.",
      sourceId: "rfc791",
      locator: "Section 3.1, IHL and Total Length",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-header-8",
      statement:
        "The UDP Length field counts the header and data, so its minimum value is 8: the UDP header is 8 bytes.",
      sourceId: "rfc768",
      locator: "Format; Fields, Length",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "mss-default-and-formula",
      statement:
        "Without an MSS option, TCP assumes a send MSS of 536 for IPv4 or 1220 for IPv6. The effective send MSS subtracts the TCP header, 20 bytes when no options are present, and any IP options. The advertised MSS should equal the effective MTU minus the fixed IP and TCP headers.",
      sourceId: "rfc9293",
      locator: "Section 3.7.1, Maximum Segment Size Option",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "segments-not-writes",
      statement:
        "TCP segments do not correspond one-for-one to application writes, and TCP guarantees no correlation between segment boundaries and the application's read or write boundaries.",
      sourceId: "rfc9293",
      locator: "Section 3.7, Segmentation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "avoid-exceeding-mtu",
      statement:
        "TCP avoids segments whose IP datagram would exceed the smallest MTU on the path, because that causes loss or fragmentation, and some firewalls drop fragments or fragmentation-related ICMP messages.",
      sourceId: "rfc9293",
      locator: "Section 3.7, Segmentation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "pmtu-definition",
      statement: "The path MTU equals the minimum of the MTUs of each hop in the path.",
      sourceId: "rfc1191",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ethernet-1500",
      statement: "Ethernet networks have an MTU of 1500 bytes.",
      sourceId: "rfc1191",
      locator: "Section 7, Table 7-1",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-min-mtu",
      statement:
        "IPv6 requires every link to have an MTU of at least 1280 bytes. IPv6 fragmentation is done only by source nodes, not by routers on the path.",
      sourceId: "rfc8200",
      locator: "Section 5, Packet Size Issues; Section 4.5, Fragment Header",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-pmtud-stream",
      statement:
        "When Path MTU Discovery is enabled, Linux performs it on SOCK_STREAM sockets and keeps track of the path MTU per destination. The system default is set through /proc/sys/net/ipv4/ip_no_pmtu_disc.",
      sourceId: "man-ip",
      locator: "Socket options, IP_MTU_DISCOVER (linked page IP_MTU_DISCOVER(2const))",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc1122", note: "Section 1.3.3 defines segment, datagram, packet, and frame." },
    { sourceId: "rfc791", note: "Section 3.1 is the IPv4 header." },
    { sourceId: "rfc768" },
    { sourceId: "rfc9293", note: "Sections 3.7 and 3.7.1 cover segmentation and the MSS." },
    { sourceId: "rfc1191", note: "Path MTU Discovery for IPv4." },
    { sourceId: "rfc8200", note: "Section 5 covers IPv6 packet sizes." },
    { sourceId: "man-ip", note: "Linux Path MTU Discovery socket option." },
  ],
} satisfies LessonBody;
