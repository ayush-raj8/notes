import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m01.what-a-network-must-do",
  status: "complete",
  objectives: [
    "Name the problems two programs face once they stop sharing memory: naming, a shared path, loss, delay, reordering, duplication, and corruption.",
    "Explain why the Internet keeps the network simple and puts reliability in the end hosts.",
    "Predict which failures a networked program must handle even when every device works as designed.",
  ],
  prerequisites: [],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Two processes on one Linux machine can talk through a pipe or shared memory. A write is visible to the reader once, in order, almost immediately, and the kernel knows exactly who the reader is. Put the processes on different machines and none of that holds. The bytes cross links and devices owned by other people, shared with other traffic, and any of them can queue, drop, or corrupt the data.",
      whyItMatters:
        "Every networked service inherits these failures. A request can vanish, arrive twice, arrive late, or arrive after a newer request. Code that treats a remote call like a local function call works in testing and fails under load, during a deploy, or when a link flaps.",
      idea: "List the jobs a network has to do, then decide who does each one. The destination needs a name. The data needs a path that other conversations share. Somebody has to cope with loss, delay, reordering, duplication, and corruption. The Internet gives the network only naming and best-effort forwarding, and leaves recovery to the end hosts.",
      howItWorks:
        "Naming: an IP address identifies a host interface, a port identifies a conversation on that host, and DNS maps human names to addresses. Shared path: data is cut into packets, each carrying its destination, and routers forward each packet hop by hop, so many conversations share every link. Loss: a router drops packets when its queue is full or it has no route; a checksum mismatch makes the receiver discard the packet, so corruption usually becomes loss. Delay: each hop adds propagation, transmission, processing, and a queueing delay that changes with load. Reordering and duplication: packets of one conversation can take different paths, and a sender that retransmits after a lost acknowledgment creates a duplicate. End-host protocols number the data, acknowledge it, retransmit what is missing, and discard what they have already seen.",
      whyItWorks:
        "Packet switching lets idle capacity go to whoever has data now, instead of reserving a circuit per conversation. Because routers keep no per-conversation state, the network can route around a failed link without breaking the conversations crossing it. Only the end hosts know whether the data reached the program that needed it, so they are the right place to check delivery.",
      limits:
        "End hosts can recover lost data but cannot make the path faster: each retransmission costs at least one more round trip, and nothing promises a deadline or a bandwidth. A sender that gets no reply cannot tell a lost request from a lost reply or a slow peer, so after a timeout the outcome is unknown. Reliability inside the transport does not tell the application whether the remote program acted on the data.",
      connections:
        "The next lesson assigns these jobs to layers. Module 3 covers addressing, Module 4 builds the forwarding tables, Module 5 shows how TCP turns loss, reordering, and duplication into a byte stream, and Module 11 covers delay, timeouts, and retries.",
    },
    {
      type: "comparison",
      id: "problems-and-owners",
      title: "What can go wrong, and who handles it",
      caption:
        "The last column names a Linux command that shows evidence of the problem. Later modules explain each output.",
      columns: ["Cause", "Who handles it on the Internet", "Where Linux shows it"],
      rows: [
        {
          label: "Naming",
          cells: [
            "Many hosts, and many programs on each host.",
            "IP addresses pick the host, ports pick the conversation, DNS maps names to addresses.",
            "`ip addr` for local addresses, `ss -tan` for ports in use.",
          ],
        },
        {
          label: "Shared path",
          cells: [
            "No dedicated wire between the two programs.",
            "Routers forward each packet independently by its destination address.",
            "`ip route get 192.0.2.10` shows the next hop the kernel would use.",
          ],
        },
        {
          label: "Loss",
          cells: [
            "Full queues, missing routes, bit errors caught by checksums.",
            "TCP retransmits. A UDP application decides for itself.",
            "`ip -s link` shows drops and errors per interface; `nstat -az TcpRetransSegs` counts TCP retransmissions.",
          ],
        },
        {
          label: "Delay",
          cells: [
            "Distance, link speed, and queues that grow under load.",
            "Nobody removes it. Protocols measure it and size their timers.",
            "`ping` reports round-trip times; `ss -ti` shows TCP's smoothed RTT.",
          ],
        },
        {
          label: "Reordering",
          cells: [
            "Parallel paths and load balancing across links.",
            "TCP places bytes by sequence number. UDP passes datagrams up in arrival order.",
            "`ss -ti` adds a reordering field once TCP has adjusted for reordering on a connection.",
          ],
        },
        {
          label: "Duplication",
          cells: [
            "A retransmission after a lost acknowledgment, or a link-level retry.",
            "TCP discards bytes it already delivered. Duplicate requests above TCP are the application's job.",
            "Application logs show the same request id twice.",
          ],
        },
        {
          label: "Corruption",
          cells: [
            "Bit errors on a link or in faulty hardware.",
            "Checksums at the link, IPv4 header, and TCP or UDP layers detect it, and the packet is dropped.",
            "RX error counters in `ip -s link`.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "lost-reply",
      title: "A payment request whose reply never arrives",
      setup:
        "A checkout service sends `POST /charge` to a payment service and waits up to two seconds. No reply arrives.",
      walkthrough: [
        "Possibility one: the request was lost before it reached the payment service. Nothing was charged.",
        "Possibility two: the payment service charged the card, and the reply was lost or the connection reset on the way back.",
        "Possibility three: the payment service is still working, and the reply will arrive after the timeout.",
        "From the client, all three look the same: silence until the timer fires.",
        "A blind retry is correct in case one and charges the card twice in cases two and three.",
      ],
      result:
        "The network cannot tell the client which case happened, so the application has to make retries safe. The usual fix is an idempotency key: the client sends the same key on every attempt, and the server returns the first result instead of charging again.",
    },
    {
      type: "callout",
      id: "tcp-does-not-remove-ambiguity",
      tone: "caution",
      title: "A reliable transport does not make a remote call reliable",
      body: "TCP delivers bytes in order while the connection is alive. If the connection breaks after the request bytes left the client, the client still does not know whether the server read them or acted on them. Timeouts, retries, and idempotency belong in the application.",
    },
    {
      type: "expandable",
      id: "packet-vs-circuit",
      title: "Why packets instead of reserved circuits",
      paragraphs: [
        "The telephone network reserved a fixed share of every link for the length of a call. That guarantees capacity, but the reservation sits idle whenever nobody talks, and a failed switch drops every call through it.",
        "Computer traffic is bursty: a page load sends a lot for a second and then nothing. Packet switching lets bursts from many hosts share a link, at the cost of queues that form when bursts collide. Those queues are where most variable delay and most loss come from.",
        "The Internet design also assumes the hosts must tolerate wide variation in bandwidth, delay, loss, reordering, and maximum packet size, because a packet may cross networks with very different properties.",
      ],
    },
    {
      type: "expandable",
      id: "corruption-becomes-loss",
      title: "Why corruption usually looks like loss",
      paragraphs: [
        "A receiver that finds a bad checksum cannot know which bits are wrong, so it discards the whole unit. The Ethernet frame check sequence, the IPv4 header checksum, and the TCP and UDP checksums all work this way.",
        "The sender sees only a missing acknowledgment, which is the same signal a queue drop produces. These checksums are short, so rare corruption can pass all of them and reach the application. TLS adds a cryptographic integrity check that also catches it.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "duplicate-source",
          prompt:
            "No device on the path copies packets, yet the server receives the same TCP payload twice. How?",
          answer:
            "The sender's packet arrived, but the acknowledgment was lost or late, so the sender's timer fired and it retransmitted. TCP at the receiver recognizes the repeated sequence numbers and delivers the bytes to the application only once.",
        },
        {
          id: "where-reliability-lives",
          prompt:
            "Why does the Internet put retransmission in the end hosts rather than in every router?",
          answer:
            "Routers forward each packet independently and keep no per-conversation state, which keeps them fast and lets traffic reroute around failures. Only the end hosts know whether data reached the program that needed it, so they are the place that can check and repair delivery.",
        },
        {
          id: "timeout-meaning",
          prompt: "A client times out waiting for a reply. What does it know about the request?",
          answer:
            "Nothing definite. The request may have been lost, processed with the reply lost, or still be in progress. A retry is safe only if the operation is idempotent or carries a key the server uses to detect repeats.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ip-no-delivery-guarantee",
      statement:
        "IP is a connectionless datagram service with no end-to-end delivery guarantees. Datagrams may arrive damaged, duplicated, out of order, or not at all, and the layers above IP provide reliable delivery when it is required.",
      sourceId: "rfc1122",
      locator: "Section 1.1.3, Internet Layer",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ip-scope",
      statement: "IP has no mechanisms to add end-to-end reliability, flow control, or sequencing.",
      sourceId: "rfc791",
      locator: "Section 1.2, Scope",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "gateways-stateless",
      statement:
        "Gateways forward each IP datagram independently and keep no connection state. State for flow control and reliability lives in the hosts, so it is lost only if an end point fails, and redundant paths can carry traffic around failed gateways.",
      sourceId: "rfc1122",
      locator: "Section 1.1.2 (b)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tolerate-variation",
      statement:
        "The Internet design assumes hosts tolerate wide variation in bandwidth, delay, packet loss, packet reordering, and maximum packet size.",
      sourceId: "rfc1122",
      locator: "Section 1.1.2 (d)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ttl-bounds-lifetime",
      statement:
        "The IP Time to Live is set by the sender and reduced at each point that processes the datagram. When it reaches zero the datagram is destroyed, which bounds how long a datagram can exist in the network.",
      sourceId: "rfc791",
      locator: "Section 1.4, Operation; Section 3.1, Time to Live",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc1122", note: "Section 1.1 describes the architectural assumptions." },
    { sourceId: "rfc791", note: "Sections 1.2 to 1.4 state what IP does and does not do." },
  ],
} satisfies LessonBody;
