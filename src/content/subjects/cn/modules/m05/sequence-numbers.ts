import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m05.header-and-numbers",
  status: "complete",
  objectives: [
    "Name the TCP header fields and say which decision each one feeds.",
    "Compute sequence and acknowledgment numbers across a SYN, data, and a FIN.",
    "Explain why each connection starts at a pseudorandom initial sequence number.",
    "Read sequence numbers, flags, and windows in `tcpdump` output, relative and absolute.",
  ],
  prerequisites: ["m05.service-model"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Segments can be lost, duplicated, delayed, or reordered, and the receiver sees only what arrives. To rebuild the byte stream, it needs to know where each segment's bytes belong. To stop resending, the sender needs to know which bytes the receiver holds. Both sides need this for each direction, and old segments from an earlier connection on the same ports must not be mistaken for new ones.",
      whyItMatters:
        "Every TCP behaviour later in this module, from the handshake to retransmission and both windows, is arithmetic on these numbers. Reading a packet capture is mostly reading seq, ack, and win. 'Where did the bytes go?' is answered by comparing them.",
      idea: "Number bytes, not segments. Each direction of a connection has its own 32-bit sequence space starting at an initial sequence number (ISN) chosen by its sender. A segment's sequence number is the number of its first data byte. An acknowledgment number is the next byte the receiver expects, so it acknowledges everything before it at once. SYN and FIN each take one number, so they are acknowledged like data.",
      howItWorks:
        "The fixed header is 20 bytes: source and destination port; sequence number; acknowledgment number, meaningful only when the ACK flag is set and always set once the connection is established; data offset, the header length in 32-bit words; the flags CWR, ECE, URG, ACK, PSH, RST, SYN, and FIN; the window, the number of bytes beginning at the acknowledgment number that the sender of this segment will accept; a checksum over the header, data, and a pseudo-header of the IP addresses; and the urgent pointer. Options follow, up to 40 bytes: MSS, window scale, SACK-permitted, and timestamps are sent in the SYN, and SACK blocks and timestamps later. The sender tracks SND.UNA (oldest unacknowledged byte) and SND.NXT (next byte to send). The receiver tracks RCV.NXT (next byte expected) and RCV.WND. A new ACK is acceptable when SND.UNA < SEG.ACK ≤ SND.NXT. All comparisons are modulo 2^32.",
      whyItWorks:
        "Because every byte has a number, any copy of any segment can be placed exactly, and a duplicate is recognized because its bytes are below RCV.NXT. Because the acknowledgment is cumulative, one ACK repairs the loss of earlier ACKs, and the sender can discard everything below it. Because each direction's numbering starts at its sender's own ISN, the two directions are independent, and a fresh, hard-to-guess ISN makes old or forged segments unlikely to fall inside the current window.",
      limits:
        "A cumulative ACK cannot say 'I have bytes 2000 to 2999 but not 1000 to 1999'. That needs the SACK option, covered in the reliability lesson. The 32-bit space wraps after 4 GiB, which takes 34 seconds at 1 Gbit/s and about 3 at 10 Gbit/s, so fast connections rely on the timestamp option (PAWS) to reject old segments. The 16-bit window field caps an unscaled window at 64 KiB. The checksum is weak, and the ISN only makes guessing hard. It is not authentication.",
      connections:
        "The handshake lesson shows how each side learns the other's ISN. The reliability lesson uses SND.UNA, duplicate ACKs, and SACK. The flow-control lesson uses the window field and the window scale option. Module 3's MSS and Path MTU Discovery decide how many bytes fit in each segment.",
    },
    {
      type: "comparison",
      id: "header-fields",
      title: "The TCP header, field by field",
      caption: "The fixed part is 20 bytes. Options extend it in 4-byte steps, up to 60 bytes.",
      columns: ["Size", "What it answers"],
      rows: [
        {
          label: "Source and destination port",
          cells: ["16 + 16 bits", "Which sockets, together with the IP addresses."],
        },
        {
          label: "Sequence number",
          cells: [
            "32 bits",
            "The number of this segment's first data byte. In a SYN it is the ISN, and the first data byte is ISN + 1.",
          ],
        },
        {
          label: "Acknowledgment number",
          cells: [
            "32 bits",
            "With ACK set: the next byte I expect from you. Everything before it arrived.",
          ],
        },
        {
          label: "Data offset",
          cells: ["4 bits", "Header length in 32-bit words, so where the data starts."],
        },
        {
          label: "Flags",
          cells: [
            "8 bits",
            "SYN (synchronize numbers), ACK (ack field valid), FIN (no more data from me), RST (abort), PSH (deliver promptly), URG, and the ECN bits ECE and CWR.",
          ],
        },
        {
          label: "Window",
          cells: [
            "16 bits",
            "How many bytes, starting at the ack number, I will accept now. Scaled by the window scale option if both sides offered it.",
          ],
        },
        {
          label: "Checksum",
          cells: [
            "16 bits",
            "Mandatory. Covers the header, the data, and the IP addresses via a pseudo-header.",
          ],
        },
        { label: "Urgent pointer", cells: ["16 bits", "Rarely used. Meaningful only with URG."] },
        {
          label: "Options",
          cells: [
            "0 to 40 bytes",
            "MSS (SYN only), window scale (SYN only), SACK-permitted (SYN only), SACK blocks, timestamps.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "numbers-walkthrough",
      title: "Following the numbers through one short connection",
      setup:
        "A client with ISN 1000 connects to a server with ISN 5000, sends a 120-byte request, receives a 300-byte response, and closes. These are absolute numbers. `tcpdump -n -S` prints them this way; without `-S`, tcpdump prints each side's numbers relative to its ISN.",
      walkthrough: [
        "Client SYN: seq 1000, no ACK. The SYN takes sequence number 1000, so the request's first byte will be 1001.",
        "Server SYN-ACK: seq 5000, ack 1001. The server says it expects byte 1001 next, which acknowledges the SYN.",
        "Client ACK: seq 1001, ack 5001. A bare ACK takes no sequence number. The next client segment also has seq 1001.",
        "Client data, 120 bytes: seq 1001, ack 5001. tcpdump shows `seq 1001:1121`, meaning bytes 1001 up to but not including 1121. Server replies ack 1121.",
        "Server data, 300 bytes: seq 5001:5301. Client replies ack 5301.",
        "Client FIN: seq 1121. The FIN takes number 1121, so the server's ACK of it is ack 1122. In relative terms, tcpdump would show the request as `seq 1:121` and this ACK as `ack 122`.",
        "Server FIN: seq 5301. The client's last ACK is ack 5302.",
      ],
      result:
        "Each ack number is the other side's last seq plus its data length, plus one for a SYN or FIN. Every gap or repeat in a capture shows up as a break in that arithmetic.",
    },
    {
      type: "why",
      id: "why-sequence-numbers",
      questionId: "why.12",
    },
    {
      type: "why",
      id: "why-acknowledgments",
      questionId: "why.13",
    },
    {
      type: "prose",
      id: "isn",
      title: "Why the initial sequence number is not zero",
      paragraphs: [
        "A connection is named by its two addresses and two ports. When the same four values are reused, segments from the earlier connection, an earlier 'incarnation', may still be in the network. If every connection started at 0, a delayed segment from the old one could land inside the new one's window and be accepted as data.",
        "RFC 9293 has each side pick its ISN from a clock that ticks roughly every 4 microseconds, plus a keyed pseudorandom function of the four connection values: ISN = M + F(local address, local port, remote address, remote port, secret). The clock part keeps successive ISNs for the same 4-tuple moving forward. The keyed part makes the ISN impossible to predict from outside, which matters because an off-path attacker who can guess the sequence numbers can inject segments or a reset.",
        "The ISN is not authentication. An attacker who can see the traffic reads the numbers directly. TLS, in Module 9, is what protects the bytes.",
      ],
    },
    {
      type: "expandable",
      id: "edge-cases",
      title: "Wraparound, window scaling, and reading captures",
      paragraphs: [
        "Sequence arithmetic is modulo 2^32. 'a is before b' means (b − a) mod 2^32 is less than 2^31. Code that compares raw 32-bit values with < breaks when a connection crosses the wrap point.",
        "At 1 Gbit/s, the sequence space wraps in about 34 seconds, at 10 Gbit/s in about 3, and at 100 Gbit/s in about a third of a second. An old duplicate could then carry a valid-looking number within one segment lifetime. The timestamp option and PAWS (Protection Against Wrapped Sequences) reject such segments by their stale timestamp. Linux enables timestamps by default, with a random offset per connection.",
        "The window field is 16 bits, so without scaling the largest window is 64 KiB. The window scale option, sent only in the SYN and SYN-ACK and used only if both sides send it, gives a shift count of up to 14, for windows up to 1 GiB. The shift is fixed when the connection opens. A capture that misses the handshake cannot know the scale, which is why tcpdump and Wireshark may show implausibly small windows for a connection captured mid-stream.",
        "If no MSS option is received, a sender must assume 536 bytes for IPv4 or 1220 for IPv6. A SYN usually carries the MSS derived from the interface MTU, for example 1460 on a 1500-byte Ethernet link.",
        "tcpdump flags: `S` is SYN, `F` FIN, `P` PSH, `R` RST, and `.` alone means only ACK. So `Flags [S.]` is a SYN-ACK and `Flags [P.]` is data with ACK. `win` is the raw window field, before scaling.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "ack-after-fin",
          prompt:
            "A client's last data segment is `seq 4001:4501`, and the same segment carries FIN. What acknowledgment number will the server send?",
          answer:
            "4502. The data occupies 4001 to 4500, and the FIN takes the next number, 4501. The ACK names the next expected number, which is 4502.",
        },
        {
          id: "duplicate",
          prompt:
            "The receiver's RCV.NXT is 9000, and a segment arrives with seq 8000 and 500 bytes. What does the receiver do, and why does the sender ever send such a segment?",
          answer:
            "Bytes 8000 to 8499 are all below 9000, so they are a duplicate. The receiver drops the data and sends an ACK of 9000 so the sender learns where things stand. The sender sent it because it retransmitted after a timeout while the original, or its ACK, was only delayed. The network can also duplicate a packet.",
        },
        {
          id: "random-isn",
          prompt:
            "Why would always starting every connection at sequence number 0 be both unsafe and insecure?",
          answer:
            "Unsafe: a reused 4-tuple would reuse the same numbers, so a delayed segment from the previous connection could fall inside the new window and be accepted as data. Insecure: anyone who knows the ports could predict valid sequence numbers and inject data or a RST without seeing the traffic. The clock plus keyed hash prevents both.",
        },
        {
          id: "small-window",
          prompt:
            "A capture started in the middle of a bulk transfer shows `win 501` on every ACK from the receiver, yet the transfer runs at 900 Mbit/s. What is going on?",
          answer:
            "Window scaling. The scale was set in the SYN and SYN-ACK, which the capture missed, so tcpdump prints the raw 16-bit field. With a shift of 7, for example, 501 means 501 × 128 = 64,128 bytes, and larger shifts mean larger windows.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "header-format",
      statement:
        "The sequence number is that of the first data octet, except in a SYN, where it is the ISN and the first data octet is ISN+1. With ACK set, the acknowledgment number is the next sequence number expected, and once a connection is established it is always sent. The window is the number of octets, starting with the acknowledged one, that the segment's sender will accept. The checksum covers the header, data, and a pseudo-header, and is mandatory. The control bits are CWR, ECE, URG, ACK, PSH, RST, SYN, and FIN.",
      sourceId: "rfc9293",
      locator: "Section 3.1, Header Format",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "options",
      statement:
        "End of Option List, No-Operation, and MSS must be supported. The MSS option may be sent only in SYN segments. SACK, timestamps, and window scale are other common options recommended for performance. If no MSS option is received, the send MSS defaults to 536 for IPv4 or 1220 for IPv6.",
      sourceId: "rfc9293",
      locator: "Section 3.1, Options; Section 3.2; Section 3.2.1; Section 3.7.1",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "every-octet",
      statement:
        "Every octet has a sequence number and the acknowledgment is cumulative: an ACK of X means all octets before X were received, which makes duplicate detection straightforward. Sequence arithmetic is modulo 2^32. A new ACK is acceptable if SND.UNA < SEG.ACK =< SND.NXT. SYN and FIN occupy sequence space; a bare ACK does not.",
      sourceId: "rfc9293",
      locator:
        "Section 3.4, Sequence Numbers; Section 3.5, Figure 6 discussion; Section 4, Glossary (ACK, FIN)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "state-variables",
      statement:
        "The TCB holds SND.UNA, SND.NXT, SND.WND, and ISS for sending, and RCV.NXT, RCV.WND, and IRS for receiving. The send window is the space from SND.UNA to SND.UNA+SND.WND; the receive window starts at RCV.NXT.",
      sourceId: "rfc9293",
      locator: "Section 3.3.1, Key Connection State Variables",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "isn-generation",
      statement:
        "ISNs come from a clock that typically increments about every 4 microseconds and should be generated as ISN = M + F(localip, localport, remoteip, remoteport, secretkey), where F must not be computable from outside, because predictable ISNs let off-path attackers guess sequence numbers. The purpose is to protect against confusing segments from earlier incarnations of a connection.",
      sourceId: "rfc9293",
      locator: "Section 3.4.1, Initial Sequence Number Selection (MUST-8, SHLD-1, MUST-9)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "wraparound",
      statement:
        "The sequence space cycles in about 34 seconds at 1 Gbps, 3 seconds at 10 Gbps, and a third of a second at 100 Gbps; in these cases, timestamps and PAWS are needed to detect and discard old duplicates.",
      sourceId: "rfc9293",
      locator: "Section 3.4.3, The TCP Quiet Time Concept, final paragraph",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "window-scale",
      statement:
        "The 16-bit window field limits the window to 64 KiB. The Window Scale option is sent only in a SYN or SYN-ACK, both sides must send it to enable scaling, the shift count is at most 14 (a 1 GiB window), the scale is fixed for each direction when the connection opens, and the window in a SYN or SYN-ACK is never scaled.",
      sourceId: "rfc7323",
      locator: "Section 1.1 (1); Section 2.1; Section 2.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "linux-timestamps",
      statement:
        "Linux supports PAWS, window scaling, and timestamps. tcp_timestamps defaults to 1, which enables timestamps with a random offset per connection, and tcp_window_scaling is enabled by default.",
      sourceId: "man-tcp",
      locator: "DESCRIPTION; /proc interfaces, tcp_timestamps, tcp_window_scaling",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "tcpdump-output",
      statement:
        "tcpdump prints TCP flags as S (SYN), F (FIN), P (PSH), R (RST) and others, with `.` for ACK; sequence ranges as first:last, meaning up to but not including last; and, after the first packet of a conversation, sequence numbers relative to the initial one. -S prints absolute sequence numbers.",
      sourceId: "man-tcpdump",
      locator: "OUTPUT FORMAT, TCP Packets; OPTIONS, -S",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    {
      sourceId: "rfc9293",
      note: "Sections 3.1 to 3.4.3: header, options, state variables, sequence numbers, and ISN selection.",
    },
    { sourceId: "rfc7323", note: "Window scaling and why the 16-bit window is too small." },
    { sourceId: "man-tcp", note: "Linux defaults for timestamps and window scaling." },
    { sourceId: "man-tcpdump", note: "Reading flags and relative sequence numbers." },
  ],
} satisfies LessonBody;
