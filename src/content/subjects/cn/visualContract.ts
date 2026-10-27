/**
 * Closed list of interactive diagrams. A lesson may reference one of these ids.
 * Add an id here only when a written learning objective says what the interaction teaches.
 * Marking one implemented happens in the visual registry, after a tested model exists.
 */
export const approvedVisuals = [
  {
    id: "encapsulation-stack",
    learningObjective:
      "See headers added on the way out and removed on the way in, and see payload size compete with the MTU.",
  },
  {
    id: "hop-address-walk",
    learningObjective:
      "See the destination MAC change at each routed hop while the destination IP stays the same.",
  },
  {
    id: "prefix-match",
    learningObjective: "See a longer prefix win over a shorter matching prefix.",
  },
  {
    id: "tcp-timeline",
    learningObjective:
      "Step a TCP exchange and read sequence numbers, acknowledgments, and what each endpoint knows.",
  },
  {
    id: "two-windows",
    learningObjective:
      "See the usable window limited by the smaller of flow control and congestion control.",
  },
  {
    id: "dns-lookup",
    learningObjective: "See which server is queried at each step and what a resolver caches.",
  },
  {
    id: "stream-blocking",
    learningObjective:
      "See one TCP loss stall every HTTP/2 stream on that connection, and one QUIC loss stall one stream.",
  },
  {
    id: "tls-stages",
    learningObjective:
      "See which keys exist at each stage and what the certificate has authenticated.",
  },
  {
    id: "nat-table",
    learningObjective:
      "See an outbound mapping created, and see an unsolicited inbound packet fail to match it.",
  },
  {
    id: "socket-calls",
    learningObjective: "See socket API calls move a connection through its states.",
  },
  {
    id: "bandwidth-delay",
    learningObjective: "See throughput capped by the amount of data in flight divided by the RTT.",
  },
] as const;

export type VisualId = (typeof approvedVisuals)[number]["id"];

export const approvedVisualIds: ReadonlySet<string> = new Set(
  approvedVisuals.map((item) => item.id),
);
