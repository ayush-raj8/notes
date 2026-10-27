import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m07.caching",
  status: "complete",
  objectives: [
    "Explain what a TTL permits a cache to do, who sets it, and how the remaining TTL counts down.",
    "Separate NXDOMAIN from NODATA, and say how long each is cached and where that duration comes from.",
    "Explain why 'DNS propagation' is really independent caches expiring, and plan a record change around it.",
    "Name the limits resolvers put on caching: TTL caps, serve-stale, and caching of resolution failures.",
  ],
  prerequisites: ["m07.records"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "A cold lookup costs several round trips to servers that may be on other continents, and the root and TLD servers sit on the path of every one. If every connection paid that cost, lookups would add hundreds of milliseconds and the top of the tree would carry every query on the Internet. But the data behind a name does change, and the zone owner needs a way to make a change take effect.",
      whyItMatters:
        "Caching decides how fast your changes take effect and how long a mistake lasts. A migration, a failover to another region, or a rollback of a bad record is visible to a client only when the copy in its resolver expires. A name created a few minutes after someone looked it up can stay 'nonexistent' for an hour. Planning a DNS change is planning around caches you do not control.",
      idea: "Let the source say how long its data may be reused. Every record carries a TTL, set by the zone's administrator, that permits any cache to keep and reuse it for that many seconds. Negative answers carry a TTL too, taken from the zone's SOA record. Nothing is pushed to caches. A change is seen by each cache when its own copy expires and it asks again.",
      howItWorks:
        "A recursive resolver stores each RRset it receives with its TTL and counts the TTL down. Until it reaches zero, the resolver answers from cache and reports the remaining TTL, so a downstream cache cannot keep the data longer than the original. A name error (NXDOMAIN) is cached for the query name and class, and a no-data answer (NODATA) for the name, type, and class. The authoritative server includes the zone's SOA in a negative answer, with a TTL equal to the smaller of the SOA's own TTL and its MINIMUM field, and that is how long the negative answer may be cached. Resolvers may cap long TTLs, commonly at about a week, and also cache resolution failures such as SERVFAIL for at most five minutes.",
      whyItWorks:
        "Most lookups are for names someone else asked about recently, so a shared resolver answers most of them from memory, and the delegations for `com` and the root are almost never re-fetched. The source of the data, not every cache operator, decides the trade between freshness and load, which is a stated design goal of DNS. Counting TTLs down end to end bounds how stale any copy can be: no cache can hold data longer than the TTL the authoritative server gave, unless it chooses to serve stale data because the source cannot be reached.",
      limits:
        "Clients see changes late, by up to the old TTL, and there is no way to revoke a cached record early from the authoritative side. A cache that fetched just before a change keeps the old data for a full TTL even if you lower the TTL afterward. Different resolvers fetched at different times, so for a while different users see different answers. Resolvers may cap a TTL, keep serving expired data when the source is down, and forwarders, local daemons, and applications may add caches of their own. A TTL is a maximum, not a promise that anyone will ask again on time.",
      connections:
        "The resolution lesson showed one resolver answering a second client from cache. The records lesson explained why a whole RRset shares one TTL. The transport lesson covers what happens when the resolver cannot reach a server at all, which is where serve-stale and failure caching apply. Module 13's investigation method checks the resolver's cache before blaming the network.",
    },
    {
      type: "steps",
      id: "ttl-mechanics",
      title: "What a cache does with a TTL",
      steps: [
        {
          title: "The zone owner sets the TTL",
          body: "The TTL is assigned in the zone where the data originates. It is an unsigned number of seconds, from 0 to 2^31−1. Zero means the record may be used for the transaction in progress and should not be cached.",
        },
        {
          title: "The cache counts it down",
          body: "A resolver keeps the record and, when it answers from cache, reports the TTL that remains. Two `dig` queries to the same home resolver 4 seconds apart, in October 2026, showed `www.wikipedia.org. 31392 IN CNAME` and then `31388`. Run `dig` twice yourself and watch the number fall.",
        },
        {
          title: "At zero, the cache asks again",
          body: "Once the remaining TTL reaches zero, the record must not be used, and the next client's query goes upstream. A popular name is re-fetched about once per TTL per resolver, no matter how many clients use it.",
        },
        {
          title: "Caches are careful about what they keep",
          body: "A resolver caches a whole RRset or none of it, and does not cache a set from a truncated response. It does not combine cached records with new ones for the same set, and authoritative data in an answer section replaces what it had. It discards unsolicited records and data it did not ask for.",
        },
        {
          title: "Resolvers may shorten or stretch a TTL",
          body: "Any implementation may cap a TTL and treat larger values as the cap. RFC 8767 recommends a cap of 7 days. It also lets a resolver keep answering with expired data, with a TTL such as 30 seconds, when it cannot reach the authoritative servers to refresh it.",
        },
      ],
    },
    {
      type: "comparison",
      id: "what-gets-cached",
      title: "What a resolver caches, and for how long",
      columns: ["Recognized by", "Cached against", "For how long"],
      rows: [
        {
          label: "Positive answer",
          cells: [
            "NOERROR with the requested records in the answer section",
            "The name, class, and type of each RRset",
            "The record's TTL, counted down, possibly capped by the resolver",
          ],
        },
        {
          label: "NXDOMAIN",
          cells: [
            "RCODE Name Error, usually with the zone's SOA in the authority section",
            "The query name and class: no type of record exists there",
            "The smaller of the SOA's TTL and its MINIMUM field",
          ],
        },
        {
          label: "NODATA",
          cells: [
            "NOERROR, no relevant answer, and an SOA in the authority section rather than a referral's NS records",
            "The query name, type, and class: the name exists, this type does not",
            "The same SOA-derived TTL",
          ],
        },
        {
          label: "Resolution failure",
          cells: [
            "SERVFAIL, timeouts, or unreachable servers",
            "Implementation-specific: the query, or the server address",
            "At least 1 second and at most 5 minutes, with backoff for repeated failures",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "planned-migration",
      title: "Moving `api.example.com` to a new address without a long outage",
      setup:
        "`api.example.com` has `A 192.0.2.10` with TTL 3600. The service moves to 198.51.100.20 at 14:00. The zone's SOA has TTL 3600 and MINIMUM 3600. A second name, `api-v2.example.com`, does not exist yet and will be created at 13:40. Times are one afternoon; names and addresses are documentation values.",
      walkthrough: [
        "12:50. Lower the TTL of `api.example.com A` from 3600 to 60, without changing the address. This must happen at least one old TTL before the change. A resolver that fetched the record at 12:49 holds the 3600-second copy until 13:49, and lowering the TTL now does not reach that copy.",
        "13:50. Every cached copy of the 3600-second record has now expired. Any resolver that asked since 12:50 holds the 60-second version, so no copy anywhere is more than 60 seconds from expiry.",
        "14:00. Change the A record to 198.51.100.20. If the zone has secondary servers fed by zone transfer, check that each one serves the new SOA serial with `dig +norecurse @<server> example.com SOA`, because a resolver may ask any of them.",
        "14:00 to 14:01. Resolvers whose 60-second copy has not expired still return 192.0.2.10. By 14:01 every resolver that respects TTLs has asked again and has the new address. Keep the old server answering for a while longer, because a resolver that serves stale data, a forwarder, or an application holding the address in its own cache can lag behind.",
        "Meanwhile, a deploy script ran `curl https://api-v2.example.com/health` at 13:30, before the name existed. The company resolver got NXDOMAIN with the SOA in the authority section, TTL min(3600, 3600) = 3600, and cached that the name does not exist.",
        "13:40. The team creates `api-v2.example.com`. Queries through other resolvers find it at once. Through the company resolver, the name stays nonexistent until 14:30, when the negative entry expires. Nothing propagates. One cache is holding a correct answer from 13:30 for as long as the zone allowed.",
        "After the migration is stable, raise the TTL of `api.example.com` back to 3600 so that resolvers stop re-fetching it every minute.",
      ],
      result:
        "The worst-case delay for a change is the TTL that caches held before the change, so lower it at least one old TTL in advance and raise it afterward. A name that was looked up before it existed stays missing for the negative TTL, which the zone's SOA sets. Plan migrations with both numbers, and keep the old target working past the last expiry.",
    },
    {
      type: "why",
      id: "why-caching",
      questionId: "why.22",
    },
    {
      type: "callout",
      id: "propagation-myth",
      tone: "caution",
      title: "There is no propagation to wait for",
      body: "'Wait 24 to 48 hours for DNS to propagate' describes caches, not a push. The authoritative servers have the new data as soon as the zone is updated, and each recursive resolver gets it when its own copy expires and it asks again. How long that takes is set by the TTL that was published before the change, and by the negative TTL if someone asked for a name before it existed. A delegation change at the registrar is the same thing one level up: resolvers follow the parent's NS records until their cached copy, often with a two-day TTL, runs out.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "caching-details",
      title: "Serve-stale, failure caching, secondaries, and caches you did not expect",
      paragraphs: [
        "Serve-stale. RFC 8767 lets a resolver answer with expired data when the authoritative servers cannot be reached. Its example method waits about 1.8 seconds for a fresh answer before falling back to stale data, retries a failing server no more than every 30 seconds, and keeps expired records for 1 to 3 days. An authoritative NOERROR or NXDOMAIN response with AA set counts as a refresh. Any other response code leaves the old data in place. So an outage of your authoritative servers may be hidden from some users and not others.",
        "Failure caching. RFC 9520 requires resolvers to remember resolution failures, such as SERVFAIL or unresponsive servers, for at least 1 second and at most 5 minutes, and to back off for failures that persist. While the entry lasts, the resolver sends nothing upstream for that query. After you fix a broken zone, some clients can keep failing for up to five minutes.",
        "Negative caching without an SOA is unsafe, because the TTL cannot be carried, and two misconfigured servers could hand a negative answer back and forth forever. RFC 2308 says such answers should not be cached, and that resolvers should cap negative caching at one to three hours. A server answering a negative from its cache must include the SOA with its TTL decremented. Not every forwarder does: in October 2026, a home router's second NXDOMAIN answer for the same name came back with an empty authority section.",
        "Authoritative servers can disagree for a short time too. In the classic primary and secondary setup, a secondary polls the primary's SOA every REFRESH seconds and transfers the zone when the serial has increased. A resolver that happens to ask a lagging secondary caches the old data for a full TTL. Hosted DNS services usually update their servers much faster, but checking the serial on each server is how you know.",
        "More caches sit between the resolver and your code. A home router or local caching daemon may cache. Some language runtimes and HTTP client libraries cache resolved addresses themselves, and a long-lived connection pool keeps using the address it connected to regardless of DNS. A DNS change affects new lookups and new connections only.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "lower-ttl-timing",
          prompt:
            "A record has TTL 86400. You lower it to 300 and change the address 10 minutes later. Some users keep reaching the old address for most of a day. Why?",
          answer:
            "Lowering the TTL affects only copies fetched after the change. A resolver that fetched the record just before you lowered it holds a copy with up to 86,400 seconds left, and it keeps answering with the old address until that expires. Lower the TTL at least one old TTL, here a full day, before changing the address.",
        },
        {
          id: "nxdomain-after-create",
          prompt:
            "You create `new.example.com`, and `dig @8.8.8.8 new.example.com` finds it, but the office resolver still returns NXDOMAIN 20 minutes later. The zone's SOA reads `... 2026102801 7200 3600 1209600 3600` with TTL 3600. What happened, and how long might it last?",
          answer:
            "Someone using the office resolver asked for the name before it existed. The negative answer was cached for the smaller of the SOA's TTL and its MINIMUM field, the last number, both 3600, so up to one hour from that query. The authoritative data is already correct. Either wait for the entry to expire, or flush that resolver's cache if you operate it. Check the SOA returned in the NXDOMAIN response to see the remaining time.",
        },
        {
          id: "nodata-vs-nxdomain",
          prompt:
            "`dig example.com AAAA` returns `status: NOERROR`, `ANSWER: 0`, and an SOA in the authority section. `dig nothere.example.com A` returns `status: NXDOMAIN` with the same SOA. What is each one saying, and what does each cache entry cover?",
          answer:
            "The first is NODATA: `example.com` exists but has no AAAA records. It is cached for that name, type, and class, so an A query for `example.com` is unaffected. The second is a name error: `nothere.example.com` has no records of any type. It is cached for the name and class, so it answers every type at that name. Both are cached for the SOA-derived negative TTL.",
        },
        {
          id: "ttl-zero",
          prompt:
            "An engineer sets every record's TTL to 0 so changes are instant. What do they gain, and what does it cost?",
          answer:
            "A TTL of 0 tells caches not to keep the record past the current transaction, so a change is seen on the next lookup by resolvers that honor it. The cost is that every lookup goes to the authoritative servers. Clients wait for that round trip each time, the authoritative servers take the whole query load, and if they are unreachable there is no cached copy to fall back on. A short positive TTL, such as 60 seconds, around planned changes gives most of the benefit.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ttl-meaning",
      statement:
        "The TTL is a time limit on how long an RR can be kept in a cache, assigned by the administrator of the zone where the data originates. A zero TTL prohibits caching. If a change can be anticipated, the TTL can be reduced before the change to minimize inconsistency and increased afterward.",
      sourceId: "rfc1034",
      locator: "Section 3.6, Resource Records",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "design-goal-source-controls",
      statement:
        "Where there are trade-offs between the cost of acquiring data, the speed of updates, and the accuracy of caches, the source of the data should control the trade-off.",
      sourceId: "rfc1034",
      locator: "Section 2.2, DNS design goals",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ttl-zero-transaction",
      statement:
        "A zero TTL means the RR can only be used for the transaction in progress and should not be cached.",
      sourceId: "rfc1035",
      locator: "Section 3.2.1, Format (TTL)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ttl-range-and-cap",
      statement:
        "A TTL is unsigned, from 0 to 2^31 − 1. Implementations are free to place an upper bound on any TTL received. The TTL is a maximum time to live, not a mandatory one.",
      sourceId: "rfc2181",
      locator: "Section 8, Time to Live (TTL)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "cache-rules",
      statement:
        "A resolver should cache all or none of the RRs of one type at a name, should not cache a possibly partial set from a truncated response, should never prefer cached data to authoritative data, should not combine cached and received data for the same set, and should discard unsolicited or unrequested data without caching it.",
      sourceId: "rfc1035",
      locator: "Section 7.4, Using the cache",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "negative-response-kinds",
      statement:
        "NXDOMAIN is the Name Error RCODE and means the query name does not exist. NODATA is inferred from NOERROR with no relevant answer, and is distinguished from a referral by an SOA in the authority section or the absence of NS records there.",
      sourceId: "rfc2308",
      locator: "Sections 1, 2.1, and 2.2",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "negative-soa-ttl",
      statement:
        "Authoritative servers must include the zone's SOA in the authority section of an NXDOMAIN or NODATA response. Its TTL is the minimum of the SOA's MINIMUM field and the SOA's own TTL, and indicates how long the negative answer may be cached. The MINIMUM field's defined meaning is now the negative-caching TTL.",
      sourceId: "rfc2308",
      locator: "Sections 3 and 4",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "negative-cache-keys",
      statement:
        "A negative TTL decrements like a normal cached answer and the entry must not be used at zero. NXDOMAIN is cached against <QNAME, QCLASS> and NODATA against <QNAME, QTYPE, QCLASS>. Negative responses without SOA records should not be cached. Resolvers should limit negative caching; one to three hours works well and over a day is problematic.",
      sourceId: "rfc2308",
      locator: "Section 5, Caching Negative Answers",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "negative-from-cache",
      statement:
        "A server answering from a cached negative response must add the cached SOA to the authority section with its TTL decremented by the time it was stored.",
      sourceId: "rfc2308",
      locator: "Section 6, Negative answers from the cache",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "ttl-amended-serve-stale",
      statement:
        "RFC 8767 amends the TTL definition: values should be capped on the order of days to weeks, with a recommended cap of 604,800 seconds, and a record that cannot be refreshed at expiry may be used as though unexpired. Stale records are returned with a TTL above 0, recommended 30 seconds. An authoritative NOERROR or NXDOMAIN response with AA set refreshes the data; other response codes leave previous state intact.",
      sourceId: "rfc8767",
      locator: "Section 4, Standards Action",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "serve-stale-timers",
      statement:
        "In RFC 8767's example method, a client response timer of about 1.8 seconds triggers stale answers, failing authorities are rechecked no more often than every 30 seconds, and expired records are kept for a suggested 1 to 3 days.",
      sourceId: "rfc8767",
      locator: "Section 5, Example Method",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "failure-caching",
      statement:
        "Resolvers must cache resolution failures for at least 1 second and must not cache them for longer than 5 minutes, and should back off for persistent failures. While a failure is cached, the resolver must not send the corresponding queries upstream.",
      sourceId: "rfc9520",
      locator: "Section 3.2, Caching",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "secondary-refresh",
      statement:
        "Secondary servers check the primary's SOA serial after REFRESH seconds, retry every RETRY seconds if the check fails, and transfer the zone when the serial has advanced. The serial is advanced on every change.",
      sourceId: "rfc1034",
      locator: "Section 4.3.5, Zone maintenance and transfers",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "rfc1034", note: "Sections 2.2, 3.6, and 4.3.5: TTL meaning and zone refresh." },
    { sourceId: "rfc1035", note: "Sections 3.2.1 and 7.4: the TTL field and cache rules." },
    { sourceId: "rfc2181", note: "Section 8: TTL range and caps." },
    { sourceId: "rfc2308", note: "Sections 1 to 6: negative responses and their caching." },
    { sourceId: "rfc8767", note: "Sections 4 and 5: TTL cap and serve-stale." },
    { sourceId: "rfc9520", note: "Section 3.2: caching resolution failures." },
  ],
} satisfies LessonBody;
