import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.service-to-service",
  status: "complete",
  objectives: [
    "Explain how two locally healthy services still fail to talk across a boundary.",
    "Check security groups, NetworkPolicies, DNS, and mesh sidecars in order.",
    "Test from the caller's network namespace, not from a laptop.",
  ],
  prerequisites: ["m13.periodic-spikes"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Service A and service B each pass local health checks.\n• A cannot call B (timeout, refuse, or policy error).",
      whyItMatters:
        "• Microservice incidents cluster on the path between namespaces, not on process crash loops.\n• Local health never exercises that path (why.45, why.55).",
      idea: "• Cross-boundary calls need DNS that returns a reachable address, allow rules both ways, and working NAT/mesh.\n• Debug from A's pod/VM toward B's ClusterIP/pod IP.",
      howItWorks:
        "• Resolve B as A does (in-cluster DNS).\n• Connect to that IP:port from A's network namespace.\n• Check SG/NACL/NetworkPolicy/mesh AuthorizationPolicy.\n• Only then inspect B's app logs.",
      whyItWorks:
        "• Each hop can allow local probes while denying east-west traffic from A's CIDR.",
      limits:
        "• Service meshes add mTLS identity failures that look like connection errors.\n• Headless vs ClusterIP changes which IPs clients dial.",
      connections:
        "• localhost-only and Module 10 NAT/SG lessons.\n• Method lesson order applied inside the cluster.",
    },
    {
      type: "why",
      id: "why-boundary",
      questionId: "why.55",
    },
    {
      type: "why",
      id: "why-local-again",
      questionId: "why.45",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "Security group / NACL / NetworkPolicy denies A's CIDR to B's port.",
        "DNS returns an address A cannot route to (wrong cluster view, stale Endpoint).",
        "B listens only on localhost inside its container/VM.",
        "Mesh mTLS or authorization denies A's identity.",
        "NAT or egress gateway missing for cross-network calls.",
        "A calls the public name and hairpin/NAT fails; should use the internal name.",
      ],
      order: [
        "From A's shell: resolve B and curl/wget the URL A uses.",
        "Compare success calling B's pod IP vs Service IP vs public name.",
        "Inspect network policies and SG rules for A→B on that port.",
        "Check Endpoints/EndpointSlices ready; mesh proxy logs if present.",
        "tcpdump on both pods if policy counters are unavailable.",
      ],
      commands: [
        {
          command:
            "dig +short b.svc.cluster.local; curl -v --connect-timeout 3 http://b.svc.cluster.local:8080/healthz",
          shows: "What A’s DNS and data path see (run inside A’s namespace).",
        },
        {
          command: "ss -ltnp  # on B",
          shows: "Whether B listens on 0.0.0.0/:: or only loopback.",
        },
        {
          command: "tcpdump -ni eth0 'tcp port 8080'  # on A and B",
          shows: "Whether SYNs leave A and arrive on B.",
        },
        {
          command: "curl -v http://10.0.2.15:8080/healthz  # B pod IP from A",
          shows: "Policy/Service abstraction vs direct pod reachability.",
        },
      ],
      implications: [
        "Pod IP works, Service fails: kube-proxy/Endpoints/mesh issue.",
        "Nothing arrives on B: policy or routing between namespaces.",
        "SYN arrives, RST: B not listening on that address/port.",
        "TLS alert with mesh: identity/trust, not SG.",
      ],
      distinguishing: [
        "Local health vs east-west: probe from A, not from B.",
        "DNS vs policy: dig fails vs dig OK and connect times out.",
        "SG vs NetworkPolicy: cloud vs Kubernetes control plane—change one at a time.",
      ],
      mistakes: [
        "Testing B from a bastion in a different SG than A.",
        "Opening B to the world instead of allowing A's CIDR.",
        "Trusting both 'Ready' probes while NetworkPolicy still drops A.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "from-a",
          prompt:
            "Both services are healthy on their hosts; A→B times out. Where must you run curl?",
          answer:
            "From A's network namespace (pod/VM), toward the same name/IP A uses—not only on B's localhost.",
        },
        {
          id: "why-boundary",
          prompt: "Why can two healthy processes fail across a security-group boundary?",
          answer:
            "Local health never needs that ingress/egress rule or NAT mapping; the cross-boundary path can still be denied (why.55).",
        },
      ],
    },
  ],
  claims: [
    {
      id: "nat-not-security",
      statement:
        "NAT is not a security boundary by itself; filtering policy determines what is allowed.",
      sourceId: "rfc4864",
      locator: "Section 2; Section 4",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ss-listen-svc",
      statement: "ss shows which address a service process is listening on.",
      sourceId: "man-ss",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "rfc4864", note: "NAT versus firewalling." },
    { sourceId: "man-ss", note: "Listener bind on B." },
    { sourceId: "man-tcpdump", note: "SYN across the boundary." },
    { sourceId: "bind-dig", note: "In-cluster name resolution." },
    { sourceId: "curl-manpage", note: "Calls from A's namespace." },
  ],
} satisfies LessonBody;
