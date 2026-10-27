import type { DesignCodingQuestion } from "../../../schema";

export const restaurantWaitlist: DesignCodingQuestion = {
  id: "dc.restaurant-waitlist",
  slug: "restaurant-waitlist",
  title: "Restaurant Waitlist",
  classification: "reported-google",
  level: "L4",
  interviewFormat: "Phone screen",
  priority: "must-revise",
  verification: "verified",
  categories: ["misc-ds-design", "scheduling-heaps"],
  sources: [
    {
      label: "LeetCode Discuss — Google L4 phone screen waitlist",
      url: "https://leetcode.com/discuss/post/5120225/Google-L4-interview-phone-screen/",
      note: "add group; leave any time; findGroup(tableSize) = earliest group with size <= tableSize.",
    },
  ],
  coreApproach:
    "Track arrival order with a doubly linked list (or ordered ids) plus a map groupId → node for O(1) leave; for seating, either scan in order or keep per-size structures / tree of sizes for faster eligible lookup.",
  summaryComplexity:
    "add/leave O(1) · seat naïve O(n) · seat with size buckets O(tableSize) or better with a tree",
  originalPrompt:
    "Design a restaurant waitlist: (a) new groups join; (b) a waiting group may leave anytime; (c) given tableSize, assign the earliest-arrived group whose size is <= tableSize.",
  originalRequirements: [
    "Add a customer group to the waitlist (arrival order matters).",
    "Remove an arbitrary waiting group.",
    "findGroup(tableSize): among groups with size <= tableSize, pick the one that arrived first; example in the report walks a list [4,2,3,5] for table 3 → group of size 2.",
  ],
  clarifyingQuestions: [
    "Do groups have ids, or are they identified by position/size only?",
    "After seating, is the group removed from the waitlist?",
    "Can two groups have the same size?",
    "Is party size always positive and <= some max?",
  ],
  approach: {
    whyStructure:
      "Arrival order is FIFO among eligible groups, but leave is arbitrary — a plain queue cannot delete middle elements efficiently without ids. A DLL + id map gives O(1) leave. Seating needs the earliest eligible size constraint.",
    invariant:
      "List order = arrival order among currently waiting groups. Map locates nodes for leave. Seated groups are removed.",
    operations:
      "add: append node, store id. leave: unlink via map. seat(table): scan from head for first size <= table (optimal enough for interview unless pushed); or maintain per-size queues of ids in arrival order.",
    whyNotSimpler:
      "Array scan works for small n but leave is O(n). Heap by size loses arrival order among different sizes. Single FIFO queue cannot skip an oversized head to seat a later smaller party (report requires earliest eligible, which may not be the head).",
    tradeoffs:
      "Per-size deques: seat checks sizes 1..tableSize and picks the globally earliest among those heads — need timestamps or a merge. TreeMap size→deque is a common step-up.",
  },
  algorithm: [
    "Node: group_id, size, prev, next.",
    "add(group_id, size): append.",
    "leave(group_id): unlink.",
    "seat(table_size): walk from head; first with size <= table_size; unlink and return id.",
  ],
  pseudocode: `dll + map[id] = node

add(id, size): append; map[id]=node
leave(id): unlink map[id]
seat(t):
  for node in dll from head:
    if node.size <= t: leave(node.id); return node
  return none`,
  implementation: `from __future__ import annotations

from typing import Optional


class _Node:
    __slots__ = ("group_id", "size", "prev", "next")

    def __init__(self, group_id: int, size: int) -> None:
        self.group_id = group_id
        self.size = size
        self.prev: Optional[_Node] = None
        self.next: Optional[_Node] = None


class RestaurantWaitlist:
    def __init__(self) -> None:
        self._head = _Node(-1, -1)
        self._tail = _Node(-1, -1)
        self._head.next = self._tail
        self._tail.prev = self._head
        self._nodes: dict[int, _Node] = {}

    def add(self, group_id: int, size: int) -> None:
        if group_id in self._nodes:
            raise ValueError("duplicate group_id")
        if size <= 0:
            raise ValueError("size must be positive")
        node = _Node(group_id, size)
        prev = self._tail.prev
        assert prev is not None
        prev.next = node
        node.prev = prev
        node.next = self._tail
        self._tail.prev = node
        self._nodes[group_id] = node

    def leave(self, group_id: int) -> None:
        node = self._nodes.pop(group_id, None)
        if node is None:
            return
        assert node.prev is not None and node.next is not None
        node.prev.next = node.next
        node.next.prev = node.prev

    def find_group(self, table_size: int) -> Optional[int]:
        cur = self._head.next
        while cur is not None and cur is not self._tail:
            if cur.size <= table_size:
                gid = cur.group_id
                self.leave(gid)
                return gid
            cur = cur.next
        return None


if __name__ == "__main__":
    w = RestaurantWaitlist()
    for gid, sz in enumerate([4, 2, 3, 6], start=1):
        w.add(gid, sz)
    w.add(5, 5)
    w.leave(4)  # group id 4 size 6 leaves; list sizes 4,2,3,5
    assert w.find_group(3) == 2  # earliest eligible is size 2 (id 2)
    print("ok")
`,
  complexity: [
    { operation: "add / leave", time: "O(1)", space: "O(1) extra" },
    { operation: "find_group (scan)", time: "O(n)", space: "O(1)" },
    {
      operation: "find_group (per-size deques + timestamps)",
      time: "O(S) or O(log S) with a tree on sizes",
      notes: "S = max party size or distinct sizes; say this if interviewer asks for faster seating.",
    },
  ],
  dryRun: {
    setup: "Sizes by arrival [4,2,3,6]; add 5; leave the size-6 group; findGroup(3).",
    steps: [
      "List becomes [4,2,3,5].",
      "Eligible for table 3: 2 and 3; earliest is 2.",
      "Remove that group from the list.",
    ],
    result: "Returns the group of size 2, matching the report's example intent.",
  },
  edgeCases: [
    "No eligible group.",
    "Head too large but later small group should still be found.",
    "leave of unknown id.",
    "table_size smaller than every party.",
    "Duplicate sizes with different arrival times.",
  ],
  followUps: [
    {
      text: "Optimize find_group below O(n).",
      kind: "practice",
      guidance: "Per-size FIFO queues plus a structure to pick the minimum arrival among sizes ≤ table.",
    },
  ],
  checklist: [
    "Earliest eligible, not merely queue head.",
    "O(1) arbitrary leave.",
    "Confirm seating removes the group.",
    "Walk the report example.",
  ],
};
