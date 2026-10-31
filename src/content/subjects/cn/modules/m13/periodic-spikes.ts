import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.periodic-spikes",
  status: "complete",
  objectives: [
    "Treat periodic latency as a scheduled or cyclic cause.",
    "Correlate spikes with cron, GC, deploys, and DNS TTL boundaries.",
    "Avoid mistaking periodic load for random network loss.",
  ],
  prerequisites: ["m13.large-response-hangs"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• Latency jumps on a clock—every N minutes or at a fixed time of day.\n• Between spikes the service looks fine.",
      whyItMatters:
        "• Random-loss playbooks waste time. Periodicity points at jobs, caches, and leases.",
      idea: "• Measure the period; match it to a timer (cron, TTL, certificate reload, autoscaler).\n• Confirm with logs aligned to the same clock.",
      howItWorks:
        "• Plot p99 vs wall clock; find fundamental period.\n• Candidates: cron/batch, GC, log rotate, DNS TTL expiry stampedes, LB health flap, lease renewals.\n• Network: timed routing changes or backup windows are rarer but real.",
      whyItWorks:
        "• A fixed period is almost never pure Bernoulli packet loss; something wakes on a timer.",
      limits:
        "• Aliasing: sampling every 60s can invent a period.\n• Multiple timers can beat together and look irregular.",
      connections: "• Module 7 TTLs; Module 11 tail latency; intermittent-timeouts lesson.",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "Cron or batch job saturating CPU, disk, or a dependency on a schedule.",
        "Stop-the-world GC or runtime maintenance on a timer.",
        "DNS TTL aligned stampedes when many clients refresh together.",
        "Certificate/config reload that briefly drops connections.",
        "Autoscaling or rolling deploy waves.",
        "Backup, compaction, or index jobs on storage dependencies.",
        "Periodic BGP/policy change (uncommon in app-tier incidents).",
      ],
      order: [
        "Establish period and phase from metrics (wall clock, not only relative).",
        "List timers on the host and in orchestration (cron, Controllers, sidecars).",
        "Check dependency metrics for the same period.",
        "During a spike: CPU, GC logs, DNS error rate, deploy events.",
        "Disable or shift one timer as a controlled test.",
      ],
      commands: [
        {
          command: "crontab -l; systemctl list-timers --all",
          shows: "Host-scheduled jobs near the spike phase.",
        },
        {
          command: "dig api.example.com",
          shows: "TTL on the answer—whether many clients may expire together.",
        },
        {
          command:
            "curl -s -w '%{time_total}\\n' https://api.example.com/ -o /dev/null  # sample across the period",
          shows: "Whether client-observed latency tracks the metric spikes.",
        },
        {
          command: "ss -s; ss -ltn",
          shows: "Connection/accept pressure only during the spike window.",
        },
      ],
      implications: [
        "Spike only on one instance: local cron/GC, not the shared network.",
        "Spike across fleet at once: shared dependency or DNS stampede.",
        "Spike aligns with TTL: cache expiry thundering herd.",
        "No host timer matches: look at upstream SaaS maintenance windows.",
      ],
      distinguishing: [
        "GC vs cron: runtime GC logs vs OS timers.",
        "DNS vs app job: resolver CPU/query rate vs app job metrics.",
        "Network change vs app: packet loss counters vs pure queue delay.",
      ],
      mistakes: [
        "Averaging latency over windows longer than the period (hides spikes).",
        "Restarting pods every spike instead of finding the timer.",
        "Blaming 'the network' because traceroute during a quiet interval looks fine.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "period",
          prompt: "p99 jumps every 15 minutes for 30 seconds. First move?",
          answer:
            "Find what runs on a 15-minute timer (cron, Controllers, TTL). Correlate logs to that phase before packet captures.",
        },
        {
          id: "ttl-stampede",
          prompt: "How can DNS TTLs create periodic latency?",
          answer:
            "Many clients expire the same RR together and stampede the resolver or origin when the TTL hits zero.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "ttl-caching",
      statement:
        "DNS Resource Records are cached for a period not exceeding their TTL; when the TTL expires, resolvers must refresh.",
      sourceId: "rfc1035",
      locator: "Section 3.2.1 (TTL); Section 7.3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "curl-total",
      statement: "curl can print time_total for each transfer via -w/--write-out.",
      sourceId: "curl-manpage",
      locator: "-w, --write-out",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
  ],
  references: [
    { sourceId: "rfc1035", note: "TTL and caching." },
    { sourceId: "rfc2181", note: "TTL clarifications." },
    { sourceId: "curl-manpage", note: "Sampling client latency." },
    { sourceId: "man-ss", note: "Pressure during spike windows." },
  ],
} satisfies LessonBody;
