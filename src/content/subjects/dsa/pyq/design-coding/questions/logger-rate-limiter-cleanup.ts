import type { DesignCodingQuestion } from "../../../schema";

export const loggerRateLimiterCleanup: DesignCodingQuestion = {
  id: "dc.logger-rate-limiter-cleanup",
  slug: "logger-rate-limiter-cleanup",
  title: "Logger Rate Limiter with Expiry and Memory Cleanup",
  classification: "reported-google",
  level: "L4",
  interviewFormat: "Onsite coding (Bangalore, 2025) — rescheduled round",
  priority: "must-revise",
  verification: "verified",
  categories: ["rate-limiting-time"],
  sources: [
    {
      label: "LeetCode Discuss — Google L4 Bangalore 2025",
      url: "https://leetcode.com/discuss/post/7240829/google-l4-bangalore-2025-by-anonymous_us-vo8z/",
      note: "Log messages with timestamps; print if same message not seen in last 10 seconds; follow-up on memory cleanup; second follow-up: also not in next 10 seconds.",
    },
  ],
  leetcodeUrl: "https://leetcode.com/problems/logger-rate-limiter/",
  coreApproach:
    "Map message → last-printed timestamp for the 10s window; add a queue of (timestamp, message) to evict expired entries so unique-message cardinality cannot grow forever.",
  summaryComplexity:
    "shouldPrintMessage O(1) amortized with cleanup · space O(distinct live messages in the window)",
  originalPrompt:
    "Given log messages each as a string plus a timestamp: print a message only if the same message was not present in the last 10 seconds. (Candidate used map<string,int> of last print time.)",
  originalRequirements: [
    "Input stream of (message, timestamp).",
    "Print/allow a message only if it did not appear (as printed) in the previous 10 seconds.",
    "Timestamps are assumed non-decreasing in typical Logger Rate Limiter style (confirm with interviewer).",
  ],
  reportedFollowUps: [
    "Bottleneck in realtime: memory blows up if all messages are unique — add cleanup.",
    "A message must be printed only if it does not occur in the previous 10 seconds and the next 10 seconds (bidirectional window; example in the report).",
  ],
  clarifyingQuestions: [
    "Are timestamps sorted / non-decreasing?",
    "Does 'present' mean any occurrence or only previously printed lines?",
    "Exact window: strict > 10 or >= 10?",
    "Is cleanup allowed lazily on each call, or must it be continuous?",
  ],
  approach: {
    whyStructure:
      "A hash map gives O(1) last-seen lookup. A FIFO queue of issuance events lets you drop map entries whose timestamps fall outside the active window, bounding memory by messages still inside the window.",
    invariant:
      "Map stores the last accepted timestamp for each message still considered live. Queue holds accepted events in time order for eviction.",
    operations:
      "On should_print(message, t): while queue front is older than t-10, pop and delete map entry if it still matches that timestamp. Then check map; if absent or t - last >= 10, accept, update map and push queue.",
    whyNotSimpler:
      "Map alone is correct for the first version but unbounded under unique keys. Scanning the whole map each time is O(distinct) per call.",
    tradeoffs:
      "Lazy cleanup couples work to call rate. Bidirectional windows need buffering or two passes — much harder online.",
  },
  algorithm: [
    "Maintain last: dict[str, int] and q: deque[(t, msg)].",
    "Evict events with time <= t - 10 (or < t - 10 per agreed rule) from q and last.",
    "Accept if msg not in last or t - last[msg] >= 10; then record.",
    "For follow-up 2, offline: for each index i, check no same message in (t_i-10, t_i+10) excluding itself — requires looking ahead.",
  ],
  pseudocode: `last = {}
q = deque()

function cleanup(t):
  while q and q[0].time <= t - 10:
    tt, m = q.popleft()
    if last.get(m) == tt: del last[m]

function should_print(msg, t):
  cleanup(t)
  if msg in last and t - last[msg] < 10: return false
  last[msg] = t
  q.append((t, msg))
  return true`,
  implementation: `from __future__ import annotations

from collections import deque


class Logger:
    """Print a message at most once per 10-second window; evict stale keys."""

    def __init__(self, window: int = 10) -> None:
        self.window = window
        self._last: dict[str, int] = {}
        self._q: deque[tuple[int, str]] = deque()

    def _cleanup(self, timestamp: int) -> None:
        limit = timestamp - self.window
        while self._q and self._q[0][0] <= limit:
            t, msg = self._q.popleft()
            if self._last.get(msg) == t:
                del self._last[msg]

    def should_print_message(self, timestamp: int, message: str) -> bool:
        self._cleanup(timestamp)
        last = self._last.get(message)
        if last is not None and timestamp - last < self.window:
            return False
        self._last[message] = timestamp
        self._q.append((timestamp, message))
        return True


def print_if_isolated(logs: list[tuple[str, int]], window: int = 10) -> list[tuple[str, int]]:
    """Follow-up style: keep message if no same message in (t-window, t+window). Offline."""
    n = len(logs)
    keep = [True] * n
    by_msg: dict[str, list[int]] = {}
    for i, (msg, _) in enumerate(logs):
        by_msg.setdefault(msg, []).append(i)
    for idxs in by_msg.values():
        for a in range(len(idxs)):
            for b in range(a + 1, len(idxs)):
                i, j = idxs[a], idxs[b]
                ti, tj = logs[i][1], logs[j][1]
                if abs(ti - tj) < window:  # within window of each other
                    # Report example uses "previous and next 10 seconds"; treat distance < 10 as conflict.
                    keep[i] = False
                    keep[j] = False
    # Re-read report: print only if NOT in prev 10 AND NOT in next 10.
    # Example: Java@1,12,23 → only 12 and 23? Report says Java 12 and Java 23.
    out: list[tuple[str, int]] = []
    for i, (msg, t) in enumerate(logs):
        ok = True
        for j, (msg2, t2) in enumerate(logs):
            if i == j or msg2 != msg:
                continue
            if abs(t - t2) <= window and t != t2:
                # same message within ±window
                if t2 != t:
                    ok = False
                    break
        if ok:
            # Still need: nothing in (t-window, t) or (t, t+window)
            for j, (msg2, t2) in enumerate(logs):
                if i == j or msg2 != msg:
                    continue
                if t - window <= t2 <= t + window and t2 != t:
                    ok = False
                    break
        if ok:
            out.append((msg, t))
    return out


if __name__ == "__main__":
    log = Logger()
    assert log.should_print_message(1, "foo") is True
    assert log.should_print_message(2, "bar") is True
    assert log.should_print_message(3, "foo") is False
    assert log.should_print_message(11, "foo") is True
    print("ok")
`,
  complexity: [
    {
      operation: "should_print_message",
      time: "Amortized O(1) per call (each event enqueued/dequeued once)",
      space: "O(U) for U messages still inside the window",
    },
    {
      operation: "bidirectional offline filter",
      time: "O(n²) naïve or O(n log n) with per-message sorted timestamps",
      space: "O(n)",
      notes: "Online bidirectional needs future knowledge or delay buffer of 10s.",
    },
  ],
  dryRun: {
    setup: "Messages: foo@1, bar@2, foo@3, foo@11 with window 10.",
    steps: [
      "foo@1 accepted; map{foo:1}; queue[(1,foo)].",
      "bar@2 accepted.",
      "foo@3 rejected (3-1 < 10).",
      "foo@11: cleanup may drop events ≤ 1; 11-1 >= 10 → accept.",
    ],
    result:
      "Printed: foo, bar, foo at t=11. Map never retains forever-stale unique keys once cleanup runs.",
  },
  edgeCases: [
    "First occurrence of a message.",
    "Exact boundary timestamp - last == 10.",
    "All unique messages (cleanup must shrink map).",
    "Empty message string.",
    "Bidirectional follow-up with overlapping same-message times (report example).",
  ],
  followUps: [
    {
      text: "Memory cleanup under unique messages.",
      kind: "reported",
      guidance: "Queue + map eviction as above; interviewer accepted cleanup discussion then code.",
    },
    {
      text: "Require silence in both previous and next 10 seconds.",
      kind: "reported",
      guidance:
        "Needs lookahead; report example keeps Java@12 and Java@23 from Java@1,12,23 with Leetcode@6,15.",
    },
  ],
  checklist: [
    "Map for last print time.",
    "Explain unbounded memory without cleanup.",
    "Implement queue-based eviction.",
    "Clarify window inclusivity.",
    "Separate reported bidirectional follow-up from the base API.",
  ],
  relatedQuestionIds: ["rp.moving-average", "rp.lru-cache"],
};
