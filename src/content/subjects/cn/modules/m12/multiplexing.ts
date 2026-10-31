import type { LessonBody } from "../../../../schema";

export const body = {
  id: "m12.multiplexing",
  status: "complete",
  objectives: [
    "Say what a blocking socket call waits for.",
    "Contrast select, poll, and epoll at a conceptual level.",
    "Explain why I/O multiplexing exists for many sockets in one thread.",
  ],
  prerequisites: ["m12.lifecycle"],
  sections: [
    {
      type: "principle",
      id: "principle",
      problem:
        "• One thread blocked in `recv` on one FD cannot service others.\n• One thread per connection wastes memory and context switches at scale.\n• The kernel already knows which FDs are ready; user space needs an efficient wait API.",
      whyItMatters:
        "• Every event-driven server (nginx, Node, Go netpoll, etc.) sits on multiplexing.\n• Wrong model: busy-spin, or block forever with no timeout, looks like a hang.\n• Latency spikes often mean the event loop was stuck, not the NIC.",
      idea: "• Blocking mode: call sleeps until the operation can progress (or error).\n• Non-blocking: call returns EAGAIN/EWOULDBLOCK if it cannot progress now.\n• Multiplexing: one wait for readiness across many FDs, then do non-blocking work.",
      howItWorks:
        "• `select`: bitmasks of FDs; O(n) scan; FD_SETSIZE limit historically.\n• `poll`: array of `pollfd`; no bitmask size limit of select; still O(n) per wait.\n• `epoll` (Linux): interest set in the kernel; wait returns only ready FDs; scales better for large idle sets.\n• Edge vs level trigger (epoll): edge notifies on transition; level while condition holds.",
      whyItWorks:
        "• Kernel demuxes readiness once per wakeup instead of N blocking threads.\n• Non-blocking I/O + readiness avoids sleeping on the wrong FD.\n• Timeouts on the wait bound how long the loop can stall.",
      limits:
        "• Ready ≠ you can read the whole request; still loop until EAGAIN.\n• epoll is Linux-specific; other OSes have kqueue / IOCP.\n• CPU-bound work on the event thread blocks every connection.\n• Thundering herd and accept mutexes are separate scaling topics.",
      connections:
        "• Lifecycle lesson: which FDs exist.\n• Buffers lesson: readiness for write means send buffer has space.\n• Module 11: backpressure when you stop reading.",
    },
    {
      type: "comparison",
      id: "apis",
      title: "select vs poll vs epoll",
      columns: ["Interest set", "Scales with", "Typical use"],
      rows: [
        {
          label: "select",
          cells: ["Bitmasks copied each call", "Small FD counts", "Portable / simple tools"],
        },
        {
          label: "poll",
          cells: ["pollfd array each call", "Moderate counts", "Portable replacement for select"],
        },
        {
          label: "epoll",
          cells: ["Kernel interest set", "Many mostly-idle FDs", "Linux high-connection servers"],
        },
      ],
    },
    {
      type: "steps",
      id: "event-loop",
      title: "One event-loop iteration (notes)",
      steps: [
        {
          title: "Wait",
          body: "• `epoll_wait` (or poll/select) with a timeout.\n• Wake on readable, writable, error, hangup.",
        },
        {
          title: "Handle",
          body: "• For each ready FD: non-blocking read/write until EAGAIN.\n• Accept on listen FD when readable.",
        },
        {
          title: "Repeat",
          body: "• Never run unbounded CPU work on this thread without yielding.",
        },
      ],
    },
    {
      type: "callout",
      id: "blocking-default",
      tone: "note",
      title: "Default is blocking",
      body: "• New sockets are blocking unless you set O_NONBLOCK.\n• A blocking `recv` on a quiet connection parks the thread until data, error, or timeout (if set).",
    },
    {
      type: "example",
      id: "stuck-worker",
      title: "Blocking recv in a request worker",
      setup: "HTTP server uses one thread per connection. Peer stalls mid-body.",
      walkthrough: [
        "• Thread blocks in `recv` waiting for the rest of the body.",
        "• That worker cannot accept or serve anyone else.",
        "• With a pool of N threads, N stalled clients exhaust the pool.",
      ],
      result:
        "• Multiplexed non-blocking I/O (or timeouts + more isolation) keeps other clients moving.",
    },
    {
      type: "checks",
      id: "checks",
      items: [
        {
          id: "eagain",
          prompt: "Non-blocking recv returns EAGAIN. What should the program do?",
          answer:
            "Stop reading that FD for now and wait (via the multiplexer) until it is readable again—do not treat it as a fatal error.",
        },
        {
          id: "why-epoll",
          prompt: "Why do large Linux servers prefer epoll over select?",
          answer:
            "epoll keeps the interest set in the kernel and returns only ready FDs, so cost stays related to activity rather than scanning every watched FD each wait like select/poll.",
        },
        {
          id: "block-mean",
          prompt: "What is a blocking socket waiting for on recv?",
          answer:
            "Enough data (or EOF/error) to satisfy the call—or until a receive timeout fires if one is set.",
        },
      ],
    },
  ],
  claims: [
    {
      id: "select-waits",
      statement:
        "select() allows a program to monitor multiple file descriptors, waiting until one or more become ready for I/O.",
      sourceId: "man-select",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "epoll-idea",
      statement:
        "epoll is a Linux I/O event notification facility that scales well for large numbers of watched file descriptors.",
      sourceId: "man-epoll",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
    {
      id: "socket-ioblocking",
      statement:
        "I/O on sockets may block until the condition for the call is met; applications use multiplexors such as select or epoll to wait until file descriptors are ready.",
      sourceId: "man-select",
      locator: "DESCRIPTION",
      verification: "checked",
      subtle: false,
      scope: "linux",
    },
  ],
  references: [
    { sourceId: "man-select", note: "select(2): multi-FD wait." },
    { sourceId: "man-epoll", note: "epoll(7): Linux scalable readiness." },
  ],
} satisfies LessonBody;
