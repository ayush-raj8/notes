import type { DesignCodingQuestion } from "../../../schema";

export const grepContext: DesignCodingQuestion = {
  id: "dc.grep-context",
  slug: "grep-context",
  title: "Grep with Surrounding Context",
  classification: "reported-google",
  level: "L4",
  interviewFormat: "Onsite coding",
  priority: "must-revise",
  verification: "verified",
  categories: ["file-processing"],
  sources: [
    {
      label: "Reddit r/leetcode — Google L4 coding experience",
      url: "https://www.reddit.com/r/leetcode/comments/1l7tc8n/google_l4_coding_experience/",
      note: "Round 2: implement something like grep word --context <Num_lines>; interviewer pushed on memory and how file reading works.",
    },
  ],
  coreApproach:
    "Find match line indices, emit the union of [i-k, i+k] windows in order without duplicating lines; for huge files, stream with a k-line ring buffer of previous lines plus deferred emission.",
  summaryComplexity: "in-memory O(n) time · streaming O(n) time with O(k) buffer",
  originalPrompt:
    "Implement behavior like `grep word --context <Num_lines>`: find a word in a file and return surrounding lines as indicated by the context count. The candidate report emphasizes practical file/memory discussion more than a pure algorithm puzzle.",
  originalRequirements: [
    "Locate lines containing the search word.",
    "Include a configurable number of surrounding context lines (grep --context).",
    "Discuss memory usage and underlying file reading (per interviewer follow-through in the report).",
  ],
  reportedFollowUps: [
    "Memory usage and how the file is read / how grep works in practice.",
  ],
  practiceVariations: [
    "Separate -B and -A counts.",
    "Print '--' between non-overlapping groups.",
    "Stream input too large to hold in memory.",
  ],
  clarifyingQuestions: [
    "Substring match vs whole-word? Case sensitivity?",
    "Are overlapping context windows merged?",
    "In-memory list of lines or a file path / stream?",
    "Include line numbers in output?",
  ],
  approach: {
    whyStructure:
      "Context windows on a line array are a sweep: track the next line to emit so overlaps do not duplicate. Streaming needs a buffer of the last k lines because past context is no longer on disk once you advance.",
    invariant:
      "Every emitted line appears once, in file order. Every match's window is covered.",
    operations:
      "Pass 1 or single pass: for each match at i, emit from max(next_emit, i-k) through i+k. Streaming: keep deque of last k lines; on match flush needed previous lines; remember pending after-context count.",
    whyNotSimpler:
      "Printing full windows independently duplicates lines. Loading multi-GB files fully may be unacceptable — the report's interviewer cared about that.",
    tradeoffs:
      "Two-pass (record matches then emit) is simple. One-pass streaming is harder but matches production grep.",
  },
  algorithm: [
    "Collect match indices (or stream).",
    "Maintain last_emitted = -1.",
    "For each match i: for j in max(0,i-k)..min(n-1,i+k): if j > last_emitted: emit; last_emitted = j.",
  ],
  pseudocode: `function grep_context(lines, target, k):
  matches = [i for i,l in enumerate(lines) if target in l]
  out = []; last = -1
  for i in matches:
    lo = max(0, i-k); hi = min(len(lines)-1, i+k)
    for j in range(max(last+1, lo), hi+1):
      out.append(lines[j])
    last = hi
  return out`,
  implementation: `from __future__ import annotations

from collections import deque
from typing import Iterable, Iterator


def grep_with_context(lines: list[str], target: str, k: int) -> list[str]:
    if k < 0:
        raise ValueError("k must be >= 0")
    matches = [i for i, line in enumerate(lines) if target in line]
    out: list[str] = []
    last = -1
    n = len(lines)
    for i in matches:
        lo = max(0, i - k)
        hi = min(n - 1, i + k)
        start = max(last + 1, lo)
        for j in range(start, hi + 1):
            out.append(lines[j])
        last = max(last, hi)
    return out


def grep_stream(lines: Iterable[str], target: str, k: int) -> Iterator[str]:
    """Streaming sketch: O(k) memory; merges overlapping windows."""
    prev: deque[str] = deque(maxlen=k)
    pending_after = 0
    last_was_emit = False
    for line in lines:
        is_match = target in line
        if is_match:
            while prev:
                yield prev.popleft()
            yield line
            pending_after = k
            last_was_emit = True
        elif pending_after > 0:
            yield line
            pending_after -= 1
            last_was_emit = True
        else:
            prev.append(line)
            last_was_emit = False
        # Note: true grep also needs to hold after-context that might overlap future matches;
        # production code tracks a look-ahead or delays after-context emission carefully.
    _ = last_was_emit


if __name__ == "__main__":
    lines = ["boot ok", "disk warn", "net ok", "cpu ok", "fan warn", "done"]
    assert grep_with_context(lines, "warn", 1) == lines
    assert grep_with_context(["a", "bx", "c", "d", "e", "fx", "g"], "x", 0) == ["bx", "fx"]
    print("ok")
`,
  complexity: [
    {
      operation: "grep_with_context (in memory)",
      time: "O(total characters)",
      space: "O(n) for input plus O(output)",
    },
    {
      operation: "streaming variant",
      time: "O(total characters)",
      space: "O(k) buffer (+ pending after-context)",
      notes: "Interviewers may probe OS page cache / reading line by line.",
    },
  ],
  dryRun: {
    setup: "lines with two 'warn' matches and k=1 covering the whole file.",
    steps: [
      "Match at index 1 → window [0,2].",
      "Match at index 4 → window [3,5]; continue from 3.",
      "All lines emitted once.",
    ],
    result: "Merged context, original order, no duplicates.",
  },
  edgeCases: [
    "k = 0 (matches only).",
    "No matches → empty output.",
    "Overlapping windows.",
    "Match on first/last line.",
    "Empty target string (define behavior).",
  ],
  followUps: [
    {
      text: "Memory / file reading discussion.",
      kind: "reported",
      guidance: "Prefer streaming; do not load entire multi-GB files; talk through buffering.",
    },
    {
      text: "Asymmetric -B/-A and group separators.",
      kind: "practice",
    },
  ],
  checklist: [
    "Merge overlapping context.",
    "No duplicate lines.",
    "Discuss streaming memory.",
    "Match semantics clarified.",
  ],
};
