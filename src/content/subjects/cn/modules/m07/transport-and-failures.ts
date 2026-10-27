import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m07.transport-and-failures",
  status: "complete",
  objectives: [
    "Explain why DNS uses UDP first, when it moves to TCP, and what EDNS(0) and the TC bit do.",
    "Describe DNS over TLS and DNS over HTTPS at the level of what they protect, between whom, and what they do not hide.",
    "Explain split-horizon DNS and predict when a client gets the inside or the outside answer.",
    "Tell NXDOMAIN, NODATA, SERVFAIL, REFUSED, timeouts, and truncation apart with `dig`, and map each to likely causes.",
  ],
  prerequisites: ["m07.caching"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "A DNS exchange is one small question and one answer. Setting up a TCP connection for each would double the latency of every lookup. But answers can be large, UDP can be lost or spoofed, a path may drop big or fragmented packets, and plain DNS on port 53 can be read and changed by anyone on the path. When something in this chain fails, the application usually sees only 'could not resolve host'.",
      whyItMatters:
        "Every outbound connection waits on a lookup, so a DNS failure looks like an outage of whatever service the program was trying to reach. Firewalls that allow only UDP 53, VPNs that change which resolver answers, browsers that send DNS over HTTPS to someone else, and an authoritative server that returns SERVFAIL all fail differently. Telling them apart quickly decides which team owns the fix.",
      idea: "Keep one message format and change only the pipe. A query goes over UDP to port 53 in one datagram when it can. If the answer does not fit, the server says so with the TC bit and the client asks again over TCP, where a 2-byte length prefixes each message. EDNS(0) lets a client advertise a larger UDP size to avoid that retry. For privacy between a stub and its resolver, the same message can travel inside TLS on port 853, or as the body of an HTTPS request.",
      howItWorks:
        "Without EDNS, a UDP message is limited to 512 bytes. A client that adds an OPT record advertises the largest UDP payload it can reassemble, and the server may send up to that size. A response that still cannot carry a required RRset is sent with TC set and the partial data ignored, and the client retries over TCP. All general-purpose DNS software must support TCP, should reuse a connection, and may pipeline several queries on it, matching answers by message ID. DNS over TLS (DoT) opens a TCP connection to port 853 and starts TLS before any DNS message. DNS over HTTPS (DoH) sends the DNS message with GET or POST to an HTTPS URI the client was configured with, using the media type `application/dns-message`.",
      whyItWorks:
        "Most answers fit in one datagram, so most lookups cost one round trip and no connection state on the server. The TC fallback keeps correctness when they do not: a client never acts on a partial RRset. TCP's handshake also proves the client's address, which UDP does not. Encrypted transports reuse the TLS and HTTPS machinery that already authenticates servers, so the DNS protocol itself did not have to change.",
      limits:
        "Large UDP answers may be fragmented, and fragments are often dropped, so a big EDNS size can cause timeouts rather than prevent them. Blocking TCP 53 breaks any answer that needs it. DoT and DoH protect only the hop between the stub and the resolver they are configured for. That resolver still sees every query, and its own queries to authoritative servers usually go in the clear. A client that sends DoH to a public resolver bypasses the local resolver and its split-horizon view. Encryption also does not make an answer true. Authenticating the data itself is DNSSEC's job.",
      connections:
        "Module 6 used DNS as the example of a request-response protocol that suits UDP. Module 5's TCP and Module 3's fragmentation explain why large answers and TCP fallback behave as they do. The HTTPS transport is ordinary Module 8 HTTP over Module 10 TLS. Module 13 starts its 'DNS resolves but TCP fails' scenario from the boundary this lesson draws: what DNS itself can and cannot get wrong.",
    },
    {
      type: "comparison",
      id: "transports",
      title: "Four ways to carry the same DNS message",
      caption:
        "Plain UDP and TCP are used both from stubs to resolvers and from resolvers to authoritative servers. RFC 7858 and RFC 8484 address the stub-to-resolver hop.",
      columns: ["Port and framing", "Used when", "Who can read the query", "`dig` option"],
      rows: [
        {
          label: "UDP",
          cells: [
            "53; one message per datagram",
            "By default, for nearly all queries",
            "Anyone on the path, who can also forge a reply",
            "Default",
          ],
        },
        {
          label: "TCP",
          cells: [
            "53; each message prefixed with a 2-byte length",
            "After a truncated UDP answer, for zone transfers, or by choice",
            "Anyone on the path",
            "`+tcp`; `+ignore` stops the automatic retry",
          ],
        },
        {
          label: "DNS over TLS",
          cells: [
            "853; TLS first, then length-prefixed messages",
            "When the stub is configured for a resolver that supports it",
            "The resolver. The path sees a TLS connection to port 853",
            "`+tls`; `+tls-ca` enables certificate validation",
          ],
        },
        {
          label: "DNS over HTTPS",
          cells: [
            "443; one DNS message per HTTP request and response",
            "When a client, often a browser, is configured with a DoH URI",
            "The DoH server. The path sees HTTPS, possibly mixed with other web traffic",
            "`+https`, which defaults to the `/dns-query` path",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "large-answers",
      title: "When an answer does not fit in 512 bytes",
      steps: [
        {
          title: "Classic limit",
          body: "Without EDNS, a UDP DNS message is at most 512 bytes, not counting IP and UDP headers. A larger response is truncated and has the TC bit set.",
        },
        {
          title: "EDNS(0) raises it",
          body: "The client adds an OPT pseudo-record whose class field holds the largest UDP payload it can accept. Values below 512 count as 512. RFC 6891 suggests starting at 4096 and falling back to about 1280 to 1410 bytes if that fails. In `dig` output this shows as `; EDNS: version: 0, flags:; udp: 1232` in the OPT pseudosection.",
        },
        {
          title: "TC means retry, not 'here is part of it'",
          body: "TC is set only when a required RRset could not be included whole. A client receiving TC ignores the response and asks again over a transport that permits larger replies, normally TCP. If only optional additional data did not fit, the server leaves it out and does not set TC.",
        },
        {
          title: "Observed",
          body: "In October 2026, `dig @1.1.1.1 +noedns +ignore google.com TXT` returned 28 bytes with `flags: qr tc rd ra` and `ANSWER: 0`. Without `+ignore`, dig printed `;; Truncated, retrying in TCP mode.` and got 17 TXT records in 1176 bytes over TCP. With EDNS's default size, the same answer arrived over UDP in 1187 bytes.",
        },
        {
          title: "TCP done well",
          body: "Clients should keep a TCP connection open for several queries, send them without waiting for earlier answers, and match responses by message ID, because servers may answer out of order. Servers must answer on the same transport, and for TCP the same connection, that the query arrived on.",
        },
      ],
    },
    {
      type: "example",
      id: "split-horizon",
      title: "An internal name that works in the office and fails at home",
      setup:
        "A company serves `corp.example.com` with split-horizon DNS. Its servers have two views. Queries from 10.0.0.0/8 get the inside view, where `wiki.corp.example.com A 10.20.0.5`. Queries from anywhere else get the outside view, where `wiki.corp.example.com` does not exist. The office resolver is 10.0.0.2. A laptop's browser has DNS over HTTPS turned on to a public resolver. Names and addresses are illustrative.",
      walkthrough: [
        "In the office, with DoH off. The stub asks 10.0.0.2, which is inside, so the authoritative servers answer from the inside view. `getent ahosts wiki.corp.example.com` prints 10.20.0.5, and the wiki loads.",
        "At home, VPN off. The laptop's resolver is the home router, which forwards to the ISP. The ISP's resolver queries the company's authoritative servers from an outside address and gets NXDOMAIN with the zone's SOA. `dig wiki.corp.example.com` shows `status: NXDOMAIN`. That answer is correct for the outside view.",
        "At home, VPN on. The VPN client sets the laptop's resolver to 10.0.0.2 for `corp.example.com`, or for everything. `dig @10.0.0.2 wiki.corp.example.com +short` prints 10.20.0.5. But if the ISP resolver's NXDOMAIN from a minute ago is still cached in a local cache on the laptop, the name can stay missing until that negative entry expires.",
        "VPN on, DoH on. `curl` and `getent` work, because they use the system stub and reach 10.0.0.2. The browser fails, because it sends the query in HTTPS to the public resolver, which asks from outside and gets NXDOMAIN. Encryption did its job and kept the query away from the local network, including the resolver that had the inside answer.",
        "To confirm which view answered, ask each resolver directly: `dig @10.0.0.2 wiki.corp.example.com` and `dig @1.1.1.1 wiki.corp.example.com`. If the inside resolver returns the address and the public one returns NXDOMAIN carrying the same zone's SOA, the zone is answering by view. The record is not broken.",
        "The fixes are configuration, not DNS records. Keep internal names in the inside view and send queries for them to the VPN's resolver. On managed laptops, make the browser use the system resolver for internal domains, either by turning its DoH off or by excluding those domains.",
      ],
      result:
        "Split horizon makes one name mean different things depending on where the query comes from. So the question is always which resolver asked, from which address. A resolver outside the network, whether it is the ISP's or a DoH server's, gets the outside answer, and every cache along the way remembers it for the negative TTL.",
    },
    {
      type: "callout",
      id: "what-encryption-hides",
      tone: "note",
      title: "What DoT and DoH do not hide",
      body: "Both encrypt and authenticate the hop to one resolver. That resolver sees every name you look up, and so does whoever runs it. Its own queries to authoritative servers are usually plain UDP. The destination address of your next connection, and usually the name in the TLS handshake for it, are still visible on the path. DoH additionally hides DNS among other HTTPS traffic on port 443, which is why it is harder for a network to block or redirect, and why it can silently bypass a company's resolver.",
    },
    {
      type: "investigation",
      id: "failures",
      causes: [
        "The name does not exist as typed: a typo, a search-list expansion such as `api.example.com.corp.internal`, or a stale negative cache entry after the name was created.",
        "The name exists but has no record of the requested type, such as no AAAA, which is NODATA, not an error.",
        "The resolver cannot complete resolution and returns SERVFAIL: every authoritative server for the zone is down or unreachable, the delegation is broken or loops, a CNAME loops, or a DNSSEC-signed zone fails validation.",
        "The server refuses: a recursive resolver rejecting clients outside its access list, or an authoritative server asked about a zone it does not serve. Both return REFUSED.",
        "Nothing answers: the configured resolver is down, UDP 53 to it is blocked, or responses are dropped. The stub retries, then reports a temporary failure.",
        "Large answers fail: UDP fragments are dropped, or the answer is truncated and TCP 53 is blocked.",
        "The answer is wrong rather than missing: `/etc/hosts`, a split-horizon view, a stale cache, a resolver that rewrites answers, or the old records still being served by an authoritative server that was not updated.",
      ],
      order: [
        "Reproduce through the same path the application uses: `getent ahosts <name>`, and note the `getaddrinfo()` error, such as EAI_NONAME or EAI_AGAIN.",
        "Ask the configured resolver directly with `dig <name>` and read the status line: NOERROR with answers, NOERROR with none, NXDOMAIN, SERVFAIL, REFUSED, or a timeout.",
        "Compare with a different resolver, for example a public one, to separate the local resolver and its view from the zone itself.",
        "Walk the delegation with `dig +trace <name>`, then ask each authoritative server directly with `+norecurse` and check for AA, the expected data, and the same SOA serial.",
        "For timeouts and truncation, test the transport: `dig +tcp`, a smaller `+bufsize`, and a different server, to find which packets are being dropped.",
      ],
      commands: [
        {
          command: "getent ahosts api.example.com",
          shows:
            "What the NSS path returns, including `/etc/hosts`. If this differs from `dig`, the difference is local to the host.",
        },
        {
          command: "dig api.example.com",
          shows:
            "The status, flags, answer, and authority sections from the first resolver in `resolv.conf`. `aa` absent and `ra` present means a recursive answer.",
        },
        {
          command: "dig @1.1.1.1 api.example.com",
          shows:
            "The same question to another resolver. Different answers point at caches, views, or the local resolver.",
        },
        {
          command: "dig +trace api.example.com",
          shows:
            "Iterative resolution from the root, one referral per zone, showing where the chain stops or which server gives an unexpected answer.",
        },
        {
          command: "dig +norecurse @ns1.example.com api.example.com",
          shows:
            "What one authoritative server holds, with `aa` set. Repeat for each NS name to find a server that disagrees.",
        },
        {
          command: "dig +cd api.example.com",
          shows:
            "The answer with DNSSEC validation disabled at the resolver. SERVFAIL without `+cd` and an answer with it means a validation failure.",
        },
        {
          command: "dig +tcp api.example.com",
          shows:
            "Whether the resolver answers over TCP. A UDP timeout that TCP fixes points at dropped large UDP responses or fragments.",
        },
      ],
      implications: [
        "NXDOMAIN with an SOA in the authority section is an authoritative statement that the name does not exist. Look at the exact name queried, the search list, and the SOA's TTL for how long it will be cached.",
        "NOERROR with `ANSWER: 0` and an SOA is NODATA: the name exists, but not with that type. A missing AAAA is normal; a missing A for a service you deployed is a record problem.",
        "SERVFAIL means the resolver tried and failed, not that the name is missing. It is cached for at most five minutes, so a fix may take that long to show.",
        "REFUSED from a resolver means you are not allowed to use it. From an authoritative server it usually means the server does not serve that zone. If that server is listed in the zone's NS records, the delegation is broken, the fault once called a lame delegation.",
        "A timeout means no response arrived. The stub then tries the next server, and `getaddrinfo()` reports EAI_AGAIN, a temporary failure worth retrying.",
      ],
      distinguishing: [
        "Local versus DNS: `getent ahosts` and `dig` disagree, so check `/etc/hosts` and `nsswitch.conf` before touching DNS.",
        "Resolver versus zone: one resolver fails and another answers, so the problem is the resolver's cache, view, or reachability. Both fail the same way, so look at the zone.",
        "Broken delegation versus broken server: `+trace` stops at a referral whose servers time out or answer REFUSED, versus every listed server answering but with old data.",
        "DNSSEC versus reachability: SERVFAIL disappears with `+cd` for a validation problem, and stays for unreachable servers.",
        "Size versus reachability: small queries such as an A record work and a large TXT or DNSSEC answer times out, so suspect fragments or blocked TCP 53.",
      ],
      mistakes: [
        "Testing with `dig` only and concluding DNS is fine, when the application reads `/etc/hosts` or uses a different resolver.",
        "Treating NXDOMAIN, SERVFAIL, and a timeout as the same 'DNS is down', when each points at a different owner.",
        "Retrying immediately after fixing a zone and assuming the fix failed, when a negative or failure cache entry is still live.",
        "Allowing only UDP 53 through a firewall. Large answers then fail, and sometimes only for some names.",
        "Assuming the browser resolves names the same way as `curl`, when the browser has DoH turned on.",
      ],
    },
    {
      type: "expandable",
      id: "transport-details",
      title: "Retries, EDNS sizes, and the encrypted transports in more detail",
      paragraphs: [
        "Retry limits. RFC 9520 says a resolver must not send a query to one server address over one transport more than three times in total before treating that address as unresponsive for the query, and expects timeouts between 3 and 30 seconds. It may try another transport to the same server. RFC 1035 recommends trying other servers before repeating a query to the same one. glibc's stub uses `timeout` and `attempts` from `resolv.conf`, and `dig` defaults to a 5-second timeout and 3 tries.",
        "EDNS sizes. The advertised size is what the requester's own stack can reassemble, not the path MTU. RFC 6891 warns that a very large value guarantees IP fragmentation and may lose answers to a single dropped fragment, and that a requester behind a firewall that blocks fragments should advertise a smaller size. The OPT record is per hop and is never cached. Middleboxes must not limit DNS over UDP to 512 bytes. In October 2026, both the Wikimedia authoritative server and 1.1.1.1 advertised 1232 bytes.",
        "DNS over TLS. A server that supports DoT must listen on TCP 853 unless the client and server agree on another port, which must not be 53. Plain DNS must never be sent on the DoT port, even after a failed handshake. Clients should remember servers that do not support DoT, for about an hour. In the opportunistic profile, the client uses TLS if it can and may not validate the server, so an active attacker can strip the privacy. In the out-of-band key-pinned profile, it uses only servers whose public-key pins it was given, and a pin mismatch is a non-recoverable error.",
        "DNS over HTTPS. Each DNS query and response is one HTTP exchange, so many can run in parallel on one HTTP/2 connection. GET carries the query base64url-encoded in the `dns` URI parameter and is more cache-friendly. POST carries it in the body. Clients should use DNS message ID 0 to help HTTP caches. A valid DNS response, including NXDOMAIN or SERVFAIL, comes back with an HTTP 2xx status. The HTTP freshness lifetime must not exceed the smallest TTL in the answer section.",
        "Split horizon is not part of the DNS standard. RFC 9499 describes it as authoritative servers giving different answers for the same names depending on the query's source, often implemented as server 'views' keyed on source address. A name that is supposed to be globally unique then means different things to different users, which is exactly why the example above failed.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "tcp-blocked",
          prompt:
            "A new firewall policy allows only UDP 53 to the corporate resolver. Most lookups work, but queries for names with many TXT records, and for some DNSSEC-signed names, time out. Why only those?",
          answer:
            "Their answers are too large for the UDP size in use, or their large UDP responses are fragmented and the fragments are dropped. When a response carries TC, the client must retry over TCP, and TCP 53 is blocked, so the lookup fails. Small A and AAAA answers fit in one datagram and never need TCP. All DNS implementations are required to support TCP, so the firewall must allow TCP 53 too.",
        },
        {
          id: "servfail-vs-nxdomain",
          prompt:
            "Your service logs `getaddrinfo: Temporary failure in name resolution` for `pay.partner.example`. `dig pay.partner.example` shows `status: SERVFAIL`. A teammate says the partner deleted the name. Is that consistent with the evidence? What do you run next?",
          answer:
            "No. A deleted name would give NXDOMAIN, an authoritative answer that the name does not exist, and `getaddrinfo()` would report the name as unknown. SERVFAIL means the resolver could not get a useful answer, which maps to the temporary failure. Run `dig +cd pay.partner.example` to test for a DNSSEC validation failure, and `dig +trace pay.partner.example` to see whether the partner's authoritative servers are unreachable, refusing, or lame.",
        },
        {
          id: "doh-what-hidden",
          prompt:
            "After turning on DNS over HTTPS to a public resolver, a user asks whether their ISP can still tell which sites they visit. What does DoH change, and what does it not?",
          answer:
            "The ISP can no longer read or modify the DNS queries, because they travel inside HTTPS to the DoH server. But the DoH provider now sees every query. The ISP still sees the IP addresses the browser connects to next, and usually the server name in each TLS handshake. DoH protects only the hop between the client and the DoH server.",
        },
        {
          id: "same-name-two-answers",
          prompt:
            "`dig @10.0.0.2 app.corp.example.com +short` prints 10.20.0.5. `dig @8.8.8.8 app.corp.example.com +short` prints 203.0.113.40. Neither changes when repeated. Which is wrong?",
          answer:
            "Probably neither. This is split horizon. The company's authoritative servers give the inside view to queries from the internal resolver's address and the outside view to everyone else. Each answer is correct for its audience. A client gets the inside address only if it uses an inside resolver, which is why VPN DNS settings and browser DoH matter.",
        },
        {
          id: "timeout-ladder",
          prompt:
            "`resolv.conf` lists one name server, with the default options, and that server has stopped responding. Roughly how long does `getaddrinfo()` take to fail, and what error does it return?",
          answer:
            "With one server, `timeout` 5 seconds and `attempts` 2, the stub sends the query, waits 5 seconds, sends it again, and waits another 5, so about 10 seconds before it gives up. A and AAAA go out in parallel, so they share that time. It returns EAI_AGAIN, a temporary failure. Treat this as an estimate: resolv.conf(5) says `timeout` may not be the total time of a resolver call, and an ICMP error from the path can end a wait sooner.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "udp-512-tc",
      statement:
        "DNS over UDP uses port 53. Messages are restricted to 512 bytes, not counting IP or UDP headers; longer messages are truncated and the TC bit is set. UDP queries may be lost or reordered, and a client should try other servers before repeating a query to the same one, with a minimum retransmission interval of 2 to 5 seconds.",
      sourceId: "rfc1035",
      locator: "Section 4.2.1, UDP usage",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tcp-length-prefix",
      statement:
        "DNS over TCP uses port 53, and each message is prefixed with a two-byte length field that excludes itself.",
      sourceId: "rfc1035",
      locator: "Section 4.2.2, TCP usage",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rcodes",
      statement:
        "RCODE 1 is a format error, 2 a server failure, 3 a name error meaningful only from an authoritative server, 4 not implemented, and 5 refused for policy reasons.",
      sourceId: "rfc1035",
      locator: "Section 4.1.1, Header section format (RCODE)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tc-semantics",
      statement:
        "TC should be set only when a required RRset could not be included in its entirety, not when additional information was left out. A client receiving TC should ignore the response and query again over a mechanism such as TCP that permits larger replies.",
      sourceId: "rfc2181",
      locator: "Section 9, The TC (truncated) header bit",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "tcp-required",
      statement:
        "All general-purpose DNS implementations must support both UDP and TCP, including stub resolvers, recursive resolvers, and authoritative servers. Resolvers may use TCP before UDP and should reuse an open connection. Servers must respond on the transport, and for TCP the connection, the query arrived on. Failing to support TCP, or blocking it, will probably cause resolution failures.",
      sourceId: "rfc7766",
      locator: "Sections 1 and 5",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "tcp-large-and-fragments",
      statement:
        "Without EDNS0, a server truncates a UDP response over 512 bytes and sets TC, and the client retries over TCP. With EDNS0 a server may send up to the client's announced size, but UDP packets above the path MTU are fragmented, fragmentation is unreliable, and many firewalls block fragments. DNSSEC-signed responses routinely exceed 1500 bytes.",
      sourceId: "rfc7766",
      locator: "Section 4, Discussion",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "tcp-reuse-pipelining",
      statement:
        "Clients and servers should reuse a TCP connection for multiple queries. Clients should pipeline queries without waiting for earlier replies and must not reuse an in-flight message ID. Servers may answer out of order, so clients must match responses by message ID and the question fields.",
      sourceId: "rfc7766",
      locator: "Sections 6.2.1, 6.2.1.1, and 7",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "edns-payload",
      statement:
        "EDNS(0) lets a requester advertise a UDP payload size larger than 512 bytes in the OPT record's CLASS field, to avoid truncation and TCP retries. It is the largest payload the requester's stack can reassemble, not the path MTU. Values below 512 are treated as 512. A requester behind a firewall that blocks fragments should not choose a value that causes fragmentation. 4096 is a reasonable starting point, with fallback to 1280 to 1410 bytes. The OPT record must not be cached.",
      sourceId: "rfc6891",
      locator: "Sections 4.3, 6.2.1, 6.2.3, and 6.2.5",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "edns-middleboxes",
      statement:
        "Conformant middleboxes must not limit DNS messages over UDP to 512 bytes, and forwarding middleboxes must not modify or delete the OPT record.",
      sourceId: "rfc6891",
      locator: "Section 6.2.6, Support in Middleboxes",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "dot-port-and-framing",
      statement:
        "A DoT server must listen on TCP port 853 unless client and server agree on another port, which must not be 53. The first exchange is a TLS handshake. Cleartext DNS must not be sent on a DoT port. Messages use the two-byte length prefix, and clients should pipeline and match responses by message ID. Clients should remember servers that do not support DoT for a period such as an hour.",
      sourceId: "rfc7858",
      locator: "Sections 3.1 and 3.3",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "dot-scope-profiles",
      statement:
        "DoT is specified for queries between stub clients and recursive servers. In the opportunistic privacy profile, the client may not validate the resolver and remains vulnerable to on-path attacks that remove privacy; in the out-of-band key-pinned profile, the client uses only servers matching its configured SPKI pins and treats a mismatch as non-recoverable.",
      sourceId: "rfc7858",
      locator: "Section 1, Introduction; Sections 4, 4.1, and 4.2",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "doh-exchange",
      statement:
        "DoH maps each DNS query and response to one HTTPS exchange to a URI from the client's configuration, using GET with the base64url-encoded query in the 'dns' variable or POST with the query as the body, and the media type application/dns-message. Servers must implement both. Clients should use DNS ID 0 for cache friendliness. Any valid DNS response, including SERVFAIL or NXDOMAIN, uses an HTTP 2xx status.",
      sourceId: "rfc8484",
      locator: "Sections 1, 3, 4.1, 4.2, and 4.2.1",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "doh-cache-and-http2",
      statement:
        "A DoH response's HTTP freshness lifetime must not exceed the smallest TTL in the answer section, or for a negative answer the SOA MINIMUM. HTTP/2 is the minimum recommended HTTP version.",
      sourceId: "rfc8484",
      locator: "Sections 5.1 and 5.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "doh-on-the-wire",
      statement:
        "DoH encrypts DNS traffic and requires server authentication, mitigating passive surveillance and diversion to rogue servers, as DoT also does. Using port 443 and mixing DoH with other HTTPS traffic can deter on-path interference and make traffic analysis harder.",
      sourceId: "rfc8484",
      locator: "Section 8.1, On the Wire",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "split-dns-definition",
      statement:
        "Split DNS, or split-horizon DNS, has no formal definition. It generally means authoritative servers giving partly or completely different answers for a set of domains depending on the query's source, so a notionally unique name means different things to different users. It is often implemented with views, which are not standardized but are widely implemented and usually keyed on source address.",
      sourceId: "rfc9499",
      locator: "Section 7 (Split DNS, View)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "failure-conditions",
      statement:
        "A resolution failure is when no available server gives a useful response; NXDOMAIN and NODATA are useful responses, not failures. Causes include SERVFAIL, REFUSED, timeouts and unreachable servers, delegation loops, alias loops, DNSSEC validation failures, and FORMERR. Authoritative servers return SERVFAIL when they have no valid zone data and REFUSED for zones they do not serve; recursive servers return REFUSED to sources outside their access lists.",
      sourceId: "rfc9520",
      locator: "Sections 2, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, and 2.7",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "retry-limit",
      statement:
        "A resolver must not send a query to one server address over one transport more than three times in total before considering it unresponsive for that query. It may retry over another transport. Typical timeouts are expected to range from 3 to 30 seconds.",
      sourceId: "rfc9520",
      locator: "Section 3.1, Retries and Timeouts",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "resolv-conf-timeout-caveat",
      statement:
        "The resolv.conf `timeout` is the time to wait for one server before retrying via another, defaulting to 5 seconds; it may not be the total time of a resolver call. `attempts` is the number of times queries are sent to the servers, defaulting to 2.",
      sourceId: "man-resolv-conf",
      locator: "options timeout:n, attempts:n",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "gai-eai-again",
      statement:
        "getaddrinfo() returns EAI_AGAIN when the name server returned a temporary failure indication, and EAI_NONAME when the node is not known.",
      sourceId: "man-getaddrinfo",
      locator: "RETURN VALUE",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "dig-transport-options",
      statement:
        "dig uses UDP by default and retries over TCP when a reply is truncated; `+ignore` prevents the retry and `+tcp` forces TCP. `+bufsize` sets the EDNS UDP size. `+tls` uses DoT on port 853, `+tls-ca` enables certificate validation, and `+https` uses DoH with POST on port 443 and the /dns-query path. `+cd` asks the resolver not to validate DNSSEC. The default timeout is 5 seconds and the default number of tries is 3.",
      sourceId: "bind-dig",
      locator:
        "dig query options +tcp, +ignore, +bufsize, +tls, +tls-ca, +https, +cdflag, +timeout, +tries",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    { sourceId: "rfc1035", note: "Sections 4.1.1, 4.2.1, and 4.2.2: RCODEs, UDP, and TCP." },
    { sourceId: "rfc2181", note: "Section 9: the TC bit." },
    { sourceId: "rfc7766", note: "Sections 1, 4, 5, 6.2.1, 6.2.1.1, and 7: DNS over TCP." },
    { sourceId: "rfc6891", note: "Sections 4.3 and 6.2: EDNS(0) payload size." },
    { sourceId: "rfc7858", note: "Sections 1, 3.1, 3.3, and 4: DNS over TLS." },
    { sourceId: "rfc8484", note: "Sections 1, 3, 4, 5.1, 5.2, and 8.1: DNS over HTTPS." },
    { sourceId: "rfc9499", note: "Section 7: split DNS and views." },
    { sourceId: "rfc9520", note: "Sections 2 and 3.1: failure conditions and retry limits." },
    { sourceId: "man-resolv-conf", note: "timeout and attempts." },
    { sourceId: "man-getaddrinfo", note: "EAI_AGAIN and EAI_NONAME." },
    { sourceId: "bind-dig", note: "Transport, EDNS, DNSSEC, and retry options." },
  ],
} satisfies LessonBody;
