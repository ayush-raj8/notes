import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m02.switching",
  status: "complete",
  objectives: [
    "Trace how a switch learns which port leads to each MAC address and decides whether to forward, filter, or flood a frame.",
    "Distinguish a collision domain from a broadcast domain, and say which devices bound each.",
    "Explain what a VLAN changes, how access and trunk ports carry it, and why traffic between VLANs needs a router.",
    "Inspect the forwarding table and VLAN membership of a Linux bridge.",
  ],
  prerequisites: ["m02.ethernet-and-mac"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Connect fifty hosts with one shared cable or a hub and every frame reaches every host, only one host can transmit at a time, and simultaneous transmissions collide. We want a box that delivers each frame only toward its destination, needs no configuration, and is invisible to the hosts, which keep addressing frames to each other's MACs as before.",
      whyItMatters:
        "Every rack, office LAN, Linux bridge, and container network is built from switching. Unknown-unicast flooding, broadcast storms, MAC table overflow, and a missing VLAN on a trunk all look like random packet loss or 'the host can reach some neighbours but not others' until you know the forwarding rule.",
      idea: "A switch learns from source addresses and forwards on destination addresses. When a frame arrives, it records that the source MAC is reachable through the arrival port. To forward, it looks up the destination MAC: if it knows the port, it sends the frame only there. If it does not, or the destination is broadcast or multicast, it floods the frame out every other port in the same VLAN. Entries expire after an idle period.",
      howItWorks:
        "The forwarding database maps (VLAN, MAC) to a port and a timestamp. For each frame: (1) record or refresh the source MAC against the ingress port; (2) if the destination is a known unicast MAC on another port, forward it out that port only; (3) if it is known on the same port, drop it, because the destination already saw it; (4) if it is unknown unicast, broadcast, or multicast without snooping state, flood it to all other ports in the VLAN. Each port is its own full-duplex link, so nothing collides. A VLAN splits one switch into several independent forwarding domains: an access port belongs to one VLAN and carries untagged frames, while a trunk port carries frames for many VLANs, each with a 4-byte 802.1Q tag holding the VLAN ID.",
      whyItWorks:
        "Every host eventually sends something, such as an ARP reply, a TCP ACK, or a DHCP request, and that frame teaches the switch where the host is. Flooding is a safe fallback: the destination still receives the frame, and its reply teaches the switch the missing entry. Aging lets a host move to another port, because a stale entry disappears and the next flood finds the new location.",
      limits:
        "Ethernet frames have no hop limit, so a loop of switches floods a broadcast forever and melts the network. Spanning tree, or a routed design, has to block redundant paths. A broadcast domain grows with every host and VLAN member: ARP, Neighbor Discovery, DHCP, and flooded unknown unicast reach all of them, which is why large networks are split by routers. When the forwarding table fills, a switch floods traffic it cannot learn, which an attacker can cause deliberately. A VLAN separates forwarding, not security on its own: a misconfigured trunk or native VLAN can leak frames between VLANs.",
      connections:
        "ARP and Neighbor Discovery, next, depend on broadcast and multicast reaching every host in the domain. The same-subnet lesson shows the switch as the only device between two hosts on one subnet. Module 4 covers the routers that join broadcast domains, and Module 13 investigates failures where one VLAN can't reach another.",
    },
    {
      type: "example",
      id: "learning-walkthrough",
      title: "A switch learns three hosts",
      setup:
        "A switch with an empty table has host A (00:00:5e:00:53:0a) on port 1, B (00:00:5e:00:53:0b) on port 2, and C (00:00:5e:00:53:0c) on port 3, all in VLAN 1. A sends a frame to B.",
      walkthrough: [
        "Frame A to B arrives on port 1. The switch records A on port 1. B is unknown, so the frame is flooded to ports 2 and 3. C's interface sees a destination that is not its own and drops the frame.",
        "B replies to A on port 2. The switch records B on port 2. A is known on port 1, so the reply goes out port 1 only. C sees nothing.",
        "A sends to B again. Both are known, so the frame goes out port 2 only. From now on, A and B talk without bothering C.",
        "C broadcasts an ARP request. The switch records C on port 3 and floods the broadcast to ports 1 and 2, as it does for every broadcast.",
        "A is unplugged and reconnected on port 4. Frames for A still go to port 1 until A sends a frame from port 4, which moves the entry, or until the entry ages out and the next frame for A is flooded.",
      ],
      result:
        "After one exchange in each direction, unicast between two hosts reaches only their two ports. Broadcasts and frames to unknown destinations still reach everyone in the VLAN.",
    },
    {
      type: "comparison",
      id: "domains",
      title: "Collision domains and broadcast domains",
      caption:
        "A collision domain is the set of interfaces whose transmissions can collide. A broadcast domain is the set of interfaces that receive a frame sent to ff:ff:ff:ff:ff:ff.",
      columns: ["Collision domain", "Broadcast domain"],
      rows: [
        {
          label: "Hub or shared cable",
          cells: ["One for all attached hosts.", "One for all attached hosts."],
        },
        {
          label: "Switch, one VLAN",
          cells: [
            "One per port, and none at all on full-duplex links.",
            "One for all ports. The switch floods broadcasts.",
          ],
        },
        {
          label: "Switch, several VLANs",
          cells: ["One per port, as above.", "One per VLAN."],
        },
        {
          label: "Router",
          cells: [
            "Each interface is its own.",
            "Each interface is its own. Routers do not forward link-layer broadcasts.",
          ],
        },
        {
          label: "Wi-Fi access point",
          cells: [
            "The radio channel is shared, so stations contend for it.",
            "The SSID's VLAN, usually bridged to the wired LAN.",
          ],
        },
        {
          label: "Why it matters today",
          cells: [
            "Mostly history on wired links. Modern switched Ethernet is full duplex.",
            "Sizing: every ARP, ND, and DHCP broadcast or multicast reaches every member.",
          ],
        },
      ],
    },
    {
      type: "comparison",
      id: "vlan-ports",
      title: "Access ports and trunk ports",
      columns: ["Access port", "Trunk port"],
      rows: [
        {
          label: "VLANs carried",
          cells: ["One.", "Many, each frame tagged with its VLAN ID."],
        },
        {
          label: "Frames on the wire",
          cells: [
            "Untagged. The switch assigns the port's VLAN on ingress and strips it on egress.",
            "Tagged with EtherType 0x8100 and a VLAN ID. One VLAN may be sent untagged (the native VLAN).",
          ],
        },
        {
          label: "Typical neighbour",
          cells: [
            "A host that knows nothing about VLANs.",
            "Another switch, a router, or a hypervisor host.",
          ],
        },
        {
          label: "On a Linux bridge",
          cells: [
            "`bridge vlan add dev eth1 vid 10 pvid untagged`.",
            "`bridge vlan add dev eth0 vid 10` and `vid 20`, tagged.",
          ],
        },
        {
          label: "On a Linux host",
          cells: [
            "Nothing to configure. The host just sends Ethernet frames.",
            "`ip link add link eth0 name eth0.10 type vlan id 10` creates an interface for one tagged VLAN.",
          ],
        },
      ],
    },
    {
      type: "prose",
      id: "why-vlans",
      title: "What a VLAN is for",
      paragraphs: [
        "A VLAN gives one physical switch several separate broadcast domains. Hosts in VLAN 10 and VLAN 20 can share a switch and still not see each other's broadcasts or frames, as if they were on two switches. That is used to keep a broadcast domain small, to put different trust levels such as guests, servers, and management on different subnets, and to carry many subnets over one cable to a hypervisor.",
        "Since each VLAN is its own broadcast domain, it is normally its own IP subnet. A host in VLAN 10 reaches a host in VLAN 20 only through a router or a layer-3 switch with an interface in each. That is also where a firewall can filter between them.",
      ],
    },
    {
      type: "example",
      id: "linux-bridge",
      title: "Looking inside a Linux bridge",
      setup:
        "Docker's `docker0`, libvirt's `virbr0`, and Kubernetes CNI bridges are Linux bridges: software switches in the kernel that learn and forward exactly as above.",
      walkthrough: [
        "`ip link add br0 type bridge` creates a bridge. `ip link set eth1 master br0` makes eth1 a bridge port.",
        "`bridge fdb show br br0` lists the forwarding database: each learned MAC, the port it was learned on, and the VLAN if filtering is on.",
        "`ip link set br0 type bridge ageing_time 300` sets how many seconds a learned entry is kept after the last frame from that MAC. `ip -d link show br0` prints the current bridge options.",
        "`ip link set br0 type bridge vlan_filtering 1` makes the bridge VLAN-aware. Without it, the bridge ignores VLAN tags entirely.",
        "`bridge link set dev eth1 learning off` stops learning on a port. The bridge then floods any frame with no entry, which shows why learning matters.",
      ],
      result:
        "The kernel bridge is a learning switch you can inspect. Its table and flags explain where a container's or VM's frames go.",
    },
    {
      type: "callout",
      id: "loops",
      tone: "caution",
      title: "A loop takes down the whole broadcast domain",
      scope: "common-implementation",
      body: "Connect two switch ports to each other, directly or through another switch, with spanning tree off, and the first broadcast circles forever, multiplying at every flood. Frames carry no TTL to stop it. Switches see the same source MAC on alternating ports, so their tables flap. Spanning tree (802.1D/802.1w), or a routed design with no layer-2 loops, prevents this. On a Linux bridge, check `stp_state` in `ip -d link show br0`, and set it with `ip link set br0 type bridge stp_state 1` before you add redundant links.",
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Flooding that does not stop, and other edge cases",
      paragraphs: [
        "Asymmetric routing can cause persistent unknown-unicast flooding. If a host sends through one router but replies come back through another, the switch may never see the host's own frames often enough to keep its entry, so every frame toward it is flooded.",
        "Multicast is flooded like broadcast unless the switch snoops IGMP or MLD to learn which ports joined each group. Linux bridges support this with the `mcast_snooping` option. Turning snooping off, or a querier missing on the VLAN, changes which hosts receive IPv6 Neighbor Discovery and other multicast traffic.",
        "The 802.1Q tag carries a 12-bit VLAN ID and a 3-bit priority. Its layout comes from IEEE 802.1Q, which is not in this course's source catalog. The tag adds 4 bytes to the frame, so equipment that carries tagged frames accepts frames 4 bytes longer than untagged ones.",
        "Cloud VPCs do not give you a real broadcast domain. The provider's virtual switch typically answers ARP for you and delivers only to known instances. Unknown-MAC flooding and gratuitous ARP failover may not behave as on a physical LAN.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "unknown-unicast",
          prompt:
            "A switch has just rebooted. Host A sends a unicast frame to host B. Which ports does the frame leave from, and why is that not a bug?",
          answer:
            "Every port in A's VLAN except the one A is on. B's location is unknown, so flooding is the only way to make sure B receives it. B's reply teaches the switch B's port, and later frames go only there.",
        },
        {
          id: "domain-count",
          prompt:
            "Twenty hosts are on one switch: ten in VLAN 10 and ten in VLAN 20, and a router has one interface in each. How many broadcast domains are there, and how does an ARP request from a VLAN 10 host travel?",
          answer:
            "Two. The ARP request is flooded to the nine other VLAN 10 hosts and the router's VLAN 10 interface. It never reaches VLAN 20, because the switch keeps VLANs separate and the router does not forward link-layer broadcasts.",
        },
        {
          id: "trunk-missing",
          prompt:
            "A VM on VLAN 30 can reach other VMs on the same hypervisor but not its gateway on the physical network. Other VLANs work. What do you check first?",
          answer:
            "Whether VLAN 30 is allowed and tagged on the trunk between the hypervisor and the physical switch, and on the switch's port toward the gateway. Local traffic works because the hypervisor's bridge switches it. Anything that needs the trunk is dropped when the VLAN is missing there.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "linux-bridge-learning",
      statement:
        "A Linux bridge port learns MAC addresses from received traffic by default; with learning off, the bridge floods any traffic for which it has no forwarding-database entry.",
      sourceId: "man-bridge",
      locator: "bridge link set, learning on or learning off",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-bridge-flooding",
      statement:
        "Per-port flags control flooding on a Linux bridge: unknown unicast (flood), broadcast (bcast_flood), and multicast with no multicast database entry (mcast_flood). All default to on.",
      sourceId: "man-bridge",
      locator: "bridge link set, flood, bcast_flood, mcast_flood",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-bridge-pvid",
      statement:
        "On a Linux bridge, a VLAN marked pvid is assigned to untagged frames at ingress, and a VLAN marked untagged is sent without a tag at egress.",
      sourceId: "man-bridge",
      locator: "bridge vlan add, pvid and untagged",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-bridge-fdb",
      statement:
        "The bridge forwarding database holds known Ethernet addresses on a link, and `bridge fdb show` lists it.",
      sourceId: "man-bridge",
      locator: "bridge fdb - forwarding database management; bridge fdb show",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-bridge-ageing",
      statement:
        "A Linux bridge's ageing_time is the number of seconds a MAC address is kept in the forwarding database after a packet is received from it, after which the entry is cleaned up.",
      sourceId: "man-ip-link",
      locator: "BRIDGE Type Support, ageing_time",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-vlan-filtering",
      statement:
        "With vlan_filtering disabled, a Linux bridge does not consider the VLAN tag when handling packets.",
      sourceId: "man-ip-link",
      locator: "BRIDGE Type Support, vlan_filtering",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-vlan-link",
      statement:
        "Linux creates a VLAN interface with `ip link add link DEVICE name NAME type vlan id VLANID`, using 802.1Q or 802.1ad.",
      sourceId: "man-ip-link",
      locator: "VLAN Type Support",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "vlan-tag-ethertype",
      statement:
        "A VLAN tag is a prefix identified by an EtherType and followed by another tag or the frame's EtherType. The 802.1Q C-Tag carries customer VLAN and priority information.",
      sourceId: "rfc9542",
      locator: "Section 3, Ethernet Protocol Parameters (tags)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "c-tag-value",
      statement: "EtherType 0x8100 identifies the 802.1Q customer VLAN tag.",
      sourceId: "iana-ieee-802-numbers",
      locator: "EtherType table, row 8100",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "man-bridge", note: "Linux bridge learning, flooding, FDB, and VLAN filters." },
    { sourceId: "man-ip-link", note: "Bridge ageing time, VLAN filtering, and VLAN interfaces." },
    { sourceId: "rfc9542", note: "Section 3 on Ethernet tags." },
    { sourceId: "iana-ieee-802-numbers", note: "The 0x8100 VLAN tag EtherType." },
  ],
} satisfies LessonBody;
