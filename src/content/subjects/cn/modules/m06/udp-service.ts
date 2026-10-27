import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m06.udp-service",
  status: "complete",
  objectives: [
    "Read the 8-byte UDP header and say what each field is for.",
    "State UDP's service exactly: ports, a length, a checksum, and one datagram per receive. Nothing else.",
    "Explain what the UDP checksum covers, when it may be omitted, and why it is not an integrity guarantee.",
    "Predict what a Linux UDP socket does on loss, a full receive buffer, a closed port, and an oversized datagram, and where each shows up.",
  ],
  prerequisites: ["m05.termination"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "IP gets a datagram to a host, best effort, and names no program on that host. Some applications want little more than that: a way to reach the right process and to notice a damaged packet, without a handshake, without an ordered stream, and without retransmission timers they do not control.",
      whyItMatters:
        "Choosing UDP moves every delivery problem TCP would have handled into your code. Knowing exactly what UDP adds tells you what you now own: loss, duplicates, reordering, pacing, message size, and noticing that the peer is gone. Most UDP bugs in production are one of those six left unhandled.",
      idea: "UDP is almost a null protocol. Over IP it adds only port numbers, so the kernel can hand each datagram to the right socket, and a checksum, so a damaged datagram is dropped instead of delivered. Each send is one datagram, and each receive returns one whole datagram. Every other transport job is left to the application.",
      howItWorks:
        "The header is four 16-bit fields: source port, destination port, length, and checksum. On send, sendto() turns one buffer into one datagram, the kernel prepends the header and hands it to IP, and nothing about the peer is remembered. On receive, the kernel finds the socket bound to the destination address and port and queues the whole datagram in that socket's receive buffer. Each recvfrom() returns exactly one datagram and the sender's address. A datagram with a bad checksum is discarded silently. If no socket owns the port, the host normally answers with an ICMP Port Unreachable. If the socket's receive buffer is full, the datagram is dropped and only a counter records it.",
      whyItWorks:
        "Because each datagram stands alone, there is nothing to set up, tear down, or remember. The first datagram can carry the request, one server socket can answer any number of clients, and a lost datagram delays no other datagram. Including the IP addresses in the checksum's pseudo-header lets the receiver reject a datagram that arrived at the wrong host, even though UDP itself never carries an address.",
      limits:
        "UDP does not retransmit, order, suppress duplicates, control flow, or control congestion, and it cannot tell you the peer is alive. Its checksum is a 16-bit one's complement sum: it catches common corruption, not every error and not an attacker. A datagram must fit in one IP packet, so a large one is fragmented or refused, with the problems Module 3 described. Network errors such as Port Unreachable arrive asynchronously, and Linux hands them to the socket only in limited cases.",
      connections:
        "Module 1 placed UDP above IP and answered why we need a transport at all (why.02). Module 3's path MTU and fragmentation apply to UDP directly, with no MSS to hide them. Module 5 listed everything TCP adds, and this lesson is that list's absence. The next lesson asks when that trade is the right one, and the last shows QUIC rebuilding a transport on top of UDP. Module 7 uses UDP for DNS, and Module 12 returns to UDP sockets and kernel buffers.",
    },
    {
      type: "comparison",
      id: "header",
      title: "The UDP header",
      caption:
        "Eight bytes, then the data. RFC 768 has not changed since 1980, and RFC 1122 says it has no known errors.",
      columns: ["Size", "What it does"],
      rows: [
        {
          label: "Source port",
          cells: [
            "16 bits",
            "The sending socket's port, and the port a reply should go to. Optional: zero means not used, which is rare in practice because a reply then has nowhere to go.",
          ],
        },
        {
          label: "Destination port",
          cells: [
            "16 bits",
            "Picks the receiving socket on the destination host. With the destination IP address it is all the kernel uses to deliver an unconnected datagram.",
          ],
        },
        {
          label: "Length",
          cells: [
            "16 bits",
            "Header plus data, in bytes, so at least 8. It is the message boundary: the receiver returns exactly this many data bytes in one read.",
          ],
        },
        {
          label: "Checksum",
          cells: [
            "16 bits",
            "One's complement sum over a pseudo-header (source and destination IP, protocol 17, UDP length), the UDP header, and the data. Zero means the sender computed none.",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "what-udp-leaves-out",
      title: "What UDP leaves out, and what you see instead",
      steps: [
        {
          title: "A datagram is lost",
          body: "Nothing happens. The sender is not told and the receiver never knew it existed. If the application needs it, the application must notice the silence, with a timeout, and send again.",
        },
        {
          title: "A datagram is duplicated or delayed",
          body: "Both copies are delivered, in whatever order they arrive. RFC 8085 tells applications to tolerate duplicates and reordering that arrive up to two minutes late, borrowing TCP's maximum segment lifetime, because nothing below them bounds it.",
        },
        {
          title: "The receiver is slower than the sender",
          body: "There is no window. Datagrams queue in the socket's receive buffer until it is full, and later ones are dropped. On Linux the drop increments RcvbufErrors in the Udp line of /proc/net/snmp. The sender sees nothing.",
        },
        {
          title: "The path is congested",
          body: "UDP keeps sending at whatever rate the application calls sendto(). Nothing slows it down, so a careless sender adds to the congestion and loses its own datagrams with everyone else's. RFC 8085 makes congestion control the application's job.",
        },
        {
          title: "No program is listening",
          body: "The destination host sends ICMP Port Unreachable and drops the datagram. Linux counts it as NoPorts. Whether the sending program hears about it depends on how its socket is set up; see the Linux details below.",
        },
        {
          title: "The datagram is bigger than the path MTU",
          body: "IP fragments it, or, with Don't Fragment set, a router drops it and sends ICMP 'too big'. Losing any fragment loses the whole datagram. On Linux, a socket set to IP_PMTUDISC_DO is refused locally with EMSGSIZE once the kernel knows the path MTU.",
        },
      ],
    },
    {
      type: "example",
      id: "one-datagram",
      title: "One datagram, and three ways it fails silently",
      setup:
        "A client at 10.0.0.5 sends a 30-byte query from port 53000 to a server at 10.0.0.53, port 53, over Ethernet with a 1500-byte MTU. Both run Linux.",
      walkthrough: [
        "The UDP length is 8 + 30 = 38. The IPv4 packet is 20 + 38 = 58 bytes, with protocol 17. The checksum is computed over a 12-byte pseudo-header (10.0.0.5, 10.0.0.53, zero, 17, 38), the 8-byte header with the checksum field set to zero, and the 30 data bytes. If the sum came out to zero it would be sent as 0xFFFF, because zero means 'no checksum'.",
        "The server's recvfrom() returns 30 bytes and the address 10.0.0.5:53000. Had the client sent two 30-byte queries, the server would make two calls and get 30 bytes each time, never 60. That is the boundary TCP does not keep.",
        "Failure one: a bit flips in the payload on a bad cable. The checksum no longer matches, the server's kernel discards the datagram, and InCsumErrors goes up by one. The server application sees nothing. The client sees nothing either, until its own timer fires and it retries.",
        "Failure two: the server process is stopped in a debugger and 10,000 queries arrive. Once the socket's receive buffer is full, the rest are dropped and RcvbufErrors climbs. `ss -uanp` shows the queued bytes in Recv-Q. The clients again see only silence.",
        "Failure three: the server process has exited. The server's kernel answers with ICMP Port Unreachable. If the client called connect() on its UDP socket, its next recv() or send() fails with ECONNREFUSED. If it used an unconnected socket and sendto(), it waits for a reply that will never come, unless it enabled IP_RECVERR.",
      ],
      result:
        "UDP delivered one whole message per read and checked it for damage. Every failure it does not handle looked the same to the client: no answer. The client's timeout and retry, and the operator's counters, are the only places those failures become visible.",
    },
    {
      type: "callout",
      id: "checksum-caution",
      tone: "caution",
      title: "The checksum is not integrity protection",
      body: "The 16-bit one's complement sum catches most random corruption, and the pseudo-header catches a misdelivered datagram. It does not catch every multi-bit error, and anyone who can modify the datagram can recompute it. RFC 8085 says applications that need integrity should add their own check, a CRC or a hash, and against an attacker that means a keyed MAC, which is what DTLS and QUIC provide.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "checksum-details",
      title: "Checksum rules for IPv4 and IPv6",
      paragraphs: [
        "In IPv4 the checksum is optional on the wire: a transmitted zero means the sender did not compute one. RFC 1122 still requires hosts to implement it, to have it on by default, and to discard silently any datagram whose non-zero checksum is wrong. It notes that applications which turned checksums off for speed on LANs saw many undetected errors.",
        "A computed sum of zero is sent as all ones (0xFFFF), which is the same value in one's complement arithmetic. A receiver needs no special case for it.",
        "IPv6 has no header checksum of its own, so the UDP checksum is the only thing protecting the addresses it depends on. RFC 8200 makes it mandatory: an IPv6 sender must compute it, and an IPv6 receiver must discard a UDP datagram whose checksum is zero. The one exception is a tunnel protocol that enables zero-checksum mode for specific ports under RFC 6936.",
        "The IPv6 pseudo-header uses the 128-bit addresses and, for UDP, the length from the UDP header. The arithmetic is otherwise the same as IPv4's and the same as TCP's.",
        "Most NICs compute and verify the checksum in hardware. A capture taken on the sending host can therefore show a 'bad' UDP checksum on outgoing packets, because the capture saw the packet before the NIC filled the field in.",
      ],
    },
    {
      type: "expandable",
      id: "linux-details",
      title: "Linux socket behaviour",
      paragraphs: [
        "udp(7) describes the service as connectionless and unreliable, with packets that may be reordered or duplicated, and says every receive returns only one packet. If the buffer passed to recv() is smaller than the datagram, the rest is discarded and MSG_TRUNC is set. MSG_WAITALL is not supported. A zero-length datagram is legal, so a UDP recv() returning 0 is a datagram, not end of file.",
        "connect() on a UDP socket sends nothing. It sets the default destination, so send() and write() work, and it filters received datagrams to that one peer. RFC 8085 stresses that the remote end is not told. It also changes error reporting, as the next paragraph explains, and a connected socket can call connect() again to change peers or AF_UNSPEC to dissolve the association.",
        "ICMP errors are the subtle part. RFC 1122 requires UDP to pass ICMP errors up to the application. In the kernel's `udp_err()`, if IP_RECVERR is off, an error is recorded on the socket only when it is a 'hard' error, such as Port Unreachable, and the socket is connected. An unconnected socket that uses sendto() never sees ECONNREFUSED. With IP_RECVERR on, every error is queued and can be read with recvmsg() and MSG_ERRQUEUE. udp(7)'s 'Error handling' paragraph says fatal errors reach the user even when the socket is not connected. The kernel source is narrower, and the lesson follows the source.",
        "A received ICMP 'fragmentation needed' updates the kernel's path MTU for that destination. With IP_PMTUDISC_DO, a later datagram larger than the known path MTU is refused with EMSGSIZE, and the application must make its messages smaller. With the default IP_PMTUDISC_WANT, the kernel fragments when it must. IP_MTU_DISCOVER(2const) also warns that the first datagrams may be dropped while discovery is still in progress.",
        "Receive-buffer drops are counted, not reported. When a datagram cannot be queued because the socket's buffer is full, `__udp_queue_rcv_skb()` increments RcvbufErrors and InErrors and frees it. `nstat -az | grep Udp` or `cat /proc/net/snmp` shows the counters. SO_RCVBUF raises a socket's limit up to net.core.rmem_max, and the kernel doubles the value you set to allow for bookkeeping.",
        "The largest UDP payload is 65,507 bytes over IPv4 and 65,527 over IPv6, because the datagram must fit in one IP packet. That is a ceiling, not a target: anything above the path MTU is fragmented.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "two-sends-one-recv",
          prompt:
            "A client calls sendto() twice on a UDP socket, with 100 and 200 bytes, and both arrive. The server calls recvfrom() with a 4096-byte buffer. What does the first call return, and how does that differ from TCP?",
          answer:
            "One datagram, either the 100-byte one or the 200-byte one, because UDP may reorder them. Never 300 bytes. UDP keeps each datagram's boundary and returns one per receive, while TCP may merge or split writes. In return, UDP does not promise that either datagram arrives, or that they arrive in order or only once.",
        },
        {
          id: "silent-drops",
          prompt:
            "A UDP metrics collector receives about 5% fewer datagrams than its clients say they sent. The network shows no loss. Where would you look on the Linux receiver, and what fixes it?",
          answer:
            "The receive buffer. Check RcvbufErrors and InErrors in the Udp line of /proc/net/snmp (or `nstat`), and Recv-Q in `ss -uanp`. If RcvbufErrors is growing, datagrams arrive faster than the process reads them during bursts, and the kernel drops them silently. Read faster (more threads, recvmmsg), raise SO_RCVBUF together with net.core.rmem_max, or make senders slow down. InCsumErrors would instead point at corruption.",
        },
        {
          id: "checksum-zero",
          prompt:
            "An IPv4 sender transmits a UDP datagram with checksum 0x0000. What does that mean, and would the same datagram be accepted over IPv6?",
          answer:
            "Over IPv4, zero means the sender did not compute a checksum, which RFC 768 allows. A sender whose sum really is zero sends 0xFFFF instead. Over IPv6 the checksum is mandatory, and RFC 8200 requires receivers to discard a UDP datagram with a zero checksum, except on ports where a tunnel protocol has enabled zero-checksum mode.",
        },
        {
          id: "no-econnrefused",
          prompt:
            "A client uses an unconnected UDP socket and sendto() to query a server whose process has crashed. tcpdump on the client shows ICMP Port Unreachable coming back, but the client just waits for its timeout. Why, and what would change it?",
          answer:
            "On Linux, without IP_RECVERR, the kernel attaches an ICMP error to a UDP socket only when the socket is connected. The unconnected socket cannot tell which of its many possible peers the error is about, so it is dropped. Calling connect() on the socket makes the next send() or recv() fail with ECONNREFUSED. Enabling IP_RECVERR queues the error for recvmsg() with MSG_ERRQUEUE. Either way the client still needs a timeout, because ICMP may be filtered.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "udp-minimal-service",
      statement:
        "UDP provides a procedure for applications to send messages with a minimum of protocol mechanism. Delivery and duplicate protection are not guaranteed; applications needing ordered, reliable delivery of streams should use TCP.",
      sourceId: "rfc768",
      locator: "Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-header-fields",
      statement:
        "The UDP header has 16-bit source port, destination port, length, and checksum fields. The source port is optional and zero when unused. Length counts the header and data in octets, so its minimum is eight. UDP is IP protocol 17.",
      sourceId: "rfc768",
      locator: "Format; Fields; Protocol Number",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-checksum-coverage",
      statement:
        "The checksum is the 16-bit one's complement of the one's complement sum of a pseudo-header (source address, destination address, protocol, UDP length), the UDP header, and the data, padded to an even length. The pseudo-header protects against misrouted datagrams. A computed zero is sent as all ones; a transmitted zero means no checksum was generated.",
      sourceId: "rfc768",
      locator: "Fields (Checksum)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-null-protocol",
      statement:
        "UDP is almost a null protocol: its only services over IP are checksumming and multiplexing by port number. An application using UDP must itself handle retransmission, packetization and reassembly, flow control, and congestion avoidance when it needs them.",
      sourceId: "rfc1122",
      locator: "Section 4.1.1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-checksum-host-rules",
      statement:
        "A host must implement UDP checksum generation and validation, and checksumming must default to on. A datagram with a non-zero, invalid checksum must be silently discarded. Applications that turned checksums off on LANs saw numerous undetected errors.",
      sourceId: "rfc1122",
      locator: "Section 4.1.3.4, UDP Checksums",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-port-unreachable",
      statement:
        "If a datagram arrives for a UDP port with no listener, UDP should send an ICMP Port Unreachable. UDP must pass received ICMP errors to the application; they arrive asynchronously, and the application must keep the state needed to match them to what it sent.",
      sourceId: "rfc1122",
      locator: "Sections 4.1.3.1, Ports, and 4.1.3.3, ICMP Messages",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ipv6-udp-checksum",
      statement:
        "In IPv6 the UDP checksum is not optional: a sender must compute it, and a receiver must discard a UDP packet with a zero checksum. Tunnel protocols may enable zero-checksum mode for specific ports under RFC 6936.",
      sourceId: "rfc8200",
      locator: "Section 8.1, Upper-Layer Checksums",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-checksum-weak",
      statement:
        "The UDP checksum is not strong from a coding or cryptographic perspective and is not designed to detect malicious modification. Applications should add their own checks where integrity matters. Over IPv6 it also protects the IPv6 header, which has no checksum.",
      sourceId: "rfc8085",
      locator: "Section 3.4, Checksum Guidelines",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-duplicates-and-msl",
      statement:
        "UDP does not protect against duplication or reordering. Applications needing reliability or ordering must implement it, and should be robust to delayed or duplicate datagrams arriving within TCP's two-minute maximum segment lifetime.",
      sourceId: "rfc8085",
      locator: "Section 3.3, Reliability Guidelines",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-socket-programming",
      statement:
        "Connecting a UDP socket is a local operation that sets addresses and filters traffic; the remote end is not notified. UDP has no flow control, so datagrams can be lost at the receiver when the receive buffer overflows. A zero-length UDP payload is valid and differs from TCP's read() returning zero.",
      sourceId: "rfc8085",
      locator: "Section 5, Programming Guidelines",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-max-payload",
      statement:
        "A UDP datagram is carried in a single IP packet, so its payload is limited to 65,507 bytes over IPv4 and 65,527 bytes over IPv6.",
      sourceId: "rfc8085",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-udp-service",
      statement:
        "Linux UDP is a connectionless, unreliable datagram service; packets may be reordered or duplicated. Every receive returns one packet, truncating it and setting MSG_TRUNC if the buffer is too small; MSG_WAITALL is not supported. connect() sets the default destination.",
      sourceId: "man-udp",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-udp-connect-filter",
      statement:
        "For a SOCK_DGRAM socket, connect() sets the address datagrams are sent to by default and the only address from which datagrams are received. Datagram sockets may call connect() again to change the association, or connect to AF_UNSPEC to dissolve it.",
      sourceId: "man-connect",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-udp-err-connected-only",
      statement:
        "In udp_err(), without IP_RECVERR an ICMP error is set on the socket only if it is a hard error and the socket is connected; otherwise it is dropped. With IP_RECVERR it is queued with ip_icmp_error(). ICMP fragmentation-needed updates the path MTU and yields EMSGSIZE unless the socket uses IP_PMTUDISC_DONT.",
      sourceId: "linux-udp-c",
      locator: "udp_err()",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-udp-drop-counters",
      statement:
        "When a datagram cannot be queued because the socket's receive buffer is full, __udp_queue_rcv_skb() increments RcvbufErrors and InErrors and drops it. A datagram for a port with no socket increments NoPorts and triggers ICMP Port Unreachable; a checksum failure increments InCsumErrors and InErrors.",
      sourceId: "linux-udp-c",
      locator: "__udp_queue_rcv_skb() and udp_rcv()",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-snmp-udp-names",
      statement:
        "The Udp line of /proc/net/snmp lists InDatagrams, NoPorts, InErrors, OutDatagrams, RcvbufErrors, SndbufErrors, InCsumErrors, IgnoredMulti, and MemErrors.",
      sourceId: "linux-proc-c",
      locator: "snmp4_udp_list and snmp_seq_show_tcp_udp()",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-so-rcvbuf",
      statement:
        "SO_RCVBUF sets the maximum socket receive buffer; the kernel doubles the value set to allow for bookkeeping. The default comes from net.core.rmem_default and the maximum from net.core.rmem_max.",
      sourceId: "man-socket",
      locator: "Socket options, SO_RCVBUF",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-udp-pmtudisc",
      statement:
        "For datagram sockets, IP_PMTUDISC_DO sets Don't Fragment and the kernel rejects with EMSGSIZE datagrams bigger than the known path MTU; IP_PMTUDISC_WANT fragments if needed. While discovery is in progress, initial datagrams may be dropped.",
      sourceId: "man-ip-mtu-discover",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc768", note: "The whole protocol, in three pages." },
    { sourceId: "rfc1122", note: "Section 4.1: host requirements for UDP." },
    { sourceId: "rfc8200", note: "Section 8.1: the mandatory IPv6 UDP checksum." },
    { sourceId: "rfc8085", note: "Sections 1, 3.3, 3.4, and 5." },
    { sourceId: "man-udp", note: "Linux's UDP socket semantics." },
    { sourceId: "man-connect", note: "What connect() means for a datagram socket." },
    { sourceId: "man-socket", note: "SO_RCVBUF." },
    { sourceId: "man-ip-mtu-discover", note: "Path MTU discovery on datagram sockets." },
    { sourceId: "linux-udp-c", note: "udp_err(), udp_rcv(), and __udp_queue_rcv_skb()." },
    { sourceId: "linux-proc-c", note: "Counter names in /proc/net/snmp." },
  ],
} satisfies LessonBody;
