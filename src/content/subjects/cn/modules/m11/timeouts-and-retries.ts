import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m11.timeouts-and-retries",
  status: "complete",
  objectives: [
    "Set connect, per-attempt, and total timeouts, and pass a deadline down a call chain.",
    "Design a retry policy: which failures, how many attempts, backoff with jitter, and a budget.",
    "Explain how retries multiply load across layers and why fan-out makes the tail common.",
  ],
  prerequisites: ["m11.bdp-and-loss"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• TCP keeps retransmitting for minutes before it reports an error.\n• Silence looks the same as slowness, so a caller cannot tell a dead peer from a busy one.\n• Retrying a failed call can cure a glitch or bury an overloaded server.",
      whyItMatters:
        "• A call with no deadline holds a thread, a socket, and memory for as long as its slowest dependency.\n• Retry storms turn a short overload into an outage.\n• Users feel the slowest requests, not the mean.",
      idea: "• A timeout bounds one wait. A deadline bounds the whole operation and travels with it.\n• Retry only what is safe and likely transient, a few times, with growing random waits, inside a budget.\n• Design for percentiles, because fan-out makes the tail the common case.",
      howItWorks:
        "• Connect timeout: a few RTTs plus room for one SYN retransmission.\n• Per-attempt timeout: just above the dependency's p99 or p99.9.\n• Each hop passes on the time remaining, minus its own work.\n• Backoff: wait a random time in [0, min(cap, base × 2^attempt)].\n• Budget: retries at most about 10% of calls, or pause them while failures are high.",
      whyItWorks:
        "• Only the application knows how long its answer stays useful; TCP does not.\n• Random waits spread retries out, so they stop arriving in synchronised waves.\n• A budget bounds the extra load no matter how many calls fail.",
      limits:
        "• A client timeout does not stop work on the server unless the server checks for cancellation.\n• Retrying a request that is not idempotent can apply it twice.\n• Hedged requests trim the tail but add load; use them for cheap, read-only calls.",
      connections:
        "• Module 5: TCP's own timers and why.15.\n• Module 8: idempotent methods, 503, and Retry-After.\n• Next: connection reuse, keepalives, and partial failure.",
    },
    {
      type: "comparison",
      id: "layer-timeouts",
      title: "Who gives up, and when (Linux defaults)",
      columns: ["What it bounds", "Default"],
      rows: [
        {
          label: "SYN retries (`tcp_syn_retries`)",
          cells: ["An unanswered connect().", "6 retries, about 127 s."],
        },
        {
          label: "Data retries (`tcp_retries2`)",
          cells: [
            "Unacknowledged data on an established connection.",
            "15 retries, about 13 to 30 minutes.",
          ],
        },
        {
          label: "`TCP_USER_TIMEOUT`",
          cells: [
            "How long sent data may stay unacknowledged before the connection is closed with ETIMEDOUT.",
            "Off: the system default applies.",
          ],
        },
        {
          label: "`SO_RCVTIMEO` / `SO_SNDTIMEO`",
          cells: [
            "One blocking socket call; it returns EAGAIN when the time passes with nothing transferred.",
            "Zero: never times out.",
          ],
        },
        {
          label: "gRPC deadline",
          cells: [
            "The whole call; it fails with DEADLINE_EXCEEDED.",
            "None set: a call can wait effectively forever.",
          ],
        },
      ],
    },
    {
      type: "why",
      id: "why-timeouts",
      questionId: "why.44",
    },
    {
      type: "steps",
      id: "deadlines",
      title: "Setting and passing a deadline",
      steps: [
        {
          title: "Start from the user's budget",
          body: "• The page must answer in 1 s. That is the deadline for everything below it.",
        },
        {
          title: "Give each phase a limit",
          body: "• Connect: e.g. 300 ms on a 20 ms RTT path.\n• Each attempt: just above the dependency's p99.9, e.g. 250 ms.",
        },
        {
          title: "Pass the remaining time down",
          body: "• Each hop sends 'time left', not a fixed value. gRPC converts the deadline to a timeout minus elapsed time, so clock skew does not matter.",
        },
        {
          title: "Stop work when time runs out",
          body: "• A gRPC server cancels the call when the deadline passes, but the handler must check and stop its own work.",
        },
        {
          title: "Do not retry without time",
          body: "• If the remaining time is less than one typical attempt, fail now. A late answer is wasted work.",
        },
      ],
    },
    {
      type: "comparison",
      id: "retry-policy",
      title: "A retry policy",
      columns: ["Rule", "Why"],
      rows: [
        {
          label: "What to retry",
          cells: [
            "Connection refused before any request was sent, 503, and requests the server says it never processed (HTTP/2 REFUSED_STREAM, or streams above the GOAWAY limit).",
            "These are likely transient, and the request was not applied.",
          ],
        },
        {
          label: "What not to retry",
          cells: [
            "A non-idempotent request whose outcome is unknown, unless it carries an idempotency key. Also 4xx errors.",
            "The first attempt may have succeeded; a 4xx will fail again.",
          ],
        },
        {
          label: "How many",
          cells: ["2 or 3 attempts in total.", "Each extra attempt adds load and latency."],
        },
        {
          label: "How long between",
          cells: [
            "Exponential backoff with jitter; honour Retry-After.",
            "Spreads retries out; lets the server say when.",
          ],
        },
        {
          label: "How much in total",
          cells: [
            "A retry budget, such as gRPC retry throttling.",
            "Stops retries from multiplying load during an outage.",
          ],
        },
        {
          label: "Where",
          cells: [
            "At one layer, usually the edge or the caller closest to the user.",
            "Retries at every layer multiply.",
          ],
        },
      ],
    },
    {
      type: "example",
      id: "retry-storm",
      title: "A retry storm through three layers",
      setup:
        "Browser → API gateway → order service → database. Each of the three callers makes up to 3 attempts with a fixed 100 ms wait. Normal load is 1,000 requests/s. The database slows down. Illustrative numbers.",
      walkthrough: [
        "• Database queries pass their callers' timeouts. Every layer sees failures and retries.",
        "• One browser request can become 3 × 3 × 3 = 27 database queries. At 1,000 requests/s, up to 27,000 queries/s.",
        "• The database was already too slow at 1,000. Now it gets far more work, so more queries time out.",
        "• Timed-out queries keep running after their callers give up. The database does work nobody is waiting for.",
        "• Fixed 100 ms waits make retries arrive in waves, so load spikes rather than spreads.",
        "• Fix: retry only at the gateway, with jittered backoff and a 10% budget (at most about 1,100 queries/s). Pass the deadline down. Let the database shed load with 503 and Retry-After.",
      ],
      result:
        "• Retries multiply across layers just when capacity is lowest. Retry in one place, with backoff, jitter, and a budget.",
    },
    {
      type: "why",
      id: "why-retries",
      questionId: "why.43",
    },
    {
      type: "example",
      id: "tail-fanout",
      title: "Why the average hides the tail",
      setup:
        "A service answers 99% of requests in 50 ms and 1% in 5 s. A page calls it once, or fans out to 100 backends that each behave the same way.",
      walkthrough: [
        "• Mean of 1,000 requests: (990 × 50 + 10 × 5,000) ÷ 1,000 ≈ 100 ms. The dashboard looks fine.",
        "• p99 is 5 s. One user in a hundred waits 5 s on a single call.",
        "• Fan-out to 100: chance at least one is slow = 1 − 0.99^100 ≈ 63%. Most page loads now take 5 s.",
        "• Fan-out to 10: 1 − 0.99^10 ≈ 10%.",
        "• Remedies: shorter per-backend deadlines with partial results, hedging a second request after the p95, and fixing the slow cause (GC pauses, a hot shard, a retransmission timeout).",
      ],
      result:
        "• Track p99, p99.9, and the share of requests over a threshold. The more a request fans out, the more the tail decides its latency.",
    },
    {
      type: "why",
      id: "why-average",
      questionId: "why.46",
    },
    {
      type: "callout",
      id: "jitter",
      tone: "note",
      title: "Backoff needs jitter",
      body: "• Plain exponential backoff still sends retries in clusters: everyone who failed together waits the same 1, 2, 4 s.\n• Picking a random wait in the backoff window spreads them out. AWS's simulation found fewer calls and faster completion than without jitter.\n• gRPC applies ±20% jitter to its retry backoff.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "propagate",
          prompt:
            "Why pass a deadline down a call chain instead of giving every hop the same 2 s timeout?",
          answer:
            "With fixed timeouts an inner call can keep running after the outer caller has given up, wasting work, and the sum of inner timeouts can exceed the user's budget. Passing the remaining time lets each hop stop when the answer is no longer useful.",
        },
        {
          id: "post-retry",
          prompt:
            "A client times out after 1 s on `POST /payments`, but the server took 1.5 s and succeeded. The client retries. What happens, and how do you prevent it?",
          answer:
            "The payment is applied twice. Don't retry automatically, or send an idempotency key the server uses to return the first result. RFC 9110 says clients should not automatically retry non-idempotent requests without such knowledge.",
        },
        {
          id: "why-jitter",
          prompt:
            "10,000 clients fail at the same moment and retry after exactly 1 s, 2 s, then 4 s. What goes wrong, and what does jitter change?",
          answer:
            "All 10,000 retries arrive in the same instant each time, so the server faces repeated spikes as large as the original. Random waits spread them over each interval, so the load is closer to a steady rate.",
        },
        {
          id: "fanout",
          prompt:
            "A request fans out to 20 backends. Each has a 2% chance of being slow. How often is the request slow?",
          answer: "1 − 0.98^20 ≈ 33%, about one request in three.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "syn-retries",
      statement:
        "tcp_syn_retries defaults to 6, which corresponds to retrying for up to about 127 seconds.",
      sourceId: "man-tcp",
      locator: "/proc interfaces: tcp_syn_retries",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "retries2",
      statement:
        "tcp_retries2 defaults to 15, corresponding to about 13 to 30 minutes depending on the retransmission timeout, before an established connection gives up.",
      sourceId: "man-tcp",
      locator: "/proc interfaces: tcp_retries2",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "user-timeout",
      statement:
        "TCP_USER_TIMEOUT sets the maximum time transmitted data may remain unacknowledged, or buffered data untransmitted because of a zero window, before TCP closes the connection and returns ETIMEDOUT; it overrides keepalive in deciding when to give up.",
      sourceId: "man-tcp",
      locator: "Socket options: TCP_USER_TIMEOUT",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "so-rcvtimeo",
      statement:
        "SO_RCVTIMEO and SO_SNDTIMEO make a blocking socket call return -1 with EAGAIN or EWOULDBLOCK (EINPROGRESS for connect) when the timeout passes with no data transferred; the default of zero never times out.",
      sourceId: "man-socket",
      locator: "Socket options: SO_RCVTIMEO and SO_SNDTIMEO",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "grpc-deadline",
      statement:
        "gRPC sets no deadline by default. A server cancels a call when the client's deadline passes, but the application must stop its own work. Propagation converts the deadline to a timeout with elapsed time deducted, to avoid clock skew.",
      sourceId: "grpc-deadlines-guide",
      locator: "Deadlines on the Client; Deadlines on the Server; Deadline Propagation",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "grpc-retry",
      statement:
        "gRPC applies ±20% jitter to its retry backoff. With retry throttling, each failure decrements a per-server token count and each success adds tokenRatio; retries pause while the count is below half of maxTokens.",
      sourceId: "grpc-retry-guide",
      locator: "Retry configuration",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "idempotent-retry",
      statement:
        "Idempotent requests can be repeated automatically after a communication failure. A client SHOULD NOT automatically retry a non-idempotent request unless it knows the semantics are idempotent or that the original was never applied.",
      sourceId: "rfc9110",
      locator: "Section 9.2.2, Idempotent Methods",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "retry-after",
      statement:
        "503 means the server is temporarily unable to handle the request because of overload or maintenance; it MAY send Retry-After to say how long to wait.",
      sourceId: "rfc9110",
      locator: "Sections 15.6.4 and 10.2.3",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "close-and-retry",
      statement:
        "A server SHOULD let transport flow control absorb temporary overloads rather than close connections expecting clients to retry; the latter can make network congestion or server load worse.",
      sourceId: "rfc9112",
      locator: "Section 9.5, Failures and Timeouts",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
    {
      id: "jitter-simulation",
      statement:
        "In AWS's simulation of contending clients, exponential backoff without jitter still produced clusters of calls; jittered backoff cut the number of calls by more than half with 100 clients and completed sooner.",
      sourceId: "aws-backoff-jitter",
      locator: "Adding Jitter",
      verification: "checked",
      subtle: false,
      scope: "common-implementation",
    },
    {
      id: "h2-safe-retry",
      statement:
        "Requests on streams numbered above a GOAWAY's last stream ID, or reset with REFUSED_STREAM, were not processed and may be retried automatically, even with non-idempotent methods.",
      sourceId: "rfc9113",
      locator: "Section 8.7, Request Reliability",
      verification: "checked",
      subtle: true,
      scope: "rfc",
    },
    {
      id: "udp-backoff",
      statement:
        "UDP applications that cannot estimate RTT MAY use a fixed interval that is exponentially backed off when packets are deemed lost; 1 second is the recommended initial value.",
      sourceId: "rfc8085",
      locator: "Section 3.1.3, Low Data-Volume Applications",
      verification: "checked",
      subtle: false,
      scope: "rfc",
    },
  ],
  references: [
    { sourceId: "man-tcp", note: "tcp_syn_retries, tcp_retries2, TCP_USER_TIMEOUT." },
    { sourceId: "man-socket", note: "SO_RCVTIMEO and SO_SNDTIMEO." },
    { sourceId: "grpc-deadlines-guide", note: "Defaults, cancellation, and propagation." },
    { sourceId: "grpc-retry-guide", note: "Backoff jitter and retry throttling." },
    { sourceId: "rfc9110", note: "Sections 9.2.2, 10.2.3, and 15.6.4." },
    { sourceId: "rfc9112", note: "Section 9.5: closing connections to shed load." },
    { sourceId: "rfc9113", note: "Section 8.7: requests that are safe to retry." },
    { sourceId: "aws-backoff-jitter", note: "Simulation of backoff with and without jitter." },
    { sourceId: "rfc8085", note: "Section 3.1.3: backoff for UDP applications." },
  ],
} satisfies LessonBody;
