/**
 * Step-by-step TCP exchanges for the tcp-timeline visual.
 * Pure data and functions. This file must not import React.
 *
 * Each step is the state AFTER the event it describes, as in the figures of RFC 9293.
 * A step with `from: null` is a local event (a timer or a reboot), not a segment.
 */

export type TcpEndpoint = "a" | "b";
export type TcpFlag = "SYN" | "ACK" | "FIN" | "RST" | "PSH";
export type TcpDelivery = "arrives" | "lost" | "in-flight";

export type TcpTimelineStep = {
  index: number;
  label: string;
  /** Who sent the segment, or null for a local event. */
  from: TcpEndpoint | null;
  flags: TcpFlag[];
  seq: number | null;
  /** Present only when the ACK flag is set. */
  ack: number | null;
  /** Bytes of data. SYN and FIN each occupy one sequence number but carry no data here. */
  len: number;
  sack: string | null;
  isRetransmit: boolean;
  delivery: TcpDelivery;
  stateA: string;
  stateB: string;
  aKnows: string;
  bKnows: string;
  note: string;
};

export type TcpTimelineScenario = {
  id: string;
  title: string;
  labels: { a: string; b: string };
  /** Fields the scenario leaves out, stated so the simplification is visible. */
  omitted: string;
  steps: TcpTimelineStep[];
};

type StepInput = Omit<
  TcpTimelineStep,
  "index" | "flags" | "seq" | "ack" | "len" | "sack" | "isRetransmit" | "delivery"
> &
  Partial<
    Pick<TcpTimelineStep, "flags" | "seq" | "ack" | "len" | "sack" | "isRetransmit" | "delivery">
  >;

function scenario(
  id: string,
  title: string,
  labels: { a: string; b: string },
  omitted: string,
  steps: StepInput[],
): TcpTimelineScenario {
  return {
    id,
    title,
    labels,
    omitted,
    steps: steps.map((step, index) => ({
      flags: [],
      seq: null,
      ack: null,
      len: 0,
      sack: null,
      isRetransmit: false,
      delivery: "arrives",
      ...step,
      index,
    })),
  };
}

const clientServer = { a: "Client", b: "Server" };
const senderReceiver = { a: "Sender", b: "Receiver" };
const headerFieldsOmitted =
  "Ports, window, checksum, and options (MSS, window scale, SACK-permitted, timestamps) are left out.";

const handshakeBasic = scenario(
  "handshake-basic",
  "Three-way handshake, then the first request",
  clientServer,
  `${headerFieldsOmitted} Sequence numbers follow RFC 9293 Figure 6; real initial sequence numbers are large pseudorandom values.`,
  [
    {
      label: "Before any segment",
      from: null,
      stateA: "CLOSED",
      stateB: "LISTEN",
      aKnows:
        "When it connects, it will start at sequence number 100. It knows nothing about the server's.",
      bKnows: "It has a listening socket on the port. It has no state for this client.",
      note: "The server called listen(). The client is about to call connect().",
    },
    {
      label: "Client sends SYN",
      from: "a",
      flags: ["SYN"],
      seq: 100,
      stateA: "SYN-SENT",
      stateB: "SYN-RECEIVED",
      aKnows: "It sent SYN with seq=100. It does not know whether the SYN arrived.",
      bKnows:
        "Someone claims to start a connection at sequence 100, so the next byte will be 101. It cannot tell whether this SYN is current or an old duplicate.",
      note: "The SYN occupies sequence number 100. It carries no ACK, so the acknowledgment field means nothing yet.",
    },
    {
      label: "Server sends SYN-ACK",
      from: "b",
      flags: ["SYN", "ACK"],
      seq: 300,
      ack: 101,
      stateA: "ESTABLISHED",
      stateB: "SYN-RECEIVED",
      aKnows:
        "ack=101 matches its own SYN, so the server received it and the reply is current. The server starts at 300, so the next server byte is 301.",
      bKnows:
        "It still does not know whether its SYN arrived, or whether the client's SYN was current.",
      note: "One segment carries both the server's SYN and its acknowledgment of the client's SYN. That is why the handshake has three messages, not four.",
    },
    {
      label: "Client sends ACK",
      from: "a",
      flags: ["ACK"],
      seq: 101,
      ack: 301,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Both initial sequence numbers. connect() has returned.",
      bKnows:
        "ack=301 acknowledges its SYN, so the client is live and agrees on both starting points. The connection is queued for accept().",
      note: "A bare ACK occupies no sequence space, so the client's next data byte is still 101.",
    },
    {
      label: "Client sends a 78-byte request",
      from: "a",
      flags: ["PSH", "ACK"],
      seq: 101,
      ack: 301,
      len: 78,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Bytes 101 to 178 are sent and unacknowledged.",
      bKnows: "Bytes 101 to 178 are in its receive buffer. The next byte it expects is 179.",
      note: "The segment has the same seq as the ACK before it, because that ACK consumed no sequence number.",
    },
    {
      label: "Server's TCP acknowledges the request",
      from: "b",
      flags: ["ACK"],
      seq: 301,
      ack: 179,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows:
        "The server's TCP has bytes up to 178. It does not know whether the server application has read them.",
      bKnows:
        "It has acknowledged everything it received. The application may not have called read() yet.",
      note: "The kernel sends this acknowledgment. It says the bytes arrived, not that the request was processed.",
    },
  ],
);

const handshakeOldDuplicate = scenario(
  "handshake-old-duplicate",
  "An old duplicate SYN is rejected by the third message",
  clientServer,
  `${headerFieldsOmitted} Numbers follow RFC 9293 Figure 8.`,
  [
    {
      label: "Before any segment",
      from: null,
      stateA: "CLOSED",
      stateB: "LISTEN",
      aKnows: "It will start at 100.",
      bKnows: "It is listening and has no state for this client.",
      note: "An earlier connection between the same ports left a SYN with seq=90 wandering in the network.",
    },
    {
      label: "Client sends SYN, which is delayed",
      from: "a",
      flags: ["SYN"],
      seq: 100,
      delivery: "in-flight",
      stateA: "SYN-SENT",
      stateB: "LISTEN",
      aKnows: "It sent SYN seq=100 and waits for an ACK of 101.",
      bKnows: "Nothing yet. The new SYN is still in the network.",
      note: "This SYN is delayed. It arrives later, at step 5.",
    },
    {
      label: "Old duplicate SYN (seq=90) arrives",
      from: "a",
      flags: ["SYN"],
      seq: 90,
      stateA: "SYN-SENT",
      stateB: "SYN-RECEIVED",
      aKnows: "Nothing. It did not send this segment on this connection.",
      bKnows:
        "A SYN says the client starts at 90. It cannot tell that this SYN is old, so it answers normally.",
      note: "The duplicate comes from the earlier connection. It has the right addresses and ports.",
    },
    {
      label: "Server answers SYN-ACK for the old SYN",
      from: "b",
      flags: ["SYN", "ACK"],
      seq: 300,
      ack: 91,
      stateA: "SYN-SENT",
      stateB: "SYN-RECEIVED",
      aKnows:
        "ack=91 does not acknowledge its SYN (it needs 101), so this reply belongs to some other attempt.",
      bKnows: "It is waiting for the third message.",
      note: "This is the question a two-way handshake never asks: did you really send that SYN?",
    },
    {
      label: "Client resets the bogus attempt",
      from: "a",
      flags: ["RST"],
      seq: 91,
      stateA: "SYN-SENT",
      stateB: "LISTEN",
      aKnows: "It is still waiting for an answer to its own SYN.",
      bKnows: "The attempt at 90 was not wanted. It discards it and returns to LISTEN.",
      note: "The RST takes its seq from the ACK field it is answering (91), so the server accepts it.",
    },
    {
      label: "The delayed SYN (seq=100) arrives",
      from: "a",
      flags: ["SYN"],
      seq: 100,
      stateA: "SYN-SENT",
      stateB: "SYN-RECEIVED",
      aKnows: "It is still waiting for ACK 101.",
      bKnows: "A SYN at 100. It again cannot judge it alone, so it answers.",
      note: "This is the same segment sent at step 1, not a retransmission.",
    },
    {
      label: "Server sends SYN-ACK for 100",
      from: "b",
      flags: ["SYN", "ACK"],
      seq: 400,
      ack: 101,
      stateA: "ESTABLISHED",
      stateB: "SYN-RECEIVED",
      aKnows: "ack=101 matches its SYN. The server starts at 400.",
      bKnows: "It waits for the client to confirm 400.",
      note: "The server chose a new initial sequence number, 400, for this attempt.",
    },
    {
      label: "Client sends ACK",
      from: "a",
      flags: ["ACK"],
      seq: 101,
      ack: 401,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Both starting points: its 100 and the server's 400.",
      bKnows: "The client confirmed 400, so this attempt is current.",
      note: "Only the attempt the client really made became a connection.",
    },
  ],
);

const handshakeRefused = scenario(
  "handshake-refused",
  "SYN to a port with no listener",
  clientServer,
  `${headerFieldsOmitted} The host is up, but nothing listens on the port.`,
  [
    {
      label: "Before any segment",
      from: null,
      stateA: "CLOSED",
      stateB: "CLOSED (no listener)",
      aKnows: "It will start at 100.",
      bKnows: "No socket is bound to this port.",
      note: "A connection that does not exist is in the fictional CLOSED state.",
    },
    {
      label: "Client sends SYN",
      from: "a",
      flags: ["SYN"],
      seq: 100,
      stateA: "SYN-SENT",
      stateB: "CLOSED (no listener)",
      aKnows: "It sent SYN seq=100.",
      bKnows: "A SYN arrived for a port with no connection and no listener.",
      note: "",
    },
    {
      label: "Host answers with RST, ACK",
      from: "b",
      flags: ["RST", "ACK"],
      seq: 0,
      ack: 101,
      stateA: "CLOSED",
      stateB: "CLOSED (no listener)",
      aKnows:
        "The RST acknowledges its SYN (ack=101), so it is a real answer. connect() fails with ECONNREFUSED.",
      bKnows: "Nothing. It kept no state.",
      note: "The SYN had no ACK, so the reset has seq=0 and ack = the SYN's seq plus its length. The fast answer proves the path to the host works.",
    },
  ],
);

const teardownFin = scenario(
  "teardown-fin",
  "Graceful close with FIN, half-close, and TIME-WAIT",
  clientServer,
  `${headerFieldsOmitted} Numbers start from RFC 9293 Figure 12; the 50 bytes the server sends while half-closed are added.`,
  [
    {
      label: "Both sides established",
      from: null,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Its next byte is 100. The server's next byte is 300.",
      bKnows: "Its next byte is 300. The client's next byte is 100.",
      note: "All data so far is acknowledged.",
    },
    {
      label: "Client closes its sending side (FIN)",
      from: "a",
      flags: ["FIN", "ACK"],
      seq: 100,
      ack: 300,
      stateA: "FIN-WAIT-1",
      stateB: "CLOSE-WAIT",
      aKnows: "It will send no more data. It does not yet know the FIN arrived.",
      bKnows:
        "The client will send nothing after byte 99. Once it reads the buffered data, read() returns 0. It may still send.",
      note: "The application called close() or shutdown(SHUT_WR). The FIN occupies sequence number 100.",
    },
    {
      label: "Server acknowledges the FIN",
      from: "b",
      flags: ["ACK"],
      seq: 300,
      ack: 101,
      stateA: "FIN-WAIT-2",
      stateB: "CLOSE-WAIT",
      aKnows: "Its FIN arrived (ack=101). The server's direction is still open.",
      bKnows: "It is waiting for its own application to close.",
      note: "The server's kernel acknowledges the FIN at once. Its own FIN waits for its application.",
    },
    {
      label: "Server sends 50 more bytes",
      from: "b",
      flags: ["PSH", "ACK"],
      seq: 300,
      ack: 101,
      len: 50,
      stateA: "FIN-WAIT-2",
      stateB: "CLOSE-WAIT",
      aKnows:
        "Bytes 300 to 349 arrived. A half-closed connection still carries data the other way.",
      bKnows: "Bytes 300 to 349 are sent and unacknowledged.",
      note: "This is a half-close: one direction is finished, the other is not.",
    },
    {
      label: "Client acknowledges the data",
      from: "a",
      flags: ["ACK"],
      seq: 101,
      ack: 350,
      stateA: "FIN-WAIT-2",
      stateB: "CLOSE-WAIT",
      aKnows: "It has every server byte up to 349.",
      bKnows: "The client received everything it sent.",
      note: "The client's seq stays at 101, one past its FIN.",
    },
    {
      label: "Server closes (FIN)",
      from: "b",
      flags: ["FIN", "ACK"],
      seq: 350,
      ack: 101,
      stateA: "TIME-WAIT",
      stateB: "LAST-ACK",
      aKnows: "The server will send nothing more. Both directions are finished.",
      bKnows: "It sent its FIN, at sequence number 350, and waits for the last ACK.",
      note: "",
    },
    {
      label: "Client sends the last ACK",
      from: "a",
      flags: ["ACK"],
      seq: 101,
      ack: 351,
      stateA: "TIME-WAIT",
      stateB: "CLOSED",
      aKnows:
        "It cannot know whether this ACK arrives. If it is lost, the server will retransmit its FIN, and the client must still be there to answer.",
      bKnows: "Its FIN is acknowledged. It deletes the connection.",
      note: "The side that closed first is the one that waits in TIME-WAIT.",
    },
    {
      label: "TIME-WAIT expires",
      from: null,
      stateA: "CLOSED",
      stateB: "CLOSED",
      aKnows:
        "Any delayed segment from this connection has had time to expire. The address and port pair is free.",
      bKnows: "Nothing. It closed earlier.",
      note: "RFC 9293 requires 2 × MSL. Linux uses a fixed 60 seconds.",
    },
  ],
);

const teardownRst = scenario(
  "teardown-rst",
  "A peer that lost its state answers with RST",
  clientServer,
  `${headerFieldsOmitted} Numbers follow RFC 9293 Figure 10, with the rebooted host as the server.`,
  [
    {
      label: "Both sides established",
      from: null,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Its next byte is 300. The server's next byte is 100.",
      bKnows: "Its next byte is 100. The client's next byte is 300.",
      note: "",
    },
    {
      label: "Server host reboots",
      from: null,
      stateA: "ESTABLISHED",
      stateB: "CLOSED (state lost)",
      aKnows: "Nothing has changed, as far as it can tell. No segment was sent.",
      bKnows: "It has no record of the connection.",
      note: "The connection is now half-open: one end believes in it and the other does not. A process crash would not do this, because the kernel would close the socket and send FIN or RST.",
    },
    {
      label: "Client sends 10 bytes",
      from: "a",
      flags: ["PSH", "ACK"],
      seq: 300,
      ack: 100,
      len: 10,
      stateA: "ESTABLISHED",
      stateB: "CLOSED (state lost)",
      aKnows: "Bytes 300 to 309 are sent and unacknowledged.",
      bKnows: "A data segment arrived for a connection it does not have.",
      note: "",
    },
    {
      label: "Server answers RST",
      from: "b",
      flags: ["RST"],
      seq: 100,
      stateA: "CLOSED",
      stateB: "CLOSED (state lost)",
      aKnows:
        "The peer has no such connection. seq=100 is exactly the next byte it expected, so the RST is valid. Its next call on the socket fails with ECONNRESET. It does not know which of its earlier bytes the server application processed.",
      bKnows: "Nothing. It kept no state.",
      note: "The incoming segment had ACK set, so the reset takes its seq from that ACK field (100). Nothing about the connection is retransmitted after a reset.",
    },
  ],
);

const teardownSimultaneous = scenario(
  "teardown-simultaneous",
  "Simultaneous close",
  { a: "Peer A", b: "Peer B" },
  `${headerFieldsOmitted} Numbers follow RFC 9293 Figure 13.`,
  [
    {
      label: "Both sides established",
      from: null,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Its next byte is 100. B's next byte is 300.",
      bKnows: "Its next byte is 300. A's next byte is 100.",
      note: "",
    },
    {
      label: "A sends FIN",
      from: "a",
      flags: ["FIN", "ACK"],
      seq: 100,
      ack: 300,
      delivery: "in-flight",
      stateA: "FIN-WAIT-1",
      stateB: "ESTABLISHED",
      aKnows: "It sent its FIN at 100.",
      bKnows: "Nothing yet. A's FIN is still in the network.",
      note: "",
    },
    {
      label: "B sends FIN before A's arrives",
      from: "b",
      flags: ["FIN", "ACK"],
      seq: 300,
      ack: 100,
      delivery: "in-flight",
      stateA: "FIN-WAIT-1",
      stateB: "FIN-WAIT-1",
      aKnows: "Nothing new yet.",
      bKnows: "It sent its FIN at 300. Its ack=100 does not cover A's FIN, which it has not seen.",
      note: "Both applications called close() at about the same time.",
    },
    {
      label: "B's FIN reaches A",
      from: "b",
      flags: ["FIN", "ACK"],
      seq: 300,
      ack: 100,
      stateA: "CLOSING",
      stateB: "FIN-WAIT-1",
      aKnows: "B is finished sending. Its own FIN is not acknowledged yet.",
      bKnows: "Nothing new yet.",
      note: "This is the segment from step 2 arriving.",
    },
    {
      label: "A's FIN reaches B",
      from: "a",
      flags: ["FIN", "ACK"],
      seq: 100,
      ack: 300,
      stateA: "CLOSING",
      stateB: "CLOSING",
      aKnows: "Nothing new yet.",
      bKnows: "A is finished sending. Its own FIN is not acknowledged yet.",
      note: "This is the segment from step 1 arriving.",
    },
    {
      label: "A acknowledges B's FIN",
      from: "a",
      flags: ["ACK"],
      seq: 101,
      ack: 301,
      stateA: "CLOSING",
      stateB: "TIME-WAIT",
      aKnows: "It waits for an ACK of its own FIN.",
      bKnows: "Its FIN is acknowledged and it has A's FIN, so it enters TIME-WAIT.",
      note: "",
    },
    {
      label: "B acknowledges A's FIN",
      from: "b",
      flags: ["ACK"],
      seq: 301,
      ack: 101,
      stateA: "TIME-WAIT",
      stateB: "TIME-WAIT",
      aKnows: "Both FINs are sent and acknowledged.",
      bKnows: "Both FINs are sent and acknowledged.",
      note: "Each side actively closed, so each side waits.",
    },
    {
      label: "TIME-WAIT expires on both sides",
      from: null,
      stateA: "CLOSED",
      stateB: "CLOSED",
      aKnows: "The connection is gone.",
      bKnows: "The connection is gone.",
      note: "",
    },
  ],
);

const lossFastRetransmit = scenario(
  "loss-fast-retransmit",
  "One lost segment, three duplicate ACKs, and a fast retransmit",
  senderReceiver,
  `${headerFieldsOmitted} The receiver sends no data, so its seq stays at 5000, and it acknowledges every segment at once instead of delaying. Segments are 100 bytes so the arithmetic stays readable.`,
  [
    {
      label: "Window open, nothing in flight",
      from: null,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "SND.UNA = SND.NXT = 1000. Its windows allow five segments.",
      bKnows: "RCV.NXT = 1000. SACK was permitted in the handshake.",
      note: "",
    },
    {
      label: "Segment 1000 sent",
      from: "a",
      flags: ["ACK"],
      seq: 1000,
      ack: 5000,
      len: 100,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Bytes 1000 to 1099 in flight. The retransmission timer is running.",
      bKnows: "Bytes 1000 to 1099 arrived in order. The application can read them.",
      note: "",
    },
    {
      label: "Segment 1100 sent and lost",
      from: "a",
      flags: ["ACK"],
      seq: 1100,
      ack: 5000,
      len: 100,
      delivery: "lost",
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Bytes 1000 to 1199 in flight. It cannot see the loss.",
      bKnows: "Nothing. The segment never arrived.",
      note: "A router queue dropped it.",
    },
    {
      label: "Segment 1200 sent",
      from: "a",
      flags: ["ACK"],
      seq: 1200,
      ack: 5000,
      len: 100,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Bytes 1000 to 1299 in flight.",
      bKnows:
        "Bytes 1200 to 1299 arrived above a gap. It holds them, but the application cannot read them, because 1100 to 1199 are missing.",
      note: "",
    },
    {
      label: "Segment 1300 sent",
      from: "a",
      flags: ["ACK"],
      seq: 1300,
      ack: 5000,
      len: 100,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Bytes 1000 to 1399 in flight.",
      bKnows: "Holds 1200 to 1399 out of order. RCV.NXT is still 1100.",
      note: "",
    },
    {
      label: "Segment 1400 sent",
      from: "a",
      flags: ["ACK"],
      seq: 1400,
      ack: 5000,
      len: 100,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Bytes 1000 to 1499 in flight. The window is full.",
      bKnows: "Holds 1200 to 1499 out of order. The application is still waiting for byte 1100.",
      note: "",
    },
    {
      label: "ACK 1100 (for segment 1000)",
      from: "b",
      flags: ["ACK"],
      seq: 5000,
      ack: 1100,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Bytes before 1100 are delivered. SND.UNA = 1100.",
      bKnows: "",
      note: "A new cumulative acknowledgment.",
    },
    {
      label: "Duplicate ACK 1 (for segment 1200)",
      from: "b",
      flags: ["ACK"],
      seq: 5000,
      ack: 1100,
      sack: "1200–1300",
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Something above 1100 arrived, but 1100 did not. This could be reordering.",
      bKnows: "",
      note: "The ACK number cannot move past the gap. SACK reports the block that did arrive.",
    },
    {
      label: "Duplicate ACK 2 (for segment 1300)",
      from: "b",
      flags: ["ACK"],
      seq: 5000,
      ack: 1100,
      sack: "1200–1400",
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Two segments above the gap arrived.",
      bKnows: "",
      note: "",
    },
    {
      label: "Duplicate ACK 3 (for segment 1400)",
      from: "b",
      flags: ["ACK"],
      seq: 5000,
      ack: 1100,
      sack: "1200–1500",
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows:
        "Three duplicates: 1100 is treated as lost. It retransmits now, without waiting for the timer, and cuts its congestion window.",
      bKnows: "",
      note: "Three duplicate ACKs is the RFC 5681 threshold. Linux detects loss with RACK, which uses timing rather than a count; the retransmission is the same.",
    },
    {
      label: "Retransmission of 1100 arrives",
      from: "a",
      flags: ["ACK"],
      seq: 1100,
      ack: 5000,
      len: 100,
      isRetransmit: true,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Only the missing segment was resent. SACK told it 1200 to 1499 need nothing.",
      bKnows:
        "The gap is filled. Bytes 1100 to 1499 become readable at once. RCV.NXT jumps to 1500.",
      note: "A retransmission: same bytes, same sequence numbers as the original at step 2.",
    },
    {
      label: "ACK 1500",
      from: "b",
      flags: ["ACK"],
      seq: 5000,
      ack: 1500,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Everything up to 1499 is delivered. SND.UNA = 1500.",
      bKnows: "",
      note: "Bytes 1200 to 1499 arrived early but reached the application one round trip late.",
    },
  ],
);

const lossTimeout = scenario(
  "loss-timeout",
  "A lost last segment waits for the retransmission timer",
  senderReceiver,
  `${headerFieldsOmitted} RTO values follow RFC 6298's 1-second floor; Linux allows 200 ms. Linux's tail loss probe, which would usually resend sooner, is left out. The receiver sends no data, so its seq stays at 7000.`,
  [
    {
      label: "Last request bytes are ready",
      from: null,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "SND.NXT = 2000. Current RTO = 1 s.",
      bKnows: "RCV.NXT = 2000.",
      note: "The final 100 bytes of a request. Nothing will be sent after them until a reply comes.",
    },
    {
      label: "Segment 2000 sent and lost",
      from: "a",
      flags: ["PSH", "ACK"],
      seq: 2000,
      ack: 7000,
      len: 100,
      delivery: "lost",
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Bytes 2000 to 2099 in flight. The timer will fire in 1 s.",
      bKnows: "Nothing arrived.",
      note: "No later segment follows, so the receiver has nothing to send duplicate ACKs about.",
    },
    {
      label: "Retransmission timer expires",
      from: null,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows:
        "No ACK within the RTO. It assumes the segment is lost, doubles the RTO to 2 s, and drops its congestion window to one segment.",
      bKnows: "Nothing.",
      note: "A timeout is the strongest loss signal TCP has, so the reaction is the largest.",
    },
    {
      label: "Retransmission of 2000 sent and lost",
      from: "a",
      flags: ["PSH", "ACK"],
      seq: 2000,
      ack: 7000,
      len: 100,
      isRetransmit: true,
      delivery: "lost",
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "The same bytes are in flight again. The timer will fire in 2 s.",
      bKnows: "Nothing arrived.",
      note: "",
    },
    {
      label: "Timer expires again",
      from: null,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Still no ACK. RTO doubles to 4 s.",
      bKnows: "Nothing.",
      note: "3 s have passed since the original send.",
    },
    {
      label: "Second retransmission of 2000 arrives",
      from: "a",
      flags: ["PSH", "ACK"],
      seq: 2000,
      ack: 7000,
      len: 100,
      isRetransmit: true,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows: "Waiting, with a 4 s timer.",
      bKnows: "Bytes 2000 to 2099 arrived. The application can read the full request.",
      note: "",
    },
    {
      label: "ACK 2100",
      from: "b",
      flags: ["ACK"],
      seq: 7000,
      ack: 2100,
      stateA: "ESTABLISHED",
      stateB: "ESTABLISHED",
      aKnows:
        "The bytes are delivered. It cannot tell which copy this ACK answers, so it takes no round-trip sample from it and keeps the backed-off RTO until new data is acknowledged without a retransmission.",
      bKnows: "",
      note: "Karn's rule. The request reached the server more than 3 s late on a path whose round trip might be 20 ms.",
    },
  ],
);

export const tcpTimelineScenarios: Readonly<Record<string, TcpTimelineScenario>> =
  Object.fromEntries(
    [
      handshakeBasic,
      handshakeOldDuplicate,
      handshakeRefused,
      teardownFin,
      teardownRst,
      teardownSimultaneous,
      lossFastRetransmit,
      lossTimeout,
    ].map((item) => [item.id, item]),
  );

export function getTcpTimelineScenario(id: string): TcpTimelineScenario | undefined {
  return tcpTimelineScenarios[id];
}

export function getTcpTimelineStep(scenarioId: string, index: number): TcpTimelineStep | undefined {
  return tcpTimelineScenarios[scenarioId]?.steps[index];
}

/** "SYN, ACK seq=300 ack=101" — the fields a capture would show, in text. */
export function segmentSummary(step: TcpTimelineStep): string {
  if (step.from === null) return "No segment";
  const parts = [step.flags.join(", ")];
  if (step.seq !== null) parts.push(`seq=${step.seq}`);
  if (step.ack !== null) parts.push(`ack=${step.ack}`);
  if (step.len > 0) parts.push(`len=${step.len}`);
  if (step.sack) parts.push(`SACK ${step.sack}`);
  return parts.join(" ");
}

export function directionText(scenario: TcpTimelineScenario, step: TcpTimelineStep): string {
  if (step.from === null) return "Local event";
  return step.from === "a"
    ? `${scenario.labels.a} to ${scenario.labels.b}`
    : `${scenario.labels.b} to ${scenario.labels.a}`;
}

const deliveryText: Record<TcpDelivery, string> = {
  arrives: "",
  lost: " Lost in the network.",
  "in-flight": " Still in the network.",
};

/** One sentence block that says everything the diagram shows for a step. */
export function describeTcpTimelineStep(scenario: TcpTimelineScenario, index: number): string {
  const step = scenario.steps[index];
  if (!step) return "";
  const segment =
    step.from === null
      ? ""
      : ` ${directionText(scenario, step)}: ${step.isRetransmit ? "retransmission, " : ""}${segmentSummary(step)}.${deliveryText[step.delivery]}`;
  const knows = [
    `${scenario.labels.a} is ${step.stateA}.${step.aKnows ? ` ${step.aKnows}` : ""}`,
    `${scenario.labels.b} is ${step.stateB}.${step.bKnows ? ` ${step.bKnows}` : ""}`,
  ].join(" ");
  const note = step.note ? ` ${step.note}` : "";
  return `Step ${index + 1} of ${scenario.steps.length}: ${step.label}.${segment} ${knows}${note}`;
}
