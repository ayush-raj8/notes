import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m06",
  status: "complete",
  readingMinutes: { min: 10, max: 15 },
  thesis:
    "UDP adds only ports and an optional checksum to IP, so the application keeps full control of loss, timing, and retries, which is why real-time protocols, DNS, and QUIC build on it.",
  outcomes: [
    "Read the four UDP header fields and say what the service model does and does not promise.",
    "Explain why UDP leaves out TCP-style guarantees, and what the checksum does and does not catch.",
    "Pick UDP or TCP for a workload by naming the trade-off: timeliness and control versus completeness and built-in congestion control.",
    "Describe what an application must add itself over UDP (sequence numbers, timers, retries, congestion control).",
    "Say how QUIC uses UDP as a carrier while providing its own reliable, multiplexed, encrypted streams.",
  ],
  coverage: [
    {
      bullet: "UDP header and service model.",
      notes:
        "• Header is 8 bytes: source port, destination port, length, checksum.\n• Service model: best-effort, message-oriented datagrams; boundaries preserved, one send = one datagram.\n• No handshake, no connection state, no ordering, no retransmission, no flow or congestion control.\n• A datagram can be lost, duplicated, or reordered by the network, and the app sees only what arrives.",
    },
    {
      bullet: "Why UDP does not provide TCP-style delivery guarantees.",
      notes:
        "• Guarantees cost state, round trips, and delay (handshake, ACKs, retransmit timers, in-order delivery).\n• Some apps cannot use them: a late frame is worthless, and a retransmit only delays newer data (why.18).\n• Leaving them out keeps UDP a thin layer that other protocols can build their own policy on.\n• Absence of guarantees is not absence of limits: senders are still expected to avoid congestion (RFC 8085).",
    },
    {
      bullet: "UDP checksums.",
      notes:
        "• Covers the UDP header, payload, and a pseudo-header with source/destination IP and protocol.\n• Catches corruption and misdelivery to the wrong address; it does not recover anything, a bad datagram is dropped.\n• Required in IPv6 (narrow tunnel exceptions); optional in IPv4 (all-zero means not computed).\n• Weak 16-bit sum: not security, and it does not detect an attacker rewriting data.",
    },
    {
      bullet: "Appropriate use cases.",
      notes:
        "• Good fit: small request/response (DNS), real-time audio/video/games, telemetry, discovery, broadcast/multicast, tunnels, and QUIC.\n• Common thread: the app prefers fresh data or its own retry policy over strict ordering.\n• Poor fit: bulk file transfer or anything needing every byte in order, unless the app adds reliability.\n• Check the network path too: some firewalls and NATs treat UDP less kindly than TCP.",
    },
    {
      bullet: "Why DNS, real-time media, and some other protocols use UDP.",
      notes:
        "• DNS: one small question and one small answer; a handshake would double the cost, and the client can just retry (falls back to TCP for large answers).\n• Real-time media: deadline matters more than completeness; conceal or skip a lost frame (why.18).\n• Others: DHCP before the host has an address, NTP, SNMP, syslog, and many games and VoIP stacks.\n• No head-of-line blocking: later datagrams are usable even when an earlier one is lost.",
    },
    {
      bullet: "Application-level reliability over UDP.",
      notes:
        "• The app adds only what it needs: sequence numbers, ACKs or NACKs, timers, retries, FEC, de-duplication.\n• RTP: sequence numbers for loss/order, timestamps for jitter buffers, RTCP reports to drive rate control.\n• Selective reliability: retransmit the important packets and let stale ones go.\n• Congestion control is still the app's job; ignoring it harms other flows on the path.",
    },
    {
      bullet: "QUIC and its relationship to UDP.",
      notes:
        "• QUIC runs in user space on top of UDP datagrams; UDP is the carrier that passes through existing networks and middleboxes.\n• QUIC supplies what UDP lacks: reliability, per-stream ordering, flow and congestion control, and integrated TLS 1.3.\n• Independent streams mean a loss stalls only the streams whose data was lost.\n• Clients fall back to TCP when UDP is blocked; details in Module 8 (HTTP/3) and Module 9.",
    },
    {
      bullet: "TCP vs UDP trade-offs.",
      notes:
        "• TCP: ordered reliable byte stream, congestion control, but handshake cost and head-of-line blocking; message boundaries not preserved.\n• UDP: message boundaries, no setup, no built-in recovery; the app owns policy and tuning.\n• Choose by what a lost or late packet means to the application, not by which seems faster.\n• Reimplementing TCP badly on UDP is the usual failure; use QUIC or an existing library instead.",
    },
  ],
  mentalModel:
    "• UDP is IP plus ports plus an optional integrity check: a postcard, not a phone call.\n• Each datagram stands alone; the network may drop, duplicate, or reorder it, and nothing in UDP notices.\n• Anything beyond that (order, recovery, pacing, encryption) is a layer the application or a protocol like RTP or QUIC adds on top.\n• TCP bundles those policies for everyone; UDP lets each app pick its own.",
  failuresAndSignals: [
    "Silent loss: a UDP sender never learns a packet was dropped — signs are gaps in app sequence numbers, glitching audio, or stalls until an app-level timeout; check socket receive buffer drops and interface counters.",
    "Datagram larger than path MTU: IP fragments it or the path drops it — large sends fail while small ones work; keep payloads under the path MTU and watch for ICMP too-big or missing fragments.",
    "Receive buffer overflow: a fast sender or slow reader loses packets inside the host — look at `netstat -su` or `/proc/net/snmp` for receive-buffer errors and raise the buffer or read faster.",
    "UDP blocked or throttled on a path: DNS or QUIC works on some networks but not others — traffic shows outbound packets with no replies; clients should fall back to TCP.",
    "Amplification and spoofing: UDP has no handshake to prove the source address, so small spoofed requests can produce large responses — signs are unexpected traffic volume from reflection abuse; apply rate limiting and response limits.",
  ],
  whyAnchors: ["why.18"],
  connections: {
    before:
      "• Module 5: TCP in depth shows what reliability, ordering, and congestion control cost, which is what UDP omits.",
    after:
      "• Module 7: DNS is the most common UDP application, with TCP fallback.\n• Module 8: HTTP/3 builds on QUIC over UDP.",
  },
  selfCheck: [
    {
      id: "no-guarantees",
      prompt: "Why does UDP not retransmit lost datagrams?",
      answer:
        "Recovery costs delay and state and cannot suit every app; for real-time data a retransmitted frame arrives too late to use and blocks newer data (why.18). UDP leaves the policy to the application.",
    },
    {
      id: "checksum",
      prompt: "What does the UDP checksum detect, and what happens when it fails?",
      answer:
        "Corruption in the header, payload, or pseudo-header addresses. The receiver silently drops the datagram; nothing is repaired or reported. It is not protection against a deliberate attacker.",
    },
    {
      id: "quic",
      prompt: "If QUIC is reliable, why does it run over UDP?",
      answer:
        "UDP passes through existing networks and middleboxes and lets QUIC run in user space and evolve without OS or middlebox changes. QUIC adds its own reliability, streams, congestion control, and TLS 1.3 above the datagrams.",
    },
  ],
} satisfies ChapterSummary;
