import type { DesignCodingQuestion } from "../../../schema";

export const insertRemoveGetMin: DesignCodingQuestion = {
  id: "dc.insert-remove-getmin",
  slug: "insert-remove-getmin",
  title: "Insert, Remove, and Get-Min",
  classification: "reported-google",
  level: "L3",
  interviewFormat: "Onsite coding",
  priority: "important",
  verification: "verified",
  categories: ["misc-ds-design", "caches-eviction"],
  sources: [
    {
      label: "LeetCode Discuss — Google L3 Bangalore Apr 2024",
      url: "https://leetcode.com/discuss/post/5089279/Google-or-L3-or-Banglore-or-Apr-2024-Reject/",
      note: "Sorting-based counting problem: implement insert, remove, getMin. Candidate optimized to O(1) with bucket sort + LRU-style structure. Reported at L3 — not an L4/L5-specific question.",
    },
  ],
  coreApproach:
    "If values lie in a bounded universe, keep frequency buckets and a handle to the current minimum non-empty bucket; otherwise heap + lazy delete, or balanced tree. Candidate path: buckets + extra structure for O(1).",
  summaryComplexity: "Target O(1) per op with bounded keys / buckets · else O(log n)",
  originalPrompt:
    "Implement three APIs: insert, remove, and getMin (medium difficulty; sorting/counting flavored). Candidate reached O(1) using bucket sort and an LRU-cache-like auxiliary structure.",
  originalRequirements: [
    "insert(value)",
    "remove(value)",
    "getMin() → current minimum among stored values (multiset semantics likely — confirm).",
  ],
  clarifyingQuestions: [
    "Multiset or set? Does remove delete one occurrence or all?",
    "Value domain / bounds?",
    "getMin on empty — error or sentinel?",
    "Are values only integers?",
  ],
  approach: {
    whyStructure:
      "getMin with arbitrary inserts/removes is a heap or tree unless the domain is small enough for buckets. Frequency map + ordered bucket index matches 'bucket sort' intuition.",
    invariant:
      "freq[x] is count of x. Buckets or a TreeMap of present keys. min pointer/bucket tracks smallest key with freq>0.",
    operations:
      "insert: bump freq; if first occurrence, add to key index and maybe update min. remove: decrement; if zero, delete key and advance min if needed.",
    whyNotSimpler:
      "Scanning a list for min is O(n). A min-heap alone makes remove-arbitrary O(n) without an index.",
    tradeoffs:
      "O(1) getMin with advancing min over a dense universe can be amortized if you walk empty buckets carefully; sparse universes prefer TreeMap O(log u).",
  },
  algorithm: [
    "freq: dict[int,int]; present keys in SortedList/TreeMap; or array of buckets if 0..U.",
    "insert/remove update freq and key index.",
    "getMin: first key in sorted index / current min bucket.",
  ],
  pseudocode: `freq = {}
keys = sorted set

insert(x):
  freq[x] += 1
  if freq[x]==1: keys.add(x)

remove(x):
  if x not in freq: return
  freq[x] -= 1
  if freq[x]==0: del freq[x]; keys.remove(x)

getMin():
  return keys.min()`,
  implementation: `from __future__ import annotations

from sortedcontainers import SortedSet  # optional dependency
# Pure-Python fallback: maintain min explicitly + heap of candidates.


import heapq


class InsertRemoveGetMin:
    """Multiset with insert / remove / getMin. O(log n) via heap + lazy delete."""

    def __init__(self) -> None:
        self._freq: dict[int, int] = {}
        self._heap: list[int] = []

    def insert(self, x: int) -> None:
        self._freq[x] = self._freq.get(x, 0) + 1
        heapq.heappush(self._heap, x)

    def remove(self, x: int) -> None:
        if self._freq.get(x, 0) == 0:
            return
        self._freq[x] -= 1
        if self._freq[x] == 0:
            del self._freq[x]
        # lazy: outdated heap entries skipped in get_min

    def get_min(self) -> int:
        while self._heap:
            m = self._heap[0]
            if self._freq.get(m, 0) > 0:
                return m
            heapq.heappop(self._heap)
        raise KeyError("empty")


class InsertRemoveGetMinBounded:
    """O(1) amortized getMin when values are in [0, U]."""

    def __init__(self, universe: int) -> None:
        self._u = universe
        self._freq = [0] * (universe + 1)
        self._min = universe + 1
        self._size = 0

    def insert(self, x: int) -> None:
        if not 0 <= x <= self._u:
            raise ValueError("out of universe")
        if self._freq[x] == 0:
            self._min = min(self._min, x)
        self._freq[x] += 1
        self._size += 1

    def remove(self, x: int) -> None:
        if not 0 <= x <= self._u or self._freq[x] == 0:
            return
        self._freq[x] -= 1
        self._size -= 1
        if self._freq[x] == 0 and x == self._min:
            while self._min <= self._u and self._freq[self._min] == 0:
                self._min += 1
            if self._size == 0:
                self._min = self._u + 1

    def get_min(self) -> int:
        if self._size == 0:
            raise KeyError("empty")
        return self._min


if __name__ == "__main__":
    s = InsertRemoveGetMin()
    s.insert(3)
    s.insert(1)
    s.insert(2)
    assert s.get_min() == 1
    s.remove(1)
    assert s.get_min() == 2
    b = InsertRemoveGetMinBounded(10)
    b.insert(5)
    b.insert(2)
    assert b.get_min() == 2
    print("ok")
`,
  complexity: [
    {
      operation: "insert/remove/getMin (heap + lazy)",
      time: "O(log n) insert; amortized O(log n) getMin",
      space: "O(n)",
    },
    {
      operation: "bounded bucket version",
      time: "O(1) insert; remove/getMin O(1) amortized if min walks empty slots rarely",
      space: "O(U)",
    },
  ],
  dryRun: {
    setup: "insert 3,1,2; remove 1; getMin.",
    steps: ["freq {3:1,1:1,2:1}; min=1.", "remove 1; min becomes 2."],
    result: "getMin → 2.",
  },
  edgeCases: [
    "remove missing value.",
    "getMin on empty.",
    "Duplicates (multiset).",
    "All values equal.",
  ],
  followUps: [
    {
      text: "Push all APIs to O(1) with extra space (candidate: buckets + LRU-like).",
      kind: "reported",
      guidance: "Needs a bounded (or discretized) key universe plus direct min handle.",
    },
  ],
  checklist: [
    "Label level as L3.",
    "Clarify multiset semantics.",
    "State complexity per operation.",
    "Mention bounded vs general domains.",
  ],
  relatedQuestionIds: ["rp.insert-delete-getrandom"],
};
