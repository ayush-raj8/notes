import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m07.records",
  status: "complete",
  objectives: [
    "Say what each of A, AAAA, CNAME, NS, MX, and TXT holds, and which program asks for it.",
    "Explain the RRset: why a query returns every record of one name and type, with one TTL.",
    "Apply the CNAME rule, and explain why a zone apex cannot be a CNAME and an MX or NS target must not be one.",
    "Design the records for a small domain and check them with `dig`.",
  ],
  prerequisites: ["m07.resolution"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "One name has to answer several different questions. A browser wants an IPv4 or an IPv6 address for `example.com`. A mail server wants to know which hosts accept mail for it. A resolver wants to know which servers own the zone. A certificate authority or a SaaS vendor wants proof that you control the domain. One value per name cannot carry all of that.",
      whyItMatters:
        "Most DNS changes a service team makes are record edits: pointing a name at a load balancer, adding a CDN alias, moving mail, or adding a verification token. Picking the wrong type, or breaking one of the few rules between types, produces failures that look unrelated: mail that bounces, a zone that stops resolving, or an alias that a provider's console refuses to save.",
      idea: "Store typed records. Each resource record has an owner name, a type, a class, a TTL, and data whose format depends on the type. A query names one type, and the server returns every record of that name, class, and type together as one set. New questions get new types instead of new protocols.",
      howItWorks:
        "A holds a 32-bit IPv4 address and AAAA a 128-bit IPv6 address. A host with several addresses has several records. CNAME says the owner is an alias and names the canonical name, and resolvers restart the lookup there. NS names a server that should be authoritative for the zone starting at the owner. MX holds a preference and a mail exchanger's host name, lower preference first. TXT holds one or more strings whose meaning depends on where they are found. A query for one name and type returns the whole RRset, and the server adds addresses for NS and MX targets to the additional section, so the client often needs no second query.",
      whyItWorks:
        "Typing lets each client ask only the question it has, so a mail server and a browser can look up the same name and get different, correct answers. Returning the whole set at once, with one TTL, means a cache either has a complete answer or none, so it never serves half of a server pool. Pointer types such as CNAME, NS, and MX store names rather than addresses, so the addresses behind them can change in one place.",
      limits:
        "A CNAME cannot share its name with any other data, so it cannot sit at a zone apex, which must hold SOA and NS. An NS or MX target must not be an alias, because additional-section processing does not follow CNAMEs. A record type says what the data is, not whether it is true. TXT in particular is free-form, and only the protocol that reads it, such as SPF, gives it meaning. DNS records also carry no port or health information for A and AAAA: the client picks an address and finds out whether it works.",
      connections:
        "The resolution lesson showed the CNAME restart from the resolver's side. NS and the glue in the additional section are the delegation records from the hierarchy lesson. The caching lesson explains what the TTL on each record does. Module 3's addresses are what A and AAAA hold. Module 10 uses TXT records for domain validation when certificates are issued.",
    },
    {
      type: "comparison",
      id: "record-types",
      title: "The six record types",
      caption:
        "SOA and PTR are covered in the expandable section below. Many other types exist, such as SRV and HTTPS. They follow the same RRset rules.",
      columns: ["What the data holds", "Who asks for it", "Rule that bites"],
      rows: [
        {
          label: "A",
          cells: [
            "One IPv4 address, 32 bits",
            "Any client connecting over IPv4, through `getaddrinfo()`",
            "Several A records form one set. Clients choose among them, and DNS says nothing about which are healthy.",
          ],
        },
        {
          label: "AAAA",
          cells: [
            "One IPv6 address, 128 bits",
            "Clients that can use IPv6; glibc sends it alongside the A query",
            "A broken AAAA can make dual-stack clients slower or fail while IPv4-only clients work.",
          ],
        },
        {
          label: "CNAME",
          cells: [
            "The canonical name for which the owner is an alias",
            "Resolvers, which restart the lookup at the target",
            "No other data may exist at the alias. A zone apex cannot be one.",
          ],
        },
        {
          label: "NS",
          cells: [
            "The host name of a server authoritative for the zone at the owner",
            "Resolvers following referrals",
            "The target must not be an alias. The parent's copy is what sends resolvers to the zone.",
          ],
        },
        {
          label: "MX",
          cells: [
            "A 16-bit preference and the host name of a mail exchanger",
            "Mail servers sending to an address at the owner name",
            "The target must have address records and must not be an alias. Lower preference is tried first.",
          ],
        },
        {
          label: "TXT",
          cells: [
            "One or more character strings, each up to 255 octets",
            "Whatever protocol defines a meaning there: SPF checkers, domain-verification bots",
            "DNS assigns no meaning. Several TXT records at one name are one set, and a reader must pick out its own.",
          ],
        },
      ],
    },
    {
      type: "steps",
      id: "rrsets",
      title: "Records travel as sets",
      steps: [
        {
          title: "A set is name, class, and type",
          body: "All records with the same owner, class, and type form one resource record set, an RRset. Two A records for `api.example.com` are one RRset. Its MX records are another. Two records identical in all four fields, including the data, are duplicates and should be suppressed.",
        },
        {
          title: "A query returns the whole set",
          body: "Asking for `api.example.com A` returns every A record there. If the set does not fit in the response, the response must be marked truncated, so the client can retry over TCP rather than act on part of the set.",
        },
        {
          title: "One TTL per set",
          body: "All records in a set must have the same TTL. A cache would otherwise expire some members and keep others, and hand out a partial set that looks complete. A client that receives differing TTLs from an authoritative server treats them all as the lowest.",
        },
        {
          title: "Order is not meaningful",
          body: "The records in a set have no defined order, and servers and resolvers need not keep it. Do not build load balancing on the first A record always being used, because servers, caches, and client libraries are all free to reorder the set.",
        },
      ],
    },
    {
      type: "example",
      id: "designing-a-domain",
      title: "Records for a small company domain",
      setup:
        "A team owns `example.com`. The website is served by a load balancer at 192.0.2.10 and 2001:db8::10. `www` should go to a hosting provider whose servers are reached as `example.hosting.example.net`. Mail is handled by two hosts, `mx1.example.com` at 192.0.2.25 and `mx2.example.com` at 198.51.100.25. The zone is served by `ns1.example.com` and `ns.provider.example.net`. Names and addresses are documentation ranges. The real `example.com` is shown at the end for comparison.",
      walkthrough: [
        "The apex, `example.com.`, already holds the zone's SOA and its NS set. So the website goes here as address records: `example.com. 300 IN A 192.0.2.10` and `example.com. 300 IN AAAA 2001:db8::10`. A CNAME is not allowed here, because a CNAME's name may hold no other data, and the apex must hold SOA and NS.",
        "`www.example.com. 300 IN CNAME example.hosting.example.net.` is fine: `www` holds nothing else. A resolver asked for `www.example.com A` returns the CNAME and the provider's A records. If the provider renumbers, nothing in this zone changes.",
        "Mail: `example.com. 3600 IN MX 10 mx1.example.com.` and `example.com. 3600 IN MX 20 mx2.example.com.`. A sending mail server sorts by preference and tries `mx1` first, falling back to `mx2`. Both targets get A records of their own, not CNAMEs, so the server answering the MX query can put their addresses in the additional section.",
        'SPF: `example.com. 3600 IN TXT "v=spf1 mx -all"`. Receiving mail servers that check SPF fetch the TXT set, keep only the record starting `v=spf1`, and read it as: a sender whose IP address belongs to one of this domain\'s MX hosts passes, and any other sender fails. A verification token from a vendor is another TXT record in the same set, and does not interfere.',
        "NS at the apex: `ns1.example.com.` and `ns.provider.example.net.`. The registrar's `com` delegation must list the same two names, and `com` needs glue only for `ns1.example.com`, because it is inside the zone it serves.",
        "Check from a Linux shell: `dig +short example.com A`, `dig +short example.com MX`, `dig +short www.example.com A`, and `dig +short example.com TXT`. With `+short`, the `www` query prints the CNAME target on the first line and the provider's addresses after it.",
        'The real `example.com`, queried in October 2026, shows a domain that sends and accepts no mail: `example.com. 300 IN MX 0 .` and `example.com. TXT "v=spf1 -all"`. An MX whose exchange is the root, `.`, is a null MX. It says the domain accepts no mail, so senders fail at once instead of retrying. `-all` says no host may send mail as this domain. Its TXT set also held a second string that looked like a verification token.',
      ],
      result:
        "The record type follows from who is asking. Addresses for connecting go in A and AAAA, an alias that should follow another name's addresses goes in CNAME, anywhere except the apex, mail routing goes in MX, delegation goes in NS, and protocol-specific policy goes in TXT. Targets of NS and MX are names with their own address records, never aliases.",
    },
    {
      type: "callout",
      id: "apex-cname",
      tone: "caution",
      title: "You cannot CNAME the bare domain",
      body: "A provider asks you to point your domain at `lb-123.provider.example`. For `www` a CNAME works. For `example.com` itself it does not, because the apex holds SOA and NS and a CNAME's name may hold nothing else. Either use the provider's addresses in A and AAAA records, or use a DNS host's provider-specific feature that resolves the target itself and publishes the addresses. Those features go by names such as ALIAS, ANAME, or CNAME flattening, and are not standard DNS record types.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "more-records",
      title: "SOA, PTR, and what a record does not tell you",
      paragraphs: [
        "SOA, at the top of every zone, holds the primary server's name, a contact mailbox, a 32-bit serial number, and the REFRESH, RETRY, and EXPIRE timers that secondary servers use to poll the primary. Its last field, MINIMUM, now sets how long resolvers cache a negative answer from the zone, which the caching lesson uses.",
        "Reverse lookups use PTR records under special domains. The IPv4 address 10.2.0.52 is the name `52.0.2.10.in-addr.arpa.`, with the octets reversed so that delegation follows address blocks. IPv6 uses `ip6.arpa.`, one label per hexadecimal nibble, low-order first. `dig -x 10.2.0.52` builds the name for you. A PTR can say anything: it is controlled by whoever holds the address block, not whoever owns the forward name.",
        "A CNAME chain is legal but each hop is another lookup, and a CNAME into a different domain may need a full resolution of that domain. Keep chains short.",
        "Address records carry no port and no health. SRV and HTTPS records can carry ports and connection hints for protocols that look them up, but `getaddrinfo()` asks only for A and AAAA.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "which-type",
          prompt:
            "For each question, name the record type the asker queries: (a) a browser on an IPv6-only network wants to connect to `shop.example.com`; (b) a mail server has a message for `ops@example.com`; (c) a resolver needs to know who serves `example.com`; (d) a certificate authority wants a token you published to prove you control the domain.",
          answer:
            "(a) AAAA for `shop.example.com`, possibly reached through a CNAME. (b) MX for `example.com`, then A or AAAA for the chosen exchange. (c) NS for `example.com`, which it normally learns from the `com` referral. (d) Usually TXT at a name the CA specifies. The meaning of that TXT record comes from the CA's validation method, not from DNS.",
        },
        {
          id: "cname-conflict",
          prompt:
            "A teammate tries to add `api.example.com CNAME api.provider.example` but the DNS console refuses, saying a record already exists. `api.example.com` has a TXT verification record. Why does the console refuse, and what are the options?",
          answer:
            "A CNAME's owner name may hold no other data: for any name, either one CNAME exists or other records exist, not both. The TXT record at `api.example.com` blocks the CNAME. Move the verification record if the vendor allows another name, or publish the provider's A and AAAA records at `api` instead of a CNAME, accepting that you must update them if the provider renumbers.",
        },
        {
          id: "mx-to-cname",
          prompt:
            "`example.com MX 10 mail.example.com.`, and `mail.example.com` is a CNAME to `mailhost.provider.example`. Mail mostly arrives, but slowly, and one sender rejects it. What is wrong?",
          answer:
            "An MX target must not be an alias. It must be a name with address records. Additional-section processing does not follow CNAMEs, so the MX response carries no address, and each sender needs extra queries. RFC 5321 also puts an alias target outside the standard, so some senders refuse it. Point the MX directly at `mailhost.provider.example`, or give `mail.example.com` its own A and AAAA records.",
        },
        {
          id: "partial-pool",
          prompt:
            "A service has three A records. An engineer proposes giving one of them a 30-second TTL so it can be pulled quickly, and the other two 3600 seconds. Why is that not allowed, and what would go wrong if a cache accepted it?",
          answer:
            "All records in an RRset must have the same TTL. If they differed, a cache could expire the short one and keep serving the other two, a partial set with nothing marking it partial. Clients receiving differing TTLs from an authoritative server treat the whole set as having the lowest TTL. To make a pool member easy to remove, lower the TTL of the whole set before the change.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "rr-fields",
      statement:
        "A resource record has an owner name, a 16-bit type, a 16-bit class, a TTL used by resolvers to decide how long to cache it, and type-dependent RDATA. The order of records in a set is not significant and need not be preserved.",
      sourceId: "rfc1034",
      locator: "Section 3.6, Resource Records",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rrset-definition",
      statement:
        "Records with the same label, class, and type but different data form a Resource Record Set. Identical records should be suppressed. A query returns all records in the RRset, and the response must be marked truncated if the whole set does not fit.",
      sourceId: "rfc2181",
      locator: "Sections 5 and 5.1",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "rrset-ttl",
      statement:
        "The TTLs of all RRs in an RRset must be the same, because differing TTLs let a cache return a partial set without marking it truncated. A client receiving such a set from an authoritative source treats all of them as the lowest TTL.",
      sourceId: "rfc2181",
      locator: "Section 5.2, TTLs of RRs in an RRSet",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "a-record",
      statement:
        "An A record holds a 32-bit Internet address. Hosts with several addresses have several A records. A records cause no additional section processing.",
      sourceId: "rfc1035",
      locator: "Section 3.4.1, A RDATA format",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "aaaa-record",
      statement:
        "AAAA is type 28 and holds one 128-bit IPv6 address. A query returns all AAAA records at the name. Reverse lookups for IPv6 use IP6.ARPA, with one label per nibble, low-order first.",
      sourceId: "rfc3596",
      locator: "Sections 2.1, 2.2, 2.3, and 2.5",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cname-exclusive",
      statement:
        "An alias has exactly one canonical name. Apart from DNSSEC records, a name with a CNAME may have no other data: either one CNAME exists, or other records exist with no CNAME, or the name exists with no records, or it does not exist.",
      sourceId: "rfc2181",
      locator: "Section 10.1, CNAME resource records",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cname-no-other-data",
      statement:
        "A CNAME record identifies its owner as an alias. If a CNAME is present at a node, no other data should be present, so the alias and its canonical name cannot disagree and a cached CNAME can be used for any type.",
      sourceId: "rfc1034",
      locator: "Section 3.6.2, Aliases and canonical names",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "apex-has-soa-ns",
      statement: "NS and SOA records at a zone's origin are mandatory in every zone.",
      sourceId: "rfc2181",
      locator: "Section 6, Zone Cuts",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ns-mx-not-alias",
      statement:
        "The name in an NS record, or in an MX record, must not be an alias and must have address records. Additional section processing does not follow CNAMEs, so an alias there returns no address with the NS or MX answer and causes extra queries or failures.",
      sourceId: "rfc2181",
      locator: "Section 10.3, MX and NS records",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "mx-ns-rdata",
      statement:
        "MX RDATA is a 16-bit preference, lower preferred, and an exchange host name, and causes type A additional section processing. NS RDATA names a host that should be authoritative for the zone at the owner name, and causes additional section processing and, in referrals, a search for glue.",
      sourceId: "rfc1035",
      locator: "Sections 3.3.9 (MX) and 3.3.11 (NS)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "txt-and-strings",
      statement:
        "TXT RDATA is one or more character-strings, each a length octet followed by that many characters, and its meaning depends on the domain where it is found.",
      sourceId: "rfc1035",
      locator: "Section 3.3, Standard RRs (character-string); Section 3.3.14, TXT RDATA format",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "soa-fields",
      statement:
        "SOA RDATA holds MNAME, RNAME, a 32-bit SERIAL compared with sequence-space arithmetic, REFRESH, RETRY, EXPIRE, and MINIMUM.",
      sourceId: "rfc1035",
      locator: "Section 3.3.13, SOA RDATA format",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "in-addr-arpa",
      statement:
        "Reverse mapping for IPv4 uses the IN-ADDR.ARPA domain, with one label per octet in reverse order: 10.2.0.52 is 52.0.2.10.IN-ADDR.ARPA. The reversal lets zones be delegated along network boundaries.",
      sourceId: "rfc1035",
      locator: "Section 3.5, IN-ADDR.ARPA domain",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "smtp-mx",
      statement:
        "An SMTP client looks up MX records for the domain, follows a CNAME found there, treats a domain with no MX records as having an implicit MX of preference 0 pointing to the domain itself, and must not use the domain's address records when MX records exist. An MX target that returns a CNAME is outside the standard. MX preferences must be honored, lowest first.",
      sourceId: "rfc5321",
      locator: "Section 5.1, Locating the Target Host",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "null-mx",
      statement:
        "A domain that accepts no email publishes a single MX record with preference 0 and the exchange '.', a null MX. Such a domain must not publish any other MX record.",
      sourceId: "rfc7505",
      locator: "Section 3, MX Resource Records Specifying Null MX",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "spf-in-txt",
      statement:
        "SPF records must be published as DNS TXT records only. The 'mx' mechanism matches if the sender's IP is an address of one of the domain's MX hosts. The 'all' mechanism always matches, and the '-' qualifier gives a fail result, so '-all' at the end of a record fails every sender not matched earlier.",
      sourceId: "rfc7208",
      locator: "Section 3.1, DNS Resource Records; Section 4.6.2; Sections 5.1 and 5.4",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "dig-reverse-short",
      statement:
        "`dig -x addr` performs a reverse lookup, building the in-addr.arpa or ip6.arpa name and querying PTR. `+short` prints a terse answer.",
      sourceId: "bind-dig",
      locator: "dig: Options (-x); query option +[no]short",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    { sourceId: "rfc1034", note: "Sections 3.6 and 3.6.2: records and aliases." },
    {
      sourceId: "rfc1035",
      note: "Sections 3.3, 3.3.9, 3.3.11, 3.3.13, 3.3.14, 3.4.1, and 3.5: record formats.",
    },
    {
      sourceId: "rfc2181",
      note: "Sections 5, 5.1, 5.2, 6, 10.1, and 10.3: RRsets and naming rules.",
    },
    { sourceId: "rfc3596", note: "Section 2: AAAA and IP6.ARPA." },
    { sourceId: "rfc5321", note: "Section 5.1: how mail senders use MX." },
    { sourceId: "rfc7505", note: "Section 3: null MX." },
    {
      sourceId: "rfc7208",
      note: "Sections 3.1, 4.6.2, 5.1, and 5.4: SPF in TXT, 'mx', and '-all'.",
    },
    { sourceId: "bind-dig", note: "-x and +short." },
  ],
} satisfies LessonBody;
