import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m09.handshake",
  status: "complete",
  objectives: [
    "Outline what the TLS handshake negotiates and derives.",
    "Contrast TLS 1.2 vs 1.3 at a conceptual level (round trips, encryption of handshake, forward secrecy).",
    "Place TLS on TCP versus QUIC's integrated TLS 1.3 CRYPTO frames.",
  ],
  prerequisites: ["m09.certificates"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Both sides need shared record keys before application data is safe.\n• They must agree on algorithms and authenticate at least the server.\n• Doing this in cleartext forever would leak identities and parameters; doing it slowly costs latency.",
      whyItMatters:
        "• Every HTTPS request pays handshake cost unless resumed.\n• TLS 1.3 cut a round trip and encrypts more of the handshake.\n• QUIC folds TLS into the transport handshake so 1-RTT (or 0-RTT) covers both.",
      idea: "• Handshake protocol: authenticate, negotiate, derive keying material.\n• Record protocol: protect application data with those keys.\n• TLS 1.3: (EC)DHE for forward secrecy; certificates + CertificateVerify; Finished MACs the transcript.",
      howItWorks:
        "• ClientHello → ServerHello (+ encrypted extensions, cert, CertificateVerify, Finished).\n• Client Finished (and optional client cert).\n• Both derive traffic keys; then Application Data.\n• On TCP: TLS records ride the TCP byte stream after TCP is already up.\n• On QUIC: TLS messages ride CRYPTO frames; packet protection uses TLS keys (RFC 9001).",
      whyItWorks:
        "• Transcript binding stops a MITM from splicing two handshakes.\n• Ephemeral DH means past session keys are not recoverable from a long-term private key alone.\n• Encrypting post-ServerHello handshake messages hides certificates from passive observers (TLS 1.3).",
      limits:
        "• 0-RTT data has weaker anti-replay properties (next lesson).\n• Middleboxes that expected cleartext handshake fields broke on TLS 1.3; compatibility modes exist.\n• TCP still completes before TLS on HTTPS-over-TCP (extra RTT vs QUIC).\n• No viz here; tls-stages remains unimplemented.",
      connections:
        "• Certificates lesson: what the Certificate message carries.\n• Module 5: TCP handshake is independent—connect can succeed and TLS still fail.\n• Module 6 / 8: HTTP/3 uses this QUIC+TLS path.",
    },
    {
      type: "comparison",
      id: "tls12-vs-13",
      title: "TLS 1.2 vs TLS 1.3 (conceptual)",
      columns: ["TLS 1.2", "TLS 1.3"],
      rows: [
        {
          label: "App data ready",
          cells: ["Often 2-RTT full handshake.", "1-RTT full handshake; optional 0-RTT."],
        },
        {
          label: "Key exchange",
          cells: ["Could use static RSA (no FS).", "Public-key exchange provides forward secrecy."],
        },
        {
          label: "Handshake privacy",
          cells: ["Certificates often clear.", "Handshake after ServerHello encrypted."],
        },
        {
          label: "Resumption",
          cells: ["Session IDs / tickets.", "Unified PSK (+ optional key_share for FS)."],
        },
      ],
    },
    {
      type: "steps",
      id: "tls13-flight",
      title: "TLS 1.3 full handshake (notes)",
      steps: [
        {
          title: "ClientHello",
          body: "• Versions, cipher suites, key_share, signature algorithms, SNI (server name).",
        },
        {
          title: "Server flight",
          body: "• ServerHello picks parameters.\n• EncryptedExtensions, Certificate, CertificateVerify, Finished (encrypted).",
        },
        {
          title: "Client finish",
          body: "• Finished (and Certificate* if requested).\n• Application Data may follow.",
        },
        {
          title: "Keys",
          body: "• Handshake and application traffic secrets derived via HKDF from the shared secret + transcript.",
        },
      ],
    },
    {
      type: "comparison",
      id: "tcp-vs-quic",
      title: "TLS on TCP vs QUIC",
      columns: ["HTTPS on TCP", "QUIC (HTTP/3)"],
      rows: [
        {
          label: "Transport first",
          cells: ["TCP handshake, then TLS records.", "Combined; TLS in CRYPTO frames."],
        },
        {
          label: "TLS version",
          cells: ["TLS 1.2 or 1.3 common.", "TLS 1.3 only (RFC 9001)."],
        },
        {
          label: "Record protection",
          cells: ["TLS record layer on TCP.", "QUIC packet protection; TLS record layer unused."],
        },
      ],
    },
    {
      type: "callout",
      id: "sni",
      tone: "note",
      title: "SNI",
      body: "• Server Name Indication carries the hostname in ClientHello so one IP can present the right certificate.\n• SNI is visible on the wire in typical deployments (ESNI/ECH is a separate mitigation).",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "fs-tls13",
          prompt: "What TLS 1.3 change forces forward secrecy for public-key handshakes?",
          answer:
            "Removal of static RSA / static DH cipher suites; key exchange uses ephemeral DH shares so session keys are not recoverable from the long-term private key alone.",
        },
        {
          id: "tcp-then-tls",
          prompt: "On classic HTTPS, what must succeed before TLS Application Data can flow?",
          answer:
            "TCP connection establishment, then a completed TLS handshake (Finished), except for the special 0-RTT case on a resumed/PSK path.",
        },
        {
          id: "quic-crypto",
          prompt: "Where do TLS handshake bytes travel in QUIC?",
          answer: "In CRYPTO frames. QUIC does not use the TLS record layer for packet protection.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "handshake-purpose",
      statement:
        "The TLS handshake authenticates the parties, negotiates cryptographic parameters, and establishes shared keying material; it is designed to resist tampering.",
      sourceId: "rfc8446",
      locator: "Section 1, Introduction (handshake protocol bullet)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tls13-1rtt",
      statement:
        "TLS 1.3's full handshake puts the client and server in a position to send Application Data after one round trip; 0-RTT mode can save an additional round trip at a security cost.",
      sourceId: "rfc8446",
      locator: "Section 1.2, Major Differences from TLS 1.2 (0-RTT); Section 2 overview",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tls13-encrypted-handshake",
      statement: "In TLS 1.3, all handshake messages after the ServerHello are encrypted.",
      sourceId: "rfc8446",
      locator: "Section 1.2, Major Differences from TLS 1.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "no-appdata-before-finished",
      statement:
        "Application Data MUST NOT be sent prior to sending the Finished message, except as specified for 0-RTT.",
      sourceId: "rfc8446",
      locator: "Section 2, Protocol Overview",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "quic-crypto-frames",
      statement:
        "QUIC carries TLS handshake data in CRYPTO frames. TLS record protection is not used by QUIC; QUIC packet protection applies instead.",
      sourceId: "rfc9001",
      locator: "Section 4, Carrying TLS Messages",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    {
      sourceId: "rfc8446",
      note: "Sections 1, 1.2, and 2: handshake overview and TLS 1.3 changes.",
    },
    { sourceId: "rfc9001", note: "Section 4: CRYPTO frames and no TLS record layer." },
  ],
} satisfies LessonBody;
