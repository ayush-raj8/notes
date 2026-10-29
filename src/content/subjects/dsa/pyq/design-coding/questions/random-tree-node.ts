import type { DesignCodingQuestion } from "../../../schema";

export const randomTreeNode: DesignCodingQuestion = {
  id: "dc.random-tree-node",
  slug: "random-tree-node",
  title: "Random Node / Leaf from a Tree-Like Structure",
  classification: "reported-google",
  level: "L4",
  interviewFormat: "Onsite LLD / design-coding",
  priority: "important",
  verification: "verified",
  categories: ["trees-random-selection"],
  sources: [
    {
      label: "Reddit r/leetcode — long Google interview experience",
      url: "https://www.reddit.com/r/leetcode/comments/1n0l8gg/my_long_google_interview_experience/",
      note: "Onsite LLD: tree-like data structure with methods to get random nodes/leaves. Candidate interviewed for L4.",
    },
    {
      label: "Related L3 report with concrete API (not the same interview)",
      url: "https://www.reddit.com/r/leetcode/comments/1doe0z0/google_l3_randomization_problemwhy_reservoir/",
      note: "addNode(parent, val), getRandomNode, follow-up getRandomLeafNode; target O(1). Used here only to flesh learning material; the L4 report did not publish full signatures.",
    },
  ],
  coreApproach:
    "Maintain an array (or list) of all nodes for uniform getRandomNode; maintain a dynamic leaf set/list updated on insert when a parent stops being a leaf.",
  summaryComplexity:
    "add O(1) amortized · getRandomNode/Leaf O(1) with arrays + careful leaf removal",
  originalPrompt:
    "Design a tree-like structure supporting retrieval of a random node and random leaf (exact method names not published in the L4 writeup). A separate L3 writeup describes addNode(parent, val), getRandomNode(), and getRandomLeafNode() in O(1).",
  originalRequirements: [
    "Tree-like structure with ability to obtain a random node.",
    "Ability to obtain a random leaf.",
    "L4 report does not spell further constraints; treat O(1) add/random from the related L3 report as a practice target, clearly labeled.",
  ],
  clarifyingQuestions: [
    "Binary tree or n-ary?",
    "Uniform over nodes/leaves?",
    "Are deletes required?",
    "Is the tree static after build, or must add be supported?",
  ],
  approach: {
    whyStructure:
      "Uniform random among n items needs either reservoir sampling on a walk (O(n)) or an explicit list for O(1) choice. Leaves change when children are added, so maintain a leaf index structure.",
    invariant:
      "nodes lists every node once. leaf_nodes lists exactly the current leaves. On add under parent: append child; if parent was a leaf, remove it from leaf_nodes; add child as leaf.",
    operations:
      "addNode(parent, val): create child; update lists. getRandom*: random.choice on the appropriate list.",
    whyNotSimpler:
      "Subtree-size random walk works for static binary trees but complicates n-ary dynamic inserts. Reservoir sampling is O(n) per query.",
    tradeoffs:
      "list.remove(parent) is O(n); for true O(1) leaf updates use swap-delete with an index map. Mention this if claiming O(1).",
  },
  algorithm: [
    "Store nodes: list[Node], leaves: list[Node], optional leaf_index: dict.",
    "add: append child; remove parent from leaves if present; append child to leaves.",
    "random: choice from list.",
  ],
  pseudocode: `nodes = [root]; leaves = [root]
add(parent, val):
  child = Node(val); parent.children.append(child)
  nodes.append(child)
  if parent in leaves: remove parent from leaves
  leaves.append(child)
getRandomNode(): return random.choice(nodes)
getRandomLeaf(): return random.choice(leaves)`,
  implementation: `from __future__ import annotations

import random
from typing import Optional


class TreeNode:
    def __init__(self, val: int) -> None:
        self.val = val
        self.children: list[TreeNode] = []


class RandomTree:
    def __init__(self, root_val: int) -> None:
        self.root = TreeNode(root_val)
        self._nodes: list[TreeNode] = [self.root]
        self._leaves: list[TreeNode] = [self.root]
        self._leaf_pos: dict[int, int] = {id(self.root): 0}

    def _remove_leaf(self, node: TreeNode) -> None:
        pos = self._leaf_pos.pop(id(node), None)
        if pos is None:
            return
        last = self._leaves.pop()
        if pos < len(self._leaves):
            self._leaves[pos] = last
            self._leaf_pos[id(last)] = pos

    def add_node(self, parent: TreeNode, val: int) -> TreeNode:
        child = TreeNode(val)
        parent.children.append(child)
        self._nodes.append(child)
        self._remove_leaf(parent)
        self._leaf_pos[id(child)] = len(self._leaves)
        self._leaves.append(child)
        return child

    def get_random_node(self) -> TreeNode:
        return random.choice(self._nodes)

    def get_random_leaf(self) -> TreeNode:
        return random.choice(self._leaves)


if __name__ == "__main__":
    t = RandomTree(0)
    a = t.add_node(t.root, 1)
    t.add_node(t.root, 2)
    t.add_node(a, 3)
    assert {n.val for n in t._leaves} == {2, 3}
    assert t.get_random_node().val in {0, 1, 2, 3}
    print("ok")
`,
  complexity: [
    {
      operation: "add_node",
      time: "O(1) amortized with swap-delete leaf index",
      space: "O(1) extra",
    },
    { operation: "get_random_node / get_random_leaf", time: "O(1)", space: "O(1)" },
  ],
  dryRun: {
    setup: "Root 0; add 1 and 2 under root; add 3 under 1.",
    steps: [
      "After root: nodes[0], leaves[0].",
      "Add 1: leaves become [1]; add 2: leaves [1,2].",
      "Add 3 under 1: remove 1 from leaves; leaves [2,3].",
    ],
    result: "Uniform draws over 4 nodes or over leaves {2,3}.",
  },
  edgeCases: [
    "Only root (root is a leaf).",
    "Adding under a non-leaf.",
    "Claims of O(1) without swap-delete on leaf list.",
    "Deletes (not in reports — ask before implementing).",
  ],
  followUps: [
    {
      text: "getRandomLeafNode in O(1).",
      kind: "reported",
      guidance: "Related L3 report; keep leaf index structure.",
    },
    {
      text: "Support delete.",
      kind: "practice",
      guidance: "Harder: update leaf status of parent and swap-delete from nodes array.",
    },
  ],
  checklist: [
    "Separate L4 report vs L3 API detail in sources.",
    "Uniform randomness.",
    "Leaf set updates on insert.",
    "O(1) leaf removal via swap-delete if claiming O(1).",
  ],
};
