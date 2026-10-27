import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m02.arp",
  status: "complete",
  objectives: [
    "Walk through an ARP request and reply field by field, and say who learns what from each.",
    "Explain why IPv4 needs a resolution step at all, and why ARP resolves on demand instead of announcing.",
    "Read the Linux neighbour table and its states, and predict when Linux reprobes an entry.",
    "Explain gratuitous ARP, ARP spoofing, and the failures that a stale ARP cache causes.",
  ],
  prerequisites: ["m02.switching"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "IP has decided the next hop for a datagram is 192.0.2.20, on the link attached to eth0. To build the frame, the host needs that neighbour's 48-bit MAC address. Nothing in 192.0.2.20 says what the MAC is: IPv4 addresses are 32 bits, assigned by the network, and move between machines, while MACs come with the hardware.",
      whyItMatters:
        "ARP sits under every IPv4 packet on Ethernet. A missing or stale entry shows up as 'host unreachable' on a working cable, as a failover VIP that keeps receiving traffic on the dead node, as a first packet that is slow, or as one host silently intercepting another's traffic. Large container and Kubernetes nodes also hit the kernel's neighbour table limits.",
      idea: "Ask the link. The sender broadcasts a request that says 'who has 192.0.2.20? tell 192.0.2.10, which is at this MAC.' Only the owner of 192.0.2.20 replies, by unicast, with its MAC. Both sides cache what they learned, and the cache entries expire so a changed mapping is eventually noticed.",
      howItWorks:
        "Request: EtherType 0x0806, destination MAC ff:ff:ff:ff:ff:ff, opcode 1, the sender's MAC and IP, the target IP, and an empty target MAC. Every host on the broadcast domain receives it. A host that already has an entry for the sender's IP updates it. If the target IP is its own, it adds the sender's mapping, swaps the sender and target fields, puts its own MAC in, sets opcode 2, and sends the reply straight to the requester's MAC. The requester caches the reply and sends the packet it was holding. On Linux, the neighbour table holds each entry with a state: INCOMPLETE while resolving, REACHABLE after a recent confirmation, STALE after the reachable time passes, then DELAY and PROBE when the stale entry is used and needs confirming.",
      whyItWorks:
        "Broadcast reaches every interface that could own the address, and only the owner answers, so no server or configuration is needed. Resolution happens only when there is traffic, so hosts never hold mappings for neighbours they don't talk to. Putting the sender's own mapping in the request means one exchange teaches both sides, since the target will almost certainly reply. Expiring entries, and reconfirming them with a unicast probe before resorting to broadcast, keeps the cache close to the truth without much traffic.",
      limits:
        "ARP has no authentication. Any host on the link can answer for any address, or send unsolicited replies, and others will cache them. ARP works only inside one broadcast domain: for an off-link destination the host resolves the router, never the remote host. A cached entry can stay wrong until it expires or is reprobed, so when an address moves to new hardware, the new owner has to announce itself. The first packet to a new neighbour waits for resolution and is dropped if the queue for that neighbour is full.",
      connections:
        "The same-subnet lesson uses ARP for the destination and, off-subnet, for the gateway. IPv6 replaces ARP with Neighbor Discovery in the next lesson but keeps the same cache states. Module 10 covers how firewalls and NAT sit above this, and Module 13 covers investigating a host that is up but unreachable.",
    },
    {
      type: "why",
      id: "why-arp",
      questionId: "why.32",
    },
    {
      type: "steps",
      id: "exchange",
      title: "One resolution on the wire",
      steps: [
        {
          title: "The kernel needs a MAC",
          body: "Host A (192.0.2.10, 00:00:5e:00:53:0a) sends a TCP SYN to 192.0.2.20 on its own subnet. Its neighbour table has no entry, so Linux creates one in INCOMPLETE and queues the SYN.",
        },
        {
          title: "A broadcasts a request",
          body: "Frame to ff:ff:ff:ff:ff:ff, EtherType 0x0806. The ARP payload is hardware type 1 (Ethernet), protocol type 0x0800, opcode 1 (request), sender 00:00:5e:00:53:0a / 192.0.2.10, target 00:00:00:00:00:00 / 192.0.2.20. `tcpdump` prints `ARP, Request who-has 192.0.2.20 tell 192.0.2.10, length 28`.",
        },
        {
          title: "Every host checks the target",
          body: "The switch floods the broadcast. Hosts that already cache 192.0.2.10 refresh that entry. Hosts whose address is not 192.0.2.20 stop there.",
        },
        {
          title: "B adds A and replies",
          body: "Host B (192.0.2.20, 00:00:5e:00:53:14) adds 192.0.2.10 → 00:00:5e:00:53:0a, then sends opcode 2 (reply) by unicast to 00:00:5e:00:53:0a, with itself as sender. `tcpdump` prints `ARP, Reply 192.0.2.20 is-at 00:00:5e:00:53:14, length 28`.",
        },
        {
          title: "A caches B and sends the SYN",
          body: "A's entry becomes REACHABLE with B's MAC, and the queued SYN goes out in a frame addressed to 00:00:5e:00:53:14. B's SYN-ACK needs no ARP, since B learned A from the request.",
        },
      ],
    },
    {
      type: "comparison",
      id: "linux-states",
      title: "Linux neighbour states, as `ip neigh` shows them",
      caption:
        "IPv4 ARP entries and IPv6 neighbour entries share one kernel subsystem and the same states. Timers are per interface under /proc/sys/net/ipv4/neigh/<dev>/.",
      columns: ["Meaning", "What Linux does next"],
      rows: [
        {
          label: "INCOMPLETE",
          cells: [
            "A request has been sent and no reply has arrived.",
            "Retransmits the broadcast about once a second, holding a few packets for that neighbour.",
          ],
        },
        {
          label: "REACHABLE",
          cells: [
            "The mapping was confirmed recently, by a reply or by forward progress such as new TCP ACKs.",
            "Uses it with no extra traffic until the reachable time, randomized around 30 s by default, passes.",
          ],
        },
        {
          label: "STALE",
          cells: [
            "The mapping is known but not recently confirmed.",
            "Keeps using it. The next packet sent moves it to DELAY.",
          ],
        },
        {
          label: "DELAY, PROBE",
          cells: [
            "A stale entry was used and is waiting for confirmation.",
            "Waits about 5 s for upper-layer confirmation, then sends unicast ARP probes to the cached MAC, then a broadcast.",
          ],
        },
        {
          label: "FAILED",
          cells: [
            "Probes ran out without a reply.",
            "Drops queued packets. The sender sees 'No route to host' or 'Destination Host Unreachable'.",
          ],
        },
        {
          label: "PERMANENT",
          cells: [
            "Added by hand with `ip neigh add ... nud permanent`.",
            "Never expires or probes. Removed only by an administrator.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "linux-arp",
      title: "Watching the cache on Linux",
      setup:
        "On host A, run `sudo tcpdump -n -e -i eth0 arp` in one terminal and use another terminal for the commands below.",
      walkthrough: [
        "`ip neigh flush dev eth0` empties the dynamic entries. `ping -c1 192.0.2.20` now triggers the request and reply shown above, and `ip neigh show 192.0.2.20` prints `192.0.2.20 dev eth0 lladdr 00:00:5e:00:53:14 REACHABLE`.",
        "Leave it idle for a minute and the state reads STALE. Ping again: the entry goes through DELAY and returns to REACHABLE, usually through a unicast probe to B's MAC rather than a broadcast.",
        "`ping -c1 192.0.2.99`, an unused address, shows INCOMPLETE and three broadcast requests one second apart, then FAILED. Ping prints `Destination Host Unreachable`, generated locally with no packet ever leaving the host.",
        "`ip -s neigh show` adds counters: how long since the entry was used and confirmed, and how many probes were sent.",
      ],
      result:
        "The neighbour table explains 'unreachable on the same subnet' before you look at a firewall: FAILED means nobody answered ARP.",
    },
    {
      type: "callout",
      id: "spoofing",
      tone: "caution",
      title: "Anyone on the link can claim any address",
      body: "A host can answer every ARP request with its own MAC, or send unsolicited replies, and neighbours will cache them. Pointing the gateway's IP at the attacker's MAC sends the LAN's outbound traffic through the attacker. ARP itself has no defence. Switch features such as dynamic ARP inspection, static entries for critical neighbours, and encryption above IP (TLS, SSH, IPsec) limit the damage.",
    },
    {
      type: "expandable",
      id: "gratuitous",
      title: "Probes, announcements, and failover",
      paragraphs: [
        "Before using an IPv4 address, a host can send an ARP probe: a request for its new address with the sender IP set to 0.0.0.0, so it does not pollute other caches if the address is taken. A reply means someone already has it.",
        "Once it starts using the address, the host sends ARP announcements, requests with the sender and target IP both set to its own address. Every host that cached the address updates its entry. This is often called gratuitous ARP.",
        "Failover depends on this. When keepalived or Pacemaker moves a virtual IP to a standby node, the new node sends gratuitous ARP so that clients and the gateway replace the old MAC at once instead of waiting for their entries to go stale. If the announcement is lost or filtered, traffic keeps going to the dead node for up to a minute.",
      ],
    },
    {
      type: "expandable",
      id: "linux-tunables",
      title: "Linux tunables that matter in production",
      paragraphs: [
        "`gc_thresh1`, `gc_thresh2`, and `gc_thresh3` (defaults 128, 512, 1024) bound the table. `gc_thresh3` is the hard maximum: when the table is full and nothing can be collected, new entries fail and the kernel logs a neighbour table overflow. Nodes running many containers or routing for a large flat subnet often raise them.",
        "`base_reachable_time_ms` (default 30000) sets how long an entry stays REACHABLE after confirmation, randomized between half and one and a half times the value. `delay_first_probe_time` (default 5 s) is the DELAY wait, and `ucast_solicit` and `mcast_solicit` (default 3 each) are the probe counts.",
        "`unres_qlen` limits how many packets wait per unresolved neighbour. The man page documents a default of 3; newer kernels also have a byte-based limit. Packets beyond it are dropped while resolution runs.",
        "Proxy ARP lets a router answer ARP for addresses it forwards to, so hosts with a wrong, too-wide mask still work. Linux adds proxy entries automatically when `proxy_arp` is enabled on the interface.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "who-learns",
          prompt:
            "After A's request for B and B's reply, which hosts on the LAN have learned a mapping, and why does B not need to send its own request back?",
          answer:
            "A learned B from the reply. B learned A from the request, because the request carries the sender's MAC and IP and B adds them before replying. Other hosts only refresh an entry for A they already had. They do not create new entries from a request that is not for them.",
        },
        {
          id: "off-link-arp",
          prompt: "Host 192.0.2.10/24 pings 198.51.100.7. Which IP address does it ARP for?",
          answer:
            "Its default gateway's, for example 192.0.2.1. 198.51.100.7 is off-link, so the frame goes to the router's MAC, and the remote host's MAC never matters to the sender.",
        },
        {
          id: "vip-failover",
          prompt:
            "A virtual IP moves from node 1 to node 2. Clients on the same subnet keep sending to node 1 for about a minute. What went wrong, and how does a correct failover avoid it?",
          answer:
            "Clients still hold an ARP entry mapping the VIP to node 1's MAC and keep using it until it goes stale and a probe fails. Node 2 should send gratuitous ARP announcements for the VIP when it takes over, so every cache replaces the MAC at once.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "arp-request-generation",
      statement:
        "When no mapping exists, the ARP module builds a request with its own hardware and protocol addresses as sender, the target protocol address, and opcode REQUEST, and broadcasts it on the link that routing chose.",
      sourceId: "rfc826",
      locator: "Packet Generation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "arp-reception-merge",
      statement:
        "A receiver updates an existing entry for the sender before looking at the opcode. If it is the target, it adds the sender's mapping and, for a request, swaps the fields, sets opcode REPLY, and sends the reply to the requester's hardware address.",
      sourceId: "rfc826",
      locator: "Packet Reception",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "arp-on-demand",
      statement:
        "ARP deliberately avoids periodic broadcasting and distributes mappings only as they are needed.",
      sourceId: "rfc826",
      locator: "Why is it done this way??",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "arp-mandatory-ethernet",
      statement:
        "Translating Internet addresses to link-layer addresses on Ethernet and IEEE 802 networks must be done by ARP.",
      sourceId: "rfc1122",
      locator: "Section 2.3.3, Ethernet and IEEE 802 Encapsulation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "arp-cache-flush-flood",
      statement:
        "An ARP implementation must flush out-of-date entries, should make the timeout configurable, and must prevent ARP flooding, with a recommended maximum of one request per second per destination.",
      sourceId: "rfc1122",
      locator: "Section 2.3.2.1, ARP Cache Validation",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "arp-queue",
      statement:
        "The link layer should save at least the latest packet for an unresolved address and send it once the address is resolved; otherwise the first packet of every exchange is lost.",
      sourceId: "rfc1122",
      locator: "Section 2.3.2.2, ARP Packet Queue",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-arp-reprobe",
      statement:
        "On Linux, an entry with no positive feedback, such as a TCP ACK, becomes stale. ARP then sends ucast_solicit unicast probes to the known MAC before broadcasting a new request, and sends requests only when data is queued.",
      sourceId: "man-arp",
      locator: "DESCRIPTION, paragraph on positive feedback",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-arp-tunables",
      statement:
        "Linux defaults: base_reachable_time_ms 30000 (randomized between half and 1.5 times), delay_first_probe_time 5 s, mcast_solicit 3, ucast_solicit 3, retrans_time_ms 1000, unres_qlen 3, gc_thresh1/2/3 of 128, 512, and 1024.",
      sourceId: "man-arp",
      locator: "/proc interfaces",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "linux-proxy-arp",
      statement:
        "Linux adds a nonpermanent proxy ARP entry when it receives a request for an address it forwards to and proxy ARP is enabled on the interface.",
      sourceId: "man-arp",
      locator: "DESCRIPTION, paragraph on proxy arp",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-nud-states",
      statement:
        "Linux neighbour entries are in one of the states permanent, noarp, reachable, stale, none, incomplete, delay, probe, or failed. The IPv4 neighbour table is the ARP table.",
      sourceId: "man-ip-neighbour",
      locator: "DESCRIPTION; ip neighbour add, nud STATE",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "arp-probe",
      statement:
        "An ARP probe is a request with the sender IP set to all zeros, so that it does not pollute other caches, and the target IP set to the address being probed.",
      sourceId: "rfc5227",
      locator: "Section 2.1.1, Probe Details",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "arp-announcement",
      statement:
        "An ARP announcement sets both sender and target IP to the host's new address so that other hosts do not keep stale entries for it.",
      sourceId: "rfc5227",
      locator: "Section 2.3, Announcing an Address",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "arp-spoofing",
      statement:
        "A malicious host can answer all ARP requests with its own hardware address and claim every address on the network.",
      sourceId: "rfc5227",
      locator: "Section 5, Security Considerations",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc826", note: "The ARP packet, generation, reception, and design rationale." },
    { sourceId: "rfc1122", note: "Sections 2.3.2 and 2.3.3: cache validation and queueing." },
    { sourceId: "rfc5227", note: "ARP probes, announcements, and the spoofing risk." },
    { sourceId: "man-arp", note: "Linux reprobe behavior and neighbour tunables." },
    { sourceId: "man-ip-neighbour", note: "Linux neighbour states and `ip neigh`." },
  ],
} satisfies LessonBody;
