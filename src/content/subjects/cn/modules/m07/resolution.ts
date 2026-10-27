import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m07.resolution",
  status: "complete",
  objectives: [
    "Separate the stub resolver in the host from the recursive resolver it asks, and say which one walks the hierarchy.",
    "Contrast recursive and iterative resolution, and read the RD and RA bits that negotiate them.",
    "Trace a cold lookup from `getaddrinfo()` to an authoritative answer, including a CNAME restart.",
    "Name the Linux files and options that shape a lookup: `nsswitch.conf`, `/etc/hosts`, `resolv.conf`, the search list, `ndots`, `timeout`, and `attempts`.",
  ],
  prerequisites: ["m07.hierarchy"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "The previous lesson showed that an answer may be three or more referrals away from the root. Following referrals means keeping state, retrying unresponsive servers, chasing aliases, bounding the work when someone has misconfigured a zone, and caching what was learned. Every program on every host needs names resolved, and few of them should carry all of that logic.",
      whyItMatters:
        "When a lookup is slow or fails, you need to know which machine did the work and which one failed. A 5-second stall before a connection, a name that resolves on one host but not another, and a container that cannot resolve an internal name are usually caused by the stub's configuration, the recursive resolver, or the authoritative servers, and each is checked differently.",
      idea: "Split the job in two. The stub resolver in each host does almost nothing: it sends one recursive query, with RD set, to a configured recursive resolver and waits for a final answer. The recursive resolver does the iterative work for many clients. It sends non-recursive queries, follows referrals and CNAMEs down from the root, and caches everything it learns so the next client gets it immediately.",
      howItWorks:
        "On Linux, a program calls `getaddrinfo()`. glibc reads the `hosts:` line of `/etc/nsswitch.conf` and tries each source in order, typically `files` (meaning `/etc/hosts`) and then `dns`. For `dns`, it sends a query with RD=1 to the first `nameserver` in `/etc/resolv.conf`, waits `timeout` seconds, tries the next server, and repeats the round `attempts` times. The recursive resolver checks its cache. On a miss it finds the closest enclosing zone whose servers it knows, working up toward the root. It asks one of those servers non-recursively, caches any referral, and asks the referred servers, until a response answers the question or reports a name error. If the answer is a CNAME, it restarts at the canonical name. It returns the full chain to the stub with RA=1.",
      whyItWorks:
        "Recursive service concentrates the cache. Thousands of clients share one resolver, so popular names and delegations are almost always cached, and the root and TLD servers see a tiny fraction of lookups. Stubs stay simple: a list of up to three addresses, a timeout, and a retry count. Iterative service keeps authoritative servers cheap, because each answers only from its own data and never acts on a client's behalf.",
      limits:
        "The stub trusts whatever its configured resolver returns, and the client's own TCP connection goes to that answer. A stub cannot tell a slow resolver from a dead one, so a dead first `nameserver` costs every lookup a full `timeout`, 5 seconds by default. The answer a client gets depends on which resolver it asked: corporate, VPN, cloud, and public resolvers can return different answers for the same name. And the recursive resolver's cache means an authoritative change is seen only when that resolver's copy expires.",
      connections:
        "The hierarchy lesson supplied the referrals followed here. The records lesson explains the CNAME restart in the example. The caching lesson covers what the recursive resolver remembers and for how long, and the transport lesson covers how these queries travel and how they fail. Module 11's socket API starts every client connection with `getaddrinfo()`.",
    },
    {
      type: "comparison",
      id: "who-does-what",
      title: "The resolvers and servers in one lookup",
      columns: ["Where it runs", "What it sends", "What it keeps"],
      rows: [
        {
          label: "Stub resolver",
          cells: [
            "In each host, as part of the C library or a local daemon",
            "Recursive queries (RD=1) to the servers in `resolv.conf`",
            "Usually no cache in glibc itself",
          ],
        },
        {
          label: "Recursive resolver",
          cells: [
            "A shared server: on the LAN, in the cloud VPC, at the ISP, or public",
            "Non-recursive queries to authoritative servers; recursive answers to stubs",
            "A cache of answers, delegations, and negative results, each with a TTL",
          ],
        },
        {
          label: "Forwarder",
          cells: [
            "Between stubs and a recursive resolver, such as a home router",
            "The stub's query, passed upstream with RD=1",
            "Often a cache of its own",
          ],
        },
        {
          label: "Authoritative server",
          cells: [
            "Run by the zone's owner or its DNS provider",
            "Answers from its zones, referrals, or name errors; never recursion",
            "Its zone data, not other zones' answers",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "cold-lookup",
      title: "A cold lookup of `shop.example.com`, from `getaddrinfo()` to the answer",
      setup:
        "A Linux host has `hosts: files dns` in `/etc/nsswitch.conf`, `nameserver 10.0.0.2` in `/etc/resolv.conf`, and no entry for the name in `/etc/hosts`. The recursive resolver at 10.0.0.2 has just restarted, so its cache holds only the root servers. In the zones, `shop.example.com` is a CNAME for `shop.cdn.example.net` with TTL 300, and `shop.cdn.example.net` has an A record, 203.0.113.10, with TTL 60. Every delegation has TTL 172800. Names and addresses are illustrative. The IPv6 query that glibc sends alongside the A query takes the same path and is left out.",
      walkthrough: [
        'Stub. The program calls `getaddrinfo("shop.example.com", "443", ...)`. glibc checks `/etc/hosts` and finds nothing. The name has two dots, at least `ndots:1`, so it is tried as an absolute name first, with no search-list suffix. glibc sends `shop.example.com A` with RD=1 over UDP to 10.0.0.2:53 and starts a 5-second timer.',
        "Recursive resolver, query 1. Cache miss. The closest zone it knows servers for is the root. It sends `shop.example.com A` with RD=0 to a root server. The answer is a referral: NS records for `com` and their addresses as glue. It caches them for 172,800 seconds.",
        "Query 2. It asks a `com` server the same question. The answer is a referral to `ns1.example.com` and `ns2.example.com`, with glue, because those names are inside `example.com`. Cached.",
        "Query 3. It asks `ns1.example.com`. The response has AA set and its answer section holds `shop.example.com. 300 IN CNAME shop.cdn.example.net.`, but no A record. The resolver caches the CNAME and restarts the search at `shop.cdn.example.net`.",
        "Query 4. For `shop.cdn.example.net` the closest known zone is the root again, because nothing under `net` is cached. A root server refers it to the `net` servers.",
        "Query 5. A `net` server refers it to `example.net`'s servers, say `ns.dnshost.example.org`. That name is outside `example.net`, so there is no glue. The resolver must resolve the server's own name first, through the cached `org` path, before it can continue. That is extra queries not counted here.",
        "Query 6. `example.net`'s server answers with AA set: `shop.cdn.example.net. 60 IN A 203.0.113.10`. The resolver caches it.",
        "Back to the stub. The resolver returns one response with RA=1: the CNAME and the A record in the answer section. glibc builds a `sockaddr_in` for 203.0.113.10 port 443, and the program calls `connect()`. The stub has done one exchange. The resolver did at least six.",
        "A second host asks for `shop.example.com` 30 seconds later. Both records are in the cache, so the resolver answers at once and sends nothing upstream. It reports the remaining TTLs, 270 and 30.",
        "Another client asks 2 minutes later. The CNAME is still cached, with 180 seconds left, but the A record has expired. The `example.net` delegation is still cached, so the resolver sends one query straight to `example.net`'s server and skips the root and `net`.",
      ],
      result:
        "The stub always makes one recursive request. The recursive resolver turns it into iterative queries, and the number depends entirely on what is cached: six or more on a cold cache, zero for a cached name, and one when only the leaf record has expired. A CNAME into another domain restarts the walk and can double the cold cost. Out-of-zone name server names without glue add a side lookup.",
    },
    {
      type: "steps",
      id: "recursion-bits",
      title: "How recursion is negotiated",
      steps: [
        {
          title: "RD in the query",
          body: "Recursion Desired. The stub sets it to ask the server to resolve the name fully. It is copied into the response. `dig` sets it by default, and `+norecurse` clears it.",
        },
        {
          title: "RA in the response",
          body: "Recursion Available. The server sets it in every response if it is willing to recurse for this client, whether or not RD was set. A response with both RD and RA set was resolved recursively.",
        },
        {
          title: "Non-recursive queries",
          body: "Every name server must answer them from local data only: the answer, a referral to servers closer to the name, or an error. This is how a recursive resolver talks to authoritative servers, and how `dig +trace` walks the tree itself.",
        },
        {
          title: "Recursion is optional and may be restricted",
          body: "An authoritative server normally does not offer recursion, and a recursive resolver may refuse clients outside its network, typically with REFUSED. An open resolver accepts nearly any client.",
        },
      ],
    },
    {
      type: "steps",
      id: "linux-stub",
      title: "What the Linux stub does before any packet leaves",
      steps: [
        {
          title: "Pick sources with nsswitch.conf",
          body: "The `hosts:` line lists sources in order. With `files dns`, `/etc/hosts` wins over DNS. A stale `/etc/hosts` entry is a common reason a host disagrees with `dig`, because `dig` reads only `resolv.conf` and ignores `nsswitch.conf` and `/etc/hosts`.",
        },
        {
          title: "Expand the name with search and ndots",
          body: "A name with fewer than `ndots` dots, 1 by default, is tried with each `search` domain appended before being tried as typed. With `ndots:5`, `api.example.com` has two dots, so it is first tried as `api.example.com.corp.internal` and so on, and each miss costs a query.",
        },
        {
          title: "Ask the name servers in order",
          body: "Up to three `nameserver` lines are used, in the order listed unless `options rotate` is set. Each try waits `timeout` seconds, 5 by default, before moving to the next server, and the whole list is tried `attempts` times, 2 by default.",
        },
        {
          title: "Send A and AAAA together",
          body: "Since glibc 2.9, `getaddrinfo()` sends the IPv4 and IPv6 queries in parallel. `options single-request` makes them sequential for servers that mishandle two queries from one socket.",
        },
        {
          title: "Map the outcome to an error",
          body: "`getaddrinfo()` returns `EAI_NONAME` when the name is unknown, `EAI_NODATA` when the host exists but has no addresses, `EAI_AGAIN` for a temporary failure such as a timeout or SERVFAIL, and `EAI_FAIL` for a permanent failure. Retrying makes sense only for `EAI_AGAIN`.",
        },
      ],
    },
    {
      type: "callout",
      id: "dig-is-not-getaddrinfo",
      tone: "caution",
      title: "`dig` is not what your program does",
      body: "`dig` sends exactly the query you give it to the server you name, or to the first one in `resolv.conf`. It does not read `/etc/hosts` or `nsswitch.conf` and does not apply the search list unless you pass `+search`. To see what an application on Linux gets, use `getent ahosts <name>`, which goes through the same NSS path as `getaddrinfo()`, and use `dig` to ask a specific server a specific question.",
      scope: "linux",
    },
    {
      type: "expandable",
      id: "resolver-details",
      title: "What a careful recursive resolver also does",
      paragraphs: [
        "RFC 1034 lists the resolver designer's priorities in order. Bound the work, so that a request cannot loop or start a chain reaction even if someone has misconfigured data. Get an answer if at all possible. Avoid unnecessary transmissions. Get the answer quickly.",
        "Before accepting a referral, the resolver checks that it is closer to the name than the servers it already asked. A referral back up the tree is bogus and is ignored. It also matches each response to its query by the 16-bit ID and should be highly paranoid about the rest of the response.",
        "It sorts a zone's servers by past response time and rotates across their addresses, so one slow or dead server does not delay every query. Too aggressive a retransmission policy slows everyone using the same server.",
        "A forwarder sits between stubs and a recursive resolver. Home routers and some local daemons forward with RD set and cache the answers. A stub on such a network may see the router's address in `resolv.conf`, not the ISP's resolver.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "who-walks",
          prompt:
            "In a normal lookup from a laptop, which machine sends a query to a root server, and which flags distinguish that query from the laptop's?",
          answer:
            "The recursive resolver, not the laptop. The laptop's stub sends one query with RD=1 and gets back an answer with RA=1. The recursive resolver sends queries with RD=0 to root, TLD, and authoritative servers and follows their referrals. It contacts a root server only when it has no cached delegation closer to the name.",
        },
        {
          id: "five-second-stall",
          prompt:
            "Every new outbound connection from a service takes just over 5 seconds, then works. `resolv.conf` lists two name servers. What is the likely cause, and how do you confirm it?",
          answer:
            "The first `nameserver` is not answering. The stub waits the default `timeout` of 5 seconds before trying the second, which answers. Confirm with `dig @<first> <name>`, which times out, and `dig @<second> <name>`, which answers at once. Remove or fix the dead server. A shorter `options timeout:` only shrinks the stall.",
        },
        {
          id: "ndots-cost",
          prompt:
            "A container has `search ns.svc.cluster.local svc.cluster.local cluster.local` and `options ndots:5`. It connects to `api.example.com`. How many DNS queries might it send before the real one, and how do you avoid them?",
          answer:
            "`api.example.com` has two dots, fewer than five, so the stub first tries it with each of the three search suffixes, and each gets NXDOMAIN. Then it tries the name as typed. With parallel A and AAAA queries, that is six wasted queries before the two that matter. Write the name with a trailing dot, `api.example.com.`, so it is absolute and skips the search list, or lower `ndots` for that workload.",
        },
        {
          id: "hosts-file-wins",
          prompt:
            "`dig app.internal` returns 10.1.2.3, but `curl http://app.internal/` connects to 10.9.9.9. Neither result changes when you retry. Where does 10.9.9.9 come from?",
          answer:
            "Most likely `/etc/hosts`, or another NSS source listed before `dns` in `nsswitch.conf`. `curl` resolves through `getaddrinfo()`, which consults those sources first. `dig` asks the DNS server directly and skips them. `getent ahosts app.internal` shows what the NSS path returns. Then check `/etc/hosts`.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "resolver-role",
      statement:
        "Resolvers are programs that extract information from name servers for client requests. A resolver must be able to answer from a name server directly or pursue the query through referrals. To the user, DNS is reached through a simple procedure or OS call to a local resolver.",
      sourceId: "rfc1034",
      locator: "Section 2.4, Elements of the DNS",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "recursive-vs-nonrecursive",
      statement:
        "All name servers must implement non-recursive queries, answering from local information with an error, the answer, or a referral to servers closer to the answer. Recursive service is optional, may be restricted to some clients, and returns an answer or an error but never a referral. Recursive mode helps simple requesters and concentrates a cache for a network.",
      sourceId: "rfc1034",
      locator: "Section 4.3.1, Queries and responses",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rd-ra-bits",
      statement:
        "RA is set by a server in all responses when it is willing to provide recursion, regardless of whether it was requested. RD in a query asks for recursion and is copied into the response. A client can verify recursion was used by checking that both RA and RD are set in the reply.",
      sourceId: "rfc1034",
      locator: "Section 4.3.1, Queries and responses",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "stub-resolver",
      statement:
        "A stub resolver moves the resolution function into a name server that supports recursive queries. It needs only a list of name server addresses, typically from a configuration file. A stub has difficulty tuning retransmission for both lost UDP packets and dead servers.",
      sourceId: "rfc1034",
      locator: "Section 5.3.1, Stub resolvers",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "resolver-algorithm",
      statement:
        "The resolver algorithm checks local information, finds the best servers by looking for NS records at the name and then each ancestor toward the root, queries them until one responds, and then caches and returns an answer or name error, caches a better delegation and repeats, or caches a CNAME and restarts at the canonical name. A delegation that is not closer to the name than the current servers is bogus and should be ignored.",
      sourceId: "rfc1034",
      locator: "Section 5.3.3, Algorithm",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "resolver-priorities",
      statement:
        "Resolver designers should, in order: bound the work so a request cannot loop or cause a chain reaction even with misconfigured data, get back an answer if possible, avoid unnecessary transmissions, and get the answer quickly. Too aggressive a retransmission policy slows responses. Responses should be matched to queries by the ID field.",
      sourceId: "rfc1034",
      locator: "Section 5.3.3, Algorithm (steps 2 to 4)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cname-restart",
      statement:
        "When a name server finds a CNAME at the queried name and the query type is not CNAME, it includes the CNAME in the response and restarts the query at the canonical name.",
      sourceId: "rfc1034",
      locator: "Section 3.6.2, Aliases and canonical names",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "terminology-modes",
      statement:
        "A stub resolver cannot perform all resolution itself and depends on a recursive resolver. In iterative resolution the client repeatedly makes non-recursive queries and follows referrals and aliases. A recursive resolver is generally expected to cache. A forwarder in current use often stands between stub resolvers and recursive servers. An open resolver accepts queries from nearly any client.",
      sourceId: "rfc9499",
      locator: "Section 6, DNS Servers and Clients",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "nsswitch-hosts",
      statement:
        "The hosts database in /etc/nsswitch.conf lists the sources glibc consults for host names, in order. The `files` source for hosts reads /etc/hosts, and the `dns` source is implemented by libnss_dns.",
      sourceId: "man-nsswitch-conf",
      locator: "DESCRIPTION; FILES",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "resolv-conf-servers",
      statement:
        "resolv.conf lists up to MAXNS, currently 3, name servers, queried in order: on a timeout the next is tried, and the list is repeated until the retry limit. `timeout` defaults to 5 seconds and `attempts` to 2. `rotate` selects servers round-robin.",
      sourceId: "man-resolv-conf",
      locator: "nameserver; options timeout:n, attempts:n, rotate",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "resolv-conf-search",
      statement:
        "A name with fewer dots than `ndots`, default 1, is tried with each search-list domain in turn. With one or more dots at the default, the name is tried first as an absolute name. `ndots` is capped at 15.",
      sourceId: "man-resolv-conf",
      locator: "search; options ndots:n",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "resolv-conf-parallel",
      statement:
        "Since glibc 2.9, IPv4 and IPv6 lookups are performed in parallel. `single-request` makes them sequential for servers that cannot handle the parallel queries.",
      sourceId: "man-resolv-conf",
      locator: "options single-request",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "gai-errors",
      statement:
        "getaddrinfo() returns EAI_AGAIN when the name server returned a temporary failure, EAI_FAIL for a permanent failure, EAI_NODATA when the host exists but has no network addresses, and EAI_NONAME when the node or service is not known.",
      sourceId: "man-getaddrinfo",
      locator: "RETURN VALUE",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "getent-ahosts",
      statement:
        "`getent ahosts <key>` passes each key to getaddrinfo(3) with AF_UNSPEC and prints each socket address returned.",
      sourceId: "man-getent",
      locator: "DESCRIPTION, database ahosts",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "refused-acl",
      statement:
        "Recursive servers generally return REFUSED to query sources that do not match their access control lists. Authoritative servers generally return REFUSED for zones they do not serve.",
      sourceId: "rfc9520",
      locator: "Section 2.2, REFUSED Responses",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "dig-defaults",
      statement:
        "Without @server, dig queries the servers in /etc/resolv.conf. It does not use the search list unless +search is given. +trace makes iterative queries from the root, following referrals and showing the answer from each server.",
      sourceId: "bind-dig",
      locator: "dig: Description; Simple Usage; +[no]search; +[no]trace",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    { sourceId: "rfc1034", note: "Sections 2.4, 3.6.2, 4.3.1, 5.3.1, and 5.3.3." },
    {
      sourceId: "rfc9499",
      note: "Section 6: stub, recursive, iterative, forwarder, open resolver.",
    },
    { sourceId: "man-nsswitch-conf", note: "The hosts database and its sources." },
    {
      sourceId: "man-resolv-conf",
      note: "nameserver, search, ndots, timeout, attempts, rotate, single-request.",
    },
    { sourceId: "man-getaddrinfo", note: "Error codes." },
    { sourceId: "man-getent", note: "getent ahosts goes through getaddrinfo()." },
    { sourceId: "rfc9520", note: "Section 2.2: who returns REFUSED." },
    { sourceId: "bind-dig", note: "Default server, +search, and +trace." },
  ],
} satisfies LessonBody;
