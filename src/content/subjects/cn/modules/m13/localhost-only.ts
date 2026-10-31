import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.localhost-only",
  status: "complete",
  objectives: [
    "List why local success does not imply remote reachability.",
    "Check bind address, firewall, and NAT/port-forward separately.",
    "Test from outside the host, not only via loopback.",
  ],
  prerequisites: ["m13.latency-cpu-low"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• `curl localhost` works on the server; clients on other machines fail.\n• Local health checks stay green.",
      whyItMatters:
        "• Bind address and host firewalls are classic deploy bugs.\n• Remote paths exercise rules loopback never hits (why.45).",
      idea: "• Localhost uses loopback; remote clients need a non-loopback listen address, routes, and allow rules.\n• Prove each boundary from outside.",
      howItWorks:
        "• `ss -ltnp`: 127.0.0.1 vs 0.0.0.0 / ::.\n• From another host: timeout ⇒ DROP/route; refused ⇒ no listener on that address.\n• Cloud: security group / NACL; on-prem: iptables and NAT DNAT.",
      whyItWorks:
        "• Health on loopback never sends packets through the NIC or edge filters remote users use.",
      limits:
        "• Binding to 0.0.0.0 still fails if the security group blocks the port.\n• IPv6-only clients need an IPv6 listen and path.",
      connections:
        "• Module 10 connectivity failures.\n• service-to-service scenario for cross-SG calls.",
    },
    {
      type: "why",
      id: "why-local",
      questionId: "why.45",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "Process bound to 127.0.0.1 (or ::1) only.",
        "Host firewall allows local, blocks external interfaces.",
        "Cloud security group or ACL missing ingress for the client CIDR.",
        "No DNAT/port forward / load-balancer listener for the public name.",
        "Service listens on a private interface clients cannot route to.",
        "Wrong published port in compose/k8s Service vs container port.",
      ],
      order: [
        "Reproduce from a second machine (or cloud instance in another SG).",
        "`ss -ltnp` on the server for bind address.",
        "Compare localhost vs `curl http://<private-ip>:port` from the same host.",
        "Check security group / iptables counters while a remote attempt runs.",
        "If public name: verify LB/DNAT target matches the listening socket.",
      ],
      commands: [
        {
          command: "ss -ltnp 'sport = :8080'",
          shows: "Bound address: 127.0.0.1:8080 vs *:8080.",
        },
        {
          command:
            "curl -v --connect-timeout 3 http://127.0.0.1:8080/ && curl -v --connect-timeout 3 http://10.0.0.5:8080/",
          shows: "Loopback vs primary IP on the same host.",
        },
        {
          command: "iptables -L INPUT -n -v",
          shows: "Whether INPUT DROP counters rise on remote attempts.",
        },
      ],
      implications: [
        "Localhost OK, private IP refused on same host: bound to loopback only.",
        "Private IP OK, remote timeout: filter or missing route/SG.",
        "Private OK, public name fails: DNAT/LB/DNS pointing elsewhere.",
      ],
      distinguishing: [
        "Refused vs timeout from remote: nothing on address vs DROP/blackhole.",
        "SG vs host iptables: change one at a time; watch which counter moves.",
        "Publish typo vs bind: container listens 8080, Service targets 80.",
      ],
      mistakes: [
        "Only curling localhost in the health probe used for 'uptime'.",
        "Opening the host firewall while the cloud SG still blocks.",
        "Assuming 0.0.0.0 listen makes the service internet-reachable.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "bind",
          prompt: "Remote clients get connection refused; localhost works. Most likely?",
          answer:
            "Listening on 127.0.0.1 only. `ss -ltnp` shows it. Bind to 0.0.0.0 or the service interface.",
        },
        {
          id: "why-local-health",
          prompt: "Why can a service be healthy locally but unreachable remotely?",
          answer:
            "Local checks use loopback or a path that skips bind/firewall/NAT rules remote clients need (why.45).",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ss-bind",
      statement: "ss shows the local address a socket is bound to, including 127.0.0.1 versus *.",
      sourceId: "man-ss",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "reject-drop",
      statement:
        "iptables REJECT answers with an error or TCP RST; DROP is silent—clients typically time out.",
      sourceId: "man-iptables-extensions",
      locator: "TARGET EXTENSIONS: REJECT",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "man-ss", note: "Bind address." },
    { sourceId: "man-iptables-extensions", note: "REJECT vs DROP symptoms." },
    { sourceId: "curl-manpage", note: "Connect tests from remote." },
  ],
} satisfies LessonBody;
