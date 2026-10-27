import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m07.hierarchy",
  status: "complete",
  objectives: [
    "Explain why names are needed on top of IP addresses, and why one central table of them failed.",
    "Describe the name space as a tree of labels, divided into zones at delegation points.",
    "Say what the root, TLD, and authoritative servers each hold, and what each answers.",
    "Read a referral: NS records in the authority section, glue in the additional section, and no AA bit.",
  ],
  prerequisites: ["m06.quic-and-tradeoffs"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "Programs reach services by IP address, but addresses are the wrong thing to remember or configure. They follow the network's topology, so they change when a service moves to another provider, region, or load balancer, and one service often has many of them. Before DNS, every host on the Internet downloaded one HOSTS.TXT file from a central registry, and every change waited for that registry.",
      whyItMatters:
        "Every outbound connection a service makes, to a database, an API, or a package mirror, starts with a name lookup. Configuration, TLS certificates, and mail routing all refer to names. When the delegation for a name is wrong, the service behind it is unreachable even though every server is running, and the symptom usually looks like a network fault.",
      idea: "Make the name space a tree and hand each subtree to whoever owns it. A domain name is the path of labels from a node up to the root. The tree is cut into zones. A zone's owner runs the servers for it and can change its data, or delegate a subtree, without asking anyone. A parent knows only who serves each child, so any name can be found by walking down from the root.",
      howItWorks:
        "A zone is published by its authoritative servers, at least two of them. At its top node it holds NS records listing those servers and one SOA record. A delegation is a set of NS records in the parent zone at the child's name, naming the child's servers. Where a server's own name is inside the child zone, the parent also stores its address as glue, because otherwise you would need the child's servers to find the child's servers. A query to a server for a name below one of its cuts gets a referral: the child's NS records in the authority section and any glue in the additional section, with the AA bit clear. The root zone delegates top-level domains such as `com`, TLD zones delegate registered domains such as `example.com`, and that domain's authoritative servers answer with AA set.",
      whyItWorks:
        "Each server only has to know its own zones and the cuts at their edges, so the database is spread over many independent operators and grows without a central bottleneck. A change inside a zone is made once, on that zone's servers. Delegation works through a referral that any server can produce from local data alone, so a resolver that starts from the root's address can reach any name by following referrals, one level at a time.",
      limits:
        "The parent's NS records and the child's own NS records are two copies that can disagree, and the parent's copy is what sends resolvers to the child. If the parent names a server that does not serve the zone, resolution fails or is slow. The root and TLD servers are on the path of a cold lookup for every name, which only works because their answers are cached for a long time. The tree also does not say where a service is. The same name can point anywhere, and the hierarchy only says who decides.",
      connections:
        "Module 3's addresses and Module 4's routing deliver packets to an address. DNS supplies the address. The next lesson walks a full resolution through these servers, and the caching lesson explains why the root is not asked for every lookup. Module 8 uses names again in the HTTP Host header, and Module 10 checks them in TLS certificates.",
    },
    {
      type: "steps",
      id: "tree-and-zones",
      title: "From a name to the server that owns it",
      steps: [
        {
          title: "A name is a path of labels",
          body: "`www.example.com.` is the label `www`, under `example`, under `com`, under the root, whose label is empty. The trailing dot is that empty root label. A label is at most 63 octets and a whole name at most 255. Labels compare case-insensitively, so `WWW.Example.COM` is the same name.",
        },
        {
          title: "A zone is a connected piece of the tree",
          body: "The tree is cut between nodes, and each connected region is one zone, named after its top node. `example.com` may be one zone that holds `www.example.com` and `mail.example.com`, while `dev.example.com` is cut off into its own zone with different servers.",
        },
        {
          title: "The parent delegates with NS records",
          body: "The `com` zone holds NS records at `example.com` naming that zone's servers. Those records are not `com`'s own authoritative data. They mark the cut. The child zone holds a matching NS set at its top, which is authoritative there.",
        },
        {
          title: "Glue breaks the loop",
          body: "If `example.com`'s server is `ns1.example.com`, a resolver cannot look up that name without already reaching `example.com`. So the `com` zone also stores the A and AAAA records for `ns1.example.com` as glue, returned only with the referral.",
        },
        {
          title: "Each server answers from local data",
          body: "Asked about a name in its zone, a server answers with the AA bit set, or says the name does not exist. Asked about a name below a cut, it returns a referral to the child's servers. Asked about a name outside its zones, it has nothing authoritative to say.",
        },
      ],
    },
    {
      type: "comparison",
      id: "server-roles",
      title: "Root, TLD, and authoritative servers",
      caption:
        "All three are authoritative servers. They differ only in which zone they serve. A recursive resolver, covered next lesson, is a different role.",
      columns: ["Zone served", "Typical answer", "What a resolver learns"],
      rows: [
        {
          label: "Root servers",
          cells: [
            "The root zone, `.`",
            "A referral to the TLD's servers, with glue",
            "Who serves `com`, `org`, `uk`, and every other TLD",
          ],
        },
        {
          label: "TLD servers",
          cells: [
            "One top-level domain, such as `com`",
            "A referral to the registered domain's servers, with glue when it is needed",
            "Who serves `example.com`",
          ],
        },
        {
          label: "A domain's authoritative servers",
          cells: [
            "That domain's zone, such as `example.com`",
            "The records themselves, with AA set, or a name error",
            "The A, AAAA, MX, or other records, and their TTLs",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "reading-a-referral",
      title: "Asking a `com` server about `example.com`",
      setup:
        "From a Linux shell, send a non-recursive query straight to one of the `com` servers: `dig +norecurse @a.gtld-servers.net example.com A`. This output was observed in October 2026 and trimmed to the relevant lines. The provider and addresses will differ when you run it.",
      walkthrough: [
        "The header line reads `status: NOERROR` and `flags: qr`. `qr` marks a response. `aa` is missing, so this server is not authoritative for `example.com`. `rd` is missing because `+norecurse` cleared it, so the server was not asked to resolve the name for us.",
        "`ANSWER: 0`. The `com` server has no A record for `example.com`, because that data lives in the child zone.",
        "The authority section holds two records: `example.com. 172800 IN NS hera.ns.cloudflare.com.` and the same for `elliott.ns.cloudflare.com.`. These are the delegation. They tell the resolver which servers own the zone, and that it may cache this for 172,800 seconds, two days.",
        "The additional section lists A and AAAA records for both servers. Their names are under `cloudflare.com`, also in the `com` zone, so this server can supply their addresses and the resolver can skip separate lookups.",
        "Repeat the query against one of those addresses: `dig +norecurse @173.245.58.162 www.example.com A`. Now the flags include `aa` and the answer section holds the A records. That server owns the zone.",
      ],
      result:
        "A referral is a NOERROR response with no answer, no AA bit, NS records in the authority section, and usually addresses in the additional section. It means 'not mine, ask them'. The final, authoritative answer is the one with AA set. Every lookup of a new domain walks this chain from the root down, unless the resolver already has the delegation cached.",
    },
    {
      type: "why",
      id: "why-dns",
      questionId: "why.21",
    },
    {
      type: "callout",
      id: "parent-child-ns",
      tone: "caution",
      title: "Changing name servers is a change in the parent",
      body: "Editing the NS records inside your own zone does not move the domain. Resolvers are sent to a zone by the NS records in the parent, which for a registered domain you change through the registrar. Both sets should match. During a provider migration, keep the old servers serving the zone until the parent's NS TTL, often two days for a TLD, has passed since the parent changed.",
      scope: "rfc",
    },
    {
      type: "expandable",
      id: "delegation-details",
      title: "Glue, bailiwick, and lame delegations",
      paragraphs: [
        "Glue is needed only when a server's name is at or below the cut it serves. `ns1.example.com` serving `example.com` needs glue in `com`. `ns.provider.net` serving `example.com` does not, because a resolver can find `provider.net` through the `net` zone. A server for one zone should not return authoritative answers for names in another zone, and servers ignore data at a cut other than NS records and the addresses needed to reach them.",
        "RFC 9499 names three relationships between a delegation and its server's name. In-domain: `ns.child.example.com` serving `child.example.com`. Sibling domain: `ns.another.example.com` serving `child.example.com`, both under the parent `example.com`. Unrelated: a server name outside the parent zone. The parent can hold addresses for in-domain and sibling servers, which is why a referral from `com` can include addresses for servers under `cloudflare.com`. The older words in-bailiwick and out-of-bailiwick mean roughly the same and are now considered historic.",
        "'Lame delegation' originally meant a server listed in the NS records that is not set up to serve the zone. In use it now covers several faults: a listed server that does not answer, one whose address is unreachable, or one that answers with an error or without AA. RFC 9499 calls the term historic and recommends naming the specific fault.",
        "A resolver starts from a configured list of root server addresses, its root hints, and replaces it with the root's own NS set. This is called priming. The root zone's NS set lists 13 names, `a.root-servers.net` through `m.root-servers.net`.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "who-to-ask",
          prompt:
            "A resolver with an empty cache, except for root hints, needs the address of `api.dev.example.com`. `dev.example.com` is its own zone. Which zones does it consult, in order, and what does each return?",
          answer:
            "The root zone returns a referral to the `com` servers. A `com` server returns a referral to the `example.com` servers. An `example.com` server returns a referral to the `dev.example.com` servers, because `dev` is cut off as a separate zone. A `dev.example.com` server returns the A record with AA set. Each referral carries NS records, plus glue where a server's name is inside the zone being delegated.",
        },
        {
          id: "glue-needed",
          prompt:
            "`example.org` is delegated to `ns1.example.org` and `ns.hosting.net`. Which of these needs glue in the `org` zone, and why?",
          answer:
            "`ns1.example.org` does, because its name is inside the zone it serves. Without its address in `org`, a resolver would have to ask `example.org`'s servers for the address of `example.org`'s server. `ns.hosting.net` does not need glue in `org`. A resolver can find its address through the `net` delegation, independently of `example.org`.",
        },
        {
          id: "no-aa",
          prompt:
            "You run `dig @<some server> www.example.com` and get `status: NOERROR`, `ANSWER: 0`, and two NS records in the authority section. No `aa` flag. Is the name missing?",
          answer:
            "No. That is a referral. The server is telling you which servers are responsible for the zone, not that the name has no data. A negative answer from an authoritative server has the AA bit set and carries the zone's SOA record in the authority section, either with RCODE NXDOMAIN or with NOERROR and no answer.",
        },
        {
          id: "ns-mismatch",
          prompt:
            "A team moved `example.com` to a new DNS provider and updated the NS records inside the zone on the new provider. A day later, some users still hit the old records, and some lookups fail. What did they miss?",
          answer:
            "The delegation lives in the parent. Resolvers follow the NS records in the `com` zone, which are changed at the registrar. If those still name the old provider, resolvers keep going there. If the old provider has already deleted the zone, those resolvers get errors. Update the delegation at the registrar, keep the old provider serving the same data until the parent's NS TTL has passed, and make both NS sets match.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "hosts-txt-history",
      statement:
        "Before DNS, host names were kept in one HOSTS.TXT file maintained by the NIC and copied by every host. Distribution cost grew with the square of the number of hosts, and organizations had to wait for the NIC to publish their changes.",
      sourceId: "rfc1034",
      locator: "Section 2.1, The history of domain names",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "dns-design-goals",
      statement:
        "Names should not contain network identifiers, addresses, or routes. The database must be distributed with local caching, and where there are trade-offs between the cost of acquiring data, the speed of updates, and the accuracy of caches, the source of the data should control them.",
      sourceId: "rfc1034",
      locator: "Section 2.2, DNS design goals",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "name-space-tree",
      statement:
        "The name space is a tree. Each node has a label of 0 to 63 octets; the root's label is empty. A domain name is the list of labels from a node to the root, its total length is at most 255 octets, and comparisons are case-insensitive.",
      sourceId: "rfc1034",
      locator: "Section 3.1, Name space specifications and terminology",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "zones-and-cuts",
      statement:
        "The database is divided into zones by cuts between adjacent nodes, at points where an organization takes control of a subtree. Every zone must be available from at least two servers. A zone's owner can change its data or delegate subzones unilaterally.",
      sourceId: "rfc1034",
      locator: "Sections 4.1 and 4.2",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "zone-data-and-glue",
      statement:
        "A zone's top node holds NS records for all its servers and one SOA record. NS records at cuts below the zone name the subzone's servers, are not authoritative data, and should match the subzone's own NS records. Glue address records are needed only when a server's name is below the cut, and are used only in referrals.",
      sourceId: "rfc1034",
      locator: "Section 4.2.1, Technical considerations",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "delegation-in-parent",
      statement:
        "The last step of a delegation is adding the NS and glue records to the parent zone. The administrators of both zones should keep the NS and glue records on both sides of the cut consistent.",
      sourceId: "rfc1034",
      locator: "Section 4.2.2, Administrative considerations",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "referral-algorithm",
      statement:
        "When matching a query would leave the server's authoritative data at a node with NS records marking a cut, the server returns a referral: the subzone's NS records in the authority section, and available addresses, including glue, in the additional section.",
      sourceId: "rfc1034",
      locator: "Section 4.3.2, Algorithm, step 3b",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "aa-bit",
      statement:
        "The AA (Authoritative Answer) bit is valid in responses and means the responding server is an authority for the name in the question section.",
      sourceId: "rfc1035",
      locator: "Section 4.1.1, Header section format (AA)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "zone-cut-ownership",
      statement:
        "The existence of a zone cut is shown in the parent by NS records for the child's origin. NS and SOA records at the origin are mandatory in every zone. A server should not give authoritative answers for names in another zone, and should ignore data at a cut other than NS records and the addresses needed to reach those servers.",
      sourceId: "rfc2181",
      locator: "Section 6, Zone Cuts; Section 6.1, Zone authority",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "terminology-delegation",
      statement:
        "Delegation creates a separate zone beneath a domain's apex by adding an NS RRset in the parent for the child origin. A delegation's name server is in-domain when its name is at or below the delegated name, a sibling domain when it is under the parent zone but not under the delegated name, and unrelated otherwise; 'in-bailiwick' is historic. 'Lame delegation' has drifted from its original meaning and should be avoided in favor of specific terms. Priming is finding the root servers from a configured list.",
      sourceId: "rfc9499",
      locator:
        "Section 6 (Priming); Section 7 (Delegation, Glue records, Lame delegation, Bailiwick)",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "dig-norecurse",
      statement:
        "`dig @server name type` queries the named server. `+norecurse` clears the RD bit, which dig sets by default.",
      sourceId: "bind-dig",
      locator: "dig: Simple Usage; query option +[no]recurse",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    { sourceId: "rfc1034", note: "Sections 2.1, 2.2, 3.1, 4.1, 4.2, 4.2.1, 4.2.2, and 4.3.2." },
    { sourceId: "rfc1035", note: "Section 4.1.1: the AA bit." },
    { sourceId: "rfc2181", note: "Sections 6 and 6.1: zone cuts and authority." },
    { sourceId: "rfc9499", note: "Sections 6 and 7: delegation, glue, bailiwick, and priming." },
    { sourceId: "bind-dig", note: "dig usage and +norecurse." },
  ],
} satisfies LessonBody;
