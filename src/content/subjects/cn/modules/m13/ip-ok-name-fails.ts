import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.ip-ok-name-fails",
  status: "complete",
  objectives: [
    "Separate reachability by address from name resolution failure.",
    "Check stub, resolver, search list, and hosts file differences.",
    "Use dig and getent to see what the application path returns.",
  ],
  prerequisites: ["m13.accept-stall"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Connecting to the IP works; using the hostname fails.\n• The host is up; the name path is not.",
      whyItMatters:
        "• Apps almost always use names. Fixing 'ping by IP' does not restore the service for clients.",
      idea: "• Prove DNS/NSS separately from TCP.\n• Compare getent (NSS) with dig (DNS) and the resolver the app uses.",
      howItWorks:
        "• `curl http://IP` OK; `curl http://name` fails at resolve or hits wrong address.\n• dig status: NXDOMAIN, SERVFAIL, timeout, NODATA.\n• Check `/etc/hosts`, search domains, split horizon, stale negative cache.",
      whyItWorks:
        "• IP success only proves L3/L4 to that address; names add resolution and possibly different addresses (v4/v6).",
      limits:
        "• Happy Eyeballs may try AAAA first and hang while A would work.\n• Browser DoH can bypass system resolvers.",
      connections:
        "• Module 7 DNS failures investigation.\n• TLS name mismatch if the IP works with --insecure but name verify fails.",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "NXDOMAIN or typo; search-list expanded the wrong suffix.",
        "NODATA for the requested type (e.g. AAAA only queried).",
        "Resolver timeout / SERVFAIL; authoritative servers unreachable.",
        "`/etc/hosts` or nsswitch overrides disagree with DNS.",
        "Split-horizon: public resolver lacks the internal name.",
        "Stale negative cache after the name was created.",
        "Name resolves to a different (wrong) address than the IP you tested.",
      ],
      order: [
        "Reproduce with the app's name; note getaddrinfo error.",
        "`getent ahosts <name>` vs `dig <name>`.",
        "Ask another resolver; `dig +trace` if the zone may be broken.",
        "Compare resolved addresses to the working IP.",
        "Check search list and `/etc/hosts`.",
      ],
      commands: [
        {
          command: "getent ahosts api.example.com",
          shows: "What NSS returns (includes hosts file).",
        },
        {
          command: "dig api.example.com A api.example.com AAAA",
          shows: "DNS status and RRsets from the configured resolver.",
        },
        {
          command: "dig @1.1.1.1 api.example.com",
          shows: "Whether a public resolver answers differently (view/cache).",
        },
        {
          command:
            "curl -v --connect-timeout 3 http://198.51.100.10/ && curl -v http://api.example.com/",
          shows: "IP path vs name path in one client.",
        },
      ],
      implications: [
        "getent fails, dig works: NSS/hosts/search-list issue.",
        "Both fail NXDOMAIN: name missing in that view.",
        "Name resolves to other IPs: you tested the wrong target.",
        "SERVFAIL with +cd success: DNSSEC validation problem.",
      ],
      distinguishing: [
        "Resolution vs TLS name: connect by name fails before TCP vs after with verify error.",
        "Local vs zone: only the corporate resolver answers.",
        "AAAA hang vs missing A: force family or fix v6 path.",
      ],
      mistakes: [
        "Declaring DNS fine because dig @8.8.8.8 works while the app uses an internal resolver.",
        "Testing only A when the client prefers AAAA.",
        "Editing hosts as a permanent fix without fixing the zone.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "getent-dig",
          prompt: "dig returns the right A record; getent ahosts fails. Where to look?",
          answer:
            "NSS path: `/etc/hosts`, `nsswitch.conf`, and search domains—not the authoritative zone alone.",
        },
        {
          id: "wrong-ip",
          prompt: "Name resolves; connect fails; raw IP you were given works. Next check?",
          answer:
            "Whether dig/getent's address equals that IP. Stale or split-horizon answers often differ.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "getent-ahosts",
      statement: "getent ahosts performs a name-to-address lookup via the Name Service Switch.",
      sourceId: "man-getent",
      locator: "ahosts",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "dig-lookup",
      statement: "dig is a DNS lookup utility that shows status and answer sections from a server.",
      sourceId: "bind-dig",
      locator: "dig — DNS lookup utility",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    { sourceId: "man-getent", note: "NSS view of the name." },
    { sourceId: "bind-dig", note: "DNS status and RRsets." },
    { sourceId: "man-resolv-conf", note: "Stub resolver configuration." },
    { sourceId: "man-nsswitch-conf", note: "hosts lookup order." },
    { sourceId: "rfc1035", note: "RCODE meanings." },
  ],
} satisfies LessonBody;
