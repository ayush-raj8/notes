import { syllabusBullet, type ModuleId } from "./syllabus";

export type CatalogTopic = {
  id: string;
  bullet: string;
  kind?: "scenario";
};

export type CatalogLesson = {
  id: string;
  moduleId: ModuleId;
  slug: string;
  title: string;
  order: number;
  summary: string;
  ownsTopics: CatalogTopic[];
};

function topic(moduleId: ModuleId, id: string, bullet: string, kind?: "scenario"): CatalogTopic {
  return kind
    ? { id, bullet: syllabusBullet(moduleId, bullet), kind }
    : { id, bullet: syllabusBullet(moduleId, bullet) };
}

const m01 = (id: string, bullet: string) => topic("m01", id, bullet);
const m02 = (id: string, bullet: string) => topic("m02", id, bullet);
const m03 = (id: string, bullet: string) => topic("m03", id, bullet);
const m04 = (id: string, bullet: string) => topic("m04", id, bullet);
const m05 = (id: string, bullet: string) => topic("m05", id, bullet);
const m06 = (id: string, bullet: string) => topic("m06", id, bullet);
const m07 = (id: string, bullet: string) => topic("m07", id, bullet);
const m08 = (id: string, bullet: string) => topic("m08", id, bullet);
const m09 = (id: string, bullet: string) => topic("m09", id, bullet);
const m10 = (id: string, bullet: string) => topic("m10", id, bullet);
const m11 = (id: string, bullet: string) => topic("m11", id, bullet);
const m12 = (id: string, bullet: string) => topic("m12", id, bullet);
const scenario = (id: string, bullet: string) => topic("m13", id, bullet, "scenario");

export const lessonCatalog: CatalogLesson[] = [
  {
    id: "m01.what-a-network-must-do",
    moduleId: "m01",
    slug: "what-a-network-must-do",
    title: "What a network must do",
    order: 1,
    summary:
      "The problems two programs face when they do not share memory: naming, a shared path, loss, delay, and duplication.",
    ownsTopics: [m01("m01.network-architecture", "Network architecture and protocol layering.")],
  },
  {
    id: "m01.layering",
    moduleId: "m01",
    slug: "layering",
    title: "Why networks are layered",
    order: 2,
    summary:
      "Why those problems are split across protocols, and how the OSI and TCP/IP models describe the split.",
    ownsTopics: [
      m01("m01.protocol-layering", "Network architecture and protocol layering."),
      m01("m01.osi-tcpip", "OSI model and TCP/IP model."),
    ],
  },
  {
    id: "m01.encapsulation",
    moduleId: "m01",
    slug: "encapsulation",
    title: "Encapsulation and the MTU",
    order: 3,
    summary:
      "How application data becomes a segment, a packet, and a frame, and what the MTU limits.",
    ownsTopics: [
      m01("m01.encapsulation", "Encapsulation and decapsulation."),
      m01("m01.pdu-names", "Frames, packets, segments, and application data."),
      m01("m01.mtu-payload", "MTU and payload sizes."),
    ],
  },
  {
    id: "m01.planes-and-connections",
    moduleId: "m01",
    slug: "planes-and-connections",
    title: "Planes and connections",
    order: 4,
    summary:
      "Control-plane decisions versus forwarding, and what a connection does and does not mean.",
    ownsTopics: [
      m01("m01.control-vs-data-plane", "Control plane vs data plane."),
      m01(
        "m01.connection-vs-connectionless",
        "Connection-oriented vs connectionless communication.",
      ),
    ],
  },
  {
    id: "m02.ethernet-and-mac",
    moduleId: "m02",
    slug: "ethernet-and-mac",
    title: "Ethernet frames and MAC addresses",
    order: 1,
    summary: "The frame a switch forwards, and why that frame carries a MAC address.",
    ownsTopics: [m02("m02.ethernet-mac", "Ethernet frames and MAC addresses.")],
  },
  {
    id: "m02.switching",
    moduleId: "m02",
    slug: "switching",
    title: "Switches, domains, and VLANs",
    order: 2,
    summary: "How a switch learns ports, what a broadcast domain is, and what a VLAN changes.",
    ownsTopics: [
      m02("m02.switch-learning", "Switch learning and forwarding."),
      m02("m02.domains", "Broadcast domains and collision domains."),
      m02("m02.vlans", "VLANs and their purpose."),
    ],
  },
  {
    id: "m02.arp",
    moduleId: "m02",
    slug: "arp",
    title: "ARP and the ARP cache",
    order: 3,
    summary:
      "How a host resolves an on-link IPv4 address to a MAC address, and what the cache stores.",
    ownsTopics: [m02("m02.arp", "ARP and ARP caching.")],
  },
  {
    id: "m02.same-subnet",
    moduleId: "m02",
    slug: "same-subnet",
    title: "Delivery on the same subnet",
    order: 4,
    summary:
      "The path between two hosts on one subnet, and what changes when the destination is off-subnet.",
    ownsTopics: [
      m02(
        "m02.same-subnet",
        "What happens when a host communicates with another host on the same subnet.",
      ),
    ],
  },
  {
    id: "m02.ipv6-nd",
    moduleId: "m02",
    slug: "ipv6-neighbor-discovery",
    title: "IPv6 Neighbor Discovery",
    order: 5,
    summary: "How IPv6 resolves a neighbor on the link without ARP.",
    ownsTopics: [m02("m02.ipv6-nd", "IPv6 Neighbor Discovery.")],
  },
  {
    id: "m03.ipv4-cidr",
    moduleId: "m03",
    slug: "ipv4-and-cidr",
    title: "IPv4 prefixes and subnetting",
    order: 1,
    summary:
      "Prefixes, masks, and worked decisions about whether two IPv4 addresses share a subnet.",
    ownsTopics: [
      m03("m03.ipv4-cidr", "IPv4 addressing and CIDR."),
      m03("m03.masks", "Subnet masks and network prefixes."),
      m03("m03.same-subnet", "Determining whether two addresses belong to the same subnet."),
    ],
  },
  {
    id: "m03.special-addresses",
    moduleId: "m03",
    slug: "special-addresses",
    title: "Public, private, and special-purpose addresses",
    order: 2,
    summary:
      "Which addresses are routable on the public Internet, and which stay on a host or a link.",
    ownsTopics: [
      m03("m03.public-private", "Public vs private addresses."),
      m03("m03.special-purpose", "Loopback, link-local, and special-purpose addresses."),
    ],
  },
  {
    id: "m03.gateway-and-lpm",
    moduleId: "m03",
    slug: "gateway-and-longest-prefix",
    title: "Default gateways and longest-prefix matching",
    order: 3,
    summary: "Why off-subnet traffic goes to a gateway, and how a host chooses a route.",
    ownsTopics: [
      m03("m03.default-gateway", "Default gateways."),
      m03("m03.routing-table-lpm", "Routing tables and longest-prefix matching."),
    ],
  },
  {
    id: "m03.ipv6",
    moduleId: "m03",
    slug: "ipv6-addressing",
    title: "IPv4 and IPv6 addressing",
    order: 4,
    summary: "How IPv6 addressing differs from IPv4 in the host's view.",
    ownsTopics: [m03("m03.ipv4-vs-ipv6", "IPv4 vs IPv6.")],
  },
  {
    id: "m03.ttl-and-fragmentation",
    moduleId: "m03",
    slug: "ttl-and-fragmentation",
    title: "Hop limits, fragmentation, and Path MTU Discovery",
    order: 5,
    summary: "Why a packet expires, when IPv4 fragments, and why Path MTU Discovery exists.",
    ownsTopics: [
      m03("m03.ttl", "TTL and hop limits."),
      m03("m03.fragmentation", "IP fragmentation and Path MTU Discovery."),
    ],
  },
  {
    id: "m04.routing-vs-forwarding",
    moduleId: "m04",
    slug: "routing-vs-forwarding",
    title: "Routing versus forwarding",
    order: 1,
    summary:
      "Building a route versus using one, and why a router keeps a table instead of flooding.",
    ownsTopics: [
      m04("m04.routing-vs-forwarding", "Routing vs forwarding."),
      m04("m04.routing-tables", "Routing tables."),
    ],
  },
  {
    id: "m04.algorithms",
    moduleId: "m04",
    slug: "routing-algorithms",
    title: "How routing tables get built",
    order: 2,
    summary:
      "Static and dynamic routes, distance-vector and link-state, and how loops and convergence arise.",
    ownsTopics: [
      m04("m04.static-vs-dynamic", "Static vs dynamic routing."),
      m04("m04.dv-vs-ls", "Distance-vector vs link-state routing."),
      m04("m04.loops-convergence", "Routing loops and convergence."),
    ],
  },
  {
    id: "m04.ospf-bgp",
    moduleId: "m04",
    slug: "ospf-and-bgp",
    title: "OSPF and BGP",
    order: 3,
    summary: "What OSPF and BGP decide, and how longest-prefix matching is applied at a router.",
    ownsTopics: [
      m04("m04.ospf", "High-level operation of OSPF and BGP."),
      m04("m04.bgp", "High-level operation of OSPF and BGP."),
      m04("m04.lpm", "Longest-prefix matching."),
    ],
  },
  {
    id: "m04.icmp",
    moduleId: "m04",
    slug: "icmp",
    title: "ICMP, ping, traceroute, and unreachability",
    order: 4,
    summary: "The signals a host gets when a destination answers, and when it cannot be reached.",
    ownsTopics: [
      m04("m04.icmp", "ICMP, ping, and traceroute."),
      m04("m04.unreachable", "What happens when a destination is unreachable."),
    ],
  },
  {
    id: "m05.service-model",
    moduleId: "m05",
    slug: "service-model",
    title: "What TCP promises",
    order: 1,
    summary: "The reliable ordered byte stream, and the guarantees TCP deliberately does not make.",
    ownsTopics: [
      m05("m05.service-model", "TCP's service model."),
      m05("m05.byte-stream", "Reliable, ordered byte-stream delivery."),
    ],
  },
  {
    id: "m05.header-and-numbers",
    moduleId: "m05",
    slug: "sequence-numbers",
    title: "Sequence numbers and the TCP header",
    order: 2,
    summary:
      "The header fields that carry sequence, acknowledgment, flags, window, and the initial sequence number.",
    ownsTopics: [
      m05("m05.headers", "TCP headers and important fields."),
      m05("m05.seq-ack", "Sequence numbers and acknowledgment numbers."),
      m05("m05.isn", "Initial sequence numbers."),
    ],
  },
  {
    id: "m05.handshake",
    moduleId: "m05",
    slug: "handshake",
    title: "Establishing a connection",
    order: 3,
    summary: "Why both endpoints need shared state, and how the three-way handshake creates it.",
    ownsTopics: [
      m05("m05.three-way-handshake", "The three-way handshake."),
      m05("m05.syn-synack-ack", "SYN, SYN-ACK, ACK, FIN, and RST."),
      m05("m05.connection-establishment", "Connection establishment."),
      m05(
        "m05.simultaneous-open",
        "Simultaneous open and simultaneous close at a conceptual level.",
      ),
    ],
  },
  {
    id: "m05.reliability",
    moduleId: "m05",
    slug: "reliability",
    title: "Loss, duplication, delay, and reorder",
    order: 4,
    summary:
      "Timers, duplicate acknowledgments, selective acknowledgments, and what a gap does to later bytes.",
    ownsTopics: [
      m05("m05.timeout-retransmission", "Retransmission timers and timeout-based retransmission."),
      m05("m05.fast-retransmit", "Duplicate acknowledgments and fast retransmission."),
      m05("m05.sack", "Cumulative acknowledgments and selective acknowledgments."),
      m05("m05.rto-estimation", "Retransmission timeout estimation."),
      m05(
        "m05.loss-reorder",
        "What happens when packets are lost, duplicated, delayed, or reordered.",
      ),
    ],
  },
  {
    id: "m05.flow-control",
    moduleId: "m05",
    slug: "flow-control",
    title: "Flow control and the receiver window",
    order: 5,
    summary: "The sliding window, the advertised window, and a receiver that cannot keep up.",
    ownsTopics: [
      m05("m05.sliding-window", "Sliding-window protocols."),
      m05("m05.receiver-window", "Receiver window and advertised window."),
      m05("m05.flow-control", "Flow control."),
      m05("m05.zero-window", "Zero-window behavior."),
    ],
  },
  {
    id: "m05.congestion",
    moduleId: "m05",
    slug: "congestion-control",
    title: "Congestion control",
    order: 6,
    summary:
      "Why the network's capacity is a separate limit, and how slow start and recovery react.",
    ownsTopics: [
      m05("m05.congestion-control", "Congestion control."),
      m05(
        "m05.slow-start",
        "Slow start, congestion avoidance, and fast recovery at a conceptual level.",
      ),
    ],
  },
  {
    id: "m05.interactions",
    moduleId: "m05",
    slug: "tcp-interactions",
    title: "Delayed ACKs, Nagle, and high latency",
    order: 7,
    summary:
      "Interactions that stall a correct connection, including head-of-line blocking inside the byte stream.",
    ownsTopics: [
      m05("m05.delayed-ack-nagle", "Delayed ACKs and Nagle's algorithm."),
      m05("m05.keepalive", "TCP keepalive."),
      m05("m05.high-latency", "TCP performance over high-latency networks."),
      m05("m05.byte-stream-hol", "Head-of-line blocking within a TCP byte stream."),
    ],
  },
  {
    id: "m05.termination",
    moduleId: "m05",
    slug: "termination",
    title: "Ending a connection",
    order: 8,
    summary: "FIN, half-close, TIME_WAIT, resets, simultaneous close, and a crashed peer.",
    ownsTopics: [
      m05("m05.fin-rst", "SYN, SYN-ACK, ACK, FIN, and RST."),
      m05("m05.half-open", "Half-open connections."),
      m05("m05.half-close", "Half-close behavior."),
      m05("m05.time-wait", "TIME_WAIT and its purpose."),
      m05(
        "m05.simultaneous-close",
        "Simultaneous open and simultaneous close at a conceptual level.",
      ),
      m05("m05.resets", "Connection resets."),
      m05("m05.endpoint-crash", "What happens when a client or server crashes."),
    ],
  },
  {
    id: "m06.udp-service",
    moduleId: "m06",
    slug: "udp-service",
    title: "What UDP provides",
    order: 1,
    summary: "The UDP header, the checksum, and the delivery guarantees UDP omits.",
    ownsTopics: [
      m06("m06.header", "UDP header and service model."),
      m06("m06.no-guarantees", "Why UDP does not provide TCP-style delivery guarantees."),
      m06("m06.checksum", "UDP checksums."),
    ],
  },
  {
    id: "m06.use-cases",
    moduleId: "m06",
    slug: "udp-use-cases",
    title: "When UDP is the right tool",
    order: 2,
    summary:
      "Why DNS and real-time media accept loss, and how an application rebuilds reliability.",
    ownsTopics: [
      m06("m06.use-cases", "Appropriate use cases."),
      m06("m06.dns-and-media", "Why DNS, real-time media, and some other protocols use UDP."),
      m06("m06.app-reliability", "Application-level reliability over UDP."),
    ],
  },
  {
    id: "m06.quic-and-tradeoffs",
    moduleId: "m06",
    slug: "quic-and-tradeoffs",
    title: "QUIC, and TCP versus UDP",
    order: 3,
    summary: "How QUIC sits on UDP, and the trade that chooses TCP or UDP.",
    ownsTopics: [
      m06("m06.quic", "QUIC and its relationship to UDP."),
      m06("m06.tcp-vs-udp", "TCP vs UDP trade-offs."),
    ],
  },
  {
    id: "m07.hierarchy",
    moduleId: "m07",
    slug: "hierarchy",
    title: "The DNS hierarchy",
    order: 1,
    summary: "Delegation from the root through a TLD to an authoritative server.",
    ownsTopics: [
      m07("m07.hierarchy", "DNS hierarchy and delegation."),
      m07("m07.servers", "Root, TLD, and authoritative name servers."),
    ],
  },
  {
    id: "m07.resolution",
    moduleId: "m07",
    slug: "resolution",
    title: "Resolving a name",
    order: 2,
    summary: "Stub resolvers, recursive resolvers, and a full lookup from a name to an address.",
    ownsTopics: [
      m07("m07.recursive-iterative", "Recursive and iterative resolution."),
      m07("m07.resolvers", "Stub resolvers and recursive resolvers."),
    ],
  },
  {
    id: "m07.records",
    moduleId: "m07",
    slug: "records",
    title: "DNS record types",
    order: 3,
    summary: "What A, AAAA, CNAME, NS, MX, and TXT records are for.",
    ownsTopics: [m07("m07.records", "A, AAAA, CNAME, NS, MX, and TXT records.")],
  },
  {
    id: "m07.caching",
    moduleId: "m07",
    slug: "caching",
    title: "Caching and the propagation myth",
    order: 4,
    summary: "TTL, negative caching, and what actually changes when a record is updated.",
    ownsTopics: [
      m07("m07.ttl", "TTL and caching."),
      m07("m07.negative-caching", "Negative caching."),
      m07("m07.propagation", "DNS propagation myths and actual caching behavior."),
    ],
  },
  {
    id: "m07.transport-and-failures",
    moduleId: "m07",
    slug: "transport-and-failures",
    title: "DNS transport and failures",
    order: 5,
    summary: "UDP, TCP, DNS over HTTPS, DNS over TLS, split-horizon DNS, and common failures.",
    ownsTopics: [
      m07("m07.udp-tcp", "DNS over UDP and TCP."),
      m07("m07.doh-dot", "DNS over HTTPS and DNS over TLS at a conceptual level."),
      m07("m07.split-horizon", "Split-horizon DNS."),
      m07("m07.failures", "Common DNS failure scenarios."),
    ],
  },
  {
    id: "m08.request-response",
    moduleId: "m08",
    slug: "request-response",
    title: "The HTTP request and response",
    order: 1,
    summary:
      "Methods, status codes, headers, bodies, cookies, and sessions as application semantics.",
    ownsTopics: [
      m08("m08.request-response", "HTTP request-response model."),
      m08("m08.methods-status", "HTTP methods and status codes."),
      m08("m08.headers-cookies", "Headers, bodies, cookies, and sessions."),
    ],
  },
  {
    id: "m08.http-versions",
    moduleId: "m08",
    slug: "http-versions",
    title: "HTTP/1.1, HTTP/2, and HTTP/3",
    order: 2,
    summary:
      "Persistent connections, multiplexing, QUIC, and the head-of-line blocking that remains.",
    ownsTopics: [
      m08("m08.persistent", "Persistent connections."),
      m08("m08.http11", "HTTP/1.1."),
      m08("m08.http2", "HTTP/2 and multiplexing."),
      m08("m08.http3", "HTTP/3 and QUIC."),
      m08("m08.on-transport", "How application protocols interact with TCP or QUIC."),
    ],
  },
  {
    id: "m08.reuse-and-cache",
    moduleId: "m08",
    slug: "reuse-and-caching",
    title: "Reuse, compression, and caching",
    order: 3,
    summary: "Connection pooling, compression, and conditional requests.",
    ownsTopics: [
      m08("m08.pooling", "Connection reuse and pooling."),
      m08("m08.compression", "Compression."),
      m08("m08.caching", "Caching and conditional requests."),
    ],
  },
  {
    id: "m08.intermediaries",
    moduleId: "m08",
    slug: "intermediaries",
    title: "Proxies, load balancers, and long-lived protocols",
    order: 4,
    summary: "Reverse proxies, load balancers, WebSockets, gRPC, and streams that stay open.",
    ownsTopics: [
      m08("m08.proxies", "Proxies and reverse proxies."),
      m08("m08.load-balancers", "Load balancers."),
      m08("m08.websockets", "WebSockets."),
      m08("m08.grpc", "gRPC and HTTP/2."),
      m08("m08.streaming", "Streaming and long-lived connections."),
    ],
  },
  {
    id: "m09.why-tls",
    moduleId: "m09",
    slug: "why-tls",
    title: "What TCP leaves exposed",
    order: 1,
    summary:
      "Eavesdropping, tampering, and impersonation, and the roles of symmetric and asymmetric keys.",
    ownsTopics: [
      m09("m09.why-encryption", "Why encryption is necessary."),
      m09(
        "m09.symmetric-asymmetric",
        "Symmetric vs asymmetric cryptography at a conceptual level.",
      ),
    ],
  },
  {
    id: "m09.certificates",
    moduleId: "m09",
    slug: "certificates",
    title: "Certificates and hostname checks",
    order: 2,
    summary: "What a certificate proves, how a chain is checked, and why the name must match.",
    ownsTopics: [
      m09("m09.certificates", "Certificates and certificate authorities."),
      m09("m09.public-key-auth", "Public-key authentication."),
      m09("m09.chain", "Certificate-chain validation."),
      m09("m09.hostname", "Hostname verification."),
    ],
  },
  {
    id: "m09.handshake",
    moduleId: "m09",
    slug: "handshake",
    title: "The TLS handshake",
    order: 3,
    summary: "How session keys are established in TLS 1.2, TLS 1.3, and QUIC.",
    ownsTopics: [
      m09("m09.handshake", "TLS handshake."),
      m09("m09.keys", "Key establishment and session keys."),
      m09("m09.tls12-vs-13", "TLS 1.2 vs TLS 1.3 at a conceptual level."),
      m09("m09.tcp-relationship", "Relationship between TCP and TLS."),
      m09("m09.quic-handshake", "TLS over TCP vs QUIC's integrated TLS 1.3 handshake."),
    ],
  },
  {
    id: "m09.limits",
    moduleId: "m09",
    slug: "limits",
    title: "What HTTPS does and does not protect",
    order: 4,
    summary: "Resumption, forward secrecy, and the failures a valid TLS session can still hide.",
    ownsTopics: [
      m09("m09.resumption", "Session resumption."),
      m09("m09.forward-secrecy", "Forward secrecy."),
      m09("m09.protects", "What HTTPS protects and what it does not protect."),
      m09("m09.failures", "Common TLS failures."),
    ],
  },
  {
    id: "m10.nat",
    moduleId: "m10",
    slug: "nat",
    title: "Network address translation",
    order: 1,
    summary:
      "SNAT, DNAT, port translation, the connection table, and why that table is not a firewall.",
    ownsTopics: [
      m10("m10.snat-dnat", "SNAT and DNAT."),
      m10("m10.pat", "Port address translation."),
      m10("m10.connection-tracking", "NAT tables and connection tracking."),
      m10("m10.nat-is-not-a-firewall", "Why NAT is not equivalent to a firewall."),
    ],
  },
  {
    id: "m10.firewalls",
    moduleId: "m10",
    slug: "firewalls",
    title: "Firewalls",
    order: 2,
    summary:
      "Stateful and stateless filtering, and the difference between inbound and outbound traffic.",
    ownsTopics: [
      m10("m10.stateful", "Stateful vs stateless firewalls."),
      m10("m10.direction", "Inbound vs outbound traffic."),
    ],
  },
  {
    id: "m10.load-balancing",
    moduleId: "m10",
    slug: "load-balancing",
    title: "Load balancing",
    order: 3,
    summary: "Layer 4 and layer 7 distribution, health checks, persistence, and reverse proxies.",
    ownsTopics: [
      m10("m10.l4-l7", "Layer 4 vs Layer 7 load balancing."),
      m10("m10.health-checks", "Health checks."),
      m10("m10.persistence", "Connection persistence."),
      m10("m10.reverse-proxy", "Reverse proxies."),
    ],
  },
  {
    id: "m10.connectivity-failures",
    moduleId: "m10",
    slug: "connectivity-failures",
    title: "Connectivity failures at the edge",
    order: 4,
    summary:
      "Failures that come from translation, filtering, or a backend the load balancer has given up on.",
    ownsTopics: [m10("m10.failures", "Common causes of connectivity failures.")],
  },
  {
    id: "m11.metrics",
    moduleId: "m11",
    slug: "metrics",
    title: "Latency, bandwidth, and jitter",
    order: 1,
    summary:
      "Four measurements that are easy to mix up, and how to separate application time from network time.",
    ownsTopics: [
      m11("m11.metrics", "Latency, bandwidth, throughput, and jitter."),
      m11("m11.rtt", "Round-trip time."),
      m11("m11.app-vs-network", "Distinguishing application latency from network latency."),
    ],
  },
  {
    id: "m11.bdp-and-loss",
    moduleId: "m11",
    slug: "bandwidth-delay-and-loss",
    title: "Bandwidth-delay product, queues, and loss",
    order: 2,
    summary:
      "Why a large pipe can still move little data, and how queues and retransmission add delay.",
    ownsTopics: [
      m11("m11.bdp", "Bandwidth-delay product."),
      m11("m11.queues", "Queueing and congestion."),
      m11("m11.loss", "Packet loss."),
      m11("m11.retransmission", "Retransmissions."),
    ],
  },
  {
    id: "m11.timeouts-and-retries",
    moduleId: "m11",
    slug: "timeouts-and-retries",
    title: "Timeouts, retries, and tail latency",
    order: 3,
    summary:
      "Why a reliable protocol still needs a deadline, and why the average can hide the tail.",
    ownsTopics: [
      m11("m11.timeouts", "Timeouts and deadlines."),
      m11("m11.retries", "Retries and exponential backoff."),
      m11("m11.tail", "Tail latency."),
    ],
  },
  {
    id: "m11.partial-failure",
    moduleId: "m11",
    slug: "partial-failure",
    title: "Reuse, backpressure, and partial failure",
    order: 4,
    summary: "Pooling, keepalives, partitions, and symptoms that come and go.",
    ownsTopics: [
      m11("m11.reuse", "TCP connection reuse."),
      m11("m11.keepalive", "Keepalive mechanisms."),
      m11("m11.pooling", "Connection pooling."),
      m11("m11.backpressure", "Backpressure."),
      m11("m11.partitions", "Network partitions."),
      m11("m11.partial-failures", "Partial failures."),
    ],
  },
  {
    id: "m12.lifecycle",
    moduleId: "m12",
    slug: "socket-lifecycle",
    title: "The socket lifecycle",
    order: 1,
    summary: "The socket calls, ports, file descriptors, and TCP sockets versus UDP sockets.",
    ownsTopics: [
      m12("m12.lifecycle", "Socket lifecycle."),
      m12(
        "m12.calls",
        "`socket()`, `bind()`, `listen()`, `accept()`, `connect()`, `send()`, and `recv()` at a conceptual level.",
      ),
      m12("m12.ports-fds", "Ports and file descriptors."),
      m12("m12.tcp-vs-udp-sockets", "TCP sockets vs UDP sockets."),
    ],
  },
  {
    id: "m12.multiplexing",
    moduleId: "m12",
    slug: "multiplexing",
    title: "Blocking, non-blocking, and multiplexing",
    order: 2,
    summary:
      "What a blocking call waits for, and how select, poll, and epoll wait on many sockets.",
    ownsTopics: [
      m12("m12.blocking", "Blocking vs non-blocking sockets."),
      m12("m12.select-poll-epoll", "`select`, `poll`, and `epoll`."),
      m12("m12.multiplexing", "I/O multiplexing."),
    ],
  },
  {
    id: "m12.kernel-buffers",
    moduleId: "m12",
    slug: "kernel-buffers",
    title: "Kernel buffers, backlog, and TIME_WAIT",
    order: 3,
    summary: "What read and write touch in the kernel, the accept queue, and socket exhaustion.",
    ownsTopics: [
      m12("m12.read-write", "What happens when a process reads from or writes to a socket."),
      m12("m12.buffers", "Kernel buffers."),
      m12("m12.backlog", "Connection backlog."),
      m12("m12.time-wait-exhaustion", "TIME_WAIT and socket exhaustion."),
    ],
  },
  {
    id: "m12.tools",
    moduleId: "m12",
    slug: "linux-tools",
    title: "Linux diagnostic tools",
    order: 4,
    summary: "Which question ss, ip, ping, traceroute, dig, curl, and tcpdump can answer.",
    ownsTopics: [
      m12(
        "m12.tools",
        "Common Linux network diagnostic tools: `ss`, `ip`, `ping`, `traceroute`, `dig`, `curl`, and `tcpdump`.",
      ),
    ],
  },
  {
    id: "m13.method",
    moduleId: "m13",
    slug: "method",
    title: "How to investigate a network failure",
    order: 1,
    summary: "A fixed order for eliminating causes, used by every scenario in this module.",
    ownsTopics: [],
  },
  {
    id: "m13.dns-ok-tcp-fails",
    moduleId: "m13",
    slug: "dns-ok-tcp-fails",
    title: "DNS resolves, but TCP connection establishment fails",
    order: 2,
    summary: "The name works and the handshake does not.",
    ownsTopics: [
      scenario("m13.dns-ok-tcp-fails", "DNS resolves, but TCP connection establishment fails."),
    ],
  },
  {
    id: "m13.tcp-ok-tls-fails",
    moduleId: "m13",
    slug: "tcp-ok-tls-fails",
    title: "TCP connects, but TLS negotiation fails",
    order: 3,
    summary: "The handshake finishes and the TLS handshake does not.",
    ownsTopics: [scenario("m13.tcp-ok-tls-fails", "TCP connects, but TLS negotiation fails.")],
  },
  {
    id: "m13.tls-ok-http-5xx",
    moduleId: "m13",
    slug: "tls-ok-http-5xx",
    title: "TLS succeeds, but HTTP returns 502 or 503",
    order: 4,
    summary:
      "The secure channel is up and the application status is a gateway or availability error.",
    ownsTopics: [
      scenario("m13.tls-ok-http-5xx", "TLS succeeds, but HTTP requests return 502 or 503."),
    ],
  },
  {
    id: "m13.intermittent-timeouts",
    moduleId: "m13",
    slug: "intermittent-timeouts",
    title: "Requests intermittently time out",
    order: 5,
    summary: "Some attempts fail on the deadline and others succeed.",
    ownsTopics: [scenario("m13.intermittent-timeouts", "Requests intermittently time out.")],
  },
  {
    id: "m13.latency-cpu-low",
    moduleId: "m13",
    slug: "latency-cpu-low",
    title: "Latency rises while CPU stays low",
    order: 6,
    summary: "The service is slow and the process is not busy.",
    ownsTopics: [scenario("m13.latency-cpu-low", "Latency increases while CPU usage stays low.")],
  },
  {
    id: "m13.localhost-only",
    moduleId: "m13",
    slug: "localhost-only",
    title: "Works on localhost, fails from another machine",
    order: 7,
    summary: "The bind address, firewall, or path differs once the client is no longer local.",
    ownsTopics: [
      scenario("m13.localhost-only", "A service works on localhost but not from another machine."),
    ],
  },
  {
    id: "m13.accept-stall",
    moduleId: "m13",
    slug: "accept-stall",
    title: "The server stops accepting new connections",
    order: 8,
    summary: "Existing connections remain and new handshakes do not complete.",
    ownsTopics: [
      scenario(
        "m13.accept-stall",
        "A server has thousands of connections and stops accepting new ones.",
      ),
    ],
  },
  {
    id: "m13.ip-ok-name-fails",
    moduleId: "m13",
    slug: "ip-ok-name-fails",
    title: "An IP address works, but the hostname does not",
    order: 9,
    summary: "The host is reachable by address and the name lookup is not.",
    ownsTopics: [
      scenario("m13.ip-ok-name-fails", "A client can reach an IP address but not a hostname."),
    ],
  },
  {
    id: "m13.repeated-resets",
    moduleId: "m13",
    slug: "repeated-resets",
    title: "A connection is repeatedly reset",
    order: 10,
    summary: "RST keeps ending the connection, and the cause is not yet identified.",
    ownsTopics: [scenario("m13.repeated-resets", "A connection is repeatedly reset.")],
  },
  {
    id: "m13.large-response-hangs",
    moduleId: "m13",
    slug: "large-response-hangs",
    title: "Small requests work, but large responses hang",
    order: 11,
    summary: "A short response returns and a long one stalls.",
    ownsTopics: [
      scenario("m13.large-response-hangs", "Small requests work, but large responses hang."),
    ],
  },
  {
    id: "m13.periodic-spikes",
    moduleId: "m13",
    slug: "periodic-spikes",
    title: "Periodic latency spikes",
    order: 12,
    summary: "Delay returns on an interval instead of as a one-off.",
    ownsTopics: [scenario("m13.periodic-spikes", "A service experiences periodic latency spikes.")],
  },
  {
    id: "m13.service-to-service",
    moduleId: "m13",
    slug: "service-to-service",
    title: "One service cannot reach another",
    order: 13,
    summary: "Both processes look healthy on their own hosts and the path between them does not.",
    ownsTopics: [
      scenario(
        "m13.service-to-service",
        "One microservice cannot reach another despite both being healthy locally.",
      ),
    ],
  },
];

export const framingLessonIds = new Set(["m13.method"]);
