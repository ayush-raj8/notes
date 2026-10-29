import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m09.why-tls",
  status: "complete",
  objectives: [
    "Name the three threats TCP does not stop: eavesdropping, tampering, impersonation.",
    "Separate confidentiality, integrity, and authentication.",
    "Place symmetric keys vs asymmetric keys in the TLS design.",
  ],
  prerequisites: ["m08.intermediaries"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• TCP delivers bytes in order. It does not hide them, detect a silent change, or prove who is at the other end.\n• Anyone on the path can read, alter, or inject traffic.\n• A successful connect() only proves reachability to some TCP stack.",
      whyItMatters:
        "• APIs, browsers, and microservices send secrets over the network.\n• Without TLS, a Wi-Fi observer, a compromised router, or a DNS-poisoned host sees and can rewrite the stream.\n• HTTPS is TLS under HTTP; the same threats apply to any cleartext TCP protocol.",
      idea: "• Confidentiality: outsiders cannot read content.\n• Integrity: outsiders cannot change content without detection.\n• Authentication: the client knows it reached the intended server (client auth optional).\n• Symmetric keys for bulk data. Asymmetric keys (or a PSK) only to authenticate and agree on those session keys.",
      howItWorks:
        "• Handshake: authenticate the peer, negotiate algorithms, derive shared session keys.\n• Record layer: encrypt and MAC (AEAD) application bytes with those keys.\n• Symmetric crypto is fast for every record. Asymmetric crypto is used sparingly in the handshake.\n• TLS needs a reliable ordered byte stream from TCP (or QUIC's own reliability).",
      whyItWorks:
        "• Encryption alone without authentication still leaves you talking to an attacker who terminated TLS.\n• Authentication alone without encryption leaves content open.\n• Binding both to ephemeral session keys limits what a later key leak can decrypt (forward secrecy when DH is used).",
      limits:
        "• TLS does not hide that you talked, how long, or roughly how much data (lengths can be padded).\n• A valid TLS session to the wrong name is still a failure if hostname checks are skipped.\n• Application bugs, phishing, and compromised endpoints are outside TLS.\n• TCP itself stays clear: middleboxes still see IPs, ports, and timing.",
      connections:
        "• why.19: TCP stays a transport. Encryption lives above it so stacks and middleboxes stay simple.\n• why.25: reliable delivery ≠ private or authenticated delivery.\n• Next: certificates prove the server's key. Then the handshake. Module 6 already placed QUIC's integrated TLS.",
    },
    {
      type: "comparison",
      id: "threats",
      title: "What each property stops",
      columns: ["Without TLS", "TLS answer"],
      rows: [
        {
          label: "Eavesdropping",
          cells: ["Read request and response bodies.", "Confidentiality after the handshake."],
        },
        {
          label: "Tampering",
          cells: ["Flip bits; victim may accept them.", "Integrity via AEAD; bad records fail."],
        },
        {
          label: "Impersonation",
          cells: [
            "Attacker answers on the IP you connected to.",
            "Certificate + hostname check (next lesson).",
          ],
        },
      ],
    },
    {
      type: "comparison",
      id: "key-types",
      title: "Symmetric vs asymmetric (conceptual)",
      caption: "TLS uses both. Bulk data is always symmetric after keys are set.",
      columns: ["Symmetric", "Asymmetric"],
      rows: [
        {
          label: "Speed",
          cells: ["Fast per byte.", "Slow; used in handshake / signatures."],
        },
        {
          label: "Key sharing",
          cells: [
            "Same secret both sides.",
            "Public encrypts / verifies; private decrypts / signs.",
          ],
        },
        {
          label: "In TLS",
          cells: ["Record protection keys.", "Certificates, signatures, Diffie–Hellman shares."],
        },
      ],
    },
    {
      type: "why",
      id: "why-tcp-no-encrypt",
      questionId: "why.19",
    },
    {
      type: "why",
      id: "why-https-needs-tls",
      questionId: "why.25",
    },
    {
      type: "callout",
      id: "tcp-not-private",
      tone: "caution",
      title: "Reliable is not private",
      body: "A TCP ACK means the peer's kernel has the bytes. It does not mean the peer is who you named, or that a hop in the middle did not copy the stream.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "three-properties",
          prompt: "Name the three channel properties TLS aims for after the handshake.",
          answer:
            "Confidentiality (outsiders cannot read content), integrity (tampering is detected), and authentication (at least the server is the intended peer).",
        },
        {
          id: "why-two-crypto",
          prompt:
            "Why does TLS use asymmetric crypto in the handshake and symmetric crypto for records?",
          answer:
            "Asymmetric operations authenticate and establish keys but are expensive. Once both sides share session keys, symmetric AEAD protects every record cheaply.",
        },
        {
          id: "tcp-enough",
          prompt:
            "A client connect()s successfully to port 443 without TLS. What is still unknown?",
          answer:
            "Whether anyone on the path can read or change the bytes, and whether the answering host is the intended server rather than an impostor.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "tls-goals",
      statement:
        "TLS provides a secure channel designed to prevent eavesdropping, tampering, and message forgery, with authentication of the server (client optional), confidentiality of data after establishment, and integrity of that data.",
      sourceId: "rfc8446",
      locator: "Section 1, Introduction (Abstract goals; properties list)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tls-needs-reliable-stream",
      statement:
        "The only requirement TLS places on the underlying transport is a reliable, in-order data stream.",
      sourceId: "rfc8446",
      locator: "Section 1, Introduction",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tls13-forward-secrecy-kx",
      statement:
        "TLS 1.3 removed static RSA and static Diffie-Hellman cipher suites; public-key based key exchange mechanisms provide forward secrecy.",
      sourceId: "rfc8446",
      locator: "Section 1.2, Major Differences from TLS 1.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc8446", note: "Section 1 and 1.2: goals and TLS 1.3 design changes." },
  ],
} satisfies LessonBody;
