import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m01",
  status: "complete",
  readingMinutes: { min: 10, max: 15 },
  thesis:
    "A network moves bytes between programs that do not share memory, so the work is split into layers with narrow jobs.",
  outcomes: [
    "Name what two remote programs need beyond shared memory.",
    "Explain why layering beats one mega-protocol, using OSI and TCP/IP as maps.",
    "Trace encapsulation and say what the MTU constrains.",
    "Separate control plane from data plane, and connection-oriented from connectionless service.",
  ],
  coverage: [
    {
      bullet: "Network architecture and protocol layering.",
      notes:
        "• Programs need naming, delivery, and meaning; no shared RAM.\n• Architecture = which jobs exist and which protocol owns each.\n• Layering: one job per layer, narrow interface up/down.",
    },
    {
      bullet: "OSI model and TCP/IP model.",
      notes:
        "• OSI is a seven-layer teaching map; TCP/IP is the deployed stack (link, internet, transport, application).\n• Real products blur layers; the map still names who owns what.\n• Routers stop at IP; endpoints run transport and app.",
    },
    {
      bullet: "Encapsulation and decapsulation.",
      notes:
        "• Each layer wraps a header (and maybe trailer) around the upper PDU.\n• On receive, headers peel in reverse order.\n• Same bits, different names: segment / datagram / frame by layer.",
    },
    {
      bullet: "Frames, packets, segments, and application data.",
      notes:
        "• Application data → transport segment (TCP) or datagram (UDP) → IP packet → link frame.\n• Switches see frames; routers see packets; apps see messages they framed themselves.",
    },
    {
      bullet: "MTU and payload sizes.",
      notes:
        "• MTU = max IP packet on that link (often 1500 on Ethernet).\n• Oversized packets fragment (IPv4) or are dropped (IPv6 / DF); PMTUD finds a fit.\n• App payloads ride inside after all headers.",
    },
    {
      bullet: "Control plane vs data plane.",
      notes:
        "• Control plane: build/update tables (routing protocols, ARP learning).\n• Data plane: forward each packet from the table, fast path.\n• Failure modes differ: bad routes vs saturated forwarding.",
    },
    {
      bullet: "Connection-oriented vs connectionless communication.",
      notes:
        "• Connection-oriented (TCP): shared state before data; reliability and order.\n• Connectionless (UDP/IP): each datagram standalone; app handles loss.\n• Choice is a service contract, not a moral ranking.",
    },
  ],
  mentalModel:
    "• Apps speak messages; transports speak ports and delivery rules; IP speaks host-to-host; links speak neighbors.\n• Each hop rewrites only what it owns (frame) and forwards what it must (packet).\n• Complexity stays at the edges; the middle mostly forwards.",
  failuresAndSignals: [
    "Wrong layer blame: app timeout that is really DNS or MTU — start at the lowest layer that can explain the symptom.",
    "MTU black hole: small probes work, large transfers hang — path drops DF packets and ICMP too-big never returns.",
    "Control/data split: routing daemon healthy while forwarding table is stale — packets blackhole until reconvergence.",
  ],
  whyAnchors: ["why.02", "why.03", "why.04", "why.07"],
  connections: {
    before: "• Entry point for the subject: no prior CN module required.",
    after: "• Module 2: how a frame crosses one Ethernet and how IP finds a MAC.",
  },
  selfCheck: [
    {
      id: "layers",
      prompt: "Why not one protocol for everything?",
      answer:
        "Jobs change at different rates and need different knowledge; layering keeps routers simple and apps replaceable (why.03).",
    },
    {
      id: "mtu",
      prompt: "What does the MTU limit?",
      answer:
        "The largest IP packet that link will carry. Larger needs fragmentation or a smaller send size via PMTUD.",
    },
    {
      id: "planes",
      prompt: "Give one control-plane task and one data-plane task.",
      answer:
        "Control: OSPF updating routes. Data: looking up the destination and sending the frame out an interface.",
    },
  ],
} satisfies ChapterSummary;
