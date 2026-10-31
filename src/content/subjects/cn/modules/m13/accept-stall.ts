import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m13.accept-stall",
  status: "complete",
  objectives: [
    "Recognise when established connections work but new handshakes fail.",
    "Relate listen backlog, somaxconn, and SYN backlog to client symptoms.",
    "Use ss listen Recv-Q as evidence of accept starvation.",
  ],
  prerequisites: ["m13.localhost-only"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• The server already has thousands of connections; new clients cannot connect.\n• Existing sessions may still look fine.",
      whyItMatters:
        "• Looks like 'the network is full' when the accept path or FD limit is the bottleneck.\n• Scaling pods does not help if each process stops calling accept.",
      idea: "• listen() queues completed handshakes until accept(); when the queue is full, new SYNs are dropped or refused.\n• Distinguish FD exhaustion, thread starvation, and backlog caps.",
      howItWorks:
        "• Linux listen backlog is completed sockets waiting for accept; capped by somaxconn.\n• Incomplete handshakes use tcp_max_syn_backlog (unless syncookies).\n• `ss -ltn` Recv-Q on the listen socket ≈ accept queue depth.",
      whyItWorks:
        "• The kernel can finish TCP handshakes without the app; without accept(), the queue fills and newcomers fail.",
      limits:
        "• Exact client symptom (timeout vs refuse) depends on kernel and syncookies.\n• Reverse proxies may hold connections before the app sees them.",
      connections: "• Module 12 lifecycle and buffers; Module 5 handshake.",
    },
    {
      type: "investigation",
      id: "investigation",
      causes: [
        "Application stopped calling accept() (deadlock, blocked workers).",
        "Listen backlog / somaxconn too small for the arrival rate.",
        "Process hit nofile / FD limit; accept fails.",
        "SYN backlog overflow under SYN flood or slow accept.",
        "Load balancer marks the target up while the app accept loop is stuck.",
        "Too many established connections consuming workers so accept never runs.",
      ],
      order: [
        "Confirm old connections still exchange data while new connects fail.",
        "`ss -ltnp` Recv-Q on the listen socket; note process state.",
        "Check ulimit/nofile and error logs for EMFILE/ENFILE.",
        "Compare somaxconn, listen backlog, and tcp_max_syn_backlog.",
        "Thread/goroutine dump: are accept loops blocked?",
      ],
      commands: [
        {
          command: "ss -ltnp 'sport = :443'",
          shows: "Listen Recv-Q (pending accepts) and Send-Q; owning PID.",
        },
        {
          command: "sysctl net.core.somaxconn net.ipv4.tcp_max_syn_backlog",
          shows: "Kernel caps on accept and SYN queues.",
        },
        {
          command: "cat /proc/$(pidof myapp)/limits | grep 'open files'",
          shows: "Whether the process is near its FD ceiling.",
        },
        {
          command: "ss -s",
          shows: "Aggregate TCP socket counts for saturation clues.",
        },
      ],
      implications: [
        "Listen Recv-Q at backlog: accept not keeping up.",
        "Recv-Q ~0 but new clients fail: filter, SYN backlog, or wrong listener.",
        "EMFILE in logs: raise nofile or cut connection churn / leaks.",
        "Existing OK, new fail: classic accept-path problem, not total host death.",
      ],
      distinguishing: [
        "App deadlock vs small backlog: Recv-Q pegged vs oscillating under load tests.",
        "FD limit vs backlog: EMFILE vs growing Recv-Q without EMFILE.",
        "SYN flood vs slow accept: SYN-RECV growth vs ESTABLISHED wait in accept queue.",
      ],
      mistakes: [
        "Restarting only the load balancer when the app accept loop is wedged.",
        "Raising somaxconn without fixing the blocked workers.",
        "Assuming thousands of ESTABLISHED sockets prove the server can accept more.",
      ],
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "recvq",
          prompt: "ss shows listen Recv-Q equal to the backlog; new clients time out. Meaning?",
          answer:
            "Completed handshakes wait for accept() and the queue is full. The app is not accepting fast enough (or at all).",
        },
        {
          id: "somaxconn",
          prompt: "What caps the listen backlog argument on Linux?",
          answer: "net.core.somaxconn; listen() silently caps larger values (listen(2) NOTES).",
        },
      ],
    },
  ],
  claims: [
    {
      id: "listen-backlog",
      statement:
        "On Linux since 2.2, listen backlog is the queue of completely established sockets waiting to be accepted; it is capped by somaxconn (default 4096 since Linux 5.4).",
      sourceId: "man-listen",
      locator: "NOTES",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
    {
      id: "listen-full",
      statement:
        "If a connection request arrives when the listen queue is full, the client may see ECONNREFUSED or the request may be ignored for later retry.",
      sourceId: "man-listen",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: true,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "man-listen", note: "Backlog and somaxconn behaviour." },
    { sourceId: "man-ss", note: "Listen Recv-Q." },
    { sourceId: "man-tcp", note: "tcp_max_syn_backlog / syncookies context." },
    { sourceId: "kernel-ip-sysctl", note: "Related TCP sysctls." },
  ],
} satisfies LessonBody;
