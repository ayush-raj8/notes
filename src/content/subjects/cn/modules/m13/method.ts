import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.method",
  status: "complete",
  objectives: [
    "State a fixed order for eliminating causes of a network failure.",
    "Treat each command output as evidence for or against a hypothesis.",
    "Name mistakes that send the investigation to the wrong layer.",
  ],
  prerequisites: ["m12.tools"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• A client only sees a few symptoms: timeout, refused, reset, TLS error, or HTTP status.\n• Each symptom has many causes across DNS, path, TCP, TLS, proxies, and the app.\n• Guessing one component first often wastes the capture window.",
      whyItMatters:
        "• Production failures are intermittent and state-dependent; a wrong first guess loses evidence.\n• Interview and on-call work reward a repeatable elimination order, not folklore.",
      idea: "• Reproduce once, then eliminate layers in order: name → reachability → TCP → TLS → HTTP → app.\n• At each layer, ask what observation would kill the hypothesis.\n• Capture at both ends when the path is in doubt.",
      howItWorks:
        "• Confirm the exact name and address the client used (same resolver path as the app).\n• Prove whether SYN arrives and SYN-ACK returns (`ss`, `tcpdump`).\n• If TCP works, test TLS (`curl -v` / openssl), then HTTP status and upstream.\n• Only then dig into application logs and dependencies.",
      whyItWorks:
        "• Each layer depends on the one below; a broken lower layer masquerades as every upper failure.\n• Binary evidence (packet seen / not seen) beats opinions about 'the network'.",
      limits:
        "• Intermittent failures need a dimension held fixed (backend, size, idle time) before the order helps.\n• Managed clouds hide middlebox counters; you infer from endpoints.\n• Scenario lessons below specialise this frame; they do not replace it.",
      connections:
        "• Module 12 tools: which command for which layer.\n• Module 10 connectivity failures: edge symptoms.\n• Every scenario in this module uses this order.",
    },
    {
      type: "why",
      id: "why-hypothesis",
      questionId: "why.50",
    },
    {
      type: "steps",
      id: "order",
      title: "Elimination order",
      steps: [
        {
          title: "Reproduce and freeze the path",
          body: "• Same client, name, and time window. Note errno / HTTP status / TLS alert.",
        },
        {
          title: "Name",
          body: "• `getent` / `dig`: does the name resolve to the address you expect?",
        },
        {
          title: "TCP",
          body: "• Connect to that IP:port. Timeout vs refused vs reset. `ss -ltnp` on the server.",
        },
        {
          title: "TLS",
          body: "• Only if TCP works. Certificate name, protocol, alert from `curl -v`.",
        },
        {
          title: "HTTP / proxy",
          body: "• Status and `Server` / Via headers. Hit a backend directly if a balancer sits in front.",
        },
        {
          title: "App",
          body: "• Logs and dependency health after the channel is proven.",
        },
      ],
    },
    {
      type: "comparison",
      id: "symptom-layer",
      title: "Symptom → first layer to test",
      columns: ["First test", "Do not start with"],
      rows: [
        {
          label: "Name resolution error",
          cells: ["Resolver / `dig` / search list", "tcpdump on the app port"],
        },
        {
          label: "connect() timeout",
          cells: ["SYN path both ways", "Application logs"],
        },
        {
          label: "Connection refused",
          cells: ["`ss -ltnp` bind address", "TLS certificate"],
        },
        {
          label: "TLS alert / verify fail",
          cells: ["Certificate and SNI", "Load-balancer upstream pool"],
        },
        {
          label: "HTTP 502 / 503 / 504",
          cells: ["Balancer upstream health", "Client DNS"],
        },
      ],
    },
    {
      type: "callout",
      id: "one-change",
      tone: "note",
      title: "One change at a time",
      body: "• Change one variable (backend, packet size, idle time) per attempt.\n• Write down what you proved, not what you believe.\n• Prefer evidence that falsifies a layer over evidence that 'looks related'.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "order-tcp",
          prompt:
            "TLS fails with a verify error. What must already be true, and what do you check next?",
          answer:
            "TCP connected (handshake finished). Next: certificate chain, hostname/SNI match, and clock—not routing or DNS.",
        },
        {
          id: "timeout-first",
          prompt:
            "Users report timeouts. Someone opens application error logs first. What did they skip?",
          answer:
            "Whether the name resolves and whether SYN/SYN-ACK complete. App logs cannot explain packets that never arrived.",
        },
        {
          id: "hypothesis",
          prompt: "Why eliminate layers instead of checking the component you distrust most?",
          answer:
            "One symptom maps to many layers; a favourite component may be healthy while a lower layer fails. Ordered evidence removes whole classes of causes.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ss-listen",
      statement: "ss can show listening TCP sockets and the processes that own them.",
      sourceId: "man-ss",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "curl-timing",
      statement:
        "curl's -w/--write-out can report time_namelookup, time_connect, time_appconnect, and time_total for one transfer.",
      sourceId: "curl-manpage",
      locator: "-w, --write-out",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    { sourceId: "man-ss", note: "ss(8): listen and established state." },
    { sourceId: "curl-manpage", note: "Timing phases for layering evidence." },
    { sourceId: "man-tcpdump", note: "Packet proof on the path." },
    { sourceId: "bind-dig", note: "dig for the name layer." },
  ],
} satisfies LessonBody;
