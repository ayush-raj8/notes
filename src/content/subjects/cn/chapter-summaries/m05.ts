import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m05",
  status: "complete",
  readingMinutes: { min: 12, max: 18 },
  thesis:
    "TCP turns lossy, reordering, duplicating IP into an ordered byte stream by keeping per-connection state at the two endpoints: numbered bytes, acknowledgments, timers, and two separate limits on how much may be in flight.",
  outcomes: [
    "State what TCP promises (ordered, reliable bytes) and what it does not (message boundaries, timing, application success).",
    "Trace a connection from SYN through data to FIN, TIME_WAIT, or RST, and say why each step exists.",
    "Explain how sequence numbers, ACKs, timers, duplicate ACKs, and SACK recover from loss, duplication, delay, and reorder.",
    "Separate flow control (receiver window) from congestion control (sender-estimated cwnd) and name the smaller-of-two rule.",
    "Diagnose stalls from zero windows, delayed ACK with Nagle, keepalive gaps, half-open connections, and head-of-line blocking.",
  ],
  coverage: [
    {
      bullet: "TCP's service model.",
      notes:
        "• Connection-oriented, full-duplex, reliable, in-order byte stream between two endpoints identified by the 4-tuple (src IP, src port, dst IP, dst port).\n• Hosts keep state (sequence numbers, buffers, timers, windows); routers keep none.\n• Not promised: message boundaries, bounded latency, bandwidth, or that the peer application acted on the data (why.09, why.20).\n• Reliability is end-to-end because only the endpoints know what arrived and hold the data to resend (why.51).",
    },
    {
      bullet: "TCP headers and important fields.",
      notes:
        "• Fixed 20-byte header plus options: source/dest port, sequence number, ack number, data offset, flags, window, checksum, urgent pointer.\n• Flags that matter: SYN, ACK, FIN, RST, PSH; ECE/CWR for ECN.\n• Window field is the receiver's advertised space; window scaling option extends it past 64 KB.\n• Other options: MSS (negotiated in SYN), SACK-permitted and SACK blocks, timestamps.\n• Checksum covers header, payload, and a pseudo-header with the IP addresses.",
    },
    {
      bullet: "Sequence numbers and acknowledgment numbers.",
      notes:
        "• Sequence number names the first payload byte of the segment; SYN and FIN each consume one sequence number.\n• Ack number = next byte the receiver expects; everything below it has arrived in order.\n• Numbering bytes, not segments, lets TCP re-cut data on retransmit and merge small writes (why.08).\n• Sequence numbers let the receiver reorder, spot gaps, and drop duplicates (why.12).\n• Numbers are 32-bit and wrap; comparisons use modular arithmetic.",
    },
    {
      bullet: "The three-way handshake.",
      notes:
        "• Client SYN(seq=x) → server SYN-ACK(seq=y, ack=x+1) → client ACK(ack=y+1).\n• Each side picks its own ISN and needs proof the other saw it; the server folds its ACK and its SYN into one segment.\n• Two messages cannot let the server detect a stale delayed SYN; the third message can, because a client that never sent the SYN replies RST (why.11).\n• Options (MSS, window scale, SACK-permitted, timestamps) are agreed here.",
    },
    {
      bullet: "SYN, SYN-ACK, ACK, FIN, and RST.",
      notes:
        "• SYN: open and synchronize ISN. SYN-ACK: accept and synchronize the other direction. ACK: acknowledge, set on nearly every segment after the SYN.\n• FIN: sender has no more data in this direction; graceful, ordered after earlier bytes.\n• RST: abort immediately; no further data accepted, no ACK needed, receiver discards buffered data.\n• RST answers segments for a closed port, an unknown connection, or an out-of-window/stale attempt.",
    },
    {
      bullet: "Initial sequence numbers.",
      notes:
        "• ISN is chosen per connection, not always 0.\n• Fresh, hard-to-predict ISNs keep old-incarnation segments from fitting the new window and block off-path injection (why.12).\n• Modern stacks derive the ISN from a keyed hash of the 4-tuple plus a clock component.\n• Works with TIME_WAIT: ISN choice plus waiting keeps reused 4-tuples from mixing.",
    },
    {
      bullet: "Connection establishment.",
      notes:
        "• Client states: CLOSED → SYN_SENT → ESTABLISHED. Server: LISTEN → SYN_RECEIVED → ESTABLISHED.\n• On Linux the kernel completes the handshake and queues the connection; accept() merely pulls it off the queue.\n• SYN lost: client retransmits with exponential backoff. SYN to closed port: RST. SYN dropped by a firewall: silence then timeout.\n• ESTABLISHED proves only that the stacks agreed on numbers: TLS, HTTP, the app, and large packets can still fail (why.20).",
    },
    {
      bullet: "Reliable, ordered byte-stream delivery.",
      notes:
        "• Sender writes bytes; TCP segments them to fit the MSS; receiver reassembles and hands bytes to the app in order, once.\n• Write boundaries are not preserved: one write can become many segments, many writes one segment, and one read may return part of a write.\n• Applications needing messages frame them themselves with length prefixes or delimiters (why.08).\n• A closed stream is delivered completely before EOF is seen (FIN is ordered).",
    },
    {
      bullet: "Retransmission timers and timeout-based retransmission.",
      notes:
        "• Sender keeps unacknowledged data and runs a retransmission timer on the oldest unacked segment.\n• Timer expires → resend that segment, double the RTO (exponential backoff), and treat it as strong congestion evidence (cwnd collapses to a small value).\n• Silence looks the same as delay, so the timer is the sender's only guess (why.13).\n• Gives up after a retry limit; on Linux about 15 minutes by default (why.15).",
    },
    {
      bullet: "Duplicate acknowledgments and fast retransmission.",
      notes:
        "• Out-of-order arrival makes the receiver re-send the same ack number immediately: a duplicate ACK.\n• Three duplicate ACKs imply a later segment arrived while one is missing → retransmit now without waiting for the timer.\n• Fast retransmit costs about one RTT instead of a full RTO.\n• Needs enough later packets in flight; the tail of a burst or a small window falls back to timeout.\n• Reordering by a few packets can trigger spurious retransmits.",
    },
    {
      bullet: "Cumulative acknowledgments and selective acknowledgments.",
      notes:
        '• Cumulative ACK: a single number, "all bytes below this are received"; simple and robust, but hides what arrived above a gap.\n• SACK (option) lists up to a few blocks of received data above the gap.\n• With SACK the sender resends only the holes instead of guessing or resending the whole window after the gap.\n• SACK is advisory: the receiver may later discard SACKed data, and the cumulative ACK stays authoritative.\n• ACKs also free sender buffer, feed RTT samples, and carry the window (why.13).',
    },
    {
      bullet: "Sliding-window protocols.",
      notes:
        "• Stop-and-wait sends one unit per RTT and wastes the pipe; a window lets many bytes be in flight.\n• Window slides right as the cumulative ACK advances. Bytes are in one of four states: acked, sent-unacked, allowed-unsent, not-yet-allowed.\n• Throughput ≈ window ÷ RTT, so the window must cover the bandwidth-delay product to fill a path.\n• Sender may have at most min(rwnd, cwnd) bytes outstanding.",
    },
    {
      bullet: "Receiver window and advertised window.",
      notes:
        "• Receiver advertises its free buffer space in every ACK (rwnd).\n• Window-scale option multiplies the 16-bit field so windows can reach the gigabyte range.\n• Window shrinks when the app reads slowly and reopens as it reads.\n• Silly-window avoidance: receiver does not advertise tiny increments, sender does not send tiny segments.",
    },
    {
      bullet: "Flow control.",
      notes:
        "• Purpose: protect the receiver's buffer from a faster sender.\n• Receiver knows its free space exactly and says so in the header; sender obeys rwnd.\n• Slow application → full buffer → window closes → sender stops. Backpressure reaches the sender through TCP.\n• Separate from congestion control, which protects the network (why.14).",
    },
    {
      bullet: "Congestion control.",
      notes:
        "• Purpose: protect routers and links; no header field reports their capacity, so the sender estimates it.\n• Sender keeps cwnd and sends no more than min(cwnd, rwnd).\n• Signals: loss (timeout, duplicate ACKs), rising delay, ECN marks.\n• Principle: additive increase when things work, multiplicative decrease on congestion, so flows share a bottleneck roughly fairly.\n• Network stays simple; endpoints adopt new algorithms without router changes (why.51).",
    },
    {
      bullet: "Slow start, congestion avoidance, and fast recovery at a conceptual level.",
      notes:
        "• Slow start: cwnd starts small (a few segments) and roughly doubles per RTT until loss or ssthresh.\n• Congestion avoidance: above ssthresh, grow about one segment per RTT.\n• Fast recovery: after three duplicate ACKs, halve cwnd and keep sending as ACKs return; the ACK clock shows the pipe is still moving.\n• Timeout is harsher: cwnd resets low and slow start restarts.\n• Cubic, BBR, and others change the growth rule, not the idea (cwnd, loss/delay signals, probing).",
    },
    {
      bullet: "Retransmission timeout estimation.",
      notes:
        "• RTO tracks measured RTT: smoothed RTT (SRTT) plus a multiple of RTT variance (RTTVAR); RTO = SRTT + 4×RTTVAR with a floor.\n• Karn's rule: do not sample RTT from retransmitted segments; timestamps option helps disambiguate.\n• Too small → spurious retransmits and wasted capacity; too large → slow recovery from loss.\n• Backoff doubles RTO on each consecutive timeout; a fresh good sample resets it.",
    },
    {
      bullet: "Delayed ACKs and Nagle's algorithm.",
      notes:
        "• Delayed ACK: receiver waits a short time (tens of ms up to ~200 ms) or for a second full segment to piggyback or batch ACKs.\n• Nagle: with unacked data outstanding, hold small writes until the ACK arrives or a full segment accumulates.\n• Write-write-read with small writes can stall about one delayed-ACK interval: Nagle waits for an ACK, the receiver waits to send one (why.15).\n• Fix: send whole messages in one write, or set TCP_NODELAY for latency-sensitive traffic.",
    },
    {
      bullet: "TCP keepalive.",
      notes:
        "• Optional probe sent after a long idle period: an empty segment with sequence number just below next, forcing an ACK or RST.\n• Off by default; idle time before first probe is often 2 hours, probes then repeat at intervals.\n• Detects dead peers and keeps NAT/firewall mappings alive only if the interval is shorter than their timeout.\n• Without it, an idle connection to a vanished peer looks healthy indefinitely (why.15).\n• Application-level heartbeats or TCP_USER_TIMEOUT are often better.",
    },
    {
      bullet: "Half-open connections.",
      notes:
        '• One side believes the connection is up while the other has lost it (crash, reboot, middlebox state dropped).\n• Idle connection stays half-open silently; the surviving side finds out only when it sends.\n• Data arriving at the host with no state gets an RST, and the sender sees "connection reset".\n• A sender that only waits for data, or whose peer was unplugged, may never learn without keepalive or app timeouts.',
    },
    {
      bullet: "Half-close behavior.",
      notes:
        "• shutdown(SHUT_WR) sends FIN: no more data from me, but I can still receive.\n• Other side sees EOF on read, may keep sending, then closes its own direction.\n• Four-segment close: FIN, ACK, FIN, ACK, since each direction closes on its own.\n• States: FIN_WAIT_1/2, CLOSE_WAIT, LAST_ACK. A leak of CLOSE_WAIT means the local app never called close().\n• close() on a socket with unread data may send RST instead of FIN.",
    },
    {
      bullet: "TIME_WAIT and its purpose.",
      notes:
        "• The side that closes first sits in TIME_WAIT for 2×MSL after the final ACK (60 s on Linux).\n• Reason 1: the last ACK may be lost; the peer retransmits its FIN and someone must still ACK it, or the peer gets an RST (why.16).\n• Reason 2: delayed segments from the old connection must expire before the same 4-tuple is reused.\n• Cost: one 4-tuple held per closed connection; pain shows with many short connections from one client to one server port.\n• Fixes are connection reuse or more ports or addresses, not shortening the timer.",
    },
    {
      bullet: "Simultaneous open and simultaneous close at a conceptual level.",
      notes:
        "• Simultaneous open: both sides send SYN at once; each moves SYN_SENT → SYN_RECEIVED and, on seeing the other's SYN-ACK, ESTABLISHED. One connection results, not two.\n• Simultaneous close: both send FIN before seeing the other's; states pass through CLOSING, then TIME_WAIT on both sides.\n• Rare in practice, but they show that the state machine is symmetric: no side is inherently client or server.",
    },
    {
      bullet: "Connection resets.",
      notes:
        '• RST means "this connection does not exist or is abandoned"; the receiver drops state and fails pending reads and writes.\n• Common causes: port closed, no matching connection (peer rebooted), app closed with unread data, aborted via SO_LINGER 0, stale handshake, firewall or middlebox injecting RST.\n• Sequence number must fall in the receive window to be accepted, which limits off-path forging.\n• Distinguish: RST at connect = nothing listening; RST mid-stream = state lost or deliberate abort; timeout = silence.',
    },
    {
      bullet: "Zero-window behavior.",
      notes:
        "• Receiver advertises window 0 when its buffer is full (app not reading).\n• Sender stops and runs the persist timer, probing with 1-byte segments, backing off, until a window update arrives.\n• The window-reopening ACK could be lost, so probes are what prevent deadlock.\n• Looks like a hang: both programs alive, no errors. Cause is almost always a slow or stuck reader (why.15).",
    },
    {
      bullet: "TCP performance over high-latency networks.",
      notes:
        "• Throughput ≤ window ÷ RTT. A 64 KB window over 200 ms RTT is about 2.6 Mbit/s no matter how fast the link.\n• Needs window scaling and buffers sized to the bandwidth-delay product.\n• Handshake costs 1 RTT before data; TLS adds more. Slow start spends several RTTs reaching full speed; short transfers never get there.\n• Loss recovery costs whole RTTs; each loss is more expensive.\n• Remedies: connection reuse, larger initial window, window scaling, SACK, CDNs or edge termination to cut RTT.",
    },
    {
      bullet: "Head-of-line blocking within a TCP byte stream.",
      notes:
        "• Receiver may buffer and ACK later segments, but must not deliver byte 2000 before byte 1000.\n• Everything after the gap waits at least one more RTT (three duplicate ACKs) or a full RTO.\n• Independent requests multiplexed on one connection (HTTP/2 streams) are delayed by a loss in someone else's bytes (why.17).\n• This is the price of one ordered stream; separate connections or other transports avoid it.",
    },
    {
      bullet: "What happens when a client or server crashes.",
      notes:
        "• Process crash with the OS alive: the kernel closes sockets, sends FIN or RST (RST if unread data); the peer sees EOF or reset.\n• Host crash or power loss: no FIN, no RST. Peer's data goes unanswered, retransmits back off until the connection times out, and an idle peer notices nothing.\n• After reboot, packets for the old connection get RST because no state exists.\n• ACKed does not mean processed: data in the dead host's buffer is lost, so the app protocol must confirm results and make retries safe (why.09, why.10).",
    },
    {
      bullet: "What happens when packets are lost, duplicated, delayed, or reordered.",
      notes:
        "• Lost: sender recovers by timeout, or sooner by duplicate ACKs/SACK; cwnd falls.\n• Duplicated: sequence numbers identify bytes already received; receiver discards and re-ACKs.\n• Delayed: late segment is either accepted in order or treated as duplicate; a late ACK may cause a spurious retransmit; RTO adapts.\n• Reordered: receiver buffers out-of-order data and sends duplicate ACKs; mild reorder can trigger needless fast retransmit.\n• Retransmission needs acknowledgment: ACKs say what to resend (why.13). TCP dedupes within one connection only; a new connection retry is a new stream (why.10).\n• Application retries on a new connection need idempotency (why.10).",
    },
  ],
  mentalModel:
    "• Picture two sliding windows over one numbered byte line: the sender's window (what may be in flight) and the receiver's buffer (what has landed).\n• Left edge of the sender's window = last cumulative ACK; right edge = left + min(rwnd, cwnd).\n• rwnd comes from the receiver in each ACK (flow control); cwnd is the sender's private estimate of the path (congestion control).\n• Timers and duplicate ACKs are the only loss signals; SACK says what to resend; RTO estimation sets how long to wait.\n• Handshake sets up the numbers; FIN, RST, and TIME_WAIT tear them down without letting old segments leak into a new connection.\n• TCP stops at the host's kernel: delivery to the app is not processing by the app (why.09, why.20).",
  failuresAndSignals: [
    "SYN timeout vs RST on connect: silence then ETIMEDOUT means a firewall or unreachable host dropped the SYN; an immediate RST means the host is up and nothing is listening. Check with tcpdump for SYN with no reply.",
    "Handshake succeeds but large transfers hang: MTU black hole where small packets pass and full-size packets vanish; look for retransmissions of full-size segments with no ACKs and no ICMP too-big (why.15, why.20).",
    "Stalled transfer with both programs alive: zero-window advertised by a slow reader. ss shows full Recv-Q on the receiver and persist-timer probes on the wire; fix the reader, not the network.",
    "Retransmissions, duplicate ACKs, and SACK blocks in a capture: loss or reordering on the path. Sender cwnd collapses and throughput drops; ss -ti shows retrans and cwnd values.",
    "Many sockets in TIME_WAIT on a client making short connections to one server port: expected 4-tuple use, not a leak; reuse connections or spread source ports. Many in CLOSE_WAIT is a leak: local app is not calling close() (why.16).",
    "Idle connection through NAT or firewall fails on next send with RST or timeout: mapping expired; fix with keepalive shorter than the idle timeout or app heartbeats (why.15).",
    "Small request/response pairs with a consistent ~40–200 ms delay: Nagle plus delayed ACK; send each message in one write or set TCP_NODELAY.",
    "One lost segment delays unrelated HTTP/2 requests on the same connection: head-of-line blocking inside the ordered stream (why.17).",
  ],
  whyAnchors: [
    "why.08",
    "why.09",
    "why.10",
    "why.11",
    "why.12",
    "why.13",
    "why.14",
    "why.15",
    "why.16",
    "why.17",
    "why.20",
    "why.51",
  ],
  connections: {
    before:
      "• Module 4 builds forwarding tables so IP can move a packet host to host, best effort, with no promises.\n• TCP assumes exactly that: IP may lose, duplicate, delay, and reorder, and TCP supplies the guarantees on top at the endpoints.",
    after:
      "• Module 6: UDP and transport-layer alternatives, where an app that skips TCP's ordering and retransmission trades them for lower latency and no head-of-line blocking.\n• Later modules put DNS, HTTP, and TLS on these connections, so their stalls and failures trace back to the TCP behavior here.",
  },
  selfCheck: [
    {
      id: "handshake-three",
      prompt:
        "Why does TCP need three messages to open a connection, and what does TIME_WAIT protect at the other end?",
      answer:
        "Each side must send its ISN and see it acknowledged; the server's ACK and SYN share a segment, so three messages cover both directions and let the server reject a stale delayed SYN with the client's RST (why.11). TIME_WAIT keeps the first closer around for 2×MSL so a lost final ACK can be answered and old segments expire before the 4-tuple is reused (why.16).",
    },
    {
      id: "flow-vs-congestion",
      prompt:
        "How do flow control and congestion control differ, and what limits what the sender can have in flight?",
      answer:
        "Flow control protects the receiver's buffer; the receiver states it exactly in the advertised window. Congestion control protects the network; the sender estimates it as cwnd from loss, delay, or ECN. The sender may have at most min(rwnd, cwnd) bytes outstanding (why.14).",
    },
    {
      id: "loss-recovery",
      prompt:
        "A segment is lost in the middle of a busy transfer. How does TCP notice and recover, and what does the application see?",
      answer:
        "Later segments produce duplicate ACKs (with SACK blocks if enabled). Three duplicates trigger fast retransmit; if there is no tail of later data, the retransmission timer fires instead. cwnd drops. The receiver holds later bytes but delivers nothing past the gap, so the app sees a brief stall and then in-order data, not an error (why.13, why.17).",
    },
    {
      id: "ack-not-processed",
      prompt:
        "A client's write succeeded and the server's TCP ACKed it, then the server crashed. What can the client conclude, and what must it do?",
      answer:
        "Only that the server's kernel buffered the bytes. They may never have been read or acted on, and a new connection retry looks like a new stream to TCP. The client needs an application-level response or idempotency key to know the outcome and retry safely (why.09, why.10).",
    },
  ],
} satisfies ChapterSummary;
