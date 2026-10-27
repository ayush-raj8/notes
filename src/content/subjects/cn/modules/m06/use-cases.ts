import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m06.use-cases",
  status: "complete",
  objectives: [
    "Recognize the workloads where UDP is the better choice: deadlines, single-datagram transactions, and multicast or broadcast.",
    "Explain why DNS queries and real-time media use UDP, and what each adds on top of it.",
    "List the mechanisms an application must build to get reliability over UDP, and the ones it must build even without reliability.",
    "Say when UDP is the wrong choice and an existing transport should be used instead.",
  ],
  prerequisites: ["m06.udp-service"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Some applications care more about when data arrives than whether every byte does. A voice sample is useless after its moment to play has passed. A DNS query that goes unanswered is better sent to another server than waited for. TCP offers one policy for all of them: one ordered stream, where a lost segment is retransmitted and everything after it waits.",
      whyItMatters:
        "Engineers choose between TCP and UDP for RPC, telemetry, service discovery, media, and games, and choosing UDP means writing the transport they did not get. Get it wrong one way and a lossy path produces stalls and latency spikes. Get it wrong the other way and you ship a protocol that retries into congestion, or loses data no one notices.",
      idea: "UDP fits when the application can make a better delivery decision than TCP's fixed one: when a late message is worthless, when each exchange is one small datagram that is cheap to retry, or when the data goes to many receivers at once. The application then builds exactly the reliability it needs, and it must also build congestion control, which no workload gets to skip.",
      howItWorks:
        "Three patterns cover most uses. A single-datagram transaction, such as a DNS query, puts an identifier in the request, matches the reply by it, retries on a timeout (preferably to another server), and falls back to TCP when the answer does not fit. A real-time stream, such as RTP audio, numbers and timestamps each packet, so the receiver can spot loss, reorder, and play each packet at the right moment from a small buffer. A missing packet is concealed rather than awaited, and receiver reports drive the sender's bitrate. A reliable protocol over UDP, such as QUIC, rebuilds sequence numbers, acknowledgments, retransmission timers, and congestion control inside the application, so it can choose its own policies.",
      whyItWorks:
        "Each pattern keeps only the mechanisms whose cost buys something. A DNS exchange is two datagrams, so a TCP handshake first would add a round trip to a one-round-trip job, and the server would hold connection state for every client. A call can lose a 20 ms frame and keep going, but it cannot pause for 200 ms, so it trades retransmission for bounded delay. And because UDP leaves the policy open, a protocol like QUIC can deliver each stream independently instead of holding everything behind one loss.",
      limits:
        "Everything UDP leaves out must be rebuilt or tolerated: duplicates, reordering, message sizing, and middlebox state that expires when a flow goes quiet. Congestion control is required even for a few datagrams a second. Building TCP-quality reliability correctly is hard, and RFC 8085 recommends using an existing transport when you need reliable, ordered delivery. UDP is not faster in itself. It only removes waiting that a particular application did not want, and some networks block or rate-limit it.",
      connections:
        "The previous lesson listed what UDP omits. Module 5's head-of-line blocking (why.17) is the cost real-time traffic avoids, and Module 5's congestion control is the obligation it keeps. The next lesson shows QUIC, the third pattern at full scale. Module 7 covers DNS over UDP and TCP in depth, and Module 8 places HTTP/3 on QUIC.",
    },
    {
      type: "comparison",
      id: "workloads",
      title: "Matching the workload to the transport",
      caption:
        "Typical choices, not rules. Each row says what the application cares about most, and why that points one way.",
      columns: ["What matters most", "Usual transport", "Why"],
      rows: [
        {
          label: "Name lookup (DNS)",
          cells: [
            "One fast answer",
            "UDP, with TCP for large answers",
            "Query and answer each fit in one datagram. A lost query is retried, often at another server.",
          ],
        },
        {
          label: "Voice and video calls",
          cells: [
            "Each packet on time",
            "UDP (RTP)",
            "A late packet is worthless. Concealing a loss is better than stalling for a retransmission.",
          ],
        },
        {
          label: "Game state updates",
          cells: [
            "The latest state",
            "UDP",
            "A newer update replaces a lost one, so retransmitting the old one only adds delay.",
          ],
        },
        {
          label: "Discovery on a LAN",
          cells: [
            "Reaching hosts you cannot name yet",
            "UDP broadcast or multicast",
            "TCP is unicast only. UDP can send one datagram to a group.",
          ],
        },
        {
          label: "File transfer, replication, APIs",
          cells: [
            "Every byte, in order",
            "TCP, or QUIC for HTTP/3",
            "Completeness matters more than any single delay, which is exactly TCP's promise.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "voice-loss",
      title: "One lost voice packet, over TCP and over UDP",
      setup:
        "A call sends one 20 ms audio frame every 20 ms. The one-way delay is 50 ms, so the round-trip time is 100 ms. Frame 10 is sent at t = 0 and lost. Assume Nagle and delayed ACKs are off. This is a teaching simplification that leaves out jitter and TCP's congestion window.",
      walkthrough: [
        "Over TCP: frames 11, 12, and 13 leave at 20, 40, and 60 ms and arrive at 70, 90, and 110 ms. The receiver answers each with a duplicate ACK still asking for frame 10's bytes. Those reach the sender at 120, 140, and 160 ms.",
        "The third duplicate ACK triggers fast retransmit at 160 ms, and the copy arrives at 210 ms. Frame 10 should have arrived at 50 ms, so it is 160 ms late.",
        "Frames 11 through 17 arrived meanwhile, but TCP cannot hand them to the application before frame 10. Playback stops for about 160 ms, or the player keeps a buffer at least that deep and adds that delay to the whole call. If the retransmission were lost too, recovery would wait for a retransmission timeout, which is longer still.",
        "Over UDP with RTP: frame 11 arrives at 70 ms with sequence number 11, so the receiver knows 10 is missing. The player has a 40 ms buffer and planned to play frame 10 at 90 ms. When that moment comes, it conceals the gap by stretching or interpolating neighbouring audio, and plays frame 11 on time at 110 ms.",
        "The sender is not told about that one packet. RTCP receiver reports, sent every few seconds, show the loss rate, and if it keeps rising the sender lowers its bitrate instead of queuing more.",
      ],
      result:
        "Over TCP, one lost packet cost a 160 ms stall for every frame behind it. Over UDP it cost one concealed 20 ms frame and no added delay. The playout deadline is what makes the difference: data that cannot be on time is worth less than the data behind it.",
    },
    {
      type: "why",
      id: "why-udp-realtime",
      questionId: "why.18",
    },
    {
      type: "steps",
      id: "dns-over-udp",
      title: "How DNS uses UDP",
      steps: [
        {
          title: "Send one query",
          body: "The resolver puts a 16-bit ID in the header and sends the question in one UDP datagram to port 53. No handshake, no connection, and the server keeps no state after it answers.",
        },
        {
          title: "Match the reply",
          body: "The server copies the ID into its response. The resolver uses it to match replies to outstanding queries, since answers can come back in any order.",
        },
        {
          title: "Retry on silence",
          body: "Either datagram may be lost, so the resolver needs its own timer. RFC 1035 recommends trying other servers and addresses before repeating a query to the same one, and warns that aggressive retries slow everyone down.",
        },
        {
          title: "Fall back to TCP when the answer is too big",
          body: "Classic DNS over UDP carries at most 512 bytes of message. A longer answer is truncated and the TC bit is set, and the resolver repeats the query over TCP, where a 2-byte length prefix frames each message.",
        },
        {
          title: "Treat TCP as part of DNS",
          body: "RFC 7766 makes TCP support required for every general-purpose DNS implementation and allows resolvers to use TCP first. A firewall that blocks TCP port 53 will break large answers, such as DNSSEC responses. Module 7 covers EDNS, DNS over TLS, and DNS over HTTPS.",
        },
      ],
    },
    {
      type: "steps",
      id: "building-reliability",
      title: "Building only the reliability you need",
      steps: [
        {
          title: "Name every message",
          body: "Give each request an ID or each packet a sequence number. Without one you cannot match a reply, detect a gap, or recognize a duplicate.",
        },
        {
          title: "Detect loss with a timer",
          body: "Silence is the only loss signal. Estimate the round-trip time with a moving average per destination, start at 1 second before you have a sample, and never take a sample from a reply to a retransmitted request, because you cannot tell which copy it answers. That is Karn's rule from Module 5.",
        },
        {
          title: "Decide what loss means",
          body: "Resend it (a request), skip it (a voice frame), or let newer data replace it (a game update). This choice is the reason to use UDP, so make it per message type. Reliable events such as a purchase can share the socket with unreliable ones but need their own acknowledgment.",
        },
        {
          title: "Tolerate duplicates and reordering",
          body: "A retry and the original can both arrive, and either can be minutes late. Make handlers idempotent, or remember recent IDs. Order only the data that needs it.",
        },
        {
          title: "Back off and pace",
          body: "Treat every timeout as congestion and back off exponentially. A low-volume application should average no more than one datagram per round trip to a destination. A bulk sender needs real congestion control that shares the path fairly with TCP.",
        },
        {
          title: "Fit the path",
          body: "Keep each datagram under the path MTU, so it is never fragmented. Without discovery, stay under 1280 bytes for IPv6 and 576 for IPv4. When a message is split across datagrams, make each piece independently receivable and retransmittable.",
        },
        {
          title: "Survive middleboxes",
          body: "NATs and firewalls create UDP state on the first outbound packet and delete it after an idle period. A long-lived session must either send keepalives, no more often than every 15 seconds, or detect a broken session and re-establish it. It needs the recovery path either way.",
        },
      ],
    },
    {
      type: "callout",
      id: "not-faster",
      tone: "caution",
      title: "UDP is not a faster TCP",
      body: "UDP does not move bits faster. It removes waiting that a particular application did not want. A custom UDP file transfer that beats TCP usually does so by ignoring congestion, which takes capacity from everyone else on the path and collapses under loss. RFC 8085 notes that TCP's algorithms have been refined for decades and that TCP is not as heavyweight as often claimed. It recommends using an existing transport when you need reliable, ordered delivery.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "rtp-details",
      title: "What RTP adds, and what it does not",
      paragraphs: [
        "RTP usually runs over UDP, using UDP's ports and checksum. It adds a payload type, a sequence number, a timestamp, and a source identifier. The sequence number increases by one per packet and lets the receiver detect loss and restore order. The timestamp records when the first sample in the packet was taken, which the receiver uses to schedule playback and to measure jitter.",
        "RTP does not guarantee delivery, ordering, or timeliness, and it does not assume the network provides them. Its companion, RTCP, sends periodic reports on reception quality. RFC 3550 ties those reports to the flow and congestion control functions of other transports.",
        "Media is often inelastic: an encoder produces a fixed or controlled rate, so the stream does not grab all the bandwidth as a TCP transfer can, but it also cannot simply slow down when the path is congested. Adaptive bitrate encoding is how a media sender reduces its load.",
        "Forward error correction, redundant audio frames, and selective retransmission of important video packets are common additions. They are profile and implementation choices, not part of RTP itself.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "game-over-tcp",
          prompt:
            "A multiplayer game sends 30 position updates a second over TCP. On a path with 2% loss, players see freezes followed by jumps. Explain the freezes and sketch a UDP design.",
          answer:
            "Each lost segment holds every later update in the receiver's TCP buffer until the retransmission arrives, at least a round trip later. When it does, all the queued, now-stale updates are delivered at once, which is the jump. Over UDP, send the full current state, or a delta from the last acknowledged state, in each datagram with a sequence number. The receiver drops anything older than what it already has, so a lost update is simply replaced by the next one. Events that must not be lost, such as a purchase, get their own acknowledgment and retry.",
        },
        {
          id: "custom-file-transfer",
          prompt:
            "A team wants to replace TCP with a custom UDP protocol for bulk file copies between data centres, 'because UDP is faster'. What would they have to build, and what does RFC 8085 recommend?",
          answer:
            "Sequence numbers, acknowledgments, retransmission with RTT estimation and backoff, duplicate handling, flow control so the receiver is not overrun, path-MTU sizing, and congestion control that competes fairly with TCP within an order of magnitude. RFC 8085 recommends using an existing IETF transport for reliable, ordered delivery. A UDP transfer that is faster than TCP on a shared path is usually faster because it ignores congestion.",
        },
        {
          id: "dns-truncated",
          prompt:
            "A resolver's UDP query gets a response with the TC bit set. What happens next, and what breaks if a firewall allows only UDP on port 53?",
          answer:
            "The answer did not fit in the UDP response, so the resolver repeats the query over TCP to port 53 and reads a length-prefixed message. If TCP port 53 is blocked, any answer too large for UDP, such as a large DNSSEC response or a long record set, fails or times out. RFC 7766 makes TCP support required for exactly this reason.",
        },
        {
          id: "call-goes-silent",
          prompt:
            "A VoIP call through a home NAT works, but after the user is on hold for a few minutes with no media flowing, the remote side can no longer reach them. Why, and how do real-time applications handle it?",
          answer:
            "UDP has no connection for the NAT to track, so the NAT keeps a mapping only while packets keep flowing and removes it after an idle timeout. RFC 4787 asks for at least two minutes, but many devices use less. Return traffic that arrives after the mapping expires is dropped. The application sends small keepalives during silence, no more often than every 15 seconds and with some jitter, and must still be able to detect a broken session and re-establish it.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "udp-when-used",
      statement:
        "UDP offers only a minimal, non-guaranteed datagram service. It is used by applications that do not need TCP's level of service or that want services, such as multicast or broadcast delivery, that TCP does not offer.",
      sourceId: "rfc1122",
      locator: "Section 4.1.1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tcp-unicast-in-order",
      statement:
        "TCP provides a reliable, in-order, byte-stream service, corrects loss by retransmission, and supports unicast delivery.",
      sourceId: "rfc9293",
      locator: "Section 2.2, Key TCP Concepts",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "fast-retransmit-three-dupacks",
      statement:
        "A receiver should send an immediate duplicate ACK for each out-of-order segment. The sender treats three duplicate ACKs as an indication of loss and retransmits the missing segment without waiting for the retransmission timer.",
      sourceId: "rfc5681",
      locator: "Section 3.2, Fast Retransmit/Fast Recovery",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "dns-prefers-udp",
      statement:
        "DNS can use datagrams or a virtual circuit; datagrams are preferred for queries for lower overhead and better performance, and zone transfers must use TCP. UDP messages are limited to 512 bytes, and longer ones are truncated with the TC bit set. UDP queries may be lost or reordered, so a retransmission strategy is required; a client should try other servers before repeating a query to the same one. Over TCP, each message has a two-byte length prefix.",
      sourceId: "rfc1035",
      locator: "Sections 4.2, 4.2.1, and 4.2.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "dns-id-field",
      statement:
        "The DNS header ID is a 16-bit identifier chosen by the querier and copied into the reply, so the requester can match replies to outstanding queries.",
      sourceId: "rfc1035",
      locator: "Section 4.1.1, Header section format (ID)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "dns-tcp-required",
      statement:
        "Most DNS transactions use UDP; TCP is used for zone transfers and often for messages over 512 bytes. All general-purpose DNS implementations must support both UDP and TCP, resolvers may use TCP before UDP, and blocking DNS over TCP will probably cause resolution failures or timeouts.",
      sourceId: "rfc7766",
      locator: "Section 1, Introduction; Section 5, Transport Protocol Selection",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rtp-service",
      statement:
        "RTP provides payload type identification, sequence numbering, timestamping, and delivery monitoring for real-time data, and typically runs over UDP for its multiplexing and checksum. It does not ensure timely delivery, guarantee delivery, or prevent out-of-order delivery.",
      sourceId: "rfc3550",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rtp-seq-and-timestamp",
      statement:
        "The RTP sequence number increments by one per packet and may be used to detect loss and restore packet sequence. The timestamp reflects the sampling instant of the first octet and must come from a clock that allows synchronization and jitter calculations.",
      sourceId: "rfc3550",
      locator: "Section 5.1, RTP Fixed Header Fields",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rtcp-feedback",
      statement:
        "RTCP's primary function is feedback on the quality of data distribution, related to the flow and congestion control functions of other transport protocols. The recommended fixed minimum interval between RTCP reports is 5 seconds.",
      sourceId: "rfc3550",
      locator: "Section 6, RTP Control Protocol -- RTCP; Section 6.2, RTCP Transmission Interval",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rtp-inelastic",
      statement:
        "RTP data is often inelastic. It does not expand to take all available bandwidth as a TCP stream can, but it also cannot arbitrarily reduce its load to relieve congestion.",
      sourceId: "rfc3550",
      locator: "Section 10, Congestion Control",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-congestion-obligation",
      statement:
        "UDP has no inherent congestion control, and applications can send at line rate. Applications using UDP must employ mechanisms to prevent congestion collapse and establish fairness, and should control the aggregate rate they send to each destination.",
      sourceId: "rfc8085",
      locator: "Section 1, Introduction; Section 3.1, Congestion Control Guidelines",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-timer-guidelines",
      statement:
        "Latency estimates should average recent samples, for example with an EWMA, per destination. Samples must not come from ambiguous retransmitted exchanges (Karn's algorithm). Expiry of a loss-detection timer must be treated as congestion. An initial estimate of 1 second should be used.",
      sourceId: "rfc8085",
      locator: "Section 3.1.1, Protocol Timer Guidelines",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-bulk-and-low-volume",
      statement:
        "Bulk-transfer applications over UDP should implement TFRC, TCP-like window-based congestion control, or a scheme that competes fairly with TCP within an order of magnitude. Low data-volume applications should send on average no more than one datagram per RTT to a destination, with an exponentially backed-off interval starting at 1 second when they cannot estimate the RTT.",
      sourceId: "rfc8085",
      locator: "Sections 3.1.2 and 3.1.3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-message-size",
      statement:
        "Applications should not send UDP datagrams that exceed the path MTU. Without path MTU discovery they should stay under the default effective MTU: the smaller of 576 bytes and the first-hop MTU for IPv4, and 1280 bytes for IPv6. Messages split across datagrams should be split so each can be received and retransmitted independently.",
      sourceId: "rfc8085",
      locator: "Section 3.2, Message Size Guidelines",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-reliability-guidance",
      statement:
        "Applications needing reliable delivery or ordering over UDP must implement it themselves, and their retransmissions are subject to congestion control. Where possible they should instead use an IETF transport that provides these features. TCP is not as heavyweight as often claimed.",
      sourceId: "rfc8085",
      locator: "Section 3, UDP Usage Guidelines; Section 3.3, Reliability Guidelines",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-middlebox-state",
      statement:
        "Middleboxes create per-flow UDP state when they see a new flow and delete it after an idle period; return traffic after that is dropped. DNS works because its exchanges complete within seconds. Keepalives, when needed, should be sent no more than every 15 seconds with jitter, and do not replace recovery from broken sessions. NATs are required to keep state for at least 2 minutes, but many use shorter timeouts.",
      sourceId: "rfc8085",
      locator: "Section 3.5, Middlebox Traversal Guidelines",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc1122", note: "Section 4.1.1: who UDP is for." },
    { sourceId: "rfc9293", note: "Section 2.2: TCP's service, for contrast." },
    { sourceId: "rfc5681", note: "Section 3.2: fast retransmit, used in the voice example." },
    { sourceId: "rfc1035", note: "Sections 4.1.1 and 4.2: DNS over UDP and TCP." },
    { sourceId: "rfc7766", note: "Sections 1 and 5: TCP is required for DNS." },
    { sourceId: "rfc3550", note: "Sections 1, 5.1, 6, 6.2, and 10: RTP and RTCP." },
    { sourceId: "rfc8085", note: "Sections 1, 3, 3.1.1–3.1.3, 3.2, 3.3, and 3.5." },
  ],
} satisfies LessonBody;
