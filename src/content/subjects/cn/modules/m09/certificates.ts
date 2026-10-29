import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m09.certificates",
  status: "complete",
  objectives: [
    "Say what a server certificate binds: a public key to a name, under a CA's signature.",
    "Walk chain validation to a trust anchor at a high level.",
    "Explain why hostname verification is required after the cryptographic checks succeed.",
  ],
  prerequisites: ["m09.why-tls"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Encryption to the wrong peer still leaks everything to that peer.\n• Anyone can generate a key pair. Alone it does not prove `api.example.com`.\n• Clients need a way to decide which public keys to trust for which names.",
      whyItMatters:
        "• Browsers and SDKs ship a set of trust anchors (root CAs).\n• Skipping hostname checks is a common production bug: TLS is green, traffic goes to an attacker.\n• why.26 and why.31 live here.",
      idea: "• A certificate: public key + subject names + a CA signature.\n• Chain: leaf → intermediates → a root the client already trusts.\n• After the chain is valid, match the certificate names to the name you intended to reach.",
      howItWorks:
        "• Server sends its certificate (and chain) in the handshake.\n• Client verifies signatures up to a configured trust anchor (RFC 5280 path validation).\n• Client checks validity period, key usage, and that the name matches (RFC 6125).\n• Prefer DNS names in subjectAltName (DNS-ID). CN in the subject is legacy.\n• Server proves possession by signing handshake transcript (CertificateVerify in TLS 1.3).",
      whyItWorks:
        "• Trust is concentrated in few roots the OS/browser updates.\n• Name binding stops a valid cert for `evil.test` from serving `bank.example`.\n• Signature over the handshake binds the authenticated identity to this connection's keys.",
      limits:
        "• A compromised or mis-issued CA can mint a trusted cert for your name (CT and pinning are mitigations; not covered in depth here).\n• Expired, wrong-name, or incomplete-chain certs fail loudly if clients check correctly.\n• IP SANs exist; most APIs use DNS names.\n• Client certificates are optional and rarer on the public web.",
      connections:
        "• Previous lesson: why encryption needs authentication.\n• Next: how the handshake carries certificates and derives keys.\n• Module 7: DNS told you the IP; the cert must still match the hostname you typed.",
    },
    {
      type: "steps",
      id: "client-checks",
      title: "What a client checks (notes)",
      steps: [
        {
          title: "Build a path",
          body: "• Start from the leaf. Follow issuer links to a trust anchor you already have.\n• Reject broken signatures, unknown critical extensions, or a path that never hits a trust anchor.",
        },
        {
          title: "Time and usage",
          body: "• notBefore / notAfter must cover now.\n• Key usage / extended key usage must allow server authentication.",
        },
        {
          title: "Name match",
          body: "• Reference identity = the hostname you meant (from the URL).\n• Presented identifiers = DNS-IDs (and legacy CN-ID if still used).\n• Match rules in RFC 6125. Wildcards are restricted.",
        },
        {
          title: "Prove the private key",
          body: "• TLS 1.3 CertificateVerify: signature with the leaf private key over the handshake transcript.\n• Without this, a stolen certificate file alone is not enough mid-handshake (the private key is).",
        },
      ],
    },
    {
      type: "why",
      id: "why-cert-needed",
      questionId: "why.26",
    },
    {
      type: "why",
      id: "why-hostname",
      questionId: "why.31",
    },
    {
      type: "callout",
      id: "ip-connect-wrong-name",
      tone: "caution",
      title: "Connecting by IP",
      body: '• If you open TLS to an IP but verify `api.example.com`, the cert must include that DNS name (or you must configure the right reference identity).\n• Libraries that disable verification for "just this IP" remove impersonation protection.',
    },
    {
      type: "example",
      id: "mitm-without-name-check",
      title: "Encryption without a name check",
      setup:
        "Client dials an attacker who presents a valid cert for `attacker.test` and proxies to the real site.",
      walkthrough: [
        "• Handshake completes. Keys are fresh. Wireshark shows ciphertext.",
        "• Client skipped hostname verification.",
        "• Attacker terminates TLS, reads HTTP, may re-encrypt to the origin.",
      ],
      result:
        "• Confidentiality holds against passive observers, not against the active MITM the client accepted.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "encryption-alone",
          prompt: "Encryption alone fails to solve which problem that certificates address?",
          answer:
            "Impersonation: without authenticating the peer's key for the intended name, you may run a perfect TLS session with an attacker.",
        },
        {
          id: "dns-id",
          prompt: "Where should a modern HTTPS certificate put the site's DNS name?",
          answer:
            "In a subjectAltName dNSName (DNS-ID). Relying only on CN in the subject is legacy; RFC 6125 prefers SAN DNS-IDs.",
        },
        {
          id: "skip-verify",
          prompt: "A library sets verify_mode=none for convenience. What did you lose?",
          answer:
            "Chain validation and/or hostname matching—so a MITM with any cert (or none) can win the handshake.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "path-validation",
      statement:
        "RFC 5280 defines certification path validation procedures used to validate a certification path from an end entity to a trust anchor.",
      sourceId: "rfc5280",
      locator: "Section 1, Introduction; Section 6, Certification Path Validation",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "san-dns",
      statement:
        "When the subjectAltName extension contains a domain name system name, it is encoded as an IA5String as a dNSName.",
      sourceId: "rfc5280",
      locator: "Section 4.2.1.6, Subject Alternative Name",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "reference-vs-presented",
      statement:
        "The client forms a reference identity for the service and checks that a presented identity in the certificate matches it, so it can authenticate the service.",
      sourceId: "rfc6125",
      locator: "Section 1.1, Motivation and Overview",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "dns-id-preferred",
      statement:
        "A DNS-ID is a subjectAltName entry of type dNSName. Certificates should include a DNS-ID where possible; CN-ID support is legacy.",
      sourceId: "rfc6125",
      locator: "Section 1.8, Terminology (DNS-ID); Section 3.1, certificate recommendations",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cert-verify-tls13",
      statement:
        "CertificateVerify is a signature over the entire handshake using the private key corresponding to the public key in the Certificate message.",
      sourceId: "rfc8446",
      locator: "Section 2, Protocol Overview (CertificateVerify bullet); Section 4.4.3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc5280", note: "Sections 1, 4.2.1.6, and 6: paths and SAN DNS names." },
    { sourceId: "rfc6125", note: "Sections 1.1, 1.8, and 3.1: reference identity and DNS-ID." },
    { sourceId: "rfc8446", note: "CertificateVerify binds the leaf key to the handshake." },
  ],
} satisfies LessonBody;
