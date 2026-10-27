import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m04.icmp",
  status: "complete",
  objectives: [
    "Explain how an ICMP error finds its way back to the right socket, and which errors a router or a host sends.",
    "Read `ping` and `traceroute` output, including `*`, multiple addresses at one hop, and `!H`, `!N`, and `!X`.",
    "Map what a client sees (connection refused, no route to host, a timeout) to what happened on the network.",
    "Explain why a traceroute can show a different path from the one a TCP connection uses, and how to make probes resemble the real traffic.",
  ],
  prerequisites: ["m04.ospf-bgp"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "IP is best effort. A router can drop a packet because it has no route, because the TTL ran out, because the packet is too big, or because a filter forbids it. A host can receive a packet for a port with no listener. Without feedback, the sender would learn about each of these only by waiting for a timeout. Operators also need a way to ask 'is that host reachable?' and 'which routers are in the way?'",
      whyItMatters:
        "'Connection refused', 'No route to host', and a connect that hangs for two minutes are three different failures, and the difference is usually one ICMP message or its absence. `ping` and `traceroute` are the first tools anyone reaches for, and they are easy to misread: a hop of `* * *`, one slow hop in the middle, or a trace that dies at a firewall while the real service works.",
      idea: "ICMP is IP's own control channel, carried inside IP datagrams. An error message goes back to the original sender and quotes the IP header and the first bytes of the packet that failed, which include the ports, so the sender's kernel can pass it to the right socket. `ping` uses the Echo Request and Echo Reply messages. `traceroute` sends packets with deliberately small TTLs, so that each router on the way returns Time Exceeded and reveals itself.",
      howItWorks:
        "Destination Unreachable (IPv4 type 3) has a code for the reason: 0 network and 1 host, sent by a router with no route or whose last hop does not answer ARP; 3 port, sent by the destination host; 4 fragmentation needed; and 13 administratively prohibited, for filtered packets. Time Exceeded (type 11) code 0 means the TTL reached zero in transit. ICMP never reports errors about ICMP errors, broadcasts, or non-initial fragments. The receiving host passes each error to its transport protocol. UDP passes it to the application. TCP treats network, host, and source-route unreachable as soft errors and keeps trying. It treats protocol and port unreachable as hard errors and may abort. `ping` sends Echo Requests with an identifier and a sequence number, and prints each reply's round-trip time and TTL. `traceroute` sends three probes at TTL 1, then 2, and so on. By default, the probes are UDP to an unlikely port, starting at 33434 and increasing by one per probe. Each router whose TTL check fails answers with Time Exceeded from one of its own addresses. The destination answers with Port Unreachable, which ends the trace.",
      whyItWorks:
        "The error carries enough of the original packet to identify the connection, so the source can act on it without any router keeping state. Traceroute needs no special support from routers, because decrementing the TTL and returning Time Exceeded are things every router must do anyway. RFC 1812 requires Time Exceeded explicitly because traceroute depends on it.",
      limits:
        "ICMP is itself unreliable. Routers and hosts rate-limit the errors they generate, firewalls drop ICMP, and a router may be configured not to send 'administratively prohibited'. Silence proves nothing. Network and host unreachable can result from a routing transient, so TCP must treat them as hints. Ping proves only that ICMP Echo works, not that a TCP port is open. Traceroute shows which routers answered its own probes, in the forward direction only. Load balancing, filtering, and policy routing can treat those probes differently from your application's traffic.",
      connections:
        "Module 3 covered Time Exceeded and the 'fragmentation needed' message that Path MTU Discovery depends on. Module 5 covers TCP connection setup, where these errors surface as connect() failures. Module 13 uses ping, traceroute, and these errors in its investigations, starting with 'DNS resolves, but TCP connection establishment fails'.",
    },
    {
      type: "example",
      id: "connect-failures",
      title: "Four ways connect() fails, and the packet behind each",
      setup:
        "A Linux client at 10.0.0.15/24, with default gateway 10.0.0.1, runs `curl http://<address>:8080/` against four different targets. Watch `tcpdump -n 'icmp or tcp port 8080'` while it runs.",
      walkthrough: [
        "The host is up, but nothing listens on 8080. The SYN reaches it and the host answers with a TCP RST. `connect()` fails at once with ECONNREFUSED, 'Connection refused'. No ICMP is involved, and this answer proves the network path works.",
        "The client has no route for the address and no default route. The kernel fails the call before sending anything, with ENETUNREACH ('Network is unreachable') or EHOSTUNREACH ('No route to host'). `ip route get <address>` reproduces the error.",
        "The address is in a remote subnet whose last router gets no ARP reply. That router returns ICMP type 3, code 1, Host Unreachable. RFC 1122 calls this a soft error, which TCP must not treat as fatal, but Linux, like many stacks, aborts a connection that is still in SYN-SENT on such an error. `connect()` fails with EHOSTUNREACH as soon as the message arrives. `tcpdump` shows `ICMP host <address> unreachable` from the router's address.",
        "A firewall drops the SYN silently. Nothing comes back. Linux retransmits the SYN up to `tcp_syn_retries` times (6 by default, about 127 seconds), and `connect()` then fails with ETIMEDOUT. A firewall that rejects instead, with ICMP 'administratively prohibited' or a TCP RST, turns this into a fast failure.",
        "UDP version: `sendto()` to a closed UDP port succeeds, because nothing has gone wrong yet. The host returns Port Unreachable, and Linux reports ECONNREFUSED on a later call on the same socket. With `IP_RECVERR`, the full error can be read from the socket's error queue.",
      ],
      result:
        "A fast refusal means the host answered. A fast 'no route' means a routing table, yours or a router's, had no answer. A long timeout means something dropped the packets without telling anyone. Only the timeout leaves you guessing which hop is responsible.",
    },
    {
      type: "example",
      id: "reading-traceroute",
      title: "Reading a traceroute that does not match the connection",
      setup:
        "The same client's HTTPS requests to 198.51.100.20 are slow. `traceroute -n 198.51.100.20` prints the lines below. The addresses are documentation values.",
      walkthrough: [
        "`1  10.0.0.1  0.4 ms  0.3 ms  0.3 ms` and `2  192.0.2.1  2.1 ms  2.0 ms  2.2 ms`: the default gateway and the ISP router each returned Time Exceeded for all three probes.",
        "`3  203.0.113.9  9.8 ms  203.0.113.13  10.1 ms  203.0.113.9  9.9 ms`: two routers answered at the same TTL. Each probe had a different destination port, and a router before hop 3 spreads traffic over two equal-cost paths by hashing addresses and ports. Your TCP connection has one fixed set of ports, so it uses only one of these paths, and this output does not say which.",
        "`4  * * *`, then `5  198.51.100.1  41.0 ms ...`: hop 4 did not answer, but hop 5 did, so probes passed through hop 4 without trouble. That router does not send Time Exceeded, or rate-limits it. This is not packet loss on the path.",
        "`6  * * *` through `30  * * *`: the trace never reaches 198.51.100.20, yet HTTPS to it works. The destination's firewall drops UDP to high ports, so no Port Unreachable comes back. `traceroute -T -p 443 -n 198.51.100.20` sends TCP SYNs to port 443, which the firewall allows, and the last line shows the destination.",
        "The 41 ms jump at hop 5 is real only if later hops keep it. A router that replies slowly, or that is far away on the return path, can show a high time while later hops are faster. The time on each line covers the probe going out and the ICMP reply coming back, and the reply may take a different route.",
      ],
      result:
        "The trace was evidence about the probes, and the probes differed from the connection in protocol, ports, and the path their replies took. Making the probes resemble the real traffic, with TCP to the same port, makes the evidence more relevant, but it is still not the connection's own path.",
    },
    {
      type: "why",
      id: "why-traceroute-differs",
      questionId: "why.52",
    },
    {
      type: "comparison",
      id: "icmp-messages",
      title: "ICMP messages you will meet",
      columns: ["IPv4 type/code", "IPv6 type/code", "Sent by, and meaning"],
      rows: [
        {
          label: "Echo Request / Reply",
          cells: ["8 / 0", "128 / 129", "Any host. `ping`."],
        },
        {
          label: "Network unreachable",
          cells: ["3/0", "1/0 (no route)", "A router with no route, including no default."],
        },
        {
          label: "Host unreachable",
          cells: [
            "3/1",
            "1/3 (address unreachable)",
            "The last-hop router, when the host does not answer ARP or Neighbor Discovery.",
          ],
        },
        {
          label: "Port unreachable",
          cells: ["3/3", "1/4", "The destination host. No socket for a UDP datagram."],
        },
        {
          label: "Too big",
          cells: [
            "3/4 (fragmentation needed)",
            "2 (Packet Too Big)",
            "A router whose next link is too small for a packet that must not be fragmented.",
          ],
        },
        {
          label: "Administratively prohibited",
          cells: [
            "3/13",
            "1/1",
            "A router or firewall that filtered the packet. traceroute prints `!X`.",
          ],
        },
        {
          label: "Time Exceeded",
          cells: [
            "11/0",
            "3/0",
            "A router that decremented the TTL or Hop Limit to zero. traceroute's signal.",
          ],
        },
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "ICMP rules, rate limits, and Linux knobs",
      paragraphs: [
        "An ICMP error quotes the IP header plus at least the first 8 bytes of the failed datagram, which is where the UDP and TCP ports are. That is how the sender's kernel finds the socket. It also means the error must reach the sender: if the original packet's source address was rewritten by NAT, the NAT device has to translate the quoted header back.",
        "Linux rate-limits the ICMP it sends with `icmp_ratelimit`, by default at most one message per 1000 ms to each target, for the types in `icmp_ratemask`. The default mask covers Destination Unreachable, Source Quench, Time Exceeded, and Parameter Problem, but not Echo Reply. `icmp_msgs_per_sec` caps the total. Routers have similar limits, and RFC 1812 notes that routers may also limit Echo Replies to protect their CPU. Linux traceroute sends up to 16 probes at once by default (`-N`), and its manual warns that ICMP rate throttling can then lose some replies. `-N 1` or `-z` (a gap between probes) helps when one hop shows sporadic `*`.",
        "By default, Linux sends ICMP errors from the primary address of the interface the error leaves through, toward the sender. With `icmp_errors_use_inbound_ifaddr`, it uses the interface the bad packet arrived on instead. Routers vary in the same way, so the address traceroute prints for a hop may belong to an interface your packets never crossed.",
        "`net.ipv4.icmp_echo_ignore_all = 1` makes a Linux host ignore pings. A host that does not answer ping may still serve every TCP port. ICMPv6 must be rate-limited too, and RFC 4443 recommends a token bucket, so that bursty traceroute probes are not starved.",
        "A TCP connection that is already established does not abort on a soft error. It keeps retransmitting, and when it finally gives up, it reports either ETIMEDOUT or the last ICMP error it saw. Setting `IP_RECVERR` passes errors to the application immediately, which makes TCP less tolerant of routing transients.",
        "`ping` details: `-c` sets the count, `-W` the time to wait for a reply, `-t` the TTL, and the exit status is 0 if replies arrived, 1 if none did, and 2 for other errors, which makes it usable in scripts. The TTL in a reply hints at the reverse path length only if you know the starting value the other host used.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "star-hop",
          prompt:
            "A traceroute shows `4  * * *`, and hops 5 to 9 answer normally, ending at the destination. Is there packet loss at hop 4?",
          answer:
            "No. Probes with TTL 5 and above passed through hop 4 and reached later routers, so hop 4 forwards traffic. It just did not return Time Exceeded for its own probes, because it is configured not to, rate-limits ICMP, or a filter drops its replies. Loss on the path would show up at the destination and every hop after the problem, not at one hop alone.",
        },
        {
          id: "three-errors",
          prompt:
            "Three connects to port 5432 fail: one immediately with 'Connection refused', one within a second with 'No route to host', and one after about two minutes with 'Connection timed out'. What does each tell you?",
          answer:
            "Refused: the host received the SYN and answered with RST, so the path works but nothing listens on 5432. No route to host: a routing table, the client's own or a router's, had no way to reach the address, and a router may have sent ICMP Host Unreachable, which Linux treats as fatal during connection setup. Timed out: nothing came back for any SYN retransmission, which usually means a firewall or security group dropped the packets silently, or the host is down behind a router that does not report it.",
        },
        {
          id: "ping-ok-tcp-fails",
          prompt:
            "`ping 198.51.100.20` gets replies, but `curl https://198.51.100.20` times out. Why doesn't the successful ping rule out a network problem?",
          answer:
            "Ping tests ICMP Echo only. Firewalls and security groups filter by protocol and port, so they can allow ICMP and drop TCP 443, and policy routing or load balancers can send TCP along a different path. Test the actual traffic with `traceroute -T -p 443`, `nc -vz 198.51.100.20 443`, or a packet capture on both ends.",
        },
        {
          id: "udp-refused",
          prompt:
            "A UDP client's `sendto()` to 10.0.0.9:9999 succeeds, but the following `recvfrom()` fails with ECONNREFUSED. What happened?",
          answer:
            "The datagram reached 10.0.0.9, where no socket was bound to port 9999, so the host returned ICMP Port Unreachable. The send had already completed, so Linux reported the error on the socket's next call. UDP has no handshake, so this asynchronous ICMP error is the only way it learns that nobody is listening.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "icmp-purpose",
      statement:
        "ICMP is an integral part of IP that reports problems in datagram processing, such as an unreachable destination; it provides feedback, not reliability, and some datagrams may be lost without any report. No ICMP messages are sent about ICMP messages, and errors are sent only about fragment zero.",
      sourceId: "rfc792",
      locator: "Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "destination-unreachable",
      statement:
        "Destination Unreachable is type 3, with codes 0 net, 1 host, 2 protocol, 3 port, 4 fragmentation needed and DF set, and 5 source route failed. It carries the internet header plus the first 64 bits of the original data, which hold the ports, so the host can match it to a process. Codes 0, 1, 4, and 5 may come from a gateway, and 2 and 3 from a host. Echo is type 8 and Echo Reply type 0; the reply returns the request's data.",
      sourceId: "rfc792",
      locator: "Destination Unreachable Message; Echo or Echo Reply Message",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "host-icmp-rules",
      statement:
        "Every ICMP error includes the internet header and at least the first 8 data octets of the datagram that caused it. An ICMP error must not be sent in response to an ICMP error, a broadcast or multicast datagram, a link-layer broadcast, a non-initial fragment, or a datagram whose source is not a single host. A received Destination Unreachable must be reported to the transport layer; codes 0, 1, and 5 may be routing transients and are only a hint. UDP must pass all ICMP errors to the application.",
      sourceId: "rfc1122",
      locator:
        "Section 3.2.2, ICMP; Section 3.2.2.1, Destination Unreachable; Section 4.1.3.3, ICMP Messages",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tcp-soft-hard-errors",
      statement:
        "TCP must act on ICMP errors for the connection that caused them. Soft errors (IPv4 Destination Unreachable codes 0, 1, 5; Time Exceeded; Parameter Problem) must not abort the connection and should be made available to the application. Hard errors (codes 2 to 4) should abort it. Widespread implementations treat soft errors as hard during connection establishment.",
      sourceId: "rfc9293",
      locator: "Section 3.9.2.2, ICMP Messages",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-syn-sent-soft-errors",
      statement:
        "Many TCP implementations treat ICMP soft errors as hard errors for connections in SYN-SENT or SYN-RECEIVED, aborting the attempt; Linux has done so since version 2.0.0. This violates RFC 1122 Section 4.2.3.9 and is documented as widespread non-standard behaviour.",
      sourceId: "rfc5461",
      locator:
        "Section 4, Deployed Workarounds; Section 4.1, Context-Sensitive ICMP/TCP Interaction",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "router-unreachable-codes",
      statement:
        "A router sends Network Unreachable (code 0) when it has no route, including no default; Host Unreachable (code 1) when the destination on a directly connected network is not reachable, for example it does not answer ARP; and code 13, Communication Administratively Prohibited, when it filters a packet, though it may be configured not to send code 13.",
      sourceId: "rfc1812",
      locator: "Section 4.3.3.1, Destination Unreachable; Section 5.2.7.1, Destination Unreachable",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "router-icmp-limits",
      statement:
        "A router's ICMP messages must be sourced from an address of the interface they are transmitted on. A router should be able to rate-limit ICMP error messages, and may limit other messages such as Echo Replies, to protect reverse-path bandwidth and its own CPU and memory. ICMP Time Exceeded is required because traceroute depends on it.",
      sourceId: "rfc1812",
      locator:
        "Section 4.3.2.4, ICMP Message Source Address; Section 4.3.2.8, Rate Limiting; Section 5.3.1, Time to Live",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "icmpv6-types",
      statement:
        "ICMPv6 Destination Unreachable is type 1 with codes 0 no route, 1 administratively prohibited, 3 address unreachable (for example, link-address resolution failed), and 4 port unreachable. Packet Too Big is type 2, Time Exceeded is type 3 (code 0, hop limit exceeded), and Echo Request and Reply are 128 and 129. A node must rate-limit the ICMPv6 errors it originates, preferably with a token bucket that does not starve bursty traffic such as traceroute.",
      sourceId: "rfc4443",
      locator:
        "Section 2.4, Message Processing Rules (f); Sections 3.1, 3.2, and 3.3; Sections 4.1 and 4.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "traceroute-method",
      statement:
        "traceroute sends probes with increasing TTL, starting at 1, three per hop by default and up to 30 hops, and waits for ICMP Time Exceeded from each gateway; it stops on Port Unreachable or a TCP reset. The default UDP method starts at destination port 33434 and increments it per probe. `*` marks no reply, and `!H`, `!N`, `!P`, and `!X` mark host, network, protocol unreachable and administratively prohibited. `-T` uses TCP SYN to a constant port to pass firewalls, `-I` uses ICMP Echo, and `--sport` fixes the source port. By default 16 probes are sent at once (`-N`), and ICMP rate throttling can then lose some responses; `-z` sets a minimum gap between probes.",
      sourceId: "man-traceroute",
      locator:
        "DESCRIPTION; OPTIONS, -p, -N, -z, --sport; LIST OF AVAILABLE METHODS, default and tcp",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ecmp-hash",
      statement:
        "With equal-cost multipath, a router can choose a next hop by hashing the packet header fields that identify a flow, so a flow stays on one path while different flows spread across paths.",
      sourceId: "rfc2992",
      locator: "Section 1, Hash-Threshold; Section 2.2, Disruption",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-policy-by-port",
      statement:
        "Linux policy routing rules can select a routing table by IP protocol, source port, and destination port, as well as by addresses and firewall mark.",
      sourceId: "man-ip-rule",
      locator: "SYNOPSIS; DESCRIPTION; ipproto, sport, dport",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-icmp-sysctls",
      statement:
        "Linux's icmp_ratelimit (default 1000 ms) limits ICMP sent to each target for types in icmp_ratemask, whose default covers Destination Unreachable, Source Quench, Time Exceeded, and Parameter Problem; icmp_msgs_per_sec caps the total. icmp_echo_ignore_all ignores Echo Requests. With icmp_errors_use_inbound_ifaddr disabled, the default, ICMP errors are sent from the primary address of the exiting interface.",
      sourceId: "kernel-ip-sysctl",
      locator:
        "/proc/sys/net/ipv4: icmp_ratelimit, icmp_msgs_per_sec, icmp_ratemask, icmp_echo_ignore_all, icmp_errors_use_inbound_ifaddr",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-socket-errors",
      statement:
        "On Linux UDP sockets, fatal errors, including asynchronous ones from the network, are returned even when the socket is not connected, possibly on a later call; ECONNREFUSED means no receiver was associated with the destination. With IP_RECVERR, errors are queued for recvmsg with MSG_ERRQUEUE.",
      sourceId: "man-udp",
      locator: "DESCRIPTION, Error handling; ERRORS, ECONNREFUSED",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-tcp-errors",
      statement:
        "When a network error occurs, Linux TCP retries, and if that fails it reports ETIMEDOUT or the last received error; IP_RECVERR passes errors immediately, making TCP less tolerant of routing changes. tcp_syn_retries defaults to 6, about 127 seconds of retrying an initial SYN.",
      sourceId: "man-tcp",
      locator: "DESCRIPTION, Error handling; /proc interfaces, tcp_syn_retries",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-connect-errors",
      statement:
        "connect() fails with ECONNREFUSED when a stream socket finds no one listening, ENETUNREACH when the network is unreachable, and ETIMEDOUT on a timeout while attempting the connection.",
      sourceId: "man-connect",
      locator: "ERRORS, ECONNREFUSED, ENETUNREACH, ETIMEDOUT",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "linux-ehostunreach",
      statement:
        "EHOSTUNREACH means no valid routing table entry matches the destination address, and can be caused by an ICMP message from a remote router or by the local routing table.",
      sourceId: "man-ip",
      locator: "ERRORS, EHOSTUNREACH",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ping-behaviour",
      statement:
        "ping sends ICMP ECHO_REQUEST to elicit ECHO_RESPONSE and prints round-trip times and the reply's TTL. `-c` stops after a count, `-W` sets the wait for a response, `-t` sets the TTL, and the exit status is 1 if no reply arrived, 2 on other errors, and 0 otherwise. The reply's TTL depends on the initial value the remote system chose.",
      sourceId: "man-ping",
      locator: "DESCRIPTION; OPTIONS, -c, -t, -W; EXIT STATUS; TTL DETAILS",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    {
      sourceId: "rfc792",
      note: "Introduction, Destination Unreachable, and Echo: what ICMP is for.",
    },
    {
      sourceId: "rfc1122",
      note: "Sections 3.2.2, 3.2.2.1, and 4.1.3.3: when hosts send ICMP errors and what they do with them.",
    },
    { sourceId: "rfc9293", note: "Section 3.9.2.2: TCP's soft and hard ICMP errors." },
    {
      sourceId: "rfc5461",
      note: "Section 4.1: why Linux aborts connection attempts on soft errors.",
    },
    {
      sourceId: "rfc1812",
      note: "Sections 4.3.2.4, 4.3.2.8, 4.3.3.1, 5.2.7.1, and 5.3.1: what routers send, from which address, and how often.",
    },
    { sourceId: "rfc4443", note: "ICMPv6 types, codes, and rate limiting." },
    { sourceId: "rfc2992", note: "Sections 1 and 2.2: per-flow ECMP hashing." },
    { sourceId: "man-traceroute", note: "Probe methods, ports, and output annotations." },
    { sourceId: "man-ping", note: "Options, exit status, and TTL details." },
    { sourceId: "man-ip-rule", note: "Policy routing by protocol and port." },
    { sourceId: "kernel-ip-sysctl", note: "ICMP rate limits and source-address choice." },
    { sourceId: "man-udp", note: "Asynchronous ICMP errors on UDP sockets." },
    { sourceId: "man-tcp", note: "TCP error reporting and SYN retries." },
    { sourceId: "man-connect", note: "connect() error codes." },
    { sourceId: "man-ip", note: "EHOSTUNREACH." },
  ],
} satisfies LessonBody;
