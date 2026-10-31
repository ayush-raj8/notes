# Progress

## Style from Module 8 onward

Lessons use notes style: short bullets in principle fields, tables and steps over essays. Why answers stay to a few short sentences.

## Chapter summaries (10–20 min)

Every module has a complete chapter summary under `src/content/subjects/cn/chapter-summaries/`, rendered on the module page as **Chapter notes (10–20 min)**. Benchmark is fixed in `CONTENT_STANDARDS.md` and enforced by `assessChapterSummary` (full syllabus bullet coverage, primary why anchors, notes-style fields, self-checks). These are not syllabus lessons and do not change the 67-lesson count.

## Module 8 — HTTP

All four lessons `complete` and registered (notes style). Why: 01, 23, 24, 27, 28, 29, 30, 54.

## Module 11 — Performance

Four lessons complete (notes style). Why: 40–44, 46–49. Count was 50 of 67 after this module.

## Module 12 — Sockets

Four lessons complete: lifecycle, multiplexing, kernel buffers, Linux tools. No primary why questions. Count 54 of 67.

## Module 13 — Practical Troubleshooting

Thirteen lessons complete (notes style): investigation method plus twelve scenario lessons, each with an `investigation` section. Why: 45, 50, 55 (primary); also embeds 20, 40, 46, 49, 53 where catalogued. Count 67 of 67. Shell/e2e examples now open the written `m13.method` lesson.

## Module 9 — TLS

All four lessons `complete`: `m09.why-tls`, `m09.certificates`, `m09.handshake`, `m09.limits`. Why: 19, 25, 26, 31. Sources added: RFC 5280, RFC 6125. Count 42 of 67.

## Module 10 — NAT, Firewalls and Load Balancing

All four lessons `complete` (notes style): `m10.nat`, `m10.firewalls`, `m10.load-balancing`, `m10.connectivity-failures`. Prerequisites chain from `m09.limits`. No `viz` (`nat-table` is unimplemented). `m10.connectivity-failures` has an optional investigation section and an SNAT port-exhaustion worked example. Why: 37, 38 (in `m10.nat`), 53 (in `m10.load-balancing`); why.45 and why.55 are linked by text only. Count 46 of 67. The unwritten example in the shell and e2e tests is now `m11.metrics`; the pending-why example is why.41.

- Sources added after opening them (all HTTP 200): RFC 2663, 2979, 3022, 4787, 4864, 5382, 6092, 6888, iptables(8), iptables-extensions(8), the kernel nf_conntrack sysctl page, the conntrack-tools manual, the HAProxy 2.8 configuration manual, and nginx's upstream module. RFC 9110 Sections 3.7, 15.6.3, and 15.6.5 were reopened.
- Open research: some prose is not a claim and has no checked source. That covers cloud security groups being stateful and subnet ACLs stateless; X-Forwarded-For; which errno a client gets for each REJECT type; whether Linux conntrack reuses a TIME_WAIT tuple for a new SYN (the 537 connections/s ceiling assumes it does not and is labelled a teaching simplification); the kernel's "table full" log text; HAProxy `slowstart` and `fastinter` semantics beyond the parameter names; and Linux IPVS. nft(8), conntrack(8), and ipvsadm(8) are not on man7.org and are not cited.

## Module 11 — Performance and Reliability

All four lessons `complete` (notes style): `m11.metrics`, `m11.bdp-and-loss`, `m11.timeouts-and-retries`, `m11.partial-failure`. Prerequisites chain from `m10.connectivity-failures`. No `viz` (`bandwidth-delay` is unimplemented). Topics follow the catalog: reuse, keepalive, pooling, backpressure, and partitions live in `m11.partial-failure`; tail latency and cascading retries in `m11.timeouts-and-retries`. Worked examples in every lesson (RTT-bound vs throughput-bound request, cross-region BDP copy, a three-layer retry storm, fan-out tail, stale pooled connection). Why: 41, 42 (in `m11.bdp-and-loss`); 43, 44, 46 (in `m11.timeouts-and-retries`); 40, 47, 48, 49 (in `m11.partial-failure`). Count 50 of 67. The unwritten example in the shell and e2e tests is now `m12.lifecycle`; the pending-why example is why.50.

- Sources added after opening them (all HTTP 200): RFC 2923, RFC 7567, RFC 8961, the gRPC Deadlines and Retry guides, AWS's "Exponential Backoff And Jitter" post, and the curl man page. Reopened: RFC 3550, 5681, 6298, 6928, 7323, 8085, 8446, 9110, 9112, 9113, 9293, tcp(7), socket(7), send(2), the kernel IP sysctl page, and `include/net/tcp.h`.
- Open research: some prose is not a claim. That covers the 200 km/ms fibre figure and the 5,600 km example; `ss -ti` and `ping` spread as jitter or RTT tools; ICMP deprioritisation; "bufferbloat" as a term; the 10% retry budget, 2–3 attempts, and retry-at-one-layer advice (design judgment); the full-jitter formula (the AWS post shows formulas only as images); hedged requests; the 350 s balancer idle timeout; send() returning once bytes are in the local buffer (inferred from send(2)); TCP_KEEPIDLE and TCP_KEEPINTVL beyond their tcp(7) descriptions; MSS clamping; and partition behaviour. "The Tail at Scale" returned HTTP 403 and is not cited; AWS Builders' Library on timeouts renders by script and was not read.

## Subject layout

Computer Networks is a subject under Notes, not the site root. Its files are in `src/content/subjects/cn`. Routes start at `/cn`. The site root lists subjects. GitHub Pages publishes `main` to `https://ayush-raj8.github.io/notes/`.

## Milestone 1 — website foundation

Done in the working tree. Not committed.

- Vite, React, TypeScript, React Router, Zod, Vitest, Testing Library, Playwright, ESLint, and Prettier are installed.
- The shell has a sticky header, a menu button below 720px, and a lesson rail on module and lesson routes.
- All 13 modules, 67 lessons, and 55 why-questions are in the catalog. Unwritten lessons say so. Unknown routes show a not-found page.
- Progress is a versioned local store with export, confirmed import, and review scheduling. Learner completion is refused for lessons that are not written.
- The content gate, syllabus coverage test, and shell tests pass.

## Module 1 — Networking Foundations

All four lessons are written and `complete`: `m01.what-a-network-must-do`, `m01.layering`, `m01.encapsulation`, and `m01.planes-and-connections`. Bodies live in `src/content/subjects/cn/modules/m01`. No lesson uses a `viz` section.

- Why-answers written: `why.02`, `why.03`, `why.04`, `why.07`. All four are embedded in `m01.layering`.
- Checked claims: RFC 791 (Sections 1.2–1.4, 3.1), RFC 768, RFC 1122 (Sections 1.1.2, 1.1.3, 1.3.3), RFC 9293 (Sections 2.2, 3.3.1, 3.7, 3.7.1, 4), RFC 1191 (Sections 1, 7), RFC 8200 (Sections 4.5, 5), udp(7), and ip(7) via its linked IP_MTU_DISCOVER(2const) page.
- Open research: two non-subtle teaching claims cite Kurose and Ross and are `unread`, because the book has no URL and was not opened. They are `osi-seven-layers` in `m01.layering` and `control-data-plane-terms` in `m01.planes-and-connections`. Confirm the edition and section numbers, or replace them with ISO/IEC 7498-1 and RFC 7426 after adding those sources.

## Module 2 — Ethernet, Switching and Address Resolution

All five lessons are written and `complete`: `m02.ethernet-and-mac`, `m02.switching`, `m02.arp`, `m02.same-subnet`, and `m02.ipv6-nd`. Bodies live in `src/content/subjects/cn/modules/m02`. No lesson uses a `viz` section. Every claim is `checked`.

- Why-answers written: `why.05` (embedded in `m02.ethernet-and-mac`), `why.06` (in `m02.same-subnet`), and `why.32` (in `m02.arp`).
- Sources added after opening them: RFC 894, RFC 2464, RFC 4291, RFC 4862, RFC 5227, RFC 9542 (which obsoletes RFC 7042), the IANA IEEE 802 Numbers registry, and the Linux man pages arp(7), ip-neighbour(8), ip-link(8), bridge(8), and packet(7).
- Checked claims: RFC 826 (Packet format, The Problem, Packet Generation, Packet Reception, Why is it done this way??), RFC 894 (Frame Format), RFC 1122 (Sections 2.3.2.1, 2.3.2.2, 2.3.3, 3.3.1, 3.3.1.1), RFC 791 (Sections 2.2, 2.3, 3.1), RFC 8200 (Section 3), RFC 2464 (Sections 3, 7), RFC 4291 (Section 2.7.1), RFC 4861 (Sections 1, 3, 3.1, 4.3, 4.4, 5.2, 7.1.1, 7.1.2, 7.2.2, 7.3.1, 7.3.2, 10, 11.1), RFC 4862 (Sections 5.4, 5.4.2), RFC 5227 (Sections 2.1.1, 2.3, 5), RFC 9542 (Sections 2.1.1, 2.1.4, 3), the IANA EtherType rows 0806 and 8100, and the five man pages above.
- Open research: IEEE 802.3 and 802.1Q are not in the catalog. The lessons state the 4-byte frame check sequence, the 64-byte minimum frame, and the 12-bit VLAN ID in prose and say they come from IEEE. None of these facts is a claim. Add a public IEEE or other stable source before turning them into checked claims. Spanning tree is mentioned only as a caution and has no source.
- The reader and e2e foundation tests now use the unwritten `m03.ipv4-cidr` lesson as their example, and the home count reads 9 of 67. `tests/reader/lesson.test.tsx` also checks that `m02.arp` embeds `why.32`.

## Module 3 — IP Addressing and Subnetting

All five lessons are written and `complete`: `m03.ipv4-cidr`, `m03.special-addresses`, `m03.gateway-and-lpm`, `m03.ipv6`, and `m03.ttl-and-fragmentation`. Bodies live in `src/content/subjects/cn/modules/m03`. No lesson uses a `viz` section. Every claim is `checked`. The `worked-example` requirement is met by `m03.ipv4-cidr` (same-subnet decisions under /21, /25, and mismatched masks), and the gateway, IPv6, and fragmentation lessons also have examples. Prerequisites chain from `m02.same-subnet`.

- Why-answers written: `why.33` and `why.35` (embedded in `m03.gateway-and-lpm`) and `why.39` (in `m03.ttl-and-fragmentation`).
- Sources added after opening them, all HTTP 200: RFC 1918, RFC 3927, RFC 4193, RFC 5952, RFC 6724, RFC 6890, RFC 8981, the Linux man pages ip-route(8), ip-rule(8), ip-address(8), ipv6(7), IPV6_V6ONLY(2const), IP_MTU_DISCOVER(2const), proc_sys_net_ipv4(5), ping(8), and tracepath(8), and the kernel IP Sysctl documentation.
- Checked claims: RFC 4632 (Sections 2, 3.1, 5, 5.1, 5.2), RFC 791 (Sections 2.3, 3.1, 3.2), RFC 792 (Time Exceeded), RFC 1122 (Sections 3.2.1.3, 3.2.1.7, 3.3.1.1, 3.3.1.2), RFC 1191 (Sections 2, 3, 3.1, 4), RFC 1918 (Section 3), RFC 3927 (Sections 2.1, 2.7, 7), RFC 4193 (Section 3.1), RFC 4291 (Sections 2, 2.1, 2.2, 2.3, 2.4, 2.5.1, 2.5.6, 2.8), RFC 4861 (Sections 4.2, 4.6.2, 6.3.4), RFC 4862 (Sections 5.3, 5.5.3), RFC 5952 (Section 4), RFC 6724 (Section 2.1), RFC 6890 (Sections 2.2.1, 2.2.2), RFC 8200 (Sections 3, 4.5, 5, 8.2), RFC 8201 (Sections 3, 4, 6), RFC 8900 (Sections 3.3 to 3.9), RFC 8981 (Section 1), ip(7) special addresses, and the Linux pages above.
- Open research: some prose is not a claim and has no checked source. That covers the cloud metadata address 169.254.169.254, X-Forwarded-For and the PROXY protocol, the exact `tcpdump` and `ip route get` output for a cached PMTU, Linux rejecting an off-link gateway without `onlink` (inferred from the `onlink` description, not stated), the 0.0.0.0/1 plus 128.0.0.0/1 VPN technique, distributions defaulting to stable random IPv6 interface IDs (RFC 7217 not opened), MSS clamping, Happy Eyeballs fallback, and cloud providers reserving extra subnet addresses. RFC 3021 (/31 links) and the RFC 4291 appendix on Modified EUI-64 were not opened. The /31 text relies on RFC 4632's prefix table. Add sources before turning any of these into claims.
- Tests: the shell and e2e "unwritten lesson" examples now use `m04.routing-vs-forwarding` in module "4. Routing", and the home count reads 14 of 67. `tests/reader/lesson.test.tsx` checks that `m03.gateway-and-lpm` embeds why.33 and the why.35 answer and shows its worked example.

## Module 4 — Routing

All four lessons are written and `complete`: `m04.routing-vs-forwarding`, `m04.algorithms`, `m04.ospf-bgp`, and `m04.icmp`. Bodies live in `src/content/subjects/cn/modules/m04`. No lesson uses a `viz` section. Every claim is `checked`. Each lesson has exactly one principle section, a worked example, and three or four checks. Prerequisites chain from `m03.ttl-and-fragmentation`. OSPF and BGP are taught by what they decide: costs and Dijkstra inside an AS, and per-prefix policy, AS_PATH loop rejection, NEXT_HOP resolution through the IGP, and longest match across overlapping prefixes between ASes.

- Why-answers written: `why.34` and `why.36` (embedded in `m04.routing-vs-forwarding`) and `why.52` (in `m04.icmp`). `why.04` and `why.06` are linked from `m04.routing-vs-forwarding`, and `why.35` from `m04.ospf-bgp`, by text only, not embedded.
- Sources added after opening them, all HTTP 200: RFC 1058, RFC 1812, RFC 2992, RFC 4443, RFC 5461, RFC 5715, RFC 6811, traceroute(8) and connect(2) on man7.org, and the FRR Zebra documentation.
- Checked claims: RFC 792 (Introduction, Destination Unreachable, Echo), RFC 1058 (Sections 1.1, 2, 2.1, 2.2, 2.2.1, 2.2.2), RFC 1122 (Sections 3.2.2, 3.2.2.1, 4.1.3.3), RFC 1812 (Sections 2.2.3, 4.3.2.4, 4.3.2.8, 4.3.3.1, 5.2.1.1, 5.2.4.3, 5.2.7.1, 5.3.1), RFC 2328 (Sections 1, 1.1, 3, 4, 4.1, 4.2, 4.3, 7.1, 16.1, Appendix C.3), RFC 2992 (Sections 1, 2.2), RFC 4271 (Sections 3, 3.1, 3.2, 5.1.3, 5.1.4, 5.1.5, 9.1.1, 9.1.2, 9.1.2.1, 9.1.2.2 a–e, 9.1.4, 10), RFC 4443 (Sections 2.4 f, 3.1, 3.2, 3.3, 4.1, 4.2), RFC 5461 (Sections 4, 4.1), RFC 5715 (Section 1), RFC 6811 (Sections 1, 2), RFC 9293 (Section 3.9.2.2), the FRR Zebra page (Administrative Distance, ECMP, FIB push interface), the kernel IP Sysctl page (`ip_forward`, `fib_multipath_hash_policy`, the ICMP rate-limit and source-address sysctls), ip-route(8), ip-rule(8), traceroute(8), ping(8), udp(7), tcp(7), ip(7), and connect(2).
- Open research: some prose is not a claim and has no checked source. That covers giving a static route a distance above 110 to make it a backup (FRR's static-route syntax was not opened); the exact text of `tcpdump` ICMP lines and of `ip route get` with no route; which errno Linux returns for no route at all (the lesson says ENETUNREACH or EHOSTUNREACH without mapping each to a route type); NAT translating the header quoted inside an ICMP error; the customer-over-provider BGP preference convention; Kubernetes and load balancers advertising service addresses with BGP; and Linux removing connected routes when an address is deleted. The ECMP explanation in why.52 combines two checked facts (traceroute varies the port per probe, and ECMP hashes flow fields), but whether a given router hashes ports is deployment-specific. RFC 2453 (RIPv2), RFC 4950 (MPLS in ICMP), and BFD were not opened and are not cited.
- Tests: the shell and e2e "unwritten lesson" examples now use `m05.service-model` ("What TCP promises") in module "5. TCP in Depth", and the home count reads 18 of 67. `tests/reader/lesson.test.tsx` checks that `m04.routing-vs-forwarding` embeds why.36 and that `m04.icmp` embeds why.52 and can be marked finished.

## Milestone 2 — lesson renderer

Done. `LessonPage` renders principle, prose, steps, comparison, example, callout, expandable, why, checks, investigation, and viz sections. Checks reveal answers and record confidence. Complete lessons can be marked finished. Why pages show written answers with review buttons.

## Milestone 4 — TCP timeline visualization

Done. `tcp-timeline` is the first implemented visual and the reference for later ones.

- Model: `src/visuals/models/tcpTimeline.ts`, pure data and functions with no React import. It exports `tcpTimelineScenarios`, `getTcpTimelineScenario`, `getTcpTimelineStep(scenario, index)`, `segmentSummary`, `directionText`, and `describeTcpTimelineStep`. Each step records the sender (or a local event), flags, seq, ack, length, SACK blocks, `isRetransmit`, delivery (`arrives`, `lost`, `in-flight`), both endpoints' TCP states, and what each endpoint knows. Each scenario states the header fields it leaves out.
- Scenarios: `handshake-basic` (RFC 9293 Figure 6 plus a request and its ACK), `handshake-old-duplicate` (Figure 8), `handshake-refused` (RST, ACK to a closed port), `teardown-fin` (Figure 12 plus half-close data and TIME-WAIT), `teardown-rst` (Figure 10, a rebooted peer), `teardown-simultaneous` (Figure 13), `loss-fast-retransmit` (three duplicate ACKs with SACK), and `loss-timeout` (a tail loss, RTO backoff, and Karn's rule). Retransmissions are separate steps marked as such, so the original and the resend are never merged.
- View: `src/visuals/views/TcpTimelineView.tsx`. Previous and next buttons, a "Jump to step" select, a ladder that shows steps up to the current one with direction, flags, and numbers in text, "Retransmission", "Lost", and "In flight" tags, a live-region description generated by the model, and a "knows" panel per endpoint. No autoplay. The only transition is turned off under `prefers-reduced-motion`.
- Registry: `src/visuals/registry.ts` maps `tcp-timeline` to its view. `implementedVisualIds` is derived from that map. `getVisualView(id)` returns the component (props `scenario`, `learningObjective`, `caption`), and `renderVisual` is what `SectionView` calls, so the React hooks lint rule accepts it. Unimplemented ids still show the objective and caption.
- Tests: `tests/visuals/tcpTimeline.test.ts` asserts flags, seq, ack, and both states at every step of the three handshake and three teardown scenarios, checks original versus retransmission in both loss scenarios, checks per-step consistency (ack present exactly when ACK is set), and checks that every lesson's `tcp-timeline` section names an existing scenario.

## Module 5 — TCP in Depth

All eight lessons are written and `complete`: `m05.service-model`, `m05.header-and-numbers`, `m05.handshake`, `m05.reliability`, `m05.flow-control`, `m05.congestion`, `m05.interactions`, and `m05.termination`. Bodies live in `src/content/subjects/cn/modules/m05`. Prerequisites chain from `m04.icmp`. Each lesson has exactly one principle section and four checks. The module's `tcp-timeline` requirement is met by `m05.handshake` (`handshake-basic`, `handshake-old-duplicate`, `handshake-refused`), `m05.termination` (`teardown-fin`, `teardown-rst`, `teardown-simultaneous`), and `m05.reliability` (`loss-fast-retransmit`, `loss-timeout`). Flow control and congestion control use steps, comparisons, and worked examples; `two-windows` is still unimplemented and unused.

- Why-answers written: `why.08`, `why.09`, `why.10`, `why.20`, and `why.51` (embedded in `m05.service-model`); `why.12` and `why.13` (in `m05.header-and-numbers`); `why.11` (in `m05.handshake`); `why.17` (in `m05.reliability`); `why.14` and `why.15` (in `m05.congestion`); and `why.16` (in `m05.termination`). `why.20` answers what a successful handshake still does not guarantee.
- Sources added after opening them, all HTTP 200: RFC 1337, RFC 6928, RFC 7323, RFC 8985, listen(2), shutdown(2), send(2), tcpdump(1) at tcpdump.org, and the Linux kernel's `include/net/tcp.h` on GitHub (master).
- Checked claims: RFC 9293 (Sections 2.2, 3.1, 3.2, 3.2.1, 3.3.1, 3.3.2, 3.4, 3.4.1, 3.4.2, 3.4.3, 3.5, 3.5.1, 3.5.2, 3.5.3, 3.6, 3.6.1, 3.7, 3.7.1, 3.7.4, 3.8.1, 3.8.2, 3.8.3, 3.8.4, 3.8.5, 3.8.6, 3.8.6.1, 3.8.6.2.1, 3.8.6.2.2, 3.8.6.3, 3.9.1.2, 3.9.1.6, 3.10.7.4, 3.10.8, and the Glossary), RFC 1122 (Section 1.1.2), RFC 5681 (Sections 2, 3.1, 3.2, 4.1, 4.2), RFC 6298 (Sections 2, 3, 5), RFC 2018 (Sections 1, 2, 3), RFC 896 (the small-packet problem and its solution), RFC 7323 (Sections 1.1, 2.1, 2.2), RFC 6928 (Section 2), RFC 8985 (Sections 1, 1.1, 3.1, 3.2, 7.2), RFC 1337 (Section 1), tcp(7), socket(7), listen(2), shutdown(2), send(2), connect(2), ss(8), ip-route(8), tcpdump(1), the kernel IP Sysctl page, and `include/net/tcp.h` (`TCP_TIMEWAIT_LEN`, `TCP_FIN_TIMEOUT`, `TCP_RTO_MIN`, `TCP_TIMEOUT_INIT`, `TCP_RTO_MAX_SEC`, `TCP_DELACK_MIN`, `TCP_DELACK_MAX`, `TCP_INIT_CWND`). Every retransmission and termination claim is subtle and checked, as are the socket-buffer claims in `m05.flow-control`.
- Open research: some prose is not a claim and has no checked source. That covers the meaning of `Recv-Q` and `Send-Q` in `ss` for established and listening sockets (ss(8) does not define them); `nstat` counter names such as `ListenOverflows`; the exact `tcpdump` option list printed for a SYN and whether its `win` is shown unscaled; Wireshark's retransmission and dup-ACK labels; how SYN cookies encode state (RFC 4987 not opened); TCP Fast Open details (RFC 7413 not opened); Linux sending RST instead of FIN on close() with unread data (the lesson cites RFC 9293's SHOULD, not Linux); SO_LINGER with a zero timeout sending RST; TIME_WAIT sockets holding no file descriptor; that many RPC and HTTP libraries set TCP_NODELAY; CUBIC's growth curve and BBR's behaviour (no CUBIC or BBR source opened; only ss(8)'s statement that cubic is the default); and middlebox idle timeouts being shorter than two hours (tcp(7) says only that they "may be much shorter"). tcp(7) and the kernel documentation disagree on the `tcp_syn_retries` timeout (127 versus 131 seconds) and on the `tcp_tw_reuse` default; the lessons cite both or the kernel page.
- Tests: the shell and e2e "unwritten lesson" examples now use `m06.udp-service` ("What UDP provides") in module "6. UDP and Transport-Layer Alternatives", and the home count reads 26 of 67. `tests/reader/lesson.test.tsx` steps the handshake timeline with Next and the jump control and checks that its text matches the model, and checks the termination and reliability lessons. A new e2e test steps the handshake timeline and checks for horizontal overflow on both viewports.

## Module 6 — UDP and Transport-Layer Alternatives

All three lessons are written and `complete`: `m06.udp-service`, `m06.use-cases`, and `m06.quic-and-tradeoffs`. Bodies live in `src/content/subjects/cn/modules/m06` (`udp-service.ts`, `use-cases.ts`, `quic-and-tradeoffs.ts`). Prerequisites chain from `m05.termination`. Each lesson has exactly one principle section, a worked example, and four or five checks. No lesson uses a `viz` section; the module has no visual requirement, and stream blocking is left for the HTTP and QUIC lessons in Modules 8 and 9. QUIC is taught at the level of streams, packet numbers, frames, connection IDs, address validation, and migration. Nothing about key derivation or packet protection beyond "authenticated and mostly encrypted" is claimed.

- Why-answer written: `why.18` (embedded in `m06.use-cases`). `why.02` (alsoAppearsIn `m06.udp-service`) and `why.29` (alsoAppearsIn `m06.quic-and-tradeoffs`, primary `m08.http-versions`) are linked by text only. `why.17` and `why.09` are referenced by text from the QUIC lesson.
- Sources added after opening them, all HTTP 200: RFC 3550 (RTP), RFC 7766 (DNS over TCP), RFC 8085 (UDP Usage Guidelines), RFC 9002 (QUIC loss detection), and the Linux kernel's `net/ipv4/udp.c` and `net/ipv4/proc.c` on GitHub (master).
- Checked claims: RFC 768 (Introduction, Format, Fields, Protocol Number), RFC 1122 (Sections 4.1.1, 4.1.3.1, 4.1.3.3, 4.1.3.4), RFC 8200 (Section 8.1), RFC 8085 (Sections 1, 3, 3.1, 3.1.1, 3.1.2, 3.1.3, 3.2, 3.3, 3.4, 3.5, 5), RFC 1035 (Sections 4.1.1, 4.2, 4.2.1, 4.2.2), RFC 7766 (Sections 1, 5), RFC 3550 (Sections 1, 5.1, 6, 6.2, 10), RFC 5681 (Section 3.2), RFC 9293 (Section 2.2), RFC 9000 (Abstract and Sections 1, 2, 2.1, 2.2, 2.3, 4, 5, 5.1, 7, 8.1, 9, 9.4, 12.3, 13, 13.1, 13.3, 14, 17.2, 17.3.1), RFC 9002 (Sections 3, 4.2, 4.4, 4.5, 6.1, 6.1.1), udp(7), connect(2), socket(7) SO_RCVBUF, IP_MTU_DISCOVER(2const), `udp_err()`, `udp_rcv()`, and `__udp_queue_rcv_skb()` in `udp.c`, and `snmp4_udp_list` in `proc.c`. Every QUIC claim and every socket-buffer and TCP-retransmission claim is subtle and checked.
- Source disagreement: udp(7)'s "Error handling" paragraph says fatal errors reach the user even when the socket is not connected. `udp_err()` on master records an ICMP error on the socket only if it is a hard error and the socket is connected, unless IP_RECVERR is set. The lesson follows the kernel source and says so.
- Open research: some prose is not a claim and has no checked source. That covers `ss -uanp` Recv-Q for UDP sockets (ss(8) does not define it); `nstat` usage and recvmmsg as a fix for receive-buffer drops; capture tools showing "bad" UDP checksums on outgoing packets because of NIC checksum offload; that Linux may verify the checksum when the application reads rather than on arrival (not taught); packet-loss concealment, FEC, redundant audio, jitter-buffer depth, and adaptive bitrate in media stacks; game-netcode patterns such as deltas from the last acknowledged state; RFC 4787's two-minute NAT timeout, which is cited only through RFC 8085 (RFC 4787 not opened); RFC 6936 zero-checksum details, cited only through RFC 8200; EDNS(0) raising the DNS UDP size (RFC 6891 not opened, left to Module 7); HTTP/3 running on QUIC and clients falling back to TCP when UDP is blocked (RFC 9114 and Alt-Svc not opened in this session, left to Module 8); QUIC stacks usually being libraries in the application, and per-connection detail coming from their logs (qlog not cited); the recommendation of TCP with HTTP/2 or gRPC for intra-data-centre RPC, which is a judgment, not a sourced fact; and TCP SACK reneging, stated through RFC 9002 Section 4.4's contrast rather than RFC 2018. RFC 9221 (QUIC unreliable datagrams) and RFC 9001 packet protection were not opened and are not used.
- Tests: the shell and e2e "unwritten lesson" examples now use `m07.hierarchy` ("The DNS hierarchy") in module "7. DNS", and the home count reads 29 of 67. `tests/reader/lesson.test.tsx` checks that `m06.use-cases` embeds the why.18 answer, shows its voice-loss example, and can be marked finished, and that `m06.quic-and-tradeoffs` shows its stream-loss example.

## Module 7 — DNS

All five lessons are written and `complete`: `m07.hierarchy`, `m07.resolution`, `m07.records`, `m07.caching`, and `m07.transport-and-failures`. Bodies live in `src/content/subjects/cn/modules/m07` (`hierarchy.ts`, `resolution.ts`, `records.ts`, `caching.ts`, `transport-and-failures.ts`). Prerequisites chain from `m06.quic-and-tradeoffs`. Each lesson has exactly one principle section, a worked example, and four or five checks. The `worked-example` requirement is met by `m07.resolution` (a cold lookup of `shop.example.com` through root, TLD, and authoritative servers, with a CNAME restart into another domain and the warm-cache follow-ups), and every other lesson has an example too: reading a real `com` referral, designing a small domain's records, a TTL-lowering migration with a negative-caching trap, and a split-horizon name that fails over DoH. No lesson uses a `viz` section, because `dns-lookup` is approved but unimplemented. `m07.transport-and-failures` carries an `investigation` section for common DNS failures, although `m07.failures` is not a scenario topic and does not require one.

- Why-answers written: `why.21` (embedded in `m07.hierarchy`) and `why.22` (in `m07.caching`).
- Sources added after opening them, all HTTP 200: RFC 2181, RFC 2308, RFC 3596, RFC 5321, RFC 6891, RFC 7208, RFC 7505, RFC 8767, RFC 9499, RFC 9520, resolv.conf(5), nsswitch.conf(5), getaddrinfo(3), getent(1), and the BIND 9 `dig` manual. RFC 7766, RFC 7858, and RFC 8484 were already in the catalog and were opened again.
- Checked claims: RFC 1034 (Sections 2.1, 2.2, 2.4, 3.1, 3.6, 3.6.2, 4.1, 4.2, 4.2.1, 4.2.2, 4.3.1, 4.3.2, 4.3.5, 5.3.1, 5.3.3), RFC 1035 (Sections 3.2.1, 3.3, 3.3.9, 3.3.11, 3.3.13, 3.3.14, 3.4.1, 3.5, 4.1.1, 4.2.1, 4.2.2, 7.4), RFC 2181 (Sections 5, 5.1, 5.2, 6, 6.1, 8, 9, 10.1, 10.3), RFC 2308 (Sections 1 to 6), RFC 3596 (Sections 2.1, 2.2, 2.3, 2.5), RFC 5321 (Section 5.1), RFC 6891 (Sections 4.3, 6.2.1, 6.2.3, 6.2.5, 6.2.6), RFC 7208 (Sections 3.1, 4.6.2, 5.1, 5.4), RFC 7505 (Section 3), RFC 7766 (Sections 1, 4, 5, 6.2.1, 6.2.1.1, 7), RFC 7858 (Sections 1, 3.1, 3.3, 4, 4.1, 4.2), RFC 8484 (Sections 1, 3, 4.1, 4.2, 4.2.1, 5.1, 5.2, 8.1), RFC 8767 (Sections 4, 5), RFC 9499 (Sections 6 and 7: priming, stub, recursive, forwarder, open resolver, delegation, glue relationships, lame delegation, bailiwick, split DNS, views), RFC 9520 (Sections 2 to 2.7, 3.1, 3.2), the five Linux and BIND pages above. Every negative-caching, serve-stale, failure-caching, TCP, EDNS, DoT, and DoH claim is subtle and checked.
- Observed with `dig` 9.10 in October 2026, and labelled as observations in the lessons: the `com` referral for `example.com` and Cloudflare's AA answer, the 13 root NS names, `example.com`'s null MX and `v=spf1 -all`, a resolver's TTL counting down 4 seconds in 4 seconds, Wikimedia's NXDOMAIN carrying its SOA (TTL 3600, MINIMUM 3600), a home router returning a cached NXDOMAIN with no SOA, `google.com TXT` truncating to 28 bytes without EDNS and arriving in 1176 bytes over TCP, and 1.1.1.1 and Wikimedia advertising a 1232-byte EDNS size.
- Correction to the earlier draft of `m07.hierarchy`: it said RFC 9499 defines 'in-bailiwick'. RFC 9499 calls the bailiwick terms historic and defines in-domain, sibling-domain, and unrelated name servers instead; the expandable section and claim now say that.
- Open research: some prose is not a claim and has no checked source. That covers the ALIAS, ANAME, and CNAME-flattening features of DNS hosts; Happy Eyeballs and dual-stack clients slowing down on a broken AAAA (RFC 8305 not opened); SRV and HTTPS records (RFC 2782 and RFC 9460 not opened); NOTIFY pushing zone changes to secondaries (RFC 1996 not opened), and hosted DNS updating its servers within seconds; language runtimes, HTTP libraries, connection pools, and local daemons such as systemd-resolved or nscd caching addresses; glibc's stub having no cache of its own; VPN clients setting per-domain resolvers and browser DoH policies that exclude internal domains; DoH and DoT leaving the TLS server name visible (SNI and ECH not cited); DNSSEC beyond 'authenticates the data' and `+cd` (RFC 4033 to 4035 not opened); the Extended DNS Error text in the observed SERVFAIL (RFC 8914 not opened, not shown in the lesson); the text 'Temporary failure in name resolution' as glibc's message for EAI_AGAIN; and certificate authorities using TXT for domain validation (RFC 8555 not opened). The `m07.resolution` 5-second stall and `ndots` examples apply resolv.conf(5) defaults and were not measured.
- Tests: the shell and e2e "unwritten lesson" examples now use `m08.request-response` ("The HTTP request and response") in module "8. HTTP and Application-Layer Networking", and the home count reads 34 of 67. `tests/reader/lesson.test.tsx` checks that `m07.hierarchy` embeds why.21, that `m07.resolution` shows its cold-lookup example, that `m07.caching` embeds why.22 and can be marked finished, and that `m07.transport-and-failures` renders its investigation commands.

## Not done

- Only `tcp-timeline` is implemented. The other approved visuals, including `two-windows`, `dns-lookup`, `nat-table`, and `bandwidth-delay`, are unimplemented.
- All 67 lessons and why.01–why.55 answers are written. Remaining work is optional visuals and open-research notes above.

## Correction from the approved plan

`why.20` asks why a TCP connection can be established while the application request still fails. The plan mapped it to the scenario where TCP establishment fails. The primary lesson is `m05.service-model`, with links to the HTTP lesson and the 502/503 scenario.

## Checks run for milestone 1

- `tsc -b --pretty false` passed.
- `vitest run`: 20 tests passed across catalog, quality gate, progress, and shell.
- `eslint .` passed.
- `vite build` passed.
- `playwright test`: 5 passed, 1 skipped. The skipped test is the narrow-viewport menu check on the desktop project. Desktop and mobile both opened a module, an unwritten lesson, and the not-found page. Mobile opened the menu. Neither viewport overflowed horizontally on the lesson page.
- Every URL in `src/content/subjects/cn/sources.ts` returned HTTP 200 (39 URLs). No lesson claim is marked checked. The catalog entries are addresses, not verified quotations.

## Checks run for Module 2

- `tsc -b --pretty false`, `eslint .`, and `prettier --check .` passed.
- `vitest run`: 23 tests passed across 5 files.
- `playwright test`: 7 passed, 1 skipped (the narrow-viewport menu check on desktop).
- The 12 source URLs added for Module 2 returned HTTP 200. `pnpm curriculum:doc` left `CURRICULUM.md` unchanged, because the catalog did not change.

## Checks run for Module 3

- `tsc -b --pretty false`, `eslint .`, and `prettier --check .` passed.
- `vitest run`: 24 tests passed across 5 files.
- `vite build` passed. `playwright test`: 7 passed, 1 skipped (the narrow-viewport menu check on desktop).
- `pnpm curriculum:doc` left `CURRICULUM.md` unchanged, because the catalog did not change.

## Checks run for Module 4

- `tsc -b --pretty false`, `eslint .`, and `prettier --check .` passed.
- `vitest run`: 25 tests passed across 5 files.
- `vite build` passed. `playwright test`: 7 passed, 1 skipped (the narrow-viewport menu check on desktop).
- The 10 source URLs added for Module 4 returned HTTP 200. `pnpm curriculum:doc` left `CURRICULUM.md` unchanged, because the catalog did not change.

## Checks run for Module 5

- `tsc -b --pretty false`, `eslint .`, and `prettier --check .` passed.
- `vitest run`: 37 tests passed across 6 files.
- `vite build` passed, with Vite's warning that the single JavaScript chunk (about 857 kB) is over 500 kB. `playwright test`: 9 passed, 1 skipped (the narrow-viewport menu check on desktop).
- The 9 source URLs added for Module 5 returned HTTP 200. `pnpm curriculum:doc` left `CURRICULUM.md` unchanged, because the catalog did not change.

## Checks run for Module 6

- `tsc -b --pretty false`, `eslint .`, and `prettier --check .` passed.
- `vitest run`: 38 tests passed across 6 files.
- `vite build` passed, with the same warning that the single JavaScript chunk is over 500 kB. `playwright test`: 9 passed, 1 skipped (the narrow-viewport menu check on desktop).
- The 6 source URLs added for Module 6 returned HTTP 200. `pnpm curriculum:doc` left `CURRICULUM.md` unchanged, because the catalog did not change.

## Checks run for Module 7

- `tsc -b --pretty false`, `eslint .`, and `prettier --check .` passed.
- `vitest run`: 39 tests passed across 6 files.
- `vite build` passed, with the same warning that the single JavaScript chunk is over 500 kB. `playwright test`: 9 passed, 1 skipped (the narrow-viewport menu check on desktop).
- The source URLs added for Module 7 returned HTTP 200, after the rfc-editor.org redirect for RFCs. `pnpm curriculum:doc` left `CURRICULUM.md` unchanged, because the catalog did not change.

## Next task

Module 12: Sockets and Linux Networking, starting with `m12.lifecycle`.
