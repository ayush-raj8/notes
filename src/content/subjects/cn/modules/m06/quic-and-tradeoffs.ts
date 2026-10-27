import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m06.quic-and-tradeoffs",
  status: "complete",
  objectives: [
    "Place QUIC: a connection-oriented, reliable, encrypted transport whose packets are carried in UDP datagrams.",
    "Explain what QUIC rebuilds from TCP and what it changes: independent streams, packet numbers that are never reused, frames resent in new packets, and connection IDs.",
    "Say why QUIC runs over UDP instead of as a new IP protocol, and what that means for operating it on Linux.",
    "Choose TCP, UDP, or QUIC for a workload and name what each choice costs.",
  ],
  prerequisites: ["m06.use-cases"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "TCP gives every connection one ordered byte stream, so when HTTP/2 multiplexes many requests onto it, one lost packet holds up all of them (why.17). Setting up TCP and then TLS costs round trips before the first request. A TCP connection is named by its 4-tuple, so a phone that moves from Wi-Fi to cellular loses its connections. TCP lives in operating system kernels and is parsed by middleboxes, so changing it takes years, and a brand-new IP protocol would not get through the NATs and firewalls that pass only TCP and UDP.",
      whyItMatters:
        "HTTP/3 runs on QUIC, so a growing share of web and API traffic shows up on a Linux host as UDP to port 443. Load-balancing, firewalling, and debugging that traffic means knowing what QUIC does inside those datagrams, and what is no longer visible from outside.",
      idea: "QUIC is a complete transport, with connections, reliability, flow control, and congestion control, built in the endpoints and carried in UDP datagrams. UDP supplies ports and a path through existing middleboxes. QUIC supplies everything else, encrypts nearly all of it, and divides one connection into many streams that are each delivered in order, independently of the others.",
      howItWorks:
        "Each UDP datagram carries one or more QUIC packets, and each packet carries frames. STREAM frames hold application bytes tagged with a stream ID and an offset, and ACK frames report which packets arrived. The handshake carries TLS 1.3 messages in CRYPTO frames alongside QUIC's transport parameters, so one exchange sets up both the connection and its keys. Every packet gets a fresh packet number that is never reused. When a packet is lost, its contents are sent again in new frames in a new packet. The receiver places each stream's bytes by offset and delivers each stream in order on its own. Flow control limits each stream and the connection as a whole. Packets carry a connection ID chosen by the receiving endpoint, so a connection can continue after a client's address changes, once the new path is validated.",
      whyItWorks:
        "Ordering is kept per stream, so a lost packet blocks only the streams whose data it carried. Packet numbers record when something was sent and stream offsets record where its bytes belong, so every acknowledgment names exactly one transmission, and TCP's ambiguity over whether an ACK is for the original or the retransmission disappears. Authenticating the whole packet and encrypting most of it stops middleboxes from depending on transport fields, so the protocol can keep changing. Naming the connection by an ID, not by addresses, lets it survive an address change.",
      limits:
        "QUIC works only where UDP is allowed through, so clients still need a TCP fallback. A lost packet that carried several streams stalls all of them. 0-RTT data can be replayed by an attacker. Before a server has validated a client's address it may send only three times what it received, and QUIC cannot run at all on a path that does not carry 1200-byte UDP payloads. Because the transport is encrypted, tools that read TCP headers from a capture, and middleboxes that tuned TCP, see only UDP.",
      connections:
        "This is the third pattern from the previous lesson, built at full scale. The mechanisms are Module 5's: sequence numbers, acknowledgments, loss detection with a threshold of three, flow control, and congestion control. Module 8 answers why HTTP/3 uses QUIC over UDP rather than TCP (why.29), Module 9 covers the TLS 1.3 handshake QUIC carries, and the stream-blocking visualization is planned for those lessons.",
    },
    {
      type: "comparison",
      id: "tcp-udp-quic",
      title: "TCP, UDP, and QUIC side by side",
      caption:
        "What each transport provides to the application. QUIC's encryption comes from its integrated TLS 1.3 handshake. TCP and UDP can carry TLS or other security above them.",
      columns: ["TCP", "UDP", "QUIC"],
      rows: [
        {
          label: "Setup",
          cells: [
            "Three-way handshake, then TLS separately if wanted.",
            "None. The first datagram carries data.",
            "One combined transport and TLS 1.3 handshake. 0-RTT data is possible after prior contact.",
          ],
        },
        {
          label: "Delivery",
          cells: [
            "Reliable: retransmits until acknowledged or the connection fails.",
            "Best effort. Loss is silent.",
            "Reliable for stream data. Lost frames are resent in new packets.",
          ],
        },
        {
          label: "Ordering",
          cells: [
            "One ordered byte stream per direction.",
            "None.",
            "In order within each stream. No ordering across streams.",
          ],
        },
        {
          label: "Message boundaries",
          cells: [
            "None.",
            "One datagram per send and per receive.",
            "None within a stream. Ending a stream marks the end of its data.",
          ],
        },
        {
          label: "Flow control",
          cells: [
            "Receive window per connection.",
            "None.",
            "Limits per stream and per connection, plus a limit on open streams.",
          ],
        },
        {
          label: "Congestion control",
          cells: [
            "Required, in the kernel.",
            "The application's job.",
            "Required, in the QUIC implementation.",
          ],
        },
        {
          label: "Connection identity",
          cells: [
            "The 4-tuple. Changing an address breaks the connection.",
            "No connection. A connected socket filters on the peer address.",
            "Connection IDs. A client can migrate to a new address.",
          ],
        },
        {
          label: "Multicast and broadcast",
          cells: [
            "No, unicast only.",
            "Yes.",
            "No. A connection is between one client and one server.",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "quic-connection",
      title: "A QUIC connection, at teaching level",
      steps: [
        {
          title: "Client Initial",
          body: "The client sends an Initial packet with a CRYPTO frame holding the TLS ClientHello and its transport parameters. It pads the UDP payload to at least 1200 bytes, which both tests that the path can carry that size and allows the server to send more in reply.",
        },
        {
          title: "Server Initial and Handshake",
          body: "The server answers with its own Initial and Handshake packets carrying the rest of the TLS handshake. Until the client's address is validated, the server sends no more than three times the bytes it has received, so QUIC cannot be used to amplify a spoofed request into a flood.",
        },
        {
          title: "Client finishes, data flows",
          body: "The client sends its Handshake packet and can send 1-RTT application data in the same flight. The server confirms with HANDSHAKE_DONE. A client that has talked to this server before may send 0-RTT data in its very first flight, at the cost of replay protection.",
        },
        {
          title: "Streams carry the application's data",
          body: "Opening a stream costs nothing more than sending a STREAM frame with a new stream ID. The client's bidirectional streams are numbered 0, 4, 8, and so on. Frames from different streams are interleaved in packets.",
        },
        {
          title: "Loss is repaired with new packets",
          body: "ACK frames report received packet numbers as ranges. A packet three or more numbers below an acknowledged one, or sent long enough ago, is declared lost. Its frames' contents are put into new frames in a new packet with a new number. The lost packet number is never sent again.",
        },
        {
          title: "The client moves",
          body: "After the handshake is confirmed, a client that changes networks keeps sending with a connection ID from the same connection. The server sees a new source address and validates the path with PATH_CHALLENGE and PATH_RESPONSE before relying on it. The same mechanism covers a NAT that quietly changes the client's port.",
        },
      ],
    },
    {
      type: "example",
      id: "one-loss-three-streams",
      title: "One lost packet, three streams",
      setup:
        "A browser fetches a stylesheet, a script, and an image over one HTTP/3 connection, on client streams 0, 4, and 8. The server sends packets 20 to 24, each carrying one STREAM frame of about 1200 bytes: 20 (stream 0, offset 0), 21 (stream 4, offset 0), 22 (stream 8, offset 0), 23 (stream 0, offset 1200), 24 (stream 4, offset 1200). Packet 21 is lost. This omits header and frame details that do not change the result.",
      walkthrough: [
        "The client receives 20, 22, 23, and 24 and acknowledges them with ranges: 20, and 22 to 24. QUIC acknowledges a packet once its frames are processed and queued for the application, whether or not the application has read them.",
        "Stream 0 has bytes 0 to 2399 with no gap, so the browser can read the whole stylesheet so far. Stream 8 has bytes 0 to 1199 and delivers them too. Neither waits for anything.",
        "Stream 4 has bytes 1200 to 2399 but not 0 to 1199. Its data is held in the stream's buffer, exactly as TCP would hold it, but only for this stream.",
        "On the server, packet 24 is acknowledged and packet 21 is three numbers below it, so 21 is declared lost. The server sends stream 4's bytes 0 to 1199 again, in a new STREAM frame inside packet 25. When the ACK for 25 arrives, the server knows exactly which transmission it measured, so the RTT sample is clean.",
        "When packet 25 arrives, the client places those bytes at offset 0 of stream 4, and the script's first 2400 bytes become readable.",
        "Over HTTP/2 on TCP, the same three responses share one byte stream. The bytes lost with that one segment would sit in front of the stylesheet and image bytes that arrived after them, so all three responses would wait for the retransmission.",
      ],
      result:
        "The loss delayed only the stream whose data it carried. Had packet 21 also carried a frame for stream 0, both streams would have waited. QUIC removes blocking across streams, not within a stream and not within a packet.",
    },
    {
      type: "steps",
      id: "choosing",
      title: "Choosing a transport",
      steps: [
        {
          title: "Every byte, in order, one stream: TCP",
          body: "File transfer, database replication, and most internal RPC over a well-behaved network. TCP is everywhere, tuned in the kernel, understood by every tool, and its head-of-line blocking does not matter when there is only one stream.",
        },
        {
          title: "Many independent streams, fast setup, mobile clients: QUIC",
          body: "Web and API traffic to browsers and phones over lossy or changing networks, where blocking across requests, handshake round trips, and broken connections on network changes all cost users time. In practice that means HTTP/3 through an existing QUIC library, with a TCP fallback.",
        },
        {
          title: "Deadlines, single-datagram exchanges, or groups: UDP",
          body: "Real-time media, game state, DNS queries, and LAN discovery. You accept building the mechanisms from the previous lesson, including congestion control.",
        },
        {
          title: "When in doubt, an existing transport",
          body: "A custom reliable protocol over UDP is a large commitment. Reach for one only when you can name the TCP behaviour you need to avoid and the mechanism that replaces it.",
        },
      ],
    },
    {
      type: "expandable",
      id: "why-udp-and-visibility",
      title: "Why UDP, and what the network can still see",
      paragraphs: [
        "RFC 9000 states the reason plainly: QUIC packets are carried in UDP datagrams to make deployment easier in existing systems and networks. RFC 8085 makes the same observation from the other side: middleboxes that support only TCP and UDP are a reason protocols with a new IP protocol number struggle to deploy. UDP is the thinnest layer that NATs and firewalls already pass.",
        "Before 1-RTT keys exist, QUIC uses long headers, which carry a version and both the destination and source connection IDs. After that it uses short headers, which carry only the destination connection ID, followed by a protected packet number and an encrypted payload. A load balancer can route on the connection ID, which is why its length can be fixed or encoded by agreement with the endpoints.",
        "Because packet numbers are encrypted and the payload is protected, a capture shows UDP datagrams on port 443 and little else. There is no visible sequence number, window, or ACK to read retransmissions from, as you would with TCP in Module 5. On Linux, `ss -uanp` lists the UDP sockets. Connection-level detail has to come from the QUIC implementation's own logs or metrics.",
        "RFC 9002 notes that reordering may be more common for QUIC than for TCP, because network elements that could observe and reorder TCP packets cannot do that for QUIC. It recommends a packet reordering threshold of 3, the same as TCP's duplicate-ACK threshold, and no lower.",
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Edge cases and caveats",
      paragraphs: [
        "0-RTT data is sent before the handshake proves anything about the current connection, so an attacker can replay it. RFC 9000 says 0-RTT provides no protection against replay. Applications should allow only requests that are safe to repeat in 0-RTT. Module 9 covers the TLS side.",
        "QUIC must not be used on a path that cannot carry a 1200-byte UDP payload. Its datagrams must not be fragmented at the IP layer, and over IPv4 it sets Don't Fragment where it can. Larger sizes are found with path MTU discovery or packetization-layer probing.",
        "Only clients migrate in QUIC version 1, and not before the handshake is confirmed. A server can forbid active migration with the disable_active_migration transport parameter. Any change in a peer's address, including NAT rebinding, triggers path validation.",
        "Stream IDs are 62-bit numbers that are never reused. The lowest bit says who opened the stream (0 for the client), and the next bit says whether it is bidirectional (0) or unidirectional (1). QUIC itself carries no priority between streams. The application has to tell its QUIC implementation how to share the connection between them.",
        "Like a TCP ACK, a QUIC acknowledgment means the peer's QUIC stack has processed the packet and queued its stream data, not that the application has read or acted on it. Module 5's point about application-level confirmation (why.09) applies unchanged.",
        "QUIC ACK frames can report many ranges, where TCP's SACK option carries at most a few blocks, and a QUIC acknowledgment can never be withdrawn. TCP's SACK information can be reneged.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "why-not-new-protocol",
          prompt:
            "QUIC rebuilds almost everything TCP does. Why does it run over UDP instead of having its own IP protocol number, like TCP's 6 and UDP's 17?",
          answer:
            "Deployment. NATs, firewalls, and other middleboxes on the Internet often pass only TCP and UDP, and a new protocol number would be dropped on many paths. UDP adds just ports and a checksum, which QUIC needs anyway, and is already allowed through. Running over UDP also lets QUIC be implemented outside the operating system's TCP code, so it can change without waiting for every kernel and middlebox to change.",
        },
        {
          id: "packet-with-two-streams",
          prompt:
            "A QUIC packet carrying STREAM frames for streams 0 and 4 is lost. Stream 8's data was in other packets that arrived. Which streams stall, and what does the sender retransmit?",
          answer:
            "Streams 0 and 4 stall, because each is missing bytes from that packet. Stream 8 keeps delivering. The sender does not resend the lost packet. It puts the missing stream 0 and stream 4 bytes, at the same offsets, into new STREAM frames in a packet with a new packet number. That is why implementations try not to spread a packet over more streams than they need to.",
        },
        {
          id: "where-are-the-connections",
          prompt:
            "A Linux server starts serving HTTP/3. `ss -tn` no longer shows connections for many clients, and a tcpdump of port 443 shows mostly UDP. Where are the connections, and how would you investigate a slow one?",
          answer:
            "They are QUIC connections inside UDP datagrams, so the kernel sees only UDP sockets, visible with `ss -uanp`. The capture cannot show sequence numbers, windows, or retransmissions, because QUIC encrypts its packet numbers and payload. Use the QUIC implementation's own logs or metrics for per-connection RTT, loss, and congestion window. Kernel-level UDP counters such as RcvbufErrors still show drops before QUIC sees the datagrams.",
        },
        {
          id: "wifi-to-cellular",
          prompt:
            "A phone moves from Wi-Fi to cellular halfway through a large download. What happens over TCP, and what happens over QUIC?",
          answer:
            "Over TCP, the client's IP address changes, so packets no longer match the connection's 4-tuple. The connection eventually fails, and the application has to reconnect and resume the download if it can. Over QUIC, the client keeps sending with a connection ID for the same connection from its new address. The server validates the new path and continues on it, with its congestion controller and RTT estimate reset to initial values, because the new path's capacity is unknown. If the server set disable_active_migration, the client cannot do this and must reconnect.",
        },
        {
          id: "pick-transport",
          prompt:
            "Pick a transport for each and say why: (a) a mobile app's API calls, (b) service-to-service RPC inside one data centre, (c) a video call.",
          answer:
            "(a) HTTP/3 over QUIC, with a TCP fallback: independent requests, fast setup, and migration across networks all help mobile users. (b) TCP, typically HTTP/2 or gRPC over long-lived connections: the network is low-loss, head-of-line blocking rarely matters, and kernel TCP is well understood and well instrumented. (c) UDP with RTP: frames have deadlines, so concealing a loss beats waiting for a retransmission, and the application handles congestion by adapting its bitrate.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "quic-overview",
      statement:
        "QUIC is a connection-oriented, secure, general-purpose transport. It provides flow-controlled streams, low-latency connection establishment, and path migration. It authenticates each entire packet and encrypts as much as practical, and its packets are carried in UDP datagrams to facilitate deployment in existing systems and networks.",
      sourceId: "rfc9000",
      locator: "Abstract; Section 1, Overview",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-reliability-and-congestion",
      statement:
        "QUIC provides the feedback needed to implement reliable delivery and congestion control, and depends on congestion control to avoid network congestion.",
      sourceId: "rfc9000",
      locator: "Section 1, Overview",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-handshake",
      statement:
        "QUIC uses a combined cryptographic and transport handshake, carried in CRYPTO frames and using TLS, to minimize connection establishment latency. 0-RTT lets a client send data immediately but requires prior communication or configuration, and provides no protection against replay.",
      sourceId: "rfc9000",
      locator:
        "Section 1, Overview; Section 5, Connections; Section 7, Cryptographic and Transport Handshake (Figure 4)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-streams",
      statement:
        "QUIC streams are ordered byte streams. QUIC provides no ordering between bytes on different streams. Endpoints must deliver stream data as an ordered byte stream, buffering out-of-order data up to the flow control limit. STREAM frame boundaries are not preserved.",
      sourceId: "rfc9000",
      locator: "Section 2, Streams; Section 2.2, Sending and Receiving Data",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-stream-ids",
      statement:
        "A stream ID is a 62-bit integer that must not be reused within a connection. Its least significant bit identifies the initiator (0 for client), and the second bit distinguishes bidirectional (0) from unidirectional (1). QUIC carries no prioritization information and relies on the application for it.",
      sourceId: "rfc9000",
      locator: "Section 2.1, Stream Types and Identifiers; Section 2.3, Stream Prioritization",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-no-cross-stream-hol",
      statement:
        "QUIC avoids head-of-line blocking across streams: when a packet is lost, only streams with data in that packet are blocked waiting for retransmission. When one packet carries data from several streams, its loss blocks all of them.",
      sourceId: "rfc9000",
      locator: "Section 13, Packetization and Reliability",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-ack-after-processing",
      statement:
        "A QUIC packet must not be acknowledged until its protection is removed and all its frames are processed. For STREAM frames this means the data is queued for the application protocol, not that it has been delivered and consumed.",
      sourceId: "rfc9000",
      locator: "Section 13.1, Packet Processing",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-flow-control",
      statement:
        "QUIC flow-controls each stream and the connection as a whole, and limits how many streams a peer may open.",
      sourceId: "rfc9000",
      locator: "Section 4, Flow Control",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-packet-numbers",
      statement:
        "Packet numbers are divided into Initial, Handshake, and application data spaces. Each starts at 0, increases by at least one per packet, and must not be reused within a connection.",
      sourceId: "rfc9000",
      locator: "Section 12.3, Packet Numbers",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-resend-in-new-frames",
      statement:
        "Lost QUIC packets are not retransmitted whole; the information they carried is sent again in new frames in new packets. Stream data is retransmitted in new STREAM frames unless the stream was reset.",
      sourceId: "rfc9000",
      locator: "Section 13.3, Retransmission of Information",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-connection-ids",
      statement:
        "Connection IDs ensure that address changes at lower layers do not deliver a connection's packets to the wrong endpoint. Each endpoint selects the IDs its peer uses. Long headers carry source and destination connection IDs; short headers carry only the destination ID, and load balancers that route on it can agree on a fixed length or encoding.",
      sourceId: "rfc9000",
      locator: "Section 5.1, Connection ID",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-migration",
      statement:
        "Connection IDs let a connection survive changes to endpoint addresses. Only clients migrate in this version, never before the handshake is confirmed, and not if the peer sent disable_active_migration. Any change to a peer's address, including NAT rebinding, requires path validation.",
      sourceId: "rfc9000",
      locator: "Section 1, Overview; Section 9, Connection Migration",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-migration-congestion-reset",
      statement:
        "After confirming a peer's new address, an endpoint must reset the congestion controller and RTT estimator for the new path to initial values, unless only the port changed. Packets sent on the old path must not count toward the new path's congestion control or RTT estimate.",
      sourceId: "rfc9000",
      locator: "Section 9.4, Loss Detection and Congestion Control",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-amplification-limit",
      statement:
        "Before validating the client's address, a server must not send more than three times the bytes it has received. Clients must pad datagrams carrying Initial packets to at least 1200 bytes.",
      sourceId: "rfc9000",
      locator: "Section 8.1, Address Validation during Connection Establishment",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-datagram-size",
      statement:
        "QUIC must not be used if the path cannot support a 1200-byte maximum datagram size. QUIC datagrams must not be fragmented at the IP layer, and over IPv4 the Don't Fragment bit must be set if possible.",
      sourceId: "rfc9000",
      locator: "Section 14, Datagram Size",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-header-forms",
      statement:
        "Long headers, used before 1-RTT keys are available, carry a version and destination and source connection IDs. 1-RTT packets use a short header with the destination connection ID and the packet number.",
      sourceId: "rfc9000",
      locator: "Section 17.2, Long Header Packets; Section 17.3.1, 1-RTT Packet",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-no-retransmission-ambiguity",
      statement:
        "QUIC packet numbers never repeat within a space and indicate transmission order, while stream offsets determine delivery order. Lost frames go in a new packet with a new number, removing ambiguity about which packet an ACK acknowledges and allowing more accurate RTT measurement.",
      sourceId: "rfc9002",
      locator:
        "Section 3, Design of the QUIC Transmission Machinery; Section 4.2, Monotonically Increasing Packet Numbers",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-ack-ranges-no-reneging",
      statement:
        "QUIC ACK frames carry many ranges, where TCP's SACK carries three, and QUIC acknowledgments cannot be reneged.",
      sourceId: "rfc9002",
      locator: "Sections 4.4, No Reneging, and 4.5, More ACK Ranges",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-packet-threshold",
      statement:
        "A packet is declared lost when it is unacknowledged and was sent kPacketThreshold packets before an acknowledged packet, or long enough before it. The recommended threshold is 3 and should not be lower. Reordering may be more common for QUIC because network elements cannot observe or reorder its encrypted packet numbers.",
      sourceId: "rfc9002",
      locator: "Section 6.1, Acknowledgment-Based Detection; Section 6.1.1, Packet Threshold",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "middleboxes-tcp-udp-only",
      statement:
        "Protocols using a new IP protocol number face deployment problems where middleboxes support only TCP and UDP.",
      sourceId: "rfc8085",
      locator: "Section 3, UDP Usage Guidelines",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc9000",
      note: "Sections 1, 2, 2.1–2.3, 4, 5, 5.1, 7, 8.1, 9, 9.4, 12.3, 13, 13.1, 13.3, 14, 17.2, and 17.3.1.",
    },
    { sourceId: "rfc9002", note: "Sections 3, 4.2, 4.4, 4.5, 6.1, and 6.1.1." },
    { sourceId: "rfc8085", note: "Section 3: why new protocol numbers struggle to deploy." },
  ],
} satisfies LessonBody;
