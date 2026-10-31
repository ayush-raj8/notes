import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.tcp-ok-tls-fails",
  status: "complete",
  objectives: [
    "Prove TCP succeeded before debugging TLS.",
    "Map common TLS failures to certificate, name, protocol, or middlebox causes.",
    "Read curl verbose output for the failing handshake step.",
  ],
  prerequisites: ["m13.dns-ok-tcp-fails"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• TCP connect works; the TLS handshake fails (alert, verify error, or hang).\n• Packets reach a listener, but the secure channel never opens.",
      whyItMatters:
        "• Certificate and protocol mismatches look like 'the site is down' to users.\n• Fixing routing will not help once the socket is established.",
      idea: "• TLS needs a matching name, a trusted chain, a shared protocol/cipher, and correct time.\n• Inspect the alert and the peer certificate before touching firewalls.",
      howItWorks:
        "• `curl -v` shows TCP connect, then TLS errors (verify result, alert).\n• Check SNI/hostname vs certificate SAN; chain to a trusted root; notBefore/notAfter.\n• Protocol: client-only TLS 1.3 vs server max 1.2; cipher mismatch.\n• Interception proxies present their own cert—trust store must include that CA.",
      whyItWorks:
        "• Authentication fails closed: a wrong name or untrusted chain must abort before application data.",
      limits:
        "• Some failures hang (middlebox eating ClientHello) and look like timeouts after connect.\n• Mutual TLS adds client-cert failures on the server side.",
      connections:
        "• Module 9: certificates and handshake.\n• Next: TLS works but HTTP returns 502/503.",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "Certificate hostname mismatch (wrong SAN / CN for the name the client used).",
        "Untrusted or incomplete chain; missing intermediate; unknown private CA.",
        "Expired or not-yet-valid certificate (clock skew on client or server).",
        "No shared TLS version or cipher suite.",
        "SNI missing or wrong, so the server presents the default (wrong) certificate.",
        "TLS-inspecting proxy with a cert the client does not trust.",
        "Client requires mTLS and offers no acceptable certificate.",
      ],
      order: [
        "Confirm TCP: `curl -v` shows Connected before the SSL error.",
        "Read the verify error / alert string from curl or the library.",
        "Inspect the presented chain: names, issuer, dates.",
        "Compare client TLS version/cipher policy with the server's.",
        "Retry with `--resolve` to the same IP using the expected name for SNI.",
      ],
      commands: [
        {
          command: "curl -vI https://api.example.com/",
          shows: "Connect vs TLS phase; verify error text; HTTP only if handshake works.",
        },
        {
          command: "curl -v --resolve api.example.com:443:198.51.100.10 https://api.example.com/",
          shows: "Forces that IP while keeping the name for SNI and verify.",
        },
        {
          command: "curl -v --cacert /path/to/corp-root.pem https://api.example.com/",
          shows: "Whether trusting an extra root fixes an intercepting proxy or private CA.",
        },
        {
          command: "curl -v --tlsv1.2 --tls-max 1.2 https://api.example.com/",
          shows: "Whether restricting versions changes success (protocol mismatch hint).",
        },
      ],
      implications: [
        "verify failed: hostname or trust store—not packet loss.",
        "alert handshake_failure: often cipher/version/policy.",
        "Works with --insecure but not without: trust/name problem, not connectivity.",
        "Connect hangs after TCP with no TLS alert: middlebox or server not speaking TLS on that port.",
      ],
      distinguishing: [
        "Wrong cert vs untrusted: hostname error vs unknown CA / unable to get local issuer.",
        "Client policy vs server cert: other clients succeed with different TLS stacks.",
        "SNI vs IP: connecting by IP without a name often shows the default vhost cert.",
      ],
      mistakes: [
        "Disabling verification permanently instead of fixing the chain or name.",
        "Debugging DNS when curl already printed Connected to the right IP.",
        "Assuming the load balancer cert matches every backend name clients use.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "name-mismatch",
          prompt: "curl: SSL certificate problem: certificate subject name does not match. Fix?",
          answer:
            "Serve a certificate whose SAN includes the hostname the client uses, or have the client use a name that is on the cert—not open the firewall.",
        },
        {
          id: "tcp-done",
          prompt: "How do you know to stop looking at SYN drops?",
          answer: "curl or the client already completed TCP connect before the TLS error.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "hostname-check",
      statement:
        "Clients must check that the certificate's DNS names match the reference identity (hostname) they intended to contact.",
      sourceId: "rfc6125",
      locator: "Section 6",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tls13-auth",
      statement:
        "TLS 1.3 provides authentication and encrypted handshake; certificate verification failure aborts before application data.",
      sourceId: "rfc8446",
      locator: "Abstract; Section 4.4.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc6125", note: "Hostname verification." },
    { sourceId: "rfc8446", note: "TLS 1.3 handshake and certificates." },
    { sourceId: "curl-manpage", note: "Verbose TLS errors; --resolve; --cacert." },
    { sourceId: "rfc5280", note: "Certificate path validation." },
  ],
} satisfies LessonBody;
