import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m10.connectivity-failures",
  status: "complete",
  objectives: [
    "Map a client symptom (timeout, refused, reset, 502/503) to the edge device most likely causing it.",
    "Estimate SNAT port exhaustion toward one destination from the port range and the TIME_WAIT timeout.",
    "Use conntrack, ss, tcpdump, and the rule set to find where a flow stops.",
  ],
  prerequisites: ["m10.load-balancing"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Between client and server sit NATs, firewalls, and balancers, each with its own state and rules.\n• Any of them can stop a flow while both endpoints are healthy.\n• The client sees only a few symptoms: timeout, refused, reset, or an HTTP error from a proxy.",
      whyItMatters:
        "• 'It works from my laptop' and 'it fails one in a hundred times' are usually edge-state problems, not code.\n• Picking the right layer first saves hours of packet captures in the wrong place.",
      idea: "• Each symptom narrows the culprit: silence means something dropped; RST or ICMP means something answered.\n• Edge devices fail in four ways: a rule says no, state is missing or expired, state ran out, or the backend pool is empty.\n• Find the last hop where the packet is seen, then ask which of the four applies there.",
      howItWorks:
        "• Timeout: DROP rule, missing route, security group, expired NAT mapping, full conntrack table.\n• Refused / RST: nothing listening, REJECT tcp-reset, NAT or firewall answering a packet with no state.\n• 502/503/504: the balancer is up, but its upstream failed, is marked down, or is too slow.\n• Works inside, fails outside: listener on 127.0.0.1, missing DNAT/port forward, no hairpin.",
      whyItWorks:
        "• Endpoints only report what reaches them, so the symptom tells you whether anything replied.\n• Edge state is per flow, so failures that come and go usually track timers, table sizes, or which backend was chosen.",
      limits:
        "• Symptoms overlap: a DROP and a dead host both look like a timeout.\n• Cloud filters and managed NATs often hide their counters; you infer from captures at both ends.\n• This lesson covers the edge. Application and DNS causes are in Modules 7, 8, and 13.",
      connections:
        "• NAT, firewalls, and load-balancing lessons in this module.\n• why.45 (primary in Module 13): healthy locally, unreachable remotely.\n• why.55 (Module 13): two healthy services failing across a NAT or security-group boundary.\n• Module 11: connection reuse (why.47) as the fix for port exhaustion.",
    },
    {
      type: "comparison",
      id: "symptoms",
      title: "Symptom → likely edge cause → first look",
      columns: ["Likely cause", "First look (Linux)"],
      rows: [
        {
          label: "connect() times out",
          cells: [
            "DROP rule or security group; no route back; asymmetric path through a stateful filter.",
            "`tcpdump` on both sides: does the SYN arrive? Does a SYN-ACK leave? `iptables -L -n -v` counters.",
          ],
        },
        {
          label: "Connection refused",
          cells: [
            "Nothing listening on that address:port, or REJECT with tcp-reset.",
            "`ss -ltnp` on the server: is it bound to 0.0.0.0 or only 127.0.0.1?",
          ],
        },
        {
          label: "Idle connection dies; next write hangs or resets",
          cells: [
            "NAT or firewall expired the mapping; packet with no state dropped or answered with RST.",
            "Compare idle time with device timeouts. `conntrack -L` for the flow.",
          ],
        },
        {
          label: "New connections fail intermittently under load",
          cells: [
            "Conntrack table full; SNAT ports exhausted to one destination; CGN per-subscriber port limit.",
            "`sysctl net.netfilter.nf_conntrack_count net.netfilter.nf_conntrack_max`; count entries to that destination.",
          ],
        },
        {
          label: "502 / 503 / 504 from the balancer",
          cells: [
            "No healthy upstream, upstream reset, or upstream slower than the proxy's timeout.",
            "Balancer logs and health state; hit the backend directly.",
          ],
        },
        {
          label: "Works from outside, fails from inside (same public name)",
          cells: [
            "No hairpin NAT: inside clients to the public address are not looped back.",
            "`tcpdump` on the router for the inside client's SYN to the public IP.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "snat-exhaustion",
      title: "Pods time out calling one external API",
      setup:
        "A node SNATs all pod egress to one IP. Pods open a new HTTPS connection per call to `198.51.100.10:443` and close it after. Linux defaults; no port range on the SNAT rule. Illustrative numbers.",
      walkthrough: [
        "• Every flow to the API shares dst IP, dst port, protocol, and the SNAT source IP. Only the source port can differ.",
        "• SNAT maps unprivileged ports into 1024–65535: 64,512 choices.",
        "• After close, the conntrack entry stays in TIME_WAIT for 120 s (default). Its port is held for that flow tuple.",
        "• Ceiling ≈ 64,512 / 120 ≈ 537 new connections/s to that one destination. At 800/s the NAT runs out of free tuples.",
        "• Symptom: some new connects to that API stall or time out. Other destinations are fine; CPU and the API look healthy.",
        "• Fixes, best first: reuse connections (keep-alive / pooling), add SNAT addresses, spread over more destination IPs, `--random-fully` to reduce collisions.",
      ],
      result:
        "• The limit is per destination tuple, not per node. Connection reuse removes it; more SNAT IPs only raise it.",
    },
    {
      type: "investigation",
      id: "edge-failures",
      causes: [
        "A filter drops or rejects the flow: host iptables/nftables, cloud security group or ACL, corporate edge.",
        "The service listens only on loopback or the wrong interface, so outside connections are refused.",
        "NAT is missing (no DNAT/port forward, no hairpin) or its mapping expired while idle.",
        "State ran out: conntrack table full, SNAT source ports exhausted to one destination, CGN port quota hit.",
        "Asymmetric routing: replies bypass the stateful device that saw the SYN, and are dropped as INVALID.",
        "The load balancer has no healthy upstream, or its timeout is shorter than the backend's work.",
      ],
      order: [
        "Classify the symptom from the client: timeout, refused, reset, ICMP error, or HTTP 5xx from a proxy.",
        "Test from just outside the server, then from the client's network, to find which boundary the failure crosses.",
        "On the server, confirm the listener address and port with `ss -ltnp`.",
        "Capture on both ends at once. Find the last point the SYN is seen and whether a reply is sent back.",
        "At that point, read rule counters, conntrack entries, and table usage.",
        "For 5xx, read the balancer's health state and logs before touching the network.",
      ],
      commands: [
        {
          command: "ss -ltnp",
          shows:
            "Listening sockets and their bound address. `127.0.0.1:8080` accepts only local clients; `0.0.0.0:8080` or `*:8080` accepts all.",
        },
        {
          command: "tcpdump -ni eth0 'tcp port 443 and host 198.51.100.10'",
          shows:
            "Whether SYNs arrive, whether SYN-ACKs or RSTs leave, and which addresses appear after NAT.",
        },
        {
          command: "iptables -L -n -v   (or: nft list ruleset)",
          shows:
            "Rules with packet counters. A DROP or REJECT counter that rises with each attempt is the culprit.",
        },
        {
          command: "conntrack -L -d 198.51.100.10",
          shows:
            "Tracked flows to that destination: state, remaining timeout, original and reply tuples (reveals the SNAT address and port).",
        },
        {
          command: "sysctl net.netfilter.nf_conntrack_count net.netfilter.nf_conntrack_max",
          shows: "Table use against the limit. Near the limit, new flows fail.",
        },
        {
          command: "curl -v https://api.example.com/  (direct to a backend IP with --resolve)",
          shows:
            "Whether the backend answers when the balancer is bypassed. Healthy direct + 502 via LB points at the proxy hop.",
        },
      ],
      implications: [
        "SYN never arrives at the server: the drop is upstream (security group, edge firewall, routing).",
        "SYN arrives, no SYN-ACK leaves: host firewall or nothing listening on that address.",
        "SYN-ACK leaves, client never sees it: return path or a stateful device that missed the SYN.",
        "Many TIME_WAIT entries to one destination near the port count: SNAT exhaustion.",
        "`nf_conntrack_count` at `nf_conntrack_max`: new flows to every destination are at risk.",
      ],
      distinguishing: [
        "DROP vs dead host: a dead host on the local subnet fails ARP; a DROP leaves the server's counters rising.",
        "Expired mapping vs server crash: the server still has the connection in `ss` as ESTABLISHED, but no packets reach it.",
        "Port exhaustion vs a full table: exhaustion affects one destination; a full table affects all new flows.",
        "Proxy timeout vs backend error: 504 means the proxy gave up waiting; 502 means it got an invalid or reset response.",
      ],
      mistakes: [
        "Opening a port on the host firewall when the cloud security group (or the reverse) is what blocks it.",
        "Testing from the server itself, where loopback and local routes skip every edge device.",
        "Changing a DNAT rule and expecting existing flows to move; flush their conntrack entries.",
        "REJECTing INVALID packets, which resets healthy connections.",
        "Raising the balancer's timeouts to hide a backend that is actually down.",
      ],
    },
    {
      type: "callout",
      id: "timeout-meaning",
      tone: "note",
      title: "Silence vs an answer",
      body: "• Timeout: nothing answered. Suspect drops, routing, or expired state.\n• Refused or RST: something answered. Find who: `tcpdump` shows the sender's address and TTL.\n• HTTP 5xx with a proxy's server header: the network to the proxy is fine.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "loopback-bind",
          prompt:
            "`curl localhost:8080` works on the server; from another host the same port gives 'connection refused'. Most likely cause?",
          answer:
            "The service is bound to 127.0.0.1. `ss -ltnp` shows it. Bind to 0.0.0.0 (or the interface address). A firewall DROP would give a timeout, not a refusal.",
        },
        {
          id: "exhaustion-math",
          prompt:
            "One SNAT IP, ports 1024–65535, TIME_WAIT entries held 120 s. Roughly how many new connections per second can go to a single destination IP:port?",
          answer:
            "About 64,512 / 120 ≈ 537 per second. Above that, new connections to that destination start failing; other destinations are unaffected.",
        },
        {
          id: "asymmetric",
          prompt:
            "A SYN goes out through firewall A; the SYN-ACK returns through firewall B. Both are stateful. What happens?",
          answer:
            "B has no state for the flow, so it sees a SYN-ACK with no SYN and drops it as INVALID (or not NEW). The client times out. Keep both directions on one stateful path or sync state.",
        },
        {
          id: "lb-503",
          prompt:
            "TLS to the balancer succeeds and it returns 503 for every request. Where do you look first?",
          answer:
            "Its upstream health state: all backends are probably marked down or the pool is empty. The network path to the balancer is already proven.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "conntrack-limits",
      statement:
        "nf_conntrack_max is the maximum number of allowed connection tracking entries and defaults to nf_conntrack_buckets; nf_conntrack_count is the number currently allocated. TCP TIME_WAIT entries time out after 120 s by default.",
      sourceId: "kernel-nf-conntrack-sysctl",
      locator: "nf_conntrack_max, nf_conntrack_count, nf_conntrack_tcp_timeout_time_wait",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "snat-port-range",
      statement:
        "Without a port range, SNAT maps source ports below 512 below 512, 512–1023 below 1024, and other ports to 1024 or above, keeping the original port where possible. --random-fully randomizes the source port with a PRNG.",
      sourceId: "man-iptables-extensions",
      locator: "TARGET EXTENSIONS: SNAT",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "exhaustion-estimate",
      statement:
        "The 537-per-second ceiling assumes every closed flow's conntrack entry holds its port for the full 120 s TIME_WAIT timeout and that the NAT never reuses such a tuple. It ignores port reuse heuristics and connections that close by RST.",
      sourceId: "kernel-nf-conntrack-sysctl",
      locator: "nf_conntrack_tcp_timeout_time_wait",
      verification: "checked",
      subtle: false,
      scope: "teaching-simplification",
    },
    {
      id: "no-mapping-handling",
      statement:
        "A NAT that receives non-SYN packets for a connection with no active mapping, for example after abandoning an idle connection, may drop them silently or answer with RST; the choice is left to the implementation.",
      sourceId: "rfc5382",
      locator: "Section 5, NAT Session Refresh",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "hairpin",
      statement:
        "A NAT must support hairpinning, relaying traffic from one internal host to another internal host's external address and port, using the external source address and port.",
      sourceId: "rfc4787",
      locator: "Section 6, Hairpinning Behavior (REQ-9)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cgn-limits",
      statement: "Carrier-grade NATs must support per-subscriber limits on external ports.",
      sourceId: "rfc6888",
      locator: "Section 3 (REQ-4)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "reject-vs-drop",
      statement:
        "REJECT sends an ICMP error (port unreachable by default) or a TCP RST and is otherwise like DROP; INVALID packets should be dropped rather than rejected.",
      sourceId: "man-iptables-extensions",
      locator: "TARGET EXTENSIONS: REJECT (IPv4-specific)",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "gateway-5xx",
      statement:
        "502 means a gateway or proxy received an invalid response from an inbound server; 504 means it did not receive a timely response.",
      sourceId: "rfc9110",
      locator: "Sections 15.6.3 and 15.6.5",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "kernel-nf-conntrack-sysctl", note: "Table size, count, TIME_WAIT timeout." },
    { sourceId: "man-iptables-extensions", note: "SNAT port mapping; REJECT." },
    { sourceId: "rfc5382", note: "Section 5: packets with no mapping." },
    { sourceId: "rfc4787", note: "Section 6: hairpinning." },
    { sourceId: "rfc6888", note: "Section 3: CGN port limits." },
    { sourceId: "rfc9110", note: "Sections 15.6.3 and 15.6.5: 502 and 504." },
    { sourceId: "conntrack-tools-manual", note: "conntrack -L for listing flows." },
  ],
} satisfies LessonBody;
