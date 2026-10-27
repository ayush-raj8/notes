import type { DesignCodingQuestion } from "../../../schema";

export const mostRecentlySearched: DesignCodingQuestion = {
  id: "dc.most-recently-searched",
  slug: "most-recently-searched",
  title: "Most Recently Searched Items (Cache)",
  classification: "reported-google",
  level: "unspecified",
  interviewFormat: "Phone screen — SWE3 (CA, US)",
  priority: "must-revise",
  verification: "verified",
  categories: ["caches-eviction"],
  sources: [
    {
      label: "LeetCode Discuss — Most Recently Searched Item (Cache)",
      url: "https://leetcode.com/discuss/post/5068865/Google-SWE3-CA-US-or-Phone-Screen-or-Most-Recently-Searched-Item%28Cache%29/",
      note: "Top N most recently searched (not frequently). Ordered dict / LRU-style; follow-up without ordered dict → hashmap + doubly linked list.",
    },
  ],
  leetcodeUrl: "https://leetcode.com/problems/lru-cache/",
  coreApproach:
    "Capacity-N recency list: on search, move item to the most-recent end; evict least-recent when over capacity; query returns the current ordered top N.",
  summaryComplexity: "search/insert O(1) · list top N O(N) · hashmap + DLL",
  originalPrompt:
    "Design a data structure that returns the top N most recently searched items. Capacity is N. Searching updates recency; interviewer clarified recently, not frequently (heap for frequency is the wrong first instinct).",
  originalRequirements: [
    "Max size n.",
    "On search/insert: update key and move to the recent end; if new and over capacity, evict the least recent.",
    "Return the top N elements from the structure (the current contents in recency order).",
  ],
  reportedFollowUps: [
    "Time and space complexity.",
    "Solve without an ordered dictionary: dictionary + doubly linked list with prev/next.",
  ],
  clarifyingQuestions: [
    "Is N both capacity and the size of the returned list?",
    "What is stored — query string only, or key→payload?",
    "Does searching an existing item only bump recency, or also update a value?",
    "Return order: most-recent first?",
  ],
  approach: {
    whyStructure:
      "Recency needs O(1) move-to-front and O(1) eviction of the opposite end. A doubly linked list plus hash map (classic LRU) does that. An ordered dict is the same idea with library help.",
    invariant:
      "List order is strictly recency. Map maps key → node. Length ≤ capacity.",
    operations:
      "search(key): if present, unlink node and attach at MRU; else create, attach, and if len > cap unlink LRU. top(): walk from MRU to LRU.",
    whyNotSimpler:
      "A max-heap by frequency solves a different problem. An array + scan is O(n) per update. A timestamp map without ordering needs a sort on every query.",
    tradeoffs:
      "DLL+map uses more pointers than OrderedDict but is what interviewers often want to hear.",
  },
  algorithm: [
    "Nodes with key, prev, next; sentinel head/tail.",
    "Map key → node.",
    "On hit: remove + add_to_mru.",
    "On miss: add; if over capacity remove_lru.",
  ],
  pseudocode: `map = {}
dll = empty sentinel list
cap = N

function search(key):
  if key in map: move map[key] to MRU
  else:
    node = new(key); add MRU; map[key]=node
    if len > cap: evict LRU from map and dll
  return list keys from MRU to LRU`,
  implementation: `from __future__ import annotations

from typing import Optional


class _Node:
    __slots__ = ("key", "prev", "next")

    def __init__(self, key: str) -> None:
        self.key = key
        self.prev: Optional[_Node] = None
        self.next: Optional[_Node] = None


class MostRecentlySearched:
    def __init__(self, capacity: int) -> None:
        if capacity < 0:
            raise ValueError("capacity must be >= 0")
        self.capacity = capacity
        self._map: dict[str, _Node] = {}
        self._head = _Node("")  # MRU side after head
        self._tail = _Node("")  # LRU side before tail
        self._head.next = self._tail
        self._tail.prev = self._head

    def _remove(self, node: _Node) -> None:
        assert node.prev is not None and node.next is not None
        node.prev.next = node.next
        node.next.prev = node.prev

    def _add_mru(self, node: _Node) -> None:
        nxt = self._head.next
        assert nxt is not None
        node.prev = self._head
        node.next = nxt
        self._head.next = node
        nxt.prev = node

    def search(self, key: str) -> list[str]:
        if self.capacity == 0:
            return []
        if key in self._map:
            node = self._map[key]
            self._remove(node)
            self._add_mru(node)
        else:
            node = _Node(key)
            self._map[key] = node
            self._add_mru(node)
            if len(self._map) > self.capacity:
                lru = self._tail.prev
                assert lru is not None and lru is not self._head
                self._remove(lru)
                del self._map[lru.key]
        return self.top()

    def top(self) -> list[str]:
        out: list[str] = []
        cur = self._head.next
        while cur is not None and cur is not self._tail:
            out.append(cur.key)
            cur = cur.next
        return out


if __name__ == "__main__":
    s = MostRecentlySearched(3)
    assert s.search("a") == ["a"]
    assert s.search("b") == ["b", "a"]
    assert s.search("c") == ["c", "b", "a"]
    assert s.search("a") == ["a", "c", "b"]
    assert s.search("d") == ["d", "a", "c"]
    print("ok")
`,
  complexity: [
    { operation: "search (hit or miss)", time: "O(1)", space: "O(1) extra" },
    { operation: "top / return list", time: "O(N)", space: "O(N)" },
    {
      operation: "overall space",
      time: "—",
      space: "O(N) nodes + map",
    },
  ],
  dryRun: {
    setup: "capacity 3; searches a,b,c,a,d.",
    steps: [
      "a → [a]; b → [b,a]; c → [c,b,a].",
      "a hit → move a to MRU → [a,c,b].",
      "d miss → [d,a,c]; evict b.",
    ],
    result: "Top list is always most-recent first within capacity.",
  },
  edgeCases: [
    "capacity 0.",
    "Repeated search of the same key.",
    "Exactly at capacity then new key.",
    "Single-slot cache.",
  ],
  followUps: [
    {
      text: "Complexity of the ordered-dict approach.",
      kind: "reported",
    },
    {
      text: "Implement without ordered dict (map + DLL).",
      kind: "reported",
      guidance: "This is the implementation above.",
    },
  ],
  checklist: [
    "Clarify recently vs frequently.",
    "DLL + hashmap (or OrderedDict) invariant.",
    "Evict LRU on overflow.",
    "O(1) updates.",
  ],
  relatedQuestionIds: ["rp.lru-cache", "rp.insert-delete-getrandom"],
};
