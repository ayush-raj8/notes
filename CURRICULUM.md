# Curriculum

This inventory is generated from `src/content`. Edit the catalog, then run `pnpm curriculum:doc`.

Lesson ids and question ids are stable. A later session fills a lesson body. It does not renumber the syllabus.

Content status in the app is separate from this list. A lesson listed here may still be unwritten.

## Module 1: Networking Foundations

Slug: `foundations`. Id: `m01`.

What a network has to accomplish, and why the work is split across layers.

### Lessons

- `m01.what-a-network-must-do` — What a network must do
  - Path: `/cn/learn/foundations/what-a-network-must-do`
  - Owns `m01.network-architecture`: Network architecture and protocol layering.
- `m01.layering` — Why networks are layered
  - Path: `/cn/learn/foundations/layering`
  - Owns `m01.protocol-layering`: Network architecture and protocol layering.
  - Owns `m01.osi-tcpip`: OSI model and TCP/IP model.
- `m01.encapsulation` — Encapsulation and the MTU
  - Path: `/cn/learn/foundations/encapsulation`
  - Owns `m01.encapsulation`: Encapsulation and decapsulation.
  - Owns `m01.pdu-names`: Frames, packets, segments, and application data.
  - Owns `m01.mtu-payload`: MTU and payload sizes.
- `m01.planes-and-connections` — Planes and connections
  - Path: `/cn/learn/foundations/planes-and-connections`
  - Owns `m01.control-vs-data-plane`: Control plane vs data plane.
  - Owns `m01.connection-vs-connectionless`: Connection-oriented vs connectionless communication.

### Syllabus bullets

- Network architecture and protocol layering.
- OSI model and TCP/IP model.
- Encapsulation and decapsulation.
- Frames, packets, segments, and application data.
- MTU and payload sizes.
- Control plane vs data plane.
- Connection-oriented vs connectionless communication.

## Module 2: Ethernet, Switching and Address Resolution

Slug: `ethernet`. Id: `m02`.

How a frame crosses a local link, and how an IP address becomes a MAC address.

### Lessons

- `m02.ethernet-and-mac` — Ethernet frames and MAC addresses
  - Path: `/cn/learn/ethernet/ethernet-and-mac`
  - Owns `m02.ethernet-mac`: Ethernet frames and MAC addresses.
- `m02.switching` — Switches, domains, and VLANs
  - Path: `/cn/learn/ethernet/switching`
  - Owns `m02.switch-learning`: Switch learning and forwarding.
  - Owns `m02.domains`: Broadcast domains and collision domains.
  - Owns `m02.vlans`: VLANs and their purpose.
- `m02.arp` — ARP and the ARP cache
  - Path: `/cn/learn/ethernet/arp`
  - Owns `m02.arp`: ARP and ARP caching.
- `m02.same-subnet` — Delivery on the same subnet
  - Path: `/cn/learn/ethernet/same-subnet`
  - Owns `m02.same-subnet`: What happens when a host communicates with another host on the same subnet.
- `m02.ipv6-nd` — IPv6 Neighbor Discovery
  - Path: `/cn/learn/ethernet/ipv6-neighbor-discovery`
  - Owns `m02.ipv6-nd`: IPv6 Neighbor Discovery.

### Syllabus bullets

- Ethernet frames and MAC addresses.
- Switch learning and forwarding.
- Broadcast domains and collision domains.
- ARP and ARP caching.
- VLANs and their purpose.
- IPv6 Neighbor Discovery.
- What happens when a host communicates with another host on the same subnet.

## Module 3: IP Addressing and Subnetting

Slug: `addressing`. Id: `m03`.

How addresses, prefixes, and the hop limit constrain where a packet can go.

### Lessons

- `m03.ipv4-cidr` — IPv4 prefixes and subnetting
  - Path: `/cn/learn/addressing/ipv4-and-cidr`
  - Owns `m03.ipv4-cidr`: IPv4 addressing and CIDR.
  - Owns `m03.masks`: Subnet masks and network prefixes.
  - Owns `m03.same-subnet`: Determining whether two addresses belong to the same subnet.
- `m03.special-addresses` — Public, private, and special-purpose addresses
  - Path: `/cn/learn/addressing/special-addresses`
  - Owns `m03.public-private`: Public vs private addresses.
  - Owns `m03.special-purpose`: Loopback, link-local, and special-purpose addresses.
- `m03.gateway-and-lpm` — Default gateways and longest-prefix matching
  - Path: `/cn/learn/addressing/gateway-and-longest-prefix`
  - Owns `m03.default-gateway`: Default gateways.
  - Owns `m03.routing-table-lpm`: Routing tables and longest-prefix matching.
- `m03.ipv6` — IPv4 and IPv6 addressing
  - Path: `/cn/learn/addressing/ipv6-addressing`
  - Owns `m03.ipv4-vs-ipv6`: IPv4 vs IPv6.
- `m03.ttl-and-fragmentation` — Hop limits, fragmentation, and Path MTU Discovery
  - Path: `/cn/learn/addressing/ttl-and-fragmentation`
  - Owns `m03.ttl`: TTL and hop limits.
  - Owns `m03.fragmentation`: IP fragmentation and Path MTU Discovery.

### Syllabus bullets

- IPv4 addressing and CIDR.
- Subnet masks and network prefixes.
- Determining whether two addresses belong to the same subnet.
- Public vs private addresses.
- Loopback, link-local, and special-purpose addresses.
- Default gateways.
- IPv4 vs IPv6.
- Routing tables and longest-prefix matching.
- TTL and hop limits.
- IP fragmentation and Path MTU Discovery.

## Module 4: Routing

Slug: `routing`. Id: `m04`.

How a forwarding table is built, and what a router does with it.

### Lessons

- `m04.routing-vs-forwarding` — Routing versus forwarding
  - Path: `/cn/learn/routing/routing-vs-forwarding`
  - Owns `m04.routing-vs-forwarding`: Routing vs forwarding.
  - Owns `m04.routing-tables`: Routing tables.
- `m04.algorithms` — How routing tables get built
  - Path: `/cn/learn/routing/routing-algorithms`
  - Owns `m04.static-vs-dynamic`: Static vs dynamic routing.
  - Owns `m04.dv-vs-ls`: Distance-vector vs link-state routing.
  - Owns `m04.loops-convergence`: Routing loops and convergence.
- `m04.ospf-bgp` — OSPF and BGP
  - Path: `/cn/learn/routing/ospf-and-bgp`
  - Owns `m04.ospf`: High-level operation of OSPF and BGP.
  - Owns `m04.bgp`: High-level operation of OSPF and BGP.
  - Owns `m04.lpm`: Longest-prefix matching.
- `m04.icmp` — ICMP, ping, traceroute, and unreachability
  - Path: `/cn/learn/routing/icmp`
  - Owns `m04.icmp`: ICMP, ping, and traceroute.
  - Owns `m04.unreachable`: What happens when a destination is unreachable.

### Syllabus bullets

- Routing vs forwarding.
- Static vs dynamic routing.
- Distance-vector vs link-state routing.
- High-level operation of OSPF and BGP.
- Routing tables.
- Longest-prefix matching.
- Routing loops and convergence.
- ICMP, ping, and traceroute.
- What happens when a destination is unreachable.

## Module 5: TCP in Depth

Slug: `tcp`. Id: `m05`.

How two endpoints create shared state and turn an unreliable network into a byte stream.

### Lessons

- `m05.service-model` — What TCP promises
  - Path: `/cn/learn/tcp/service-model`
  - Owns `m05.service-model`: TCP's service model.
  - Owns `m05.byte-stream`: Reliable, ordered byte-stream delivery.
- `m05.header-and-numbers` — Sequence numbers and the TCP header
  - Path: `/cn/learn/tcp/sequence-numbers`
  - Owns `m05.headers`: TCP headers and important fields.
  - Owns `m05.seq-ack`: Sequence numbers and acknowledgment numbers.
  - Owns `m05.isn`: Initial sequence numbers.
- `m05.handshake` — Establishing a connection
  - Path: `/cn/learn/tcp/handshake`
  - Owns `m05.three-way-handshake`: The three-way handshake.
  - Owns `m05.syn-synack-ack`: SYN, SYN-ACK, ACK, FIN, and RST.
  - Owns `m05.connection-establishment`: Connection establishment.
  - Owns `m05.simultaneous-open`: Simultaneous open and simultaneous close at a conceptual level.
- `m05.reliability` — Loss, duplication, delay, and reorder
  - Path: `/cn/learn/tcp/reliability`
  - Owns `m05.timeout-retransmission`: Retransmission timers and timeout-based retransmission.
  - Owns `m05.fast-retransmit`: Duplicate acknowledgments and fast retransmission.
  - Owns `m05.sack`: Cumulative acknowledgments and selective acknowledgments.
  - Owns `m05.rto-estimation`: Retransmission timeout estimation.
  - Owns `m05.loss-reorder`: What happens when packets are lost, duplicated, delayed, or reordered.
- `m05.flow-control` — Flow control and the receiver window
  - Path: `/cn/learn/tcp/flow-control`
  - Owns `m05.sliding-window`: Sliding-window protocols.
  - Owns `m05.receiver-window`: Receiver window and advertised window.
  - Owns `m05.flow-control`: Flow control.
  - Owns `m05.zero-window`: Zero-window behavior.
- `m05.congestion` — Congestion control
  - Path: `/cn/learn/tcp/congestion-control`
  - Owns `m05.congestion-control`: Congestion control.
  - Owns `m05.slow-start`: Slow start, congestion avoidance, and fast recovery at a conceptual level.
- `m05.interactions` — Delayed ACKs, Nagle, and high latency
  - Path: `/cn/learn/tcp/tcp-interactions`
  - Owns `m05.delayed-ack-nagle`: Delayed ACKs and Nagle's algorithm.
  - Owns `m05.keepalive`: TCP keepalive.
  - Owns `m05.high-latency`: TCP performance over high-latency networks.
  - Owns `m05.byte-stream-hol`: Head-of-line blocking within a TCP byte stream.
- `m05.termination` — Ending a connection
  - Path: `/cn/learn/tcp/termination`
  - Owns `m05.fin-rst`: SYN, SYN-ACK, ACK, FIN, and RST.
  - Owns `m05.half-open`: Half-open connections.
  - Owns `m05.half-close`: Half-close behavior.
  - Owns `m05.time-wait`: TIME_WAIT and its purpose.
  - Owns `m05.simultaneous-close`: Simultaneous open and simultaneous close at a conceptual level.
  - Owns `m05.resets`: Connection resets.
  - Owns `m05.endpoint-crash`: What happens when a client or server crashes.

### Syllabus bullets

- TCP's service model.
- TCP headers and important fields.
- Sequence numbers and acknowledgment numbers.
- The three-way handshake.
- SYN, SYN-ACK, ACK, FIN, and RST.
- Initial sequence numbers.
- Connection establishment.
- Reliable, ordered byte-stream delivery.
- Retransmission timers and timeout-based retransmission.
- Duplicate acknowledgments and fast retransmission.
- Cumulative acknowledgments and selective acknowledgments.
- Sliding-window protocols.
- Receiver window and advertised window.
- Flow control.
- Congestion control.
- Slow start, congestion avoidance, and fast recovery at a conceptual level.
- Retransmission timeout estimation.
- Delayed ACKs and Nagle's algorithm.
- TCP keepalive.
- Half-open connections.
- Half-close behavior.
- TIME_WAIT and its purpose.
- Simultaneous open and simultaneous close at a conceptual level.
- Connection resets.
- Zero-window behavior.
- TCP performance over high-latency networks.
- Head-of-line blocking within a TCP byte stream.
- What happens when a client or server crashes.
- What happens when packets are lost, duplicated, delayed, or reordered.

## Module 6: UDP and Transport-Layer Alternatives

Slug: `udp`. Id: `m06`.

What UDP leaves to the application, and how QUIC uses that choice.

### Lessons

- `m06.udp-service` — What UDP provides
  - Path: `/cn/learn/udp/udp-service`
  - Owns `m06.header`: UDP header and service model.
  - Owns `m06.no-guarantees`: Why UDP does not provide TCP-style delivery guarantees.
  - Owns `m06.checksum`: UDP checksums.
- `m06.use-cases` — When UDP is the right tool
  - Path: `/cn/learn/udp/udp-use-cases`
  - Owns `m06.use-cases`: Appropriate use cases.
  - Owns `m06.dns-and-media`: Why DNS, real-time media, and some other protocols use UDP.
  - Owns `m06.app-reliability`: Application-level reliability over UDP.
- `m06.quic-and-tradeoffs` — QUIC, and TCP versus UDP
  - Path: `/cn/learn/udp/quic-and-tradeoffs`
  - Owns `m06.quic`: QUIC and its relationship to UDP.
  - Owns `m06.tcp-vs-udp`: TCP vs UDP trade-offs.

### Syllabus bullets

- UDP header and service model.
- Why UDP does not provide TCP-style delivery guarantees.
- UDP checksums.
- Appropriate use cases.
- Why DNS, real-time media, and some other protocols use UDP.
- Application-level reliability over UDP.
- QUIC and its relationship to UDP.
- TCP vs UDP trade-offs.

## Module 7: DNS

Slug: `dns`. Id: `m07`.

How a name becomes an address, and what caches actually remember.

### Lessons

- `m07.hierarchy` — The DNS hierarchy
  - Path: `/cn/learn/dns/hierarchy`
  - Owns `m07.hierarchy`: DNS hierarchy and delegation.
  - Owns `m07.servers`: Root, TLD, and authoritative name servers.
- `m07.resolution` — Resolving a name
  - Path: `/cn/learn/dns/resolution`
  - Owns `m07.recursive-iterative`: Recursive and iterative resolution.
  - Owns `m07.resolvers`: Stub resolvers and recursive resolvers.
- `m07.records` — DNS record types
  - Path: `/cn/learn/dns/records`
  - Owns `m07.records`: A, AAAA, CNAME, NS, MX, and TXT records.
- `m07.caching` — Caching and the propagation myth
  - Path: `/cn/learn/dns/caching`
  - Owns `m07.ttl`: TTL and caching.
  - Owns `m07.negative-caching`: Negative caching.
  - Owns `m07.propagation`: DNS propagation myths and actual caching behavior.
- `m07.transport-and-failures` — DNS transport and failures
  - Path: `/cn/learn/dns/transport-and-failures`
  - Owns `m07.udp-tcp`: DNS over UDP and TCP.
  - Owns `m07.doh-dot`: DNS over HTTPS and DNS over TLS at a conceptual level.
  - Owns `m07.split-horizon`: Split-horizon DNS.
  - Owns `m07.failures`: Common DNS failure scenarios.

### Syllabus bullets

- DNS hierarchy and delegation.
- Root, TLD, and authoritative name servers.
- Recursive and iterative resolution.
- Stub resolvers and recursive resolvers.
- A, AAAA, CNAME, NS, MX, and TXT records.
- TTL and caching.
- Negative caching.
- DNS over UDP and TCP.
- DNS over HTTPS and DNS over TLS at a conceptual level.
- DNS propagation myths and actual caching behavior.
- Split-horizon DNS.
- Common DNS failure scenarios.

## Module 8: HTTP and Application-Layer Networking

Slug: `http`. Id: `m08`.

Why an application protocol sits on a reliable stream, and what changed across HTTP versions.

### Lessons

- `m08.request-response` — The HTTP request and response
  - Path: `/cn/learn/http/request-response`
  - Owns `m08.request-response`: HTTP request-response model.
  - Owns `m08.methods-status`: HTTP methods and status codes.
  - Owns `m08.headers-cookies`: Headers, bodies, cookies, and sessions.
- `m08.http-versions` — HTTP/1.1, HTTP/2, and HTTP/3
  - Path: `/cn/learn/http/http-versions`
  - Owns `m08.persistent`: Persistent connections.
  - Owns `m08.http11`: HTTP/1.1.
  - Owns `m08.http2`: HTTP/2 and multiplexing.
  - Owns `m08.http3`: HTTP/3 and QUIC.
  - Owns `m08.on-transport`: How application protocols interact with TCP or QUIC.
- `m08.reuse-and-cache` — Reuse, compression, and caching
  - Path: `/cn/learn/http/reuse-and-caching`
  - Owns `m08.pooling`: Connection reuse and pooling.
  - Owns `m08.compression`: Compression.
  - Owns `m08.caching`: Caching and conditional requests.
- `m08.intermediaries` — Proxies, load balancers, and long-lived protocols
  - Path: `/cn/learn/http/intermediaries`
  - Owns `m08.proxies`: Proxies and reverse proxies.
  - Owns `m08.load-balancers`: Load balancers.
  - Owns `m08.websockets`: WebSockets.
  - Owns `m08.grpc`: gRPC and HTTP/2.
  - Owns `m08.streaming`: Streaming and long-lived connections.

### Syllabus bullets

- HTTP request-response model.
- HTTP methods and status codes.
- Headers, bodies, cookies, and sessions.
- Persistent connections.
- HTTP/1.1.
- HTTP/2 and multiplexing.
- HTTP/3 and QUIC.
- Connection reuse and pooling.
- Compression.
- Caching and conditional requests.
- Proxies and reverse proxies.
- Load balancers.
- WebSockets.
- gRPC and HTTP/2.
- Streaming and long-lived connections.
- How application protocols interact with TCP or QUIC.

## Module 9: TLS and HTTPS

Slug: `tls`. Id: `m09`.

What a reliable byte stream does not protect, and how TLS authenticates a peer.

### Lessons

- `m09.why-tls` — What TCP leaves exposed
  - Path: `/cn/learn/tls/why-tls`
  - Owns `m09.why-encryption`: Why encryption is necessary.
  - Owns `m09.symmetric-asymmetric`: Symmetric vs asymmetric cryptography at a conceptual level.
- `m09.certificates` — Certificates and hostname checks
  - Path: `/cn/learn/tls/certificates`
  - Owns `m09.certificates`: Certificates and certificate authorities.
  - Owns `m09.public-key-auth`: Public-key authentication.
  - Owns `m09.chain`: Certificate-chain validation.
  - Owns `m09.hostname`: Hostname verification.
- `m09.handshake` — The TLS handshake
  - Path: `/cn/learn/tls/handshake`
  - Owns `m09.handshake`: TLS handshake.
  - Owns `m09.keys`: Key establishment and session keys.
  - Owns `m09.tls12-vs-13`: TLS 1.2 vs TLS 1.3 at a conceptual level.
  - Owns `m09.tcp-relationship`: Relationship between TCP and TLS.
  - Owns `m09.quic-handshake`: TLS over TCP vs QUIC's integrated TLS 1.3 handshake.
- `m09.limits` — What HTTPS does and does not protect
  - Path: `/cn/learn/tls/limits`
  - Owns `m09.resumption`: Session resumption.
  - Owns `m09.forward-secrecy`: Forward secrecy.
  - Owns `m09.protects`: What HTTPS protects and what it does not protect.
  - Owns `m09.failures`: Common TLS failures.

### Syllabus bullets

- Why encryption is necessary.
- Symmetric vs asymmetric cryptography at a conceptual level.
- Certificates and certificate authorities.
- Public-key authentication.
- TLS handshake.
- Key establishment and session keys.
- Certificate-chain validation.
- Hostname verification.
- TLS 1.2 vs TLS 1.3 at a conceptual level.
- Session resumption.
- Forward secrecy.
- What HTTPS protects and what it does not protect.
- Common TLS failures.
- Relationship between TCP and TLS.
- TLS over TCP vs QUIC's integrated TLS 1.3 handshake.

## Module 10: NAT, Firewalls and Load Balancing

Slug: `nat`. Id: `m10`.

How address translation, filtering, and load balancers rewrite the path.

### Lessons

- `m10.nat` — Network address translation
  - Path: `/cn/learn/nat/nat`
  - Owns `m10.snat-dnat`: SNAT and DNAT.
  - Owns `m10.pat`: Port address translation.
  - Owns `m10.connection-tracking`: NAT tables and connection tracking.
  - Owns `m10.nat-is-not-a-firewall`: Why NAT is not equivalent to a firewall.
- `m10.firewalls` — Firewalls
  - Path: `/cn/learn/nat/firewalls`
  - Owns `m10.stateful`: Stateful vs stateless firewalls.
  - Owns `m10.direction`: Inbound vs outbound traffic.
- `m10.load-balancing` — Load balancing
  - Path: `/cn/learn/nat/load-balancing`
  - Owns `m10.l4-l7`: Layer 4 vs Layer 7 load balancing.
  - Owns `m10.health-checks`: Health checks.
  - Owns `m10.persistence`: Connection persistence.
  - Owns `m10.reverse-proxy`: Reverse proxies.
- `m10.connectivity-failures` — Connectivity failures at the edge
  - Path: `/cn/learn/nat/connectivity-failures`
  - Owns `m10.failures`: Common causes of connectivity failures.

### Syllabus bullets

- SNAT and DNAT.
- Port address translation.
- NAT tables and connection tracking.
- Stateful vs stateless firewalls.
- Inbound vs outbound traffic.
- Layer 4 vs Layer 7 load balancing.
- Health checks.
- Connection persistence.
- Reverse proxies.
- Common causes of connectivity failures.
- Why NAT is not equivalent to a firewall.

## Module 11: Performance and Reliability

Slug: `performance`. Id: `m11`.

How latency, loss, and retries show up in a service you operate.

### Lessons

- `m11.metrics` — Latency, bandwidth, and jitter
  - Path: `/cn/learn/performance/metrics`
  - Owns `m11.metrics`: Latency, bandwidth, throughput, and jitter.
  - Owns `m11.rtt`: Round-trip time.
  - Owns `m11.app-vs-network`: Distinguishing application latency from network latency.
- `m11.bdp-and-loss` — Bandwidth-delay product, queues, and loss
  - Path: `/cn/learn/performance/bandwidth-delay-and-loss`
  - Owns `m11.bdp`: Bandwidth-delay product.
  - Owns `m11.queues`: Queueing and congestion.
  - Owns `m11.loss`: Packet loss.
  - Owns `m11.retransmission`: Retransmissions.
- `m11.timeouts-and-retries` — Timeouts, retries, and tail latency
  - Path: `/cn/learn/performance/timeouts-and-retries`
  - Owns `m11.timeouts`: Timeouts and deadlines.
  - Owns `m11.retries`: Retries and exponential backoff.
  - Owns `m11.tail`: Tail latency.
- `m11.partial-failure` — Reuse, backpressure, and partial failure
  - Path: `/cn/learn/performance/partial-failure`
  - Owns `m11.reuse`: TCP connection reuse.
  - Owns `m11.keepalive`: Keepalive mechanisms.
  - Owns `m11.pooling`: Connection pooling.
  - Owns `m11.backpressure`: Backpressure.
  - Owns `m11.partitions`: Network partitions.
  - Owns `m11.partial-failures`: Partial failures.

### Syllabus bullets

- Latency, bandwidth, throughput, and jitter.
- Round-trip time.
- Bandwidth-delay product.
- Queueing and congestion.
- Packet loss.
- Retransmissions.
- Timeouts and deadlines.
- TCP connection reuse.
- Keepalive mechanisms.
- Connection pooling.
- Retries and exponential backoff.
- Tail latency.
- Backpressure.
- Network partitions.
- Partial failures.
- Distinguishing application latency from network latency.

## Module 12: Sockets and Linux Networking

Slug: `sockets`. Id: `m12`.

What the socket API asks the kernel to do, and which tools observe it.

### Lessons

- `m12.lifecycle` — The socket lifecycle
  - Path: `/cn/learn/sockets/socket-lifecycle`
  - Owns `m12.lifecycle`: Socket lifecycle.
  - Owns `m12.calls`: `socket()`, `bind()`, `listen()`, `accept()`, `connect()`, `send()`, and `recv()` at a conceptual level.
  - Owns `m12.ports-fds`: Ports and file descriptors.
  - Owns `m12.tcp-vs-udp-sockets`: TCP sockets vs UDP sockets.
- `m12.multiplexing` — Blocking, non-blocking, and multiplexing
  - Path: `/cn/learn/sockets/multiplexing`
  - Owns `m12.blocking`: Blocking vs non-blocking sockets.
  - Owns `m12.select-poll-epoll`: `select`, `poll`, and `epoll`.
  - Owns `m12.multiplexing`: I/O multiplexing.
- `m12.kernel-buffers` — Kernel buffers, backlog, and TIME_WAIT
  - Path: `/cn/learn/sockets/kernel-buffers`
  - Owns `m12.read-write`: What happens when a process reads from or writes to a socket.
  - Owns `m12.buffers`: Kernel buffers.
  - Owns `m12.backlog`: Connection backlog.
  - Owns `m12.time-wait-exhaustion`: TIME_WAIT and socket exhaustion.
- `m12.tools` — Linux diagnostic tools
  - Path: `/cn/learn/sockets/linux-tools`
  - Owns `m12.tools`: Common Linux network diagnostic tools: `ss`, `ip`, `ping`, `traceroute`, `dig`, `curl`, and `tcpdump`.

### Syllabus bullets

- Socket lifecycle.
- `socket()`, `bind()`, `listen()`, `accept()`, `connect()`, `send()`, and `recv()` at a conceptual level.
- Ports and file descriptors.
- Blocking vs non-blocking sockets.
- `select`, `poll`, and `epoll`.
- I/O multiplexing.
- TCP sockets vs UDP sockets.
- What happens when a process reads from or writes to a socket.
- Kernel buffers.
- Connection backlog.
- TIME_WAIT and socket exhaustion.
- Common Linux network diagnostic tools: `ss`, `ip`, `ping`, `traceroute`, `dig`, `curl`, and `tcpdump`.

## Module 13: Practical Troubleshooting

Slug: `troubleshooting`. Id: `m13`.

A repeatable way to eliminate causes when a connection fails.

### Lessons

- `m13.method` — How to investigate a network failure
  - Path: `/cn/learn/troubleshooting/method`
  - Framing lesson for the scenarios below. It does not add a syllabus bullet.
- `m13.dns-ok-tcp-fails` — DNS resolves, but TCP connection establishment fails
  - Path: `/cn/learn/troubleshooting/dns-ok-tcp-fails`
  - Owns `m13.dns-ok-tcp-fails`: DNS resolves, but TCP connection establishment fails.
- `m13.tcp-ok-tls-fails` — TCP connects, but TLS negotiation fails
  - Path: `/cn/learn/troubleshooting/tcp-ok-tls-fails`
  - Owns `m13.tcp-ok-tls-fails`: TCP connects, but TLS negotiation fails.
- `m13.tls-ok-http-5xx` — TLS succeeds, but HTTP returns 502 or 503
  - Path: `/cn/learn/troubleshooting/tls-ok-http-5xx`
  - Owns `m13.tls-ok-http-5xx`: TLS succeeds, but HTTP requests return 502 or 503.
- `m13.intermittent-timeouts` — Requests intermittently time out
  - Path: `/cn/learn/troubleshooting/intermittent-timeouts`
  - Owns `m13.intermittent-timeouts`: Requests intermittently time out.
- `m13.latency-cpu-low` — Latency rises while CPU stays low
  - Path: `/cn/learn/troubleshooting/latency-cpu-low`
  - Owns `m13.latency-cpu-low`: Latency increases while CPU usage stays low.
- `m13.localhost-only` — Works on localhost, fails from another machine
  - Path: `/cn/learn/troubleshooting/localhost-only`
  - Owns `m13.localhost-only`: A service works on localhost but not from another machine.
- `m13.accept-stall` — The server stops accepting new connections
  - Path: `/cn/learn/troubleshooting/accept-stall`
  - Owns `m13.accept-stall`: A server has thousands of connections and stops accepting new ones.
- `m13.ip-ok-name-fails` — An IP address works, but the hostname does not
  - Path: `/cn/learn/troubleshooting/ip-ok-name-fails`
  - Owns `m13.ip-ok-name-fails`: A client can reach an IP address but not a hostname.
- `m13.repeated-resets` — A connection is repeatedly reset
  - Path: `/cn/learn/troubleshooting/repeated-resets`
  - Owns `m13.repeated-resets`: A connection is repeatedly reset.
- `m13.large-response-hangs` — Small requests work, but large responses hang
  - Path: `/cn/learn/troubleshooting/large-response-hangs`
  - Owns `m13.large-response-hangs`: Small requests work, but large responses hang.
- `m13.periodic-spikes` — Periodic latency spikes
  - Path: `/cn/learn/troubleshooting/periodic-spikes`
  - Owns `m13.periodic-spikes`: A service experiences periodic latency spikes.
- `m13.service-to-service` — One service cannot reach another
  - Path: `/cn/learn/troubleshooting/service-to-service`
  - Owns `m13.service-to-service`: One microservice cannot reach another despite both being healthy locally.

### Syllabus bullets

- DNS resolves, but TCP connection establishment fails.
- TCP connects, but TLS negotiation fails.
- TLS succeeds, but HTTP requests return 502 or 503.
- Requests intermittently time out.
- Latency increases while CPU usage stays low.
- A service works on localhost but not from another machine.
- A server has thousands of connections and stops accepting new ones.
- A client can reach an IP address but not a hostname.
- A connection is repeatedly reset.
- Small requests work, but large responses hang.
- A service experiences periodic latency spikes.
- One microservice cannot reach another despite both being healthy locally.

## Why Does Networking Work This Way?

Core questions `why.01` through `why.50` are the required collection. `why.51` through `why.55` are the approved additions.

### Protocol layering and architecture

- `why.01` — If TCP already exists, why do we need HTTP?
  - Primary lesson: `m08.request-response`
- `why.02` — If IP can deliver packets, why do we need TCP or UDP?
  - Primary lesson: `m01.layering`
- `why.03` — Why is TCP/IP divided into layers instead of using one protocol for everything?
  - Primary lesson: `m01.layering`
- `why.04` — Why can't routers simply deliver application messages directly?
  - Primary lesson: `m01.layering`
- `why.05` — Why do Ethernet frames contain MAC addresses if IP packets already contain IP addresses?
  - Primary lesson: `m02.ethernet-and-mac`
- `why.06` — Why does the destination MAC address change at each routed hop while the destination IP address usually remains the same?
  - Primary lesson: `m02.same-subnet`
- `why.07` — Why do we need both ports and IP addresses?
  - Primary lesson: `m01.layering`
- `why.08` — Why does TCP provide a byte stream rather than preserving application message boundaries?
  - Primary lesson: `m05.service-model`
- `why.09` — Why doesn't TCP guarantee that the remote application has processed a request?
  - Primary lesson: `m05.service-model`
- `why.10` — Why doesn't TCP automatically make application operations safe to retry?
  - Primary lesson: `m05.service-model`

### TCP and transport

- `why.11` — Why does TCP use a three-way handshake rather than a two-way handshake?
  - Primary lesson: `m05.handshake`
- `why.12` — Why does TCP need sequence numbers if packets are already sent in order?
  - Primary lesson: `m05.header-and-numbers`
- `why.13` — Why do we need acknowledgments if TCP can retransmit packets?
  - Primary lesson: `m05.header-and-numbers`
- `why.14` — Why are flow control and congestion control separate mechanisms?
  - Primary lesson: `m05.congestion`
- `why.15` — Why can a TCP connection stall even when neither endpoint has crashed?
  - Primary lesson: `m05.congestion`
- `why.16` — Why does TCP need TIME_WAIT?
  - Primary lesson: `m05.termination`
- `why.17` — Why can a lost TCP segment delay later data even if that later data has already arrived?
  - Primary lesson: `m05.reliability`
- `why.18` — Why might UDP be preferable to TCP for real-time communication?
  - Primary lesson: `m06.use-cases`
- `why.19` — Why doesn't TCP itself provide encryption?
  - Primary lesson: `m09.why-tls`
- `why.20` — Why can TCP connection establishment succeed while an application request still fails?
  - Primary lesson: `m05.service-model`
- `why.51` (added) — Why do endpoints, not routers, implement TCP reliability and congestion control?
  - Primary lesson: `m05.service-model`

### DNS, HTTP and TLS

- `why.21` — Why do we need DNS if IP addresses uniquely identify network interfaces?
  - Primary lesson: `m07.hierarchy`
- `why.22` — Why does DNS use caching, and what trade-offs does caching introduce?
  - Primary lesson: `m07.caching`
- `why.23` — Why do we need HTTP if TCP already transfers data reliably?
  - Primary lesson: `m08.request-response`
- `why.24` — Why isn't sending arbitrary text over TCP enough to build a scalable web application?
  - Primary lesson: `m08.request-response`
- `why.25` — Why does HTTPS need TLS if TCP already provides reliable communication?
  - Primary lesson: `m09.why-tls`
- `why.26` — Why is a server certificate necessary? What problem does encryption alone fail to solve?
  - Primary lesson: `m09.certificates`
- `why.27` — Why does HTTP/2 use multiplexing, and what problem does it solve?
  - Primary lesson: `m08.http-versions`
- `why.28` — Why can HTTP/2 still experience head-of-line blocking?
  - Primary lesson: `m08.http-versions`
- `why.29` — Why does HTTP/3 use QUIC over UDP rather than simply using TCP?
  - Primary lesson: `m08.http-versions`
- `why.30` — Why do persistent connections and connection pooling improve performance?
  - Primary lesson: `m08.reuse-and-cache`
- `why.31` — Why can a valid TLS connection still communicate with the wrong server if hostname verification is omitted?
  - Primary lesson: `m09.certificates`
- `why.54` (added) — Why does gRPC use HTTP/2 (or HTTP/3) rather than a raw TCP byte stream?
  - Primary lesson: `m08.intermediaries`

### Routing and addressing

- `why.32` — Why do we need ARP if every device already has an IP address?
  - Primary lesson: `m02.arp`
- `why.33` — Why does a host send traffic to its default gateway instead of directly to a remote server's MAC address?
  - Primary lesson: `m03.gateway-and-lpm`
- `why.34` — Why do routers use routing tables rather than broadcasting packets everywhere?
  - Primary lesson: `m04.routing-vs-forwarding`
- `why.35` — Why is longest-prefix matching necessary?
  - Primary lesson: `m03.gateway-and-lpm`
- `why.36` — Why do we need both routing protocols and forwarding tables?
  - Primary lesson: `m04.routing-vs-forwarding`
- `why.37` — Why does NAT exist, and what problems does it introduce?
  - Primary lesson: `m10.nat`
- `why.38` — Why isn't NAT itself a security mechanism?
  - Primary lesson: `m10.nat`
- `why.39` — Why does MTU matter if IP can fragment packets?
  - Primary lesson: `m03.ttl-and-fragmentation`
- `why.40` — Why can a large response hang while small responses succeed?
  - Primary lesson: `m11.partial-failure`
- `why.52` (added) — Why can traceroute show a path that is not the path your TCP connection used?
  - Primary lesson: `m04.icmp`

### Performance and reliability

- `why.41` — Why can a low-bandwidth-delay product limit throughput even on a high-bandwidth network?
  - Primary lesson: `m11.bdp-and-loss`
- `why.42` — Why can retransmissions increase latency?
  - Primary lesson: `m11.bdp-and-loss`
- `why.43` — Why do retries sometimes make an overloaded service worse?
  - Primary lesson: `m11.timeouts-and-retries`
- `why.44` — Why are timeouts necessary even when a protocol provides reliability?
  - Primary lesson: `m11.timeouts-and-retries`
- `why.45` — Why can a service be healthy locally but unreachable remotely?
  - Primary lesson: `m13.localhost-only`
- `why.46` — Why can average latency look healthy while users experience severe delays?
  - Primary lesson: `m11.timeouts-and-retries`
- `why.47` — Why can opening a new connection for every request be expensive?
  - Primary lesson: `m11.partial-failure`
- `why.48` — Why can a successful TCP write fail to imply successful business-level processing?
  - Primary lesson: `m11.partial-failure`
- `why.49` — Why do network failures often appear intermittent rather than consistently reproducible?
  - Primary lesson: `m11.partial-failure`
- `why.50` — Why is debugging network failures a process of eliminating hypotheses rather than checking one component?
  - Primary lesson: `m13.method`
- `why.53` (added) — Why does a load balancer's health check passing not prove the user request will succeed?
  - Primary lesson: `m10.load-balancing`
- `why.55` (added) — Why can two healthy processes still fail to communicate across a NAT or a security-group boundary?
  - Primary lesson: `m13.service-to-service`
