import type { DesignCodingQuestion } from "../../../schema";

export const rangePreprocessQuery: DesignCodingQuestion = {
  id: "dc.range-preprocess-query",
  slug: "range-preprocess-query",
  title: "Range-Related Preprocess and Query Design",
  classification: "reported-google",
  level: "L4",
  interviewFormat: "Onsite technical (Bangalore, March 2020)",
  priority: "important",
  verification: "partially-verified",
  categories: ["intervals-range"],
  sources: [
    {
      label: "LeetCode Discuss — Google SWE L4 Bangalore March 2020 offer",
      url: "https://leetcode.com/discuss/post/550220/google-swe-l4-bangalore-march-2020-offer/",
      note: "Onsite round 1: mobile app + library scenario; class design to receive/preprocess bulk data then answer queries; candidate used a treemap for range-related queries. Exact API not published (confidential).",
    },
  ],
  leetcodeUrl: "https://leetcode.com/problems/range-module/",
  coreApproach:
    "Separate bulk load/preprocess from online queries; keep keys ordered (TreeMap / sorted map / sorted list) so range lookups and neighbor searches are logarithmic. Exact operations were not disclosed — treat concrete APIs below as practice scaffolding labeled as such.",
  summaryComplexity:
    "Typical ordered-map range ops O(log n) · bulk build O(n log n) — exact ops unspecified in source",
  originalPrompt:
    "Scenario with a mobile app and a required library. Translate to a class design: receive and preprocess initial bulk data, then process subsequent queries using the preprocessed structure. Candidate settled on a treemap for efficient range-related information. Little coding time remained.",
  originalRequirements: [
    "Design how to ingest initial bulk data.",
    "Preprocess for later queries.",
    "Support range-related queries efficiently (candidate choice: treemap).",
    "Exact query signatures were not published — do not invent them as 'the Google question'.",
  ],
  clarifyingQuestions: [
    "What are the query types (stabbing, overlap, sum, nearest)?",
    "Are intervals closed/open? Can they overlap?",
    "Is data static after bulk load?",
    "Latency vs memory constraints on mobile?",
  ],
  approach: {
    whyStructure:
      "Range queries need ordered keys. A balanced tree / TreeMap gives floor/ceiling and range scans. Bulk load can sort once then build.",
    invariant:
      "Depends on the real API. Practice scaffold: disjoint tracked intervals in a sorted map from start → end.",
    operations:
      "Practice RangeModule-like add/remove/query exists; or preload points and answer range counts with fenwick/segment tree — pick after clarifying.",
    whyNotSimpler:
      "Linear scan of bulk data per query is too slow if query volume is high. Hash maps do not preserve order for ranges.",
    tradeoffs:
      "TreeMap is flexible for interval endpoints. Segment trees win for numeric range aggregates on a compressed axis.",
  },
  algorithm: [
    "Clarify query algebra first (this was most of the real interview).",
    "Sort endpoints; store in TreeMap.",
    "Implement the agreed operations; review edge cases with the interviewer.",
  ],
  pseudocode: `# Practice scaffold only — not the unpublished Google API
map: sorted start -> end

add(left, right): merge overlapping
remove(left, right): split/cut
query(left, right): true if fully covered`,
  implementation: `from __future__ import annotations

from sortedcontainers import SortedDict  # type: ignore  # may be unavailable
# Avoid external deps in notes site demos — use a sorted list of (start, end).


class RangeTracker:
    """Practice scaffold inspired by range preprocessing — NOT a quote of the interview API."""

    def __init__(self) -> None:
        self._intervals: list[tuple[int, int]] = []  # sorted, disjoint, half-open [s,e)

    def load_bulk(self, intervals: list[tuple[int, int]]) -> None:
        pts = sorted(intervals)
        merged: list[tuple[int, int]] = []
        for s, e in pts:
            if s >= e:
                continue
            if not merged or s > merged[-1][1]:
                merged.append((s, e))
            else:
                merged[-1] = (merged[-1][0], max(merged[-1][1], e))
        self._intervals = merged

    def covers(self, left: int, right: int) -> bool:
        """True if [left, right) is fully inside the union."""
        if left >= right:
            return True
        for s, e in self._intervals:
            if e <= left:
                continue
            if s > left:
                return False
            left = e
            if left >= right:
                return True
        return left >= right

    def any_overlap(self, left: int, right: int) -> bool:
        for s, e in self._intervals:
            if s < right and left < e:
                return True
        return False


if __name__ == "__main__":
    r = RangeTracker()
    r.load_bulk([(1, 3), (2, 5), (10, 12)])
    assert r.covers(1, 5) is True
    assert r.covers(4, 11) is False
    assert r.any_overlap(11, 15) is True
    print("ok")
`,
  complexity: [
    {
      operation: "load_bulk",
      time: "O(n log n)",
      space: "O(n)",
    },
    {
      operation: "covers / any_overlap (sorted disjoint list)",
      time: "O(n) scan; O(log n) with tree keyed by start",
      space: "O(1) extra",
    },
  ],
  dryRun: {
    setup: "Bulk intervals [1,3), [2,5), [10,12).",
    steps: [
      "Merge first two → [1,5), keep [10,12).",
      "covers(1,5) true; covers(4,11) false because of a gap.",
    ],
    result: "Preprocess once; answer coverage queries on the union.",
  },
  edgeCases: [
    "Empty bulk load.",
    "Touching intervals [1,2)+[2,3).",
    "Query empty range.",
    "Unpublished real API — do not claim LeetCode Range Module was the question.",
  ],
  followUps: [
    {
      text: "Review and improve the coded fragment under time pressure (as in report).",
      kind: "reported",
    },
    {
      text: "Practice LeetCode Range Module / My Calendar for technique.",
      kind: "practice",
      guidance: "Related practice only — not verified as the same prompt.",
    },
  ],
  checklist: [
    "Mark verification as partial — API unknown.",
    "Spend time clarifying query types.",
    "Justify ordered map for ranges.",
    "Do not present Range Module as the original question.",
  ],
  relatedQuestionIds: ["rp.range-module"],
};
