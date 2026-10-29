import type { DesignCodingQuestion } from "../../../schema";

function practice(partial: DesignCodingQuestion): DesignCodingQuestion {
  return partial;
}

export const relatedPracticeQuestions: DesignCodingQuestion[] = [
  practice({
    id: "rp.lru-cache",
    slug: "lru-cache",
    title: "LRU Cache",
    classification: "related-practice",
    level: "unspecified",
    priority: "must-revise",
    verification: "verified",
    categories: ["caches-eviction"],
    sources: [
      {
        label: "LeetCode 146 — LRU Cache",
        url: "https://leetcode.com/problems/lru-cache/",
        note: "Practice for Most Recently Searched / ordered dict follow-up.",
      },
    ],
    leetcodeUrl: "https://leetcode.com/problems/lru-cache/",
    coreApproach: "Hash map + doubly linked list; get/put move to MRU; evict LRU at capacity.",
    summaryComplexity: "get/put O(1)",
    originalPrompt:
      "Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.",
    originalRequirements: [
      "get(key)",
      "put(key, value)",
      "Evict least recently used when over capacity.",
    ],
    clarifyingQuestions: ["Capacity 0?", "Update on put of existing key?"],
    approach: {
      whyStructure: "Same spine as most-recently-searched.",
      invariant: "DLL order is recency; map points to nodes.",
      operations: "get/put splice to MRU; evict from LRU end.",
      whyNotSimpler: "List-only is O(n) lookup.",
      tradeoffs: "OrderedDict is acceptable in Python interviews if you can explain the DLL.",
    },
    algorithm: ["Sentinel DLL + dict", "move_to_mru", "evict"],
    pseudocode: `get: move MRU; put: update or add; evict if over cap`,
    implementation: `class LRUCache:
    def __init__(self, capacity: int):
        self.cap = capacity
        self.data: dict[int, int] = {}
        self.order: list[int] = []  # demo only; prefer DLL for O(1)

    def get(self, key: int) -> int:
        if key not in self.data:
            return -1
        self.order.remove(key)
        self.order.append(key)
        return self.data[key]

    def put(self, key: int, value: int) -> None:
        if key in self.data:
            self.order.remove(key)
        elif len(self.data) >= self.cap:
            old = self.order.pop(0)
            del self.data[old]
        self.data[key] = value
        self.order.append(key)
`,
    complexity: [
      { operation: "get/put with DLL", time: "O(1)", space: "O(capacity)" },
      {
        operation: "list demo above",
        time: "O(capacity)",
        notes: "Replace with DLL in interview.",
      },
    ],
    dryRun: {
      setup: "cap 2; put 1; put 2; get 1; put 3.",
      steps: ["Evict 2 after get moves 1 to MRU."],
      result: "Cache holds 1 and 3.",
    },
    edgeCases: ["capacity 1", "get missing"],
    followUps: [{ text: "LFU cache", kind: "practice" }],
    checklist: ["Not a Google PYQ by itself", "DLL + map", "O(1) ops"],
    relatedQuestionIds: ["dc.most-recently-searched"],
  }),
  practice({
    id: "rp.circular-queue",
    slug: "circular-queue",
    title: "Design Circular Queue",
    classification: "related-practice",
    level: "unspecified",
    priority: "optional",
    verification: "verified",
    categories: ["misc-ds-design"],
    sources: [
      {
        label: "LeetCode 622 — Design Circular Queue",
        url: "https://leetcode.com/problems/design-circular-queue/",
      },
    ],
    leetcodeUrl: "https://leetcode.com/problems/design-circular-queue/",
    coreApproach: "Fixed array with head index and count (or head/tail).",
    summaryComplexity: "enQueue/deQueue O(1)",
    originalPrompt: "Design your implementation of the circular queue.",
    originalRequirements: ["enQueue", "deQueue", "Front", "Rear", "isEmpty", "isFull"],
    clarifyingQuestions: ["Max size fixed?"],
    approach: {
      whyStructure: "Ring buffer avoids shifting.",
      invariant: "count items from head.",
      operations: "Modulo indexing.",
      whyNotSimpler: "Shifting array is O(n).",
      tradeoffs: "head/tail vs head/count.",
    },
    algorithm: ["buf array", "head", "count"],
    pseudocode: `enQueue: buf[(head+count)%k]=v; count++`,
    implementation: `class MyCircularQueue:
    def __init__(self, k: int):
        self.buf = [0] * k
        self.k = k
        self.head = 0
        self.count = 0

    def enQueue(self, value: int) -> bool:
        if self.isFull():
            return False
        self.buf[(self.head + self.count) % self.k] = value
        self.count += 1
        return True

    def deQueue(self) -> bool:
        if self.isEmpty():
            return False
        self.head = (self.head + 1) % self.k
        self.count -= 1
        return True

    def Front(self) -> int:
        return -1 if self.isEmpty() else self.buf[self.head]

    def Rear(self) -> int:
        if self.isEmpty():
            return -1
        return self.buf[(self.head + self.count - 1) % self.k]

    def isEmpty(self) -> bool:
        return self.count == 0

    def isFull(self) -> bool:
        return self.count == self.k
`,
    complexity: [{ operation: "all ops", time: "O(1)", space: "O(k)" }],
    dryRun: {
      setup: "k=3; enQueue 1,2,3; deQueue; enQueue 4.",
      steps: ["Full after 3; after deQueue rear becomes 4."],
      result: "Ring reuse works.",
    },
    edgeCases: ["empty Front", "full enQueue"],
    followUps: [{ text: "Thread-safe ring buffer", kind: "practice" }],
    checklist: ["Related practice only", "Modulo arithmetic"],
  }),
  practice({
    id: "rp.insert-delete-getrandom",
    slug: "insert-delete-getrandom",
    title: "Insert Delete GetRandom O(1)",
    classification: "related-practice",
    level: "unspecified",
    priority: "important",
    verification: "verified",
    categories: ["misc-ds-design", "trees-random-selection"],
    sources: [
      {
        label: "LeetCode 380 — Insert Delete GetRandom O(1)",
        url: "https://leetcode.com/problems/insert-delete-getrandom-o1/",
      },
    ],
    leetcodeUrl: "https://leetcode.com/problems/insert-delete-getrandom-o1/",
    coreApproach: "Array for random index + map value→index; delete via swap with last.",
    summaryComplexity: "insert/remove/getRandom O(1) average",
    originalPrompt: "Implement RandomizedSet with insert, remove, getRandom in average O(1).",
    originalRequirements: ["insert", "remove", "getRandom uniform"],
    clarifyingQuestions: ["Duplicates allowed?"],
    approach: {
      whyStructure: "Same swap-delete idea as O(1) leaf list updates.",
      invariant: "map[val] = index in arr.",
      operations: "swap-delete on remove.",
      whyNotSimpler: "List alone makes remove O(n).",
      tradeoffs: "Average O(1) hash.",
    },
    algorithm: ["arr", "idx map", "swap-pop"],
    pseudocode: `remove: swap with last; pop; update map`,
    implementation: `import random

class RandomizedSet:
    def __init__(self):
        self.arr: list[int] = []
        self.pos: dict[int, int] = {}

    def insert(self, val: int) -> bool:
        if val in self.pos:
            return False
        self.pos[val] = len(self.arr)
        self.arr.append(val)
        return True

    def remove(self, val: int) -> bool:
        if val not in self.pos:
            return False
        i = self.pos[val]
        last = self.arr[-1]
        self.arr[i] = last
        self.pos[last] = i
        self.arr.pop()
        del self.pos[val]
        return True

    def getRandom(self) -> int:
        return random.choice(self.arr)
`,
    complexity: [{ operation: "all", time: "O(1) average", space: "O(n)" }],
    dryRun: {
      setup: "insert 1,2; remove 1; getRandom.",
      steps: ["After remove, arr is [2]."],
      result: "Only 2 remains.",
    },
    edgeCases: ["remove missing", "single element"],
    followUps: [{ text: "Duplicates allowed (LC 381)", kind: "practice" }],
    checklist: ["Related practice", "Swap-delete"],
    relatedQuestionIds: ["dc.random-tree-node", "dc.insert-remove-getmin"],
  }),
  practice({
    id: "rp.moving-average",
    slug: "moving-average",
    title: "Moving Average from Data Stream",
    classification: "related-practice",
    level: "unspecified",
    priority: "optional",
    verification: "verified",
    categories: ["rate-limiting-time"],
    sources: [
      {
        label: "LeetCode 346 — Moving Average from Data Stream",
        url: "https://leetcode.com/problems/moving-average-from-data-stream/",
      },
    ],
    leetcodeUrl: "https://leetcode.com/problems/moving-average-from-data-stream/",
    coreApproach: "Queue of last k values + running sum.",
    summaryComplexity: "next O(1)",
    originalPrompt: "Compute the moving average of the last k values from a stream.",
    originalRequirements: ["next(val) → average of window"],
    clarifyingQuestions: ["Window size fixed?"],
    approach: {
      whyStructure: "Deque drops oldest like rate-limiter cleanup queue.",
      invariant: "sum matches queue contents.",
      operations: "push; if over k pop left and subtract.",
      whyNotSimpler: "Resum from scratch is O(k).",
      tradeoffs: "Circular buffer avoids deque alloc.",
    },
    algorithm: ["deque", "sum", "size k"],
    pseudocode: `next: append; if len>k: sum-=popleft; return sum/len`,
    implementation: `from collections import deque

class MovingAverage:
    def __init__(self, size: int):
        self.k = size
        self.q: deque[int] = deque()
        self.s = 0

    def next(self, val: int) -> float:
        self.q.append(val)
        self.s += val
        if len(self.q) > self.k:
            self.s -= self.q.popleft()
        return self.s / len(self.q)
`,
    complexity: [{ operation: "next", time: "O(1)", space: "O(k)" }],
    dryRun: {
      setup: "k=3; 1,10,3,5",
      steps: ["Averages 1, 5.5, ~4.66, 6."],
      result: "Window slides.",
    },
    edgeCases: ["first elements before k filled"],
    followUps: [{ text: "Time-based window instead of count", kind: "practice" }],
    checklist: ["Related practice for cleanup queues"],
    relatedQuestionIds: ["dc.logger-rate-limiter-cleanup"],
  }),
  practice({
    id: "rp.peeking-iterator",
    slug: "peeking-iterator",
    title: "Peeking Iterator",
    classification: "related-practice",
    level: "unspecified",
    priority: "optional",
    verification: "verified",
    categories: ["iterators"],
    sources: [
      {
        label: "LeetCode 284 — Peeking Iterator",
        url: "https://leetcode.com/problems/peeking-iterator/",
      },
    ],
    leetcodeUrl: "https://leetcode.com/problems/peeking-iterator/",
    coreApproach: "Buffer one lookahead element from the underlying iterator.",
    summaryComplexity: "peek/next/hasNext O(1) amortized",
    originalPrompt: "Given an iterator, design a peeking iterator supporting peek.",
    originalRequirements: ["peek", "next", "hasNext"],
    clarifyingQuestions: ["Iterator of what type?"],
    approach: {
      whyStructure: "Peek must not consume — cache next value.",
      invariant: "cache holds the upcoming element if any.",
      operations: "Fill cache lazily.",
      whyNotSimpler: "Calling next then pushing back is not always possible.",
      tradeoffs: "One-element buffer.",
    },
    algorithm: ["cache Optional", "advance helper"],
    pseudocode: `peek: ensure cache; return cache`,
    implementation: `class PeekingIterator:
    def __init__(self, iterator):
        self.it = iterator
        self._cache = self.it.next() if self.it.hasNext() else None

    def peek(self):
        return self._cache

    def next(self):
        v = self._cache
        self._cache = self.it.next() if self.it.hasNext() else None
        return v

    def hasNext(self):
        return self._cache is not None
`,
    complexity: [{ operation: "all", time: "O(1)", space: "O(1)" }],
    dryRun: {
      setup: "Iterator 1,2,3",
      steps: ["peek→1; next→1; peek→2"],
      result: "Peek does not advance twice.",
    },
    edgeCases: ["empty iterator"],
    followUps: [{ text: "Peek n elements", kind: "practice" }],
    checklist: ["Related practice"],
  }),
  practice({
    id: "rp.flatten-nested-iterator",
    slug: "flatten-nested-list-iterator",
    title: "Flatten Nested List Iterator",
    classification: "related-practice",
    level: "unspecified",
    priority: "optional",
    verification: "verified",
    categories: ["iterators"],
    sources: [
      {
        label: "LeetCode 341 — Flatten Nested List Iterator",
        url: "https://leetcode.com/problems/flatten-nested-list-iterator/",
      },
    ],
    leetcodeUrl: "https://leetcode.com/problems/flatten-nested-list-iterator/",
    coreApproach: "Stack of iterators/lists; advance until an integer is on top.",
    summaryComplexity: "next/hasNext amortized O(1)",
    originalPrompt: "Flatten a nested list of integers via iterator.",
    originalRequirements: ["next", "hasNext"],
    clarifyingQuestions: ["Mutable nested list during iteration?"],
    approach: {
      whyStructure: "Lazy flatten avoids huge intermediate arrays.",
      invariant: "Stack top is ready integer or needs expansion.",
      operations: "Loop make_ready.",
      whyNotSimpler: "Eager flatten uses O(n) upfront always.",
      tradeoffs: "Stack vs recursion generator.",
    },
    algorithm: ["stack of indices/lists", "drill into NestedInteger"],
    pseudocode: `while stack and top is list: expand`,
    implementation: `# Sketch assuming NestedInteger API from LeetCode
class NestedIterator:
    def __init__(self, nestedList):
        self.stack = list(reversed(nestedList))

    def _go(self):
        while self.stack and not self.stack[-1].isInteger():
            top = self.stack.pop()
            self.stack.extend(reversed(top.getList()))

    def next(self) -> int:
        self._go()
        return self.stack.pop().getInteger()

    def hasNext(self) -> bool:
        self._go()
        return bool(self.stack)
`,
    complexity: [{ operation: "next/hasNext", time: "Amortized O(1)", space: "O(depth)" }],
    dryRun: {
      setup: "[[1,1],2,[1,1]]",
      steps: ["Yields 1,1,2,1,1"],
      result: "Lazy expansion.",
    },
    edgeCases: ["empty nested lists"],
    followUps: [{ text: "Generator version", kind: "practice" }],
    checklist: ["Related practice"],
  }),
  practice({
    id: "rp.trie",
    slug: "implement-trie",
    title: "Implement Trie (Prefix Tree)",
    classification: "related-practice",
    level: "unspecified",
    priority: "important",
    verification: "verified",
    categories: ["tries-route-matching"],
    sources: [
      {
        label: "LeetCode 208 — Implement Trie",
        url: "https://leetcode.com/problems/implement-trie-prefix-tree/",
      },
    ],
    leetcodeUrl: "https://leetcode.com/problems/implement-trie-prefix-tree/",
    coreApproach: "Map of children per node; end flag. Foundation for route matching.",
    summaryComplexity: "insert/search/startsWith O(L)",
    originalPrompt: "Implement a trie with insert, search, startsWith.",
    originalRequirements: ["insert", "search", "startsWith"],
    clarifyingQuestions: ["Lowercase only?"],
    approach: {
      whyStructure: "Shared prefixes; same idea as HTTP route trie.",
      invariant: "end marks complete word.",
      operations: "Walk creating nodes.",
      whyNotSimpler: "Hash set cannot answer prefixes efficiently.",
      tradeoffs: "dict vs array[26].",
    },
    algorithm: ["node.children", "node.end"],
    pseudocode: `insert: walk/create; mark end`,
    implementation: `class Trie:
    def __init__(self):
        self.root = {}

    def insert(self, word: str) -> None:
        n = self.root
        for ch in word:
            n = n.setdefault(ch, {})
        n["#"] = True

    def search(self, word: str) -> bool:
        n = self.root
        for ch in word:
            if ch not in n:
                return False
            n = n[ch]
        return "#" in n

    def startsWith(self, prefix: str) -> bool:
        n = self.root
        for ch in prefix:
            if ch not in n:
                return False
            n = n[ch]
        return True
`,
    complexity: [{ operation: "ops", time: "O(L)", space: "O(total chars)" }],
    dryRun: {
      setup: "insert apple; search app; startsWith app",
      steps: ["search false; startsWith true"],
      result: "End flag matters.",
    },
    edgeCases: ["empty string"],
    followUps: [{ text: "Wildcard search (LC 211)", kind: "practice" }],
    checklist: ["Related practice for route matching"],
    relatedQuestionIds: ["dc.http-route-matching"],
  }),
  practice({
    id: "rp.serialize-binary-tree",
    slug: "serialize-binary-tree",
    title: "Serialize and Deserialize Binary Tree",
    classification: "related-practice",
    level: "unspecified",
    priority: "important",
    verification: "verified",
    categories: ["graph-serialization"],
    sources: [
      {
        label: "LeetCode 297 — Serialize and Deserialize Binary Tree",
        url: "https://leetcode.com/problems/serialize-and-deserialize-binary-tree/",
      },
    ],
    leetcodeUrl: "https://leetcode.com/problems/serialize-and-deserialize-binary-tree/",
    coreApproach: "Preorder with null markers; or BFS levels. Practice before general digraphs.",
    summaryComplexity: "O(n) time/space",
    originalPrompt: "Serialize a binary tree to a string and deserialize it.",
    originalRequirements: ["serialize", "deserialize round trip"],
    clarifyingQuestions: ["Format constraints?"],
    approach: {
      whyStructure:
        "Null markers recover shape. Graphs need ids instead — see reported graph question.",
      invariant: "Preorder stream uniquely determines tree with nulls.",
      operations: "Recurse encode/decode.",
      whyNotSimpler: "Inorder alone is ambiguous.",
      tradeoffs: "BFS vs DFS formats.",
    },
    algorithm: ["preorder", "null sentinel"],
    pseudocode: `ser: val, ser(left), ser(right)`,
    implementation: `class Codec:
    def serialize(self, root) -> str:
        def go(n):
            if not n:
                return ["#"]
            return [str(n.val)] + go(n.left) + go(n.right)
        return ",".join(go(root))

    def deserialize(self, data: str):
        vals = iter(data.split(","))
        def go():
            v = next(vals)
            if v == "#":
                return None
            node = TreeNode(int(v))  # noqa: F821
            node.left = go()
            node.right = go()
            return node
        return go()
`,
    complexity: [{ operation: "both", time: "O(n)", space: "O(n)" }],
    dryRun: {
      setup: "1 with left 2 right 3",
      steps: ["1,2,#,#,3,#,#"],
      result: "Round trip.",
    },
    edgeCases: ["empty tree"],
    followUps: [{ text: "Serialize N-ary tree", kind: "practice" }],
    checklist: ["Related practice — not a substitute for digraph PYQ"],
    relatedQuestionIds: ["dc.graph-serialize"],
  }),
  practice({
    id: "rp.range-module",
    slug: "range-module",
    title: "Range Module",
    classification: "related-practice",
    level: "unspecified",
    priority: "important",
    verification: "verified",
    categories: ["intervals-range"],
    sources: [
      {
        label: "LeetCode 715 — Range Module",
        url: "https://leetcode.com/problems/range-module/",
      },
    ],
    leetcodeUrl: "https://leetcode.com/problems/range-module/",
    coreApproach: "Sorted map of disjoint intervals; split/merge on add/remove; query coverage.",
    summaryComplexity: "O(n) naïve · better with TreeMap of starts",
    originalPrompt: "Track ranges covered under addRange, removeRange, queryRange.",
    originalRequirements: ["addRange", "removeRange", "queryRange"],
    clarifyingQuestions: ["Half-open intervals?"],
    approach: {
      whyStructure: "Technique practice for the partially specified L4 range design report.",
      invariant: "Stored intervals disjoint and sorted.",
      operations: "Find overlap band; rewrite.",
      whyNotSimpler: "Boolean array only works for tiny universes.",
      tradeoffs: "TreeMap vs sorted list.",
    },
    algorithm: ["sorted intervals", "merge on add", "cut on remove"],
    pseudocode: `add: find overlaps; merge into one`,
    implementation: `class RangeModule:
    def __init__(self):
        self.iv: list[list[int]] = []

    def addRange(self, left: int, right: int) -> None:
        out: list[list[int]] = []
        placed = False
        for s, e in self.iv:
            if e < left:
                out.append([s, e])
            elif right < s:
                if not placed:
                    out.append([left, right])
                    placed = True
                out.append([s, e])
            else:
                left = min(left, s)
                right = max(right, e)
        if not placed:
            out.append([left, right])
        self.iv = out

    def queryRange(self, left: int, right: int) -> bool:
        for s, e in self.iv:
            if s <= left and right <= e:
                return True
        return False

    def removeRange(self, left: int, right: int) -> None:
        out: list[list[int]] = []
        for s, e in self.iv:
            if e <= left or right <= s:
                out.append([s, e])
            else:
                if s < left:
                    out.append([s, left])
                if right < e:
                    out.append([right, e])
        self.iv = out
`,
    complexity: [{ operation: "ops with list", time: "O(n)", space: "O(n)" }],
    dryRun: {
      setup: "add 10-20; remove 14-16; query 10-14",
      steps: ["Splits into [10,14)+[16,20)"],
      result: "query 10-14 true.",
    },
    edgeCases: ["adjacent merges", "remove all"],
    followUps: [{ text: "Segment tree on compressed coords", kind: "practice" }],
    checklist: ["Related practice — not the unpublished 2020 API"],
    relatedQuestionIds: ["dc.range-preprocess-query"],
  }),
];
