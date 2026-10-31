import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m11",
  status: "complete",
  readingMinutes: { min: 14, max: 20 },
  thesis:
    "Network speed is several different measurements, and the way a service fails, whether by queueing, loss, timeouts, retries, or partial failure, follows from them.",
  outcomes: [
    "Use latency, bandwidth, throughput, jitter, and RTT as distinct measurements and say which one a symptom points to.",
    "Compute a bandwidth-delay product and explain what it says about window size and throughput.",
    "Trace how queues, loss, and retransmission turn into added delay.",
    "Design timeouts, retries, and backoff without causing a retry storm.",
    "Explain why connection reuse, pooling, and keepalive exist, and how backpressure limits overload.",
    "Separate network stall from application work, including partitions and partial failures.",
  ],
  coverage: [
    {
      bullet: "Latency, bandwidth, throughput, and jitter.",
      notes:
        "• Latency: time for one unit to cross (propagation + transmission + queueing + processing).\n• Bandwidth: link capacity (bits/s); throughput: what you actually achieve, always ≤ bandwidth.\n• Jitter: variation in latency; hurts real-time media more than bulk transfer.\n• A fat pipe does not fix a latency problem, and low latency does not give throughput.\n• Detail: why.40.",
    },
    {
      bullet: "Round-trip time.",
      notes:
        "• RTT = time for a request to reach the peer and the reply to return.\n• TCP uses smoothed RTT to set the retransmission timeout; each handshake costs at least one RTT.\n• Lower bound is distance over the speed of light in fiber, roughly 5 µs per km one way.\n• Measure with `ping`, TCP handshake time, or `curl -w` timings; ICMP RTT is not always what TCP sees.\n• Detail: why.41.",
    },
    {
      bullet: "Bandwidth-delay product.",
      notes:
        "• BDP = bandwidth × RTT = bytes that must be in flight to keep the pipe full.\n• Example: 1 Gbit/s × 100 ms ≈ 12.5 MB.\n• If the window is smaller than the BDP, throughput ≈ window / RTT regardless of link speed.\n• Long fat networks need window scaling and large buffers.\n• Detail: why.42.",
    },
    {
      bullet: "Queueing and congestion.",
      notes:
        "• Packets wait in a buffer when arrival rate exceeds the outgoing link rate.\n• Queue delay grows sharply as utilization nears 100%.\n• Oversized buffers hide loss but add large delay (bufferbloat).\n• Congestion control reacts to loss or delay by shrinking the send rate.\n• Detail: why.43.",
    },
    {
      bullet: "Packet loss.",
      notes:
        "• Causes: queue overflow (most common), bad links or Wi-Fi interference, policers, and failing hardware.\n• TCP reads loss as congestion and cuts its window, so even 1% loss cuts throughput hard on long paths.\n• UDP apps see loss directly and must conceal or repair it.\n• Check interface errors/drops, `ping -c` loss, and capture retransmits.\n• Detail: why.44.",
    },
    {
      bullet: "Retransmissions.",
      notes:
        "• Triggers: retransmission timeout (slow) or three duplicate ACKs (fast retransmit).\n• Each retransmit adds at least an RTT, and an RTO can add hundreds of ms or more.\n• The timer backs off exponentially on repeated loss.\n• Observe with `ss -ti` (retrans counters), `nstat`, or a capture.",
    },
    {
      bullet: "Timeouts and deadlines.",
      notes:
        "• A timeout bounds one step (connect, read); a deadline bounds the whole operation.\n• Propagate the remaining deadline downstream, or inner calls outlive the caller.\n• Too long: threads and sockets pile up. Too short: false failures and extra retries.\n• Set separate connect and read timeouts; never leave them infinite.\n• Detail: why.46.",
    },
    {
      bullet: "TCP connection reuse.",
      notes:
        "• A new connection costs a handshake (1 RTT), TLS (1 more RTT with TLS 1.3), and a slow-start window.\n• Reusing a warm connection skips all of that (HTTP keep-alive, HTTP/2 multiplexing).\n• Risks: stale connection closed by peer or middlebox, head-of-line blocking on a single stream.\n• Detail: why.47.",
    },
    {
      bullet: "Keepalive mechanisms.",
      notes:
        "• TCP keepalive: after idle time, send probes; no answer after N probes → reset the connection.\n• Default idle is often 2 hours, far longer than NAT and LB idle timers.\n• Application-level heartbeats detect a hung peer, not just a dead path.\n• Set keepalive shorter than the shortest middlebox idle timeout on the path.",
    },
    {
      bullet: "Connection pooling.",
      notes:
        "• A bounded set of warm connections shared across requests.\n• Sizing: too small queues callers; too large exhausts server file descriptors and ports.\n• Needs health validation (test on borrow, max age, idle eviction) to avoid stale connections.\n• Pool wait time is a latency source that looks like network delay.",
    },
    {
      bullet: "Retries and exponential backoff.",
      notes:
        "• Retry only idempotent operations, or use idempotency keys.\n• Backoff: wait base × 2^n, cap it, and add random jitter so clients do not retry in lockstep.\n• Retries multiply load on an already struggling server (retry storm); use retry budgets.\n• Retries across layers multiply: 3 × 3 × 3 = 27 attempts.\n• Detail: why.48.",
    },
    {
      bullet: "Tail latency.",
      notes:
        "• The tail is p99 and p99.9, not the mean; averages hide it.\n• Fan-out amplifies it: a request touching 100 backends hits one slow one almost every time.\n• Causes: queueing, GC pauses, retransmission timeouts, noisy neighbors.\n• Mitigations: hedged requests, timeouts, load shedding.\n• Detail: why.49.",
    },
    {
      bullet: "Backpressure.",
      notes:
        "• Slow consumers must be able to slow producers, or buffers grow without bound.\n• TCP does it with the receive window; applications need bounded queues, blocking writes, or explicit rejection (429/503).\n• Unbounded queues turn overload into memory exhaustion and enormous latency.\n• Shedding load early is better than failing everything late.",
    },
    {
      bullet: "Network partitions.",
      notes:
        "• Some nodes cannot reach others while each side still runs.\n• From either side a partition looks like a very slow peer; timeouts are the only signal.\n• Forces a choice for stateful systems: stay consistent (refuse) or stay available (diverge).\n• Detect with reachability checks from several vantage points, not one.",
    },
    {
      bullet: "Partial failures.",
      notes:
        "• Some requests, instances, links, or dependencies fail while others work.\n• The caller cannot tell whether a timed-out request was executed, so side effects need idempotency.\n• Health is per path, not per host: a node can be up and unreachable from one subnet.\n• Use timeouts, circuit breakers, and per-dependency metrics.",
    },
    {
      bullet: "Distinguishing application latency from network latency.",
      notes:
        "• Break one request into phases: DNS, connect, TLS, time to first byte, transfer.\n• Slow connect/TLS → network or handshake; slow TTFB with a quick connect → server or app work.\n• `curl -w` timing variables and `tcpdump` show the split; server-side timing confirms it.\n• Low CPU does not clear the app: it can be waiting on locks, I/O, or a downstream call.",
    },
  ],
  mentalModel:
    "• One request's time = propagation (RTT) + transmission (size ÷ bandwidth) + queueing + processing.\n• Throughput = min(window ÷ RTT, bottleneck bandwidth, receiver rate); the smallest term wins.\n• Queues build at the bottleneck; a full queue becomes loss; loss becomes retransmission delay and a smaller window.\n• Timeouts bound waiting; retries add load; backoff and jitter spread that load out.\n• Reuse, pooling, and backpressure keep connection count and queue depth bounded.",
  failuresAndSignals: [
    "Retry storm: a slow dependency triggers retries, load rises, latency rises, more retries. Signal is request rate climbing with falling success while the backend is the bottleneck.",
    "Window-limited transfer on a long path: fast link, slow copy. Signal is throughput ≈ window ÷ RTT and flat across link upgrades.",
    "Bufferbloat: ping RTT jumps from 20 ms to seconds only while a large transfer runs; no loss is reported.",
    "Stale pooled connection: first request after an idle period fails with reset or EOF, the retry works. Signal is errors clustered after idle gaps, tied to middlebox idle timers.",
    "Tail latency under fan-out: median is healthy but user-visible p99 is poor. Signal is per-backend p99 outliers, and retransmissions or GC aligned with the spikes.",
    "Partition or half-open peer: calls hang until timeout with no error. Signal is nothing returning rather than refused connections; confirm from a second vantage point.",
  ],
  whyAnchors: [
    "why.40",
    "why.41",
    "why.42",
    "why.43",
    "why.44",
    "why.46",
    "why.47",
    "why.48",
    "why.49",
  ],
  connections: {
    before:
      "• Module 5: TCP windows, RTO, and congestion control provide the mechanisms measured here.\n• Module 10: proxies, NAT, and balancers add hops, idle timers, and queues that show up as latency.",
    after:
      "• Module 12: where the buffers, backlog, and connection limits live inside the kernel.\n• Module 13: uses these measurements to decide whether a symptom is name, TCP, TLS, HTTP, or app.",
  },
  selfCheck: [
    {
      id: "bdp",
      prompt: "A 1 Gbit/s link has a 80 ms RTT and a 64 KB window. What throughput do you expect?",
      answer:
        "About window ÷ RTT = 64 KB / 0.08 s ≈ 0.8 MB/s (≈ 6.5 Mbit/s), far below link speed. The BDP is about 10 MB, so the window must scale (why.42).",
    },
    {
      id: "retry-storm",
      prompt: "Why can adding retries make an outage worse, and what limits the damage?",
      answer:
        "Each retry adds load to an overloaded server and attempts multiply across layers. Use capped exponential backoff with jitter, retry budgets, and retry only idempotent calls (why.48).",
    },
    {
      id: "split",
      prompt: "How do you tell network latency from application latency for one HTTP request?",
      answer:
        "Time the phases. Fast connect and TLS with a long wait to first byte means the server or its dependencies; slow connect or retransmits in a capture means the network.",
    },
  ],
} satisfies ChapterSummary;
