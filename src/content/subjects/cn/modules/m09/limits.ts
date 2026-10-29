import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m09.limits",
  status: "complete",
  objectives: [
    "Explain session resumption and what it saves.",
    "State what forward secrecy means for a later private-key compromise.",
    "List what HTTPS protects and common failures a green padlock still allows.",
  ],
  prerequisites: ["m09.handshake"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Full handshakes cost round trips and CPU.\n• Operators want reconnects to be cheap.\n• Attackers who steal a server key later must not decrypt yesterday's captures if FS held.\n• HTTPS success is not application success.",
      whyItMatters:
        "• Mobile and microservice fleets reconnect constantly.\n• 0-RTT is tempting and dangerous for non-idempotent APIs.\n• Incident response: distinguish TLS failures from HTTP 5xx and logic bugs.",
      idea: "• Resumption: reuse a PSK (ticket) from a prior session to skip parts of the handshake.\n• Forward secrecy: ephemeral DH so long-term key theft does not decrypt past sessions.\n• HTTPS protects the channel—not the URL bar phishing, not HTTP-level authz, not the server app.",
      howItWorks:
        "• TLS 1.3 resumption uses PSK; offering key_share keeps FS if the server agrees.\n• 0-RTT sends early data under weaker replay properties; only safe methods belong there.\n• Failures: unknown CA, name mismatch, expired cert, protocol/cipher mismatch, truncated chain.\n• After TLS: HTTP 401/403/500, wrong Host routing, and app bugs still happen.",
      whyItWorks:
        "• Tickets buy latency without repeating full public-key auth every time.\n• FS limits the blast radius of a later key compromise.\n• Clear failure classes speed debugging (cert vs network vs app).",
      limits:
        "• PSK-only resumption without a fresh DH share trades away FS for that connection.\n• 0-RTT can be replayed (RFC 8446 §2.3 / anti-replay discussion).\n• Compromised client or server endpoint defeats TLS.\n• Metadata: IPs, SNI (usually), sizes, timing remain.",
      connections:
        "• Module 8: HTTP status after a good TLS session (why.20).\n• Module 10: middleboxes and TLS inspection.\n• Module 13: TLS ok, HTTP 5xx scenarios.",
    },
    {
      type: "comparison",
      id: "protects",
      title: "What HTTPS protects / does not",
      columns: ["Protects", "Does not protect"],
      rows: [
        {
          label: "On the wire",
          cells: [
            "Request/response confidentiality and integrity to the authenticated server.",
            "Which site the user meant if they mistype or follow a phishing link.",
          ],
        },
        {
          label: "Server identity",
          cells: [
            "Cert + hostname check for the name you connected for.",
            "Whether that server's HTTP handler is correct or authorized for this user.",
          ],
        },
        {
          label: "Past sessions",
          cells: [
            "With FS: stealing today's private key does not decrypt old captures.",
            "Endpoint malware that reads plaintext before encrypt / after decrypt.",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "common-failures",
      title: "Common TLS failures (notes)",
      steps: [
        {
          title: "Name / chain",
          body: "• Hostname mismatch, expired leaf, missing intermediate, untrusted root.",
        },
        {
          title: "Negotiation",
          body: "• No shared TLS version or cipher suite. Client too new, server too old (or the reverse).",
        },
        {
          title: "Clock",
          body: "• Client clock wrong → notYetValid / expired errors.",
        },
        {
          title: "Middlebox",
          body: "• Corporate TLS inspection presents a local CA; clients without that CA fail.",
        },
      ],
    },
    {
      type: "callout",
      id: "zero-rtt",
      tone: "caution",
      title: "0-RTT",
      body: "• Early data can be replayed by an attacker who captured it.\n• Allow only idempotent, replay-safe requests (often GET-like reads).\n• When unsure, wait for 1-RTT keys.",
    },
    {
      type: "example",
      id: "tls-ok-http-fail",
      title: "TLS green, request fails",
      setup: "Mobile app completes TLS 1.3 to `api.example.com` behind a load balancer.",
      walkthrough: [
        "• Certificate chain and hostname verify.",
        "• HTTP returns 503 from the gateway: no healthy upstream.",
        "• tcpdump shows Application Data; users still see errors.",
      ],
      result: "• TLS did its job. Fix the pool / health checks—not the cipher suite.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "forward-secrecy",
          prompt: "In one sentence, what does forward secrecy buy you?",
          answer:
            "If the server's long-term private key is stolen later, past session traffic that used ephemeral DH still cannot be decrypted from that key alone.",
        },
        {
          id: "resumption-cost",
          prompt: "What does session resumption mainly save?",
          answer:
            "Round trips and public-key work on reconnect by reusing a PSK/ticket from a prior session (optionally with a fresh DH share for FS).",
        },
        {
          id: "https-not-enough",
          prompt: "Give two failures HTTPS does not prevent.",
          answer:
            "Examples: phishing to a lookalike host with a valid cert for that host; HTTP 5xx / app authorization bugs after a successful TLS session; compromised server process reading plaintext.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "psk-resumption",
      statement:
        "TLS 1.3 replaces earlier session resumption and PSK cipher suites with a single PSK exchange; a client offering resumption via PSK SHOULD also supply key_share so the server can decline resumption and keep forward secrecy.",
      sourceId: "rfc8446",
      locator: "Section 1.2; Section 2.2, Resumption and PSK",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "zero-rtt-weaker",
      statement:
        "0-RTT data has weaker security properties than other kinds of TLS data, notably regarding replay.",
      sourceId: "rfc8446",
      locator: "Section 2.3, 0-RTT Data (IMPORTANT NOTE)",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "tls13-fs-required",
      statement:
        "TLS 1.3 removed static RSA and Diffie-Hellman cipher suites; public-key based key exchange mechanisms provide forward secrecy.",
      sourceId: "rfc8446",
      locator: "Section 1.2, Major Differences from TLS 1.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc8446", note: "Sections 1.2, 2.2, and 2.3: FS, resumption/PSK, 0-RTT caveats." },
  ],
} satisfies LessonBody;
