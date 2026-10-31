import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m02",
  status: "complete",
  readingMinutes: { min: 12, max: 18 },
  thesis:
    "Ethernet carries a frame between neighbors on one link-layer segment, and switches, ARP, and Neighbor Discovery let a host turn a same-subnet IP address into a MAC address and deliver to it directly.",
  outcomes: [
    "Explain why a frame carries MAC addresses when the packet already carries IP addresses.",
    "Describe how a switch learns source MACs, forwards, floods, and ages entries.",
    "Separate broadcast domains from collision domains and say what VLANs change.",
    "Trace ARP (IPv4) and Neighbor Discovery (IPv6) from IP address to MAC address, including caching.",
    "Walk through one same-subnet exchange and say which addresses stay fixed and which change per hop.",
  ],
  coverage: [
    {
      bullet: "Ethernet frames and MAC addresses.",
      notes:
        "• Frame = dest MAC, src MAC, EtherType, payload, FCS; EtherType says what rides inside (IPv4, ARP, IPv6, VLAN tag).\n• MAC is 48 bits, flat, and names an interface on one link; IP names a host on a network and is topological. They solve different jobs (why.05).\n• Unicast, broadcast (ff:ff:ff:ff:ff:ff), and multicast (group bit set) destinations.\n• NIC accepts frames for its own MAC, broadcast, and joined multicast groups; everything else is dropped in hardware.\n• FCS is a CRC: detects corruption, does not repair it; bad frames are silently discarded.\n• Frame size is bounded by MTU (payload typically 1500 bytes).\n• A router strips and rebuilds the frame at every hop; the packet inside survives.",
    },
    {
      bullet: "Switch learning and forwarding.",
      notes:
        "• Switch keeps a MAC table: MAC → port (plus VLAN), built by reading the source MAC of each arriving frame.\n• Forward decision uses the destination MAC: known unicast → one port; unknown unicast, broadcast, multicast → flood to every other port in the VLAN.\n• Never send a frame back out the port it arrived on.\n• Entries age out (commonly a few minutes) so moved hosts and dead ports are forgotten.\n• Host moves: next frame from the new port overwrites the entry; until then, traffic goes to the old port.\n• Table is finite: a MAC flood can fill it and force flooding (fail-open eavesdropping risk).\n• Loops in a switched topology make broadcasts circulate forever; spanning tree blocks redundant links.\n• Switches do not look at IP; they are data plane at layer 2 only.",
    },
    {
      bullet: "Broadcast domains and collision domains.",
      notes:
        "• Collision domain: shared medium where simultaneous senders corrupt each other (hubs, old half-duplex CSMA/CD).\n• Switched full-duplex links give each port its own collision domain, so collisions effectively vanish.\n• Broadcast domain: every device that receives a layer-2 broadcast. A switch does not shrink it; a router (or VLAN boundary) does.\n• Bigger broadcast domain = more ARP, DHCP, and discovery noise reaching every host.\n• Rule of thumb: switch splits collision domains, router splits broadcast domains.\n• Routers do not forward link-layer broadcasts; that is why each subnet sits behind its own router interface.",
    },
    {
      bullet: "ARP and ARP caching.",
      notes:
        "• Need: sender knows next-hop IP, needs its MAC to build the frame (why.32).\n• Request: broadcast 'who has IP X, tell Y' with sender's IP/MAC; reply is unicast from X's owner.\n• ARP rides directly in Ethernet (own EtherType), not in IP.\n• Cache: IP → MAC with timeouts (seconds to minutes); lookups skip the broadcast while fresh.\n• Receivers usually learn the requester's mapping from the request too.\n• Next hop only: for off-subnet traffic the host ARPs for the default gateway, not the remote server.\n• Gratuitous ARP announces or refreshes a mapping (failover, address change, duplicate detection).\n• No authentication: any host on the segment can answer or spoof, so cache poisoning is a local-segment attack.\n• Unanswered ARP → incomplete entry; packets queue briefly, then 'host unreachable'.",
    },
    {
      bullet: "VLANs and their purpose.",
      notes:
        "• A VLAN is a separate broadcast domain carved out of shared switch hardware, identified by a 12-bit ID in an 802.1Q tag.\n• Access port: untagged, belongs to one VLAN. Trunk port: carries many VLANs with tags between switches or to a router.\n• Purpose: contain broadcasts, separate departments or device classes, and apply different policy without new cables.\n• Each VLAN normally maps to one IP subnet; crossing VLANs requires a router (or layer-3 switch) — inter-VLAN routing.\n• Switch MAC tables are per VLAN: same MAC can exist in two VLANs.\n• A VLAN is segmentation, not authentication; misconfigured trunks or native VLANs leak traffic.\n• Tag adds 4 bytes to the frame; mind MTU on trunks.",
    },
    {
      bullet: "IPv6 Neighbor Discovery.",
      notes:
        "• ND replaces ARP for IPv6 and runs over ICMPv6, so it is IP-level rather than a separate link protocol.\n• Neighbor Solicitation asks for a link-layer address; Neighbor Advertisement answers it.\n• Solicitations go to a solicited-node multicast address, so only a few NICs wake up — not an everyone broadcast.\n• Neighbor cache tracks states (e.g. incomplete, reachable, stale, probe) and verifies reachability, not just mappings.\n• Router Solicitation / Router Advertisement tell hosts prefixes, default router, and whether to use SLAAC.\n• Duplicate Address Detection checks a new address with a solicitation before using it.\n• Hosts still need the next hop's MAC to build frames; ND just supplies it differently.\n• Unauthenticated by default: rogue RAs and spoofed NAs mirror ARP spoofing (RA guard, SEND mitigate).",
    },
    {
      bullet: "What happens when a host communicates with another host on the same subnet.",
      notes:
        "• Step 1: sender compares destination IP to its own address and mask; same prefix means deliver directly, no router.\n• Step 2: look in ARP/ND cache for the destination's MAC; on a miss, send ARP request (broadcast) or NS (solicited-node multicast) and wait for the reply.\n• Step 3: build the frame — dest MAC = target's MAC, src MAC = sender's, packet inside with unchanged IPs.\n• Step 4: switch learns the sender's MAC, looks up the destination, forwards out one port (or floods if unknown).\n• Step 5: receiving NIC accepts the frame, strips the header, hands the packet to IP, then transport.\n• Reply path repeats the same logic, usually with cache already warm.\n• Contrast with routed delivery: IPs stay end-to-end, MACs are rewritten per hop (why.06). Here there is only one hop, so dest MAC = destination host.",
    },
  ],
  mentalModel:
    "• Think of IP as the street address and MAC as the person at the door of this one building.\n• ARP/ND is the lobby intercom: 'who owns this IP? answer with your MAC'; the answer is cached.\n• The switch is a learned seating chart (MAC → port) built from who talks; unknown names get shouted to the whole room.\n• A VLAN is a wall inside the room: same hardware, separate broadcasts.\n• Anything past the wall or past the subnet goes through a router, which rebuilds the frame.",
  failuresAndSignals: [
    "Stale ARP/ND entry after a host or NIC changes — traffic goes to the old MAC and times out; flush the neighbor cache (arp -d / ip neigh flush) and the MAC table entry.",
    "Layer-2 loop without spanning tree — broadcast storm, CPU and link utilisation spike, MAC addresses flap between ports in switch logs.",
    "Wrong VLAN on an access port or missing VLAN on a trunk — link light is up, DHCP fails or the host sits on the wrong subnet; check port VLAN and trunk allowed list.",
    "Duplicate IP on a segment — ARP replies from two MACs, intermittent connectivity, 'duplicate address' log lines; arp shows the cache entry flapping.",
    "ARP/ND spoofing or rogue RA — traffic silently detoured through an attacker; symptoms are changed gateway MAC in the neighbor table or unexpected default routers.",
  ],
  whyAnchors: ["why.05", "why.06", "why.32"],
  connections: {
    before:
      "• Module 1: encapsulation (frame around packet) and MTU; here the frame header's fields get real meaning.",
    after:
      "• Module 3: IP addressing, masks, and the gateway/longest-prefix decision that determines whether you ARP for the destination or for the router.",
  },
  selfCheck: [
    {
      id: "mac-and-ip",
      prompt: "Why does a frame carry a MAC address if the packet already has an IP address?",
      answer:
        "The frame only needs to reach the next interface on this link; MACs are flat link-local names, while IP is the end-to-end, hierarchical address that survives routing (why.05).",
    },
    {
      id: "switch-unknown",
      prompt: "A frame arrives with a destination MAC not in the switch table. What happens?",
      answer:
        "The switch floods it out every other port in the VLAN, and learns the source MAC from it. When the destination replies, its MAC is learned and later frames go to one port.",
    },
    {
      id: "same-subnet-path",
      prompt:
        "Host A pings host B on its own subnet for the first time. Which resolution step occurs, and which addresses go in the frame?",
      answer:
        "A broadcasts an ARP request (or sends an NS over IPv6), gets B's MAC, and caches it. Frame: dest MAC = B, src MAC = A; packet: src IP = A, dest IP = B. No router is involved (why.06 contrasts this with routed hops).",
    },
    {
      id: "domains",
      prompt: "Does adding a switch enlarge a broadcast domain, a collision domain, or both?",
      answer:
        "A switch joins segments into one broadcast domain but gives each port its own collision domain. Only a router or a VLAN boundary splits broadcast domains.",
    },
  ],
} satisfies ChapterSummary;
