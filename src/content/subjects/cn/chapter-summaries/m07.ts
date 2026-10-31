import type { ChapterSummary } from "../chapterSummarySchema";

export const summary = {
  moduleId: "m07",
  status: "complete",
  readingMinutes: { min: 12, max: 18 },
  thesis:
    "DNS is a delegated, cached, distributed database that maps stable names to current records, so a lookup works without any central server knowing every name.",
  outcomes: [
    "Explain why names exist apart from IP addresses and how delegation spreads ownership across zones.",
    "Trace a lookup from a stub resolver through a recursive resolver, root, TLD, and authoritative servers.",
    "Read the common record types and say how TTL and negative caching decide what a client sees and for how long.",
    "State what moves over UDP versus TCP, and what DoH and DoT change and do not change.",
    "Diagnose common failures by asking which cache, view, or server answered.",
  ],
  coverage: [
    {
      bullet: "DNS hierarchy and delegation.",
      notes:
        "• Names form a tree (root, TLD, second-level, ...), split into zones; each zone has one owner.\n• Delegation: a parent zone holds NS records (plus glue when needed) pointing at the child's servers.\n• No central registry approves each change; owners edit their own zone (why.21).\n• Names stay stable while addresses follow topology and may be many, IPv4 and IPv6.",
    },
    {
      bullet: "Root, TLD, and authoritative name servers.",
      notes:
        "• Root servers know who serves each TLD; TLD servers know who serves each registered domain.\n• Authoritative servers hold the zone's real records and answer with the AA flag set.\n• Each level returns a referral, not the final answer, until the authoritative server.\n• Resolvers start from built-in root hints, so no one has to ask where the root is.",
    },
    {
      bullet: "Recursive and iterative resolution.",
      notes:
        "• Recursive query: client asks a resolver to return the final answer or an error.\n• Iterative query: a server replies with the answer or the next referral and the asker follows it.\n• Typical path: stub asks recursive resolver (recursive); resolver asks root, TLD, authoritative (iterative).\n• Resolver caches each step, so most lookups skip the top of the tree (why.22).",
    },
    {
      bullet: "Stub resolvers and recursive resolvers.",
      notes:
        "• Stub: the small resolver in the OS or app; sends one recursive query to a configured server and caches little.\n• Recursive resolver: run by ISP, enterprise, or a public service; does the walk and shares its cache across users.\n• The configured server (DHCP, VPN, `resolv.conf`, or browser setting) decides what answers a host sees.\n• Mix-ups between the stub's cache, resolver cache, and authoritative data explain many surprises.",
    },
    {
      bullet: "A, AAAA, CNAME, NS, MX, and TXT records.",
      notes:
        "• A: name to IPv4 address; AAAA: name to IPv6 address.\n• CNAME: alias to another name; resolver restarts at the target; not allowed at the zone apex or beside other records for the same name.\n• NS: which servers are authoritative for a zone; used for delegation.\n• MX: mail servers with preference values; TXT: free-form text, used for SPF, DKIM, and domain verification.",
    },
    {
      bullet: "TTL and caching.",
      notes:
        "• Each record carries a TTL set by the zone owner; resolvers may reuse it until it expires.\n• Caching cuts latency and keeps load off root, TLD, and authoritative servers (why.22).\n• Nothing is pushed: a change shows up only as each cache expires.\n• Long TTL means fewer queries and survival of short outages; short TTL means faster change and more load.",
    },
    {
      bullet: "Negative caching.",
      notes:
        "• NXDOMAIN (name does not exist) and NODATA (name exists, no such type) are cached too.\n• Duration comes from the zone's SOA, capped by its minimum/negative TTL field.\n• Looking up a name before it exists can keep it missing after you create it.\n• Fix by waiting for the negative TTL, or flushing the resolver cache you control.",
    },
    {
      bullet: "DNS over UDP and TCP.",
      notes:
        "• UDP is the default: one small query, one small response, no handshake.\n• TCP is used when a response is truncated (TC bit) and for zone transfers.\n• EDNS(0) lets a UDP response be larger than the original 512-byte limit.\n• Firewalls that block TCP/53 or fragmented UDP break large answers, such as DNSSEC-signed ones.",
    },
    {
      bullet: "DNS over HTTPS and DNS over TLS at a conceptual level.",
      notes:
        "• Both wrap the same DNS messages in an encrypted channel between stub (or forwarder) and resolver: DoT on its own port over TLS, DoH inside HTTPS.\n• They hide queries from on-path observers and prevent tampering on that hop.\n• They do not hide the query from the resolver itself and do not authenticate the data; that is DNSSEC's job.\n• Where the stub sits changes who controls policy: OS, browser, or enterprise.",
    },
    {
      bullet: "DNS propagation myths and actual caching behavior.",
      notes:
        "• There is no propagation broadcast; authoritative servers update, and caches expire on their own TTLs.\n• Different users see old or new data for up to the old TTL, so lower the TTL before a planned change.\n• Resolvers may cap or ignore TTLs, and OS, browser, and app layers may cache again.\n• Secondary authoritative servers can lag by the zone transfer interval.",
    },
    {
      bullet: "Split-horizon DNS.",
      notes:
        "• The same name returns different answers by client location, such as internal private addresses versus public addresses.\n• Implemented with separate views or zones on the resolver or authoritative servers.\n• Common with VPNs and private clouds; failures happen when a client uses the wrong resolver or the views diverge.\n• Test from the vantage point of the affected client, not from your own.",
    },
    {
      bullet: "Common DNS failure scenarios.",
      notes:
        "• Wrong or missing record, or a stale cache after a change (check TTL).\n• Lame delegation: parent NS points at a server that does not answer authoritatively.\n• Resolver unreachable, TCP/53 blocked, or timeouts: SERVFAIL, or slow connections that wait on retries.\n• Split-horizon mismatch, CNAME loops, and DNSSEC validation failure.\n• Query authoritative servers directly (`dig @ns ...`) to compare with the resolver's answer.",
    },
  ],
  mentalModel:
    "• Stub asks one recursive resolver; the resolver does the walking and keeps the cache.\n• The tree is walked top-down by referrals: root says who runs the TLD, the TLD says who runs the domain, the authoritative server gives the answer.\n• Every answer carries a TTL, and every cache decides on its own when to forget it.\n• What a client sees depends on which resolver it used and what that resolver had cached.",
  failuresAndSignals: [
    "Stale answer after a change: some users reach the old address — compare `dig` against the resolver and against the authoritative server; the difference is the cached TTL remaining.",
    "NXDOMAIN lingers after creating a record: negative cache holds the earlier miss — signs are the SOA-derived TTL on the NXDOMAIN response; wait it out or flush the resolver.",
    "SERVFAIL or timeouts: broken delegation, unreachable authoritative server, or DNSSEC validation failure — trace with `dig +trace` and check each NS responds.",
    "Large responses fail while small ones work: TCP/53 blocked or UDP fragments dropped — a truncated (TC) reply followed by no TCP retry is the signal.",
    "Works inside the VPN, fails outside (or the reverse): split-horizon views or the wrong resolver — the same name returns different addresses depending on which server answered.",
  ],
  whyAnchors: ["why.21", "why.22"],
  connections: {
    before:
      "• Module 6: UDP, which carries most DNS queries and shows why a request-response protocol skips a handshake.",
    after:
      "• Module 8: HTTP uses a DNS lookup before a connection, and CNAMEs and short TTLs feed into CDNs and load balancing.",
  },
  selfCheck: [
    {
      id: "why-dns",
      prompt: "Why use names and DNS instead of IP addresses everywhere?",
      answer:
        "Addresses follow topology and change, and one service has many; names are stable identifiers. DNS adds distribution: zones are owned and edited locally, and delegation lets any resolver find any name from the root (why.21).",
    },
    {
      id: "propagation",
      prompt: "Why does a DNS change not take effect everywhere immediately?",
      answer:
        "Nothing is pushed to caches. Each resolver keeps the old record until its TTL expires, so users see old or new data for up to the old TTL. Negative answers are cached for the SOA-derived time (why.22).",
    },
    {
      id: "resolution",
      prompt: "Who does the walk from root to authoritative server?",
      answer:
        "The recursive resolver, using iterative queries and following referrals. The stub resolver only sends one recursive query and waits for the final answer.",
    },
  ],
} satisfies ChapterSummary;
