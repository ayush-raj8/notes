import type { DesignCodingQuestion } from "../../../schema";

export const secureLinkedList: DesignCodingQuestion = {
  id: "dc.secure-linked-list",
  slug: "secure-linked-list",
  title: "Secure Linked List / Hash Chain",
  classification: "reported-google",
  level: "unspecified",
  interviewFormat: "Phone screen / onsite coding (linked-list design)",
  priority: "must-revise",
  verification: "verified",
  categories: ["linked-lists-hash-integrity"],
  sources: [
    {
      label: "LeetCode Discuss — Google phone screening (Jun 2025)",
      url: "https://leetcode.com/discuss/post/6854114/google-phone-screening-round-by-anonymou-o0ry/",
      note: "States the hash rule: last node hash(data); otherwise hash(data + next.hashValue). Tasks: insertAtPosition and isChainValid.",
    },
    {
      label: "LeetCode Discuss — Google Bengaluru SDE 3 (Mar 2025)",
      url: "https://leetcode.com/discuss/post/6737844/google-bengaluru-sde-3-reject-by-anonymo-5kdk/",
      note: "Round 3: secure singly linked list; hash from current data and next hash; prepend, insert at index, validate; also array → list.",
    },
  ],
  coreApproach:
    "Singly linked list where each node's hash depends on its data and the next node's hash. Insert requires rehashing the prefix (or all prior nodes) after structural change; validate recomputes every hash forward from the tail.",
  summaryComplexity: "insert O(n) time · validate O(n) time · O(1) extra space beyond the list",
  originalPrompt:
    "Implement a secured singly linked list. Each node stores data (string), next, and an integer hashValue. A given hash(String) function is available and must not be modified. Hashes form a forward-dependent chain so tampering without updating hashes is detectable.",
  originalRequirements: [
    "Last node: hashValue = hash(data).",
    "Otherwise: hashValue = hash(data + next.hashValue) — concatenate data with the next node's hash value, then hash (per the Jun 2025 phone-screen report).",
    "insertAtPosition(data, position, head): insert, recalculate affected hashes, return (possibly new) head.",
    "isChainValid(head): true iff every node's hashValue matches the rule.",
    "Bengaluru variant also asked for prepend and building the list from an array.",
  ],
  reportedFollowUps: [
    "Detect a hacker who inserts a node at position p without updating hashes (isChainValid must fail).",
  ],
  practiceVariations: [
    "Hash depending on the previous node instead of the next (do not substitute this for the reported rule).",
    "Batch rebuild hashes from the end after multiple edits.",
  ],
  clarifyingQuestions: [
    "Is position 0-based? What if position is past the end — append or reject?",
    "How is next.hashValue converted when concatenating with data (string form of the int)?",
    "Can data contain characters that make concatenation ambiguous?",
    "Should insert recalculate only nodes whose next-hash dependency changed (all nodes before the insert point, walking from the new node backward conceptually)?",
  ],
  approach: {
    whyStructure:
      "A singly linked list matches the API. The integrity check needs a deterministic hash per node that depends on everything after it, so changing a later node or splicing without rehashing breaks earlier hashes.",
    invariant:
      "For every node, hashValue equals hash(data) if next is null, else hash(data + str(next.hashValue)). After any successful insert/prepend, the invariant holds for the whole list.",
    operations:
      "Find the splice point, insert the node, then recompute hashes from the new/changed suffix's end back toward the head (or recompute every node from the tail once). Validation walks once from head to tail after a bottom-up recompute, or recursively computes expected hashes from the end.",
    whyNotSimpler:
      "Storing only a CRC of the whole list loses per-node localization. Hashing only local data (no next.hash) would miss silent next-pointer swaps. A Merkle tree is heavier than the reported API.",
    tradeoffs:
      "Forward-dependent hashes make inserts O(n) because the prefix must update. A previous-node hash chain would make append cheap and prepend expensive — that is a different design than the reports.",
  },
  algorithm: [
    "Represent Node with data, next, hashValue.",
    "rehashFrom(node): if node is null return; recurse/rehash next first; set node.hashValue from the rule.",
    "insertAtPosition: walk to position; splice; call rehashFrom(head) (or from the earliest affected node if you rehash the whole list).",
    "isChainValid: walk each node; compare stored hashValue to the rule using the child's stored hashValue (and recursively ensure the child is valid, or recompute expected from the end).",
  ],
  pseudocode: `function expected_hash(node):
  if node.next is null: return hash(node.data)
  return hash(node.data + str(node.next.hashValue))

function rehash(node):
  if node is null: return
  rehash(node.next)
  node.hashValue = expected_hash(node)

function insert(head, data, pos):
  create n; splice at pos; rehash(head); return new_head

function valid(head):
  for each node in list:
    if node.hashValue != expected_hash(node): return false
  return true`,
  implementation: `from __future__ import annotations

from typing import Callable, Optional


def default_hash(s: str) -> int:
    """Stand-in for the interviewer-provided hash(String). Not cryptographic."""
    h = 0
    for ch in s:
        h = (h * 131 + ord(ch)) & 0x7FFFFFFF
    return h


class Node:
    def __init__(self, data: str) -> None:
        self.data = data
        self.next: Optional[Node] = None
        self.hash_value = 0


def expected_hash(node: Node, hash_fn: Callable[[str], int] = default_hash) -> int:
    if node.next is None:
        return hash_fn(node.data)
    return hash_fn(node.data + str(node.next.hash_value))


def rehash_from(node: Optional[Node], hash_fn: Callable[[str], int] = default_hash) -> None:
    if node is None:
        return
    rehash_from(node.next, hash_fn)
    node.hash_value = expected_hash(node, hash_fn)


def insert_at_position(
    data: str,
    position: int,
    head: Optional[Node],
    hash_fn: Callable[[str], int] = default_hash,
) -> Node:
    if position < 0:
        raise ValueError("position must be >= 0")
    new_node = Node(data)
    if position == 0 or head is None:
        if position > 0 and head is None:
            raise IndexError("position out of range")
        new_node.next = head
        head = new_node
    else:
        prev = head
        for _ in range(position - 1):
            if prev.next is None:
                raise IndexError("position out of range")
            prev = prev.next
        new_node.next = prev.next
        prev.next = new_node
    rehash_from(head, hash_fn)
    return head


def prepend(
    data: str,
    head: Optional[Node],
    hash_fn: Callable[[str], int] = default_hash,
) -> Node:
    return insert_at_position(data, 0, head, hash_fn)


def from_array(
    values: list[str],
    hash_fn: Callable[[str], int] = default_hash,
) -> Optional[Node]:
    head: Optional[Node] = None
    for value in reversed(values):
        head = prepend(value, head, hash_fn)
    return head


def is_chain_valid(
    head: Optional[Node],
    hash_fn: Callable[[str], int] = default_hash,
) -> bool:
    cur = head
    while cur is not None:
        if cur.hash_value != expected_hash(cur, hash_fn):
            return False
        cur = cur.next
    return True


# Quick self-check
if __name__ == "__main__":
    h = from_array(["a", "b", "c"])
    assert h is not None and is_chain_valid(h)
    h = insert_at_position("x", 1, h)
    assert is_chain_valid(h)
    # Tamper without rehashing
    assert h.next is not None
    h.next.data = "TAMPER"
    assert not is_chain_valid(h)
    print("ok")
`,
  complexity: [
    {
      operation: "rehash_from / insert_at_position / prepend",
      time: "O(n)",
      space: "O(n) recursion depth (or O(1) with an iterative reverse pass)",
    },
    {
      operation: "is_chain_valid",
      time: "O(n)",
      space: "O(1)",
    },
    {
      operation: "from_array",
      time: "O(n²) with naïve prepend+full rehash; O(n) if you build then rehash once",
      notes: "Prefer build links then one rehash_from(head).",
    },
  ],
  dryRun: {
    setup:
      "Build list from ['a','b'] then insert 'x' at position 1. Assume hash(s) is an opaque function.",
    steps: [
      "After ['a','b']: b.hash = hash('b'); a.hash = hash('a' + str(b.hash)).",
      "Insert 'x' between a and b: a → x → b.",
      "Rehash: b.hash = hash('b'); x.hash = hash('x' + str(b.hash)); a.hash = hash('a' + str(x.hash)).",
      "If a hacker splices a node without rehashing, a.hash still reflects the old next hash → is_chain_valid is false.",
    ],
    result:
      "Valid lists always satisfy the reported forward hash rule; silent splices fail validation.",
  },
  edgeCases: [
    "Empty list / insert at 0 creating a new head.",
    "Insert at the end (new last node uses hash(data) only).",
    "Single-node list.",
    "position out of range.",
    "Tamper data only vs tamper next pointer vs insert without rehash.",
  ],
  followUps: [
    {
      text: "Detect insert-without-rehash tampering.",
      kind: "reported",
      guidance:
        "is_chain_valid comparing stored vs expected hashes is enough; no separate detection API was required in the report.",
    },
    {
      text: "Implement with iterative O(1) extra space rehash.",
      kind: "practice",
      guidance:
        "Collect nodes in an array or reverse links temporarily, hash from the tail, restore order.",
    },
  ],
  checklist: [
    "Use the reported rule: hash(data) for tail; else hash(data + next.hashValue).",
    "Rehash after every structural change.",
    "Validation must fail on silent splices.",
    "State O(n) insert/validate clearly.",
    "Do not swap in a previous-hash blockchain without labeling it as a different variant.",
  ],
  relatedQuestionIds: ["rp.lru-cache"],
};
