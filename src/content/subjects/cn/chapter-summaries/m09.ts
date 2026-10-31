import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m09",
  status: "complete",
  readingMinutes: { min: 12, max: 18 },
  thesis:
    "TLS turns a reliable but exposed byte stream into a confidential, tamper-evident channel with an authenticated server, using certificates for identity and a handshake to agree on session keys.",
  outcomes: [
    "Explain why a reliable transport is not enough and what TLS adds: confidentiality, integrity, and authentication.",
    "Describe how asymmetric cryptography authenticates and establishes keys while symmetric cryptography protects the data.",
    "Walk through certificate validation: chain to a trusted CA, validity, and hostname match.",
    "Compare TLS 1.2 and 1.3, resumption, and forward secrecy at a conceptual level.",
    "State what HTTPS protects and does not protect, and recognize common TLS failures.",
    "Relate TLS to TCP, and TLS over TCP to QUIC's integrated TLS 1.3 handshake.",
  ],
  coverage: [
    {
      bullet: "Why encryption is necessary.",
      notes:
        "• Packets cross routers, Wi-Fi, and networks you do not control; anyone on the path can read or alter plain bytes.\n• TCP only moves bytes reliably; it does not hide them, detect silent modification, or prove who the peer is (why.25).\n• Goals: confidentiality (no reading), integrity (no undetected change), authentication (right peer).\n• Putting security in TLS (or QUIC) keeps TCP simple and lets it evolve separately (why.19).",
    },
    {
      bullet: "Symmetric vs asymmetric cryptography at a conceptual level.",
      notes:
        "• Symmetric: one shared secret key encrypts and decrypts; fast, used for bulk data (AES-GCM, ChaCha20-Poly1305).\n• Asymmetric: key pair, public and private; slower, used for signatures and key exchange.\n• Problem symmetric cannot solve alone: how two strangers get the same key safely.\n• TLS uses asymmetric methods to authenticate and agree on a key, then symmetric encryption with integrity for the data.",
    },
    {
      bullet: "Certificates and certificate authorities.",
      notes:
        "• A certificate binds a public key to a name (and lifetime), signed by a CA.\n• Clients carry a trust store of root CAs; trust flows from those roots.\n• Encryption alone only proves you share keys with whoever finished the handshake; the certificate lets the client reject an impostor (why.26).\n• Server also sends intermediate certificates; revocation is a separate, imperfect mechanism (CRL, OCSP).",
    },
    {
      bullet: "Public-key authentication.",
      notes:
        "• Server proves it holds the private key matching the certificate by signing handshake data.\n• Client checks the signature with the public key from the certificate, so the key and the name belong together.\n• Authentication ties the session keys to the verified server; without it, a man in the middle could negotiate keys with each side.\n• Mutual TLS adds a client certificate for the client's identity.",
    },
    {
      bullet: "TLS handshake.",
      notes:
        "• Client Hello: versions, cipher suites, key share, SNI, ALPN (such as h2).\n• Server Hello: choices and its key share, then certificate and proof of private-key possession.\n• Both sides finish with Finished messages that confirm the transcript was not altered.\n• After that, application data flows in encrypted records; handshake costs round trips and CPU.",
    },
    {
      bullet: "Key establishment and session keys.",
      notes:
        "• Ephemeral Diffie-Hellman (ECDHE): each side sends a fresh public value and both derive the same shared secret.\n• A key schedule (HKDF in TLS 1.3) derives separate keys for each direction and purpose, such as handshake traffic and application traffic.\n• The secret never crosses the wire; only the public values do.\n• Session keys last for one connection; keys can be updated during long connections.",
    },
    {
      bullet: "Certificate-chain validation.",
      notes:
        "• Client builds a chain from the leaf certificate through intermediates to a trusted root.\n• Checks each signature, validity dates, key usage, and constraints, and (where enabled) revocation.\n• Failure causes: expired certificate, missing intermediate, untrusted or unknown CA, wrong system clock.\n• A valid chain only shows some trusted CA vouched for that key (why.31).",
    },
    {
      bullet: "Hostname verification.",
      notes:
        "• Client compares the name it meant to reach with the certificate's Subject Alternative Names; wildcards match one label.\n• Without this check, a valid certificate for another name still gives a secure channel to the wrong server (why.31).\n• Check is against the intended name (from the URL), not the IP address, unless the certificate lists the IP.\n• SNI in the Client Hello lets one IP serve certificates for many names.",
    },
    {
      bullet: "TLS 1.2 vs TLS 1.3 at a conceptual level.",
      notes:
        "• TLS 1.3 completes a full handshake in one round trip; TLS 1.2 normally needs two.\n• 1.3 removes legacy options (RSA key transport, static DH, weak ciphers and compression) and always uses ephemeral key exchange.\n• 1.3 encrypts more of the handshake, including the certificate.\n• 1.3 has 0-RTT resumption data with replay risk; 1.2 negotiates more and has more downgrade and misconfiguration traps.",
    },
    {
      bullet: "Session resumption.",
      notes:
        "• Client and server reuse earlier session state to skip the full handshake and certificate work.\n• 1.2: session IDs or tickets; 1.3: pre-shared keys issued as tickets after the handshake.\n• Saves a round trip and CPU; ticket keys on the server must be protected and rotated.\n• 0-RTT sends data with the first flight, but it can be replayed, so only idempotent requests belong there.",
    },
    {
      bullet: "Forward secrecy.",
      notes:
        "• Session keys come from ephemeral key exchange, not from the server's long-term key.\n• If the server's private key leaks later, recorded old traffic still cannot be decrypted.\n• TLS 1.3 requires it; in 1.2 it depends on choosing ECDHE or DHE suites.\n• Session tickets and resumption can weaken it if ticket keys live too long.",
    },
    {
      bullet: "What HTTPS protects and what it does not protect.",
      notes:
        "• Protects: request and response contents, URL path, query, headers, and cookies from on-path readers and modification.\n• Does not hide: the destination IP, the timing and size of traffic, and usually the server name (SNI) unless Encrypted Client Hello is used.\n• Does not protect the endpoints: a compromised server, malware on the client, or a malicious but valid site.\n• The padlock means a properly authenticated encrypted connection to that name, not that the site is trustworthy.",
    },
    {
      bullet: "Common TLS failures.",
      notes:
        "• Certificate expired, not yet valid (clock skew), or revoked.\n• Hostname mismatch, missing intermediate, or unknown CA (often a corporate interception proxy).\n• No shared protocol version or cipher suite, or ALPN mismatch.\n• SNI missing so the server returns the wrong certificate.\n• Diagnose with `openssl s_client -connect host:443 -servername host` and read the alert and the chain.",
    },
    {
      bullet: "Relationship between TCP and TLS.",
      notes:
        "• TLS runs above TCP and relies on it for ordered, reliable delivery; the TCP handshake completes first, then the TLS handshake.\n• A successful TCP connect says nothing about TLS success (why.20).\n• TLS records ride in TCP segments; TCP still sees only encrypted bytes and cannot read them.\n• A connection reset or close can be injected at TCP level; TLS detects truncation and shows an error rather than quietly accepting it.",
    },
    {
      bullet: "TLS over TCP vs QUIC's integrated TLS 1.3 handshake.",
      notes:
        "• TLS over TCP: separate handshakes, one TCP round trip, then TLS 1.3 one round trip, so about two before application data (more with 1.2).\n• QUIC embeds TLS 1.3 in its own handshake: transport and crypto setup in about one round trip, with 0-RTT on resumption.\n• QUIC encrypts nearly all of its transport headers and packets by design, so middleboxes see little.\n• QUIC uses TLS for key agreement and authentication but does its own record protection and loss recovery.",
    },
  ],
  mentalModel:
    "• TCP gives a reliable pipe; TLS puts an authenticated, encrypted tunnel inside it.\n• Handshake: agree on parameters, exchange ephemeral key shares, server proves identity with a certificate and signature, both derive session keys.\n• Trust has two checks: the chain leads to a CA you trust, and the certificate names the host you meant.\n• After the handshake, data moves under fast symmetric keys; resumption and QUIC reduce the round trips to get there.",
  failuresAndSignals: [
    "Certificate expired or clock skew: handshake fails with an expired or not-yet-valid alert — check `notAfter` with `openssl s_client` and the client's system time.",
    "Missing intermediate certificate: works in some browsers, fails in curl or Java — the server sends an incomplete chain; serve the full chain.",
    "Hostname mismatch: the chain is valid but the name does not appear in the SANs — the client rejects it; check SNI and the certificate's SAN list.",
    "TLS inspection proxy or private CA: unknown-CA errors on some networks only — the issuer shown is the proxy's CA, not a public one.",
    "No shared version or cipher: handshake_failure or protocol_version alert after a good TCP connect — common with legacy clients against a server that dropped TLS 1.0/1.1 or old suites.",
    "TCP connect succeeds but the request never gets a response: the server expects TLS and the client sent plaintext, or the reverse — capture the first bytes and look for a Client Hello.",
  ],
  whyAnchors: ["why.19", "why.25", "why.26", "why.31"],
  connections: {
    before:
      "• Module 8: HTTP carries the messages TLS protects, and HTTP/3 over QUIC already includes TLS.\n• Module 5: TCP provides the ordered bytes the TLS handshake and records depend on.",
    after:
      "• Module 10: NAT, firewalls, and load balancers decide where TLS terminates and what each device can see.",
  },
  selfCheck: [
    {
      id: "why-tls",
      prompt: "TCP is reliable; why still use TLS?",
      answer:
        "TCP only moves bytes. It does not hide them from the path, detect silent modification, or prove the peer is the host you named. TLS adds confidentiality, integrity, and authentication on top of the stream (why.25).",
    },
    {
      id: "cert-and-name",
      prompt: "Why verify both the certificate chain and the hostname?",
      answer:
        "The chain shows a trusted CA vouched for the key (why.26). Hostname matching shows the certificate is for the server you meant; without it, a valid certificate for another site still gives a secure channel to the wrong server (why.31).",
    },
    {
      id: "fs",
      prompt: "What does forward secrecy protect against?",
      answer:
        "A later theft of the server's long-term private key. Session keys come from ephemeral key exchange, so recorded past traffic cannot be decrypted with that key.",
    },
    {
      id: "quic-tls",
      prompt: "How does QUIC's handshake differ from TLS over TCP?",
      answer:
        "QUIC integrates TLS 1.3 into its own handshake, so transport and crypto setup finish together in about one round trip, rather than a TCP handshake followed by a separate TLS handshake.",
    },
  ],
} satisfies ChapterSummary;
