import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m12.tools",
  status: "complete",
  objectives: [
    "Pick ss, ip, ping, traceroute, dig, curl, or tcpdump for a specific question.",
    "Read each tool as evidence for or against a hypothesis—not as a ritual.",
  ],
  prerequisites: ["m12.kernel-buffers"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Failures look similar: timeout, refuse, empty reply.\n• Guessing at random layers wastes time.\n• Each tool answers a narrow question about state or the path.",
      whyItMatters:
        "• Module 13 scenarios assume you can gather evidence fast.\n• Wrong tool (traceroute when DNS is broken) creates false stories.\n• Captures without a filter drown you.",
      idea: "• Name → dig. Route/address → ip. Socket state → ss. Reachability → ping. Path hints → traceroute. App request → curl. Bytes on the wire → tcpdump.",
      howItWorks:
        "• Start from the symptom: did the name resolve? Is there a route? Is someone listening? Did TCP complete? Did TLS/HTTP complete?\n• Match the tool to that layer.\n• Prefer one change at a time; save command output.",
      whyItWorks:
        "• Layers fail independently. Isolating the first broken hop collapses the search space.\n• Kernel state (`ss`, `ip`) is ground truth for local config; captures confirm what left the NIC.",
      limits:
        "• ping/traceroute use ICMP/UDP differently than your TCP/TLS traffic (why.52).\n• dig @resolver may not match the stub path your process uses.\n• tcpdump needs correct interface/filter; encrypted payloads stay opaque.",
      connections:
        "• Lifecycle/buffers: interpret Recv-Q/Send-Q and listen queues.\n• Module 13: same tools inside investigation sections.\n• why.50: eliminate hypotheses in order.",
    },
    {
      type: "comparison",
      id: "tool-map",
      title: "Which tool for which question",
      columns: ["Question", "Starter command"],
      rows: [
        {
          label: "dig",
          cells: ["What addresses/records does this name have?", "`dig +short example.com A`"],
        },
        {
          label: "ip",
          cells: ["Addresses, routes, links on this host?", "`ip addr`; `ip route get 1.1.1.1`"],
        },
        {
          label: "ss",
          cells: ["Who is listening / connected?", "`ss -ltnp`; `ss -tan`"],
        },
        {
          label: "ping",
          cells: ["Does ICMP echo reach the peer?", "`ping -c 3 HOST`"],
        },
        {
          label: "traceroute",
          cells: ["Which hops answer toward the peer?", "`traceroute -n HOST`"],
        },
        {
          label: "curl",
          cells: ["What does an HTTP(S) request return?", "`curl -vI https://HOST/`"],
        },
        {
          label: "tcpdump",
          cells: ["What packets hit this interface?", "`tcpdump -ni eth0 port 443`"],
        },
      ],
    },
    {
      type: "steps",
      id: "quick-path",
      title: "Fast path when connect fails",
      steps: [
        {
          title: "Name",
          body: "• `dig` / `getent hosts` — wrong A/AAAA stops here.",
        },
        {
          title: "Listen / route",
          body: "• `ss -ltn` on server; `ip route get` on client.",
        },
        {
          title: "Handshake",
          body: "• `curl -v` or `tcpdump` SYN/SYN-ACK; firewall vs nothing listening.",
        },
        {
          title: "Above TCP",
          body: "• TLS errors in curl; HTTP status if TLS works.",
        },
      ],
    },
    {
      type: "callout",
      id: "ss-queues",
      tone: "note",
      title: "ss queues",
      body: "• Recv-Q on TCP listen: connections waiting for accept (roughly).\n• Recv-Q/Send-Q on established: unread data / unsent or unacked data in kernel buffers.",
    },
    {
      type: "example",
      id: "choose-tool",
      title: "Pick one",
      setup: "Users say the API is down. Metrics show no CPU issue.",
      walkthrough: [
        "• dig the API name — resolves.",
        "• ss -ltn on the host — nothing on :443.",
        "• Process crashed; restart restores listen.",
      ],
      result: "• tcpdump was unnecessary once listen state was checked.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "name-vs-ss",
          prompt:
            "DNS works, connect times out. Which local command checks if anything listens on the port?",
          answer: "`ss -ltn` (or `-ltnp`) on the server for that port.",
        },
        {
          id: "curl-role",
          prompt: "When is curl the right next tool after TCP works?",
          answer:
            "When you need application-level evidence: TLS handshake result, HTTP status, headers—not when you still lack a SYN-ACK.",
        },
        {
          id: "tcpdump-when",
          prompt: "When do you reach for tcpdump early?",
          answer:
            "When kernel state looks fine but you need proof of what left or hit the NIC (RSTs, retransmits, wrong peer, ICMP).",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ss-purpose",
      statement:
        "ss dumps socket statistics and can show listening and established sockets with process info.",
      sourceId: "man-ss",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "tcpdump-purpose",
      statement: "tcpdump prints packet contents on a network interface matching an expression.",
      sourceId: "man-tcpdump",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "ip-route-get",
      statement: "ip route get returns the route the kernel would use to a destination.",
      sourceId: "man-ip-route",
      locator: "IP ROUTE GET",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "man-ss", note: "ss(8): socket state." },
    { sourceId: "man-ip-route", note: "ip-route(8): route get." },
    { sourceId: "man-tcpdump", note: "tcpdump(1): captures." },
    { sourceId: "man-ping", note: "ping(8): ICMP reachability." },
    { sourceId: "man-traceroute", note: "traceroute(8): path probes." },
  ],
} satisfies LessonBody;
