import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m01.planes-and-connections",
  status: "complete",
  objectives: [
    "Separate the control plane, which decides where traffic should go, from the data plane, which moves each packet.",
    "Identify the control plane and the data plane on a Linux host.",
    "Contrast connection-oriented and connectionless communication by where state lives and what setup buys.",
    "State what a TCP connection does not promise, and which devices in the middle hold connection state anyway.",
  ],
  prerequisites: ["m01.encapsulation"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "A router answers two different questions. Where should traffic for 10.2.0.0/16 go? That needs knowledge of the whole network and changes when links fail. What should happen to this packet now? That must be answered millions of times a second. Endpoints face a similar split: should they agree on shared state before sending, or should every message stand alone?",
      whyItMatters:
        "Outages are diagnosed by which plane failed. A routing protocol that withdraws a route breaks reachability even though every link is up; a full queue drops packets even though the routes are right. And a service owner who thinks a TCP connection is a reserved pipe will misread every timeout, keepalive, and NAT drop.",
      idea: "Split each router into a control plane that computes the forwarding table and a data plane that applies it to each packet. Split communication into connection-oriented service, where the endpoints first set up shared state, and connectionless service, where each message carries everything needed to deliver it.",
      howItWorks:
        "Control plane: static configuration and routing protocols such as OSPF inside a network and BGP between networks exchange reachability, and the router installs the best routes in its forwarding table. Data plane: for each packet, look up the destination by longest prefix match, decrement the TTL, rewrite the link header, and queue the packet on the output interface. Connections: TCP's handshake creates a control block at each end holding the addresses, ports, sequence numbers, and windows; after that, every segment is interpreted against that state. UDP has no setup: each datagram carries its ports and addresses and is handled on its own. IP underneath both is connectionless: every datagram is forwarded independently.",
      whyItWorks:
        "The two planes run at different speeds and fail differently. Routing can take milliseconds to seconds to converge because the data plane keeps forwarding with the last table in the meantime. Putting connection state only in the end hosts means a router can fail and traffic can reroute without breaking the connections that crossed it, as long as the path recovers before the endpoints give up.",
      limits:
        "A TCP connection is state at two hosts, not a reserved path: it promises no bandwidth or route, and on its own TCP does not detect a silent peer. Stateful devices in the middle, such as NAT, stateful firewalls, and L4 load balancers, do keep per-connection entries and expire idle ones, which is why long-lived idle connections send keepalives. Control-plane bugs cause loss that looks like a data-plane failure: a wrong or stale route silently black-holes traffic.",
      connections:
        "Module 4 separates routing from forwarding in depth (why.04 also appears there). Module 5 covers the handshake and what a successful connection still does not guarantee. Module 6 covers UDP service, and Module 10 covers NAT and stateful firewalls.",
    },
    {
      type: "comparison",
      id: "planes",
      title: "Control plane and data plane",
      columns: ["Control plane", "Data plane"],
      rows: [
        {
          label: "Question",
          cells: ["Where should traffic for each prefix go?", "Where does this packet go now?"],
        },
        {
          label: "Inputs",
          cells: [
            "Routing protocol messages, static routes, link state.",
            "One packet's headers and the forwarding table.",
          ],
        },
        {
          label: "Timescale",
          cells: ["Milliseconds to minutes per change.", "Nanoseconds to microseconds per packet."],
        },
        {
          label: "On a hardware router",
          cells: ["Software on a general-purpose CPU.", "Forwarding chips on the line cards."],
        },
        {
          label: "On Linux",
          cells: [
            "Routing daemons such as FRR or BIRD, DHCP clients, and `ip route add` install routes into the kernel.",
            "The kernel's forwarding path, enabled for transit traffic by `net.ipv4.ip_forward=1`. `ip route show` lists the table it uses.",
          ],
        },
        {
          label: "Typical failure",
          cells: [
            "Missing, wrong, or flapping routes.",
            "Queue drops, interface errors, MTU mismatches.",
          ],
        },
      ],
    },
    {
      type: "comparison",
      id: "connection-models",
      title: "Connection-oriented and connectionless communication",
      columns: ["Connection-oriented (TCP)", "Connectionless (UDP, IP)"],
      rows: [
        {
          label: "Setup",
          cells: [
            "A handshake before any data, which costs one round trip.",
            "None. The first datagram can carry data.",
          ],
        },
        {
          label: "State",
          cells: [
            "A control block at each endpoint for the life of the connection.",
            "No per-conversation state in the protocol. Any state is the application's.",
          ],
        },
        {
          label: "Ordering and reliability",
          cells: [
            "Bytes delivered in order; losses retransmitted.",
            "Each datagram arrives whole, or not at all, in any order, possibly twice.",
          ],
        },
        {
          label: "Failure detection",
          cells: [
            "Retransmission timeouts, resets, or optional keepalives.",
            "The application's own timeouts.",
          ],
        },
        {
          label: "Linux socket",
          cells: [
            "`SOCK_STREAM`: `connect`, `listen`, `accept`, then a byte stream.",
            "`SOCK_DGRAM`: `sendto` and `recvfrom`, one datagram per call.",
          ],
        },
        {
          label: "Typical use",
          cells: ["HTTP/1.1 and HTTP/2, SSH, database clients.", "DNS queries, media, QUIC."],
        },
      ],
    },
    {
      type: "example",
      id: "link-failure",
      title: "A link fails under a live SSH session",
      setup:
        "An SSH session runs between two data centres. A link on the path fails, and the network runs OSPF.",
      walkthrough: [
        "Data plane: packets sent toward the dead link are lost. The routers keep forwarding everything else with their current tables.",
        "Control plane: neighbours detect the failure, flood the change, recompute shortest paths, and install new routes. Until they finish, some packets are lost or loop until their TTL runs out.",
        "Endpoints: TCP sees missing acknowledgments, retransmits with growing timeouts, and the SSH user sees a pause.",
        "Once the new routes are installed, retransmissions get through on the new path. No router on either path knew the connection existed, so nothing has to be rebuilt.",
        "If the new path crosses a different NAT or stateful firewall, that device has no entry for the connection. It drops or resets the segments, and the session ends.",
      ],
      result:
        "Connection state at the ends lets the network reroute underneath a connection. Connection state in the middle is what breaks it.",
    },
    {
      type: "callout",
      id: "connected-udp",
      tone: "note",
      title: "A connected UDP socket is not a connection",
      scope: "linux",
      body: "Calling `connect` on a Linux UDP socket sends nothing on the wire. It records a default destination so `send` and `write` work without an address, and the socket then receives datagrams only from that peer. Each datagram is still sent and handled on its own, with no handshake and no shared state at the peer.",
    },
    {
      type: "expandable",
      id: "other-planes",
      title: "Management plane, SDN, and virtual circuits",
      paragraphs: [
        "Operators often name a third plane, the management plane: SSH, SNMP, configuration APIs, and telemetry used to configure and observe the device. It changes what the control plane is told, not individual packets.",
        "Software-defined networking moves the control plane out of each switch into a central controller that programs many data planes. The split is the same; only the location of the control plane changes.",
        "Some networks are connection-oriented inside the network itself. Telephone circuits and ATM virtual circuits reserve state in every switch along the path before data flows. MPLS label-switched paths are similar. The Internet's IP layer deliberately avoids this.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "which-plane",
          prompt:
            "Every interface is up and error-free, but traffic to one prefix is dropped at a router. Which plane do you suspect first, and what do you check?",
          answer:
            "The control plane: the router has no route, a wrong route, or a route to a dead next hop. Check the forwarding table, for example `ip route get <destination>` on Linux, and the routing protocol's state for that prefix.",
        },
        {
          id: "connection-meaning",
          prompt:
            "What does an established TCP connection guarantee about the network path between the two hosts?",
          answer:
            "Nothing about the path. It means both endpoints hold matching state. Routers keep no record of it, no bandwidth is reserved, and if the peer disappears silently, TCP notices only when it sends data that goes unacknowledged or a keepalive fails.",
        },
        {
          id: "idle-drop",
          prompt:
            "An idle database connection fails on first use after an hour, though neither host restarted. What is the likely cause?",
          answer:
            "A stateful device in the middle, such as NAT, a firewall, or a load balancer, expired its entry for the idle connection. The next segment is dropped or reset. Keepalives shorter than the device's idle timeout, or connection-pool health checks, prevent it.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ip-datagrams-independent",
      statement:
        "IP treats each datagram as an independent entity with no connections or virtual circuits. Selecting a path for a datagram is called routing.",
      sourceId: "rfc791",
      locator: "Section 1.4, Operation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "gateways-stateless-rerouting",
      statement:
        "Gateways are stateless and forward each datagram independently, so redundant paths can carry traffic around failed gateways and networks, and connection state is lost only if an endpoint fails.",
      sourceId: "rfc1122",
      locator: "Section 1.1.2 (b)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "routing-in-gateways",
      statement:
        "Routing complexity belongs in the gateways rather than the hosts, insulating host software from changes in the routing architecture.",
      sourceId: "rfc1122",
      locator: "Section 1.1.2 (c)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tcp-no-liveness",
      statement:
        "TCP is connection oriented but does not inherently include a liveness detection capability.",
      sourceId: "rfc9293",
      locator: "Section 2.2, Key TCP Concepts",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tcp-tcb",
      statement:
        "A TCP connection's state is kept in a Transmission Control Block holding the local and remote addresses and ports, buffer pointers, the retransmit queue, and the send and receive sequence variables.",
      sourceId: "rfc9293",
      locator: "Section 3.3.1, Key Connection State Variables",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "udp-transaction",
      statement:
        "UDP is transaction oriented, and delivery and duplicate protection are not guaranteed.",
      sourceId: "rfc768",
      locator: "Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-udp-connect",
      statement:
        "On Linux, calling connect on a UDP socket sets the default destination address so send and write work without one, while sendto to other destinations still works.",
      sourceId: "man-udp",
      locator: "DESCRIPTION, paragraph on connect(2)",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "control-data-plane-terms",
      statement:
        "Routing, the network-wide process that computes paths, is the control plane; forwarding, the per-router action of moving a packet from an input to an output link, is the data plane.",
      sourceId: "book-kurose",
      locator: "Chapter 4, Section 4.1, Overview of Network Layer",
      verification: "unread",
      subtle: false,
      scope: "teaching-simplification",
    },
  ],
  references: [
    { sourceId: "rfc791", note: "Section 1.4 states IP has no connections." },
    { sourceId: "rfc1122", note: "Section 1.1.2 explains why gateways keep no connection state." },
    { sourceId: "rfc9293", note: "Sections 2.2 and 3.3.1 describe TCP connection state." },
    { sourceId: "rfc768" },
    { sourceId: "man-udp", note: "Linux UDP socket behavior, including connect." },
    { sourceId: "book-kurose", note: "Teaching reference for the control and data planes." },
  ],
} satisfies LessonBody;
