import type { DesignCodingQuestion } from "../../../schema";

export const graphSerialize: DesignCodingQuestion = {
  id: "dc.graph-serialize",
  slug: "graph-serialize",
  title: "Directed Graph Serialization and Deserialization",
  classification: "reported-google",
  level: "L4",
  interviewFormat: "Onsite / technical coding (Google India L4 offer report)",
  priority: "must-revise",
  verification: "verified",
  categories: ["graph-serialization"],
  sources: [
    {
      label: "LeetCode Discuss — Google L4 interview experience",
      url: "https://leetcode.com/discuss/post/6775581/google-l4-interview-experience-by-anonym-w9dg/",
      note: "Encode/decode a directed graph with int values and neighbor nodes; cycles; node identity across serialization.",
    },
  ],
  leetcodeUrl: "https://leetcode.com/problems/clone-graph/",
  coreApproach:
    "Assign stable ids to nodes, emit adjacency (id → neighbor ids) plus node payloads, reconstruct nodes in a first pass then wire edges; handle cycles by id, not by pointer identity alone.",
  summaryComplexity: "serialize/deserialize O(V+E) time · O(V+E) space",
  originalPrompt:
    "Implement converting a directed graph (nodes with int values and neighbor lists) into a storable format and reconstructing it. Must handle cycles and preserve node identity.",
  originalRequirements: [
    "Serialize a directed graph to a storable representation.",
    "Deserialize back to an equivalent graph.",
    "Handle cycles and maintain node identity across the round trip.",
  ],
  clarifyingQuestions: [
    "May node values duplicate, or is value a unique key?",
    "Are self-loops and multiple edges allowed?",
    "Is the graph weakly connected, or must we discover all components from a list of nodes?",
    "Output format constraints (string, JSON, bytes)?",
  ],
  approach: {
    whyStructure:
      "Pointer graphs cannot be stored directly. Stable ids break cycles. Two-phase reconstruct avoids needing neighbors to exist before they are created.",
    invariant:
      "Bijection between original nodes and ids for one serialization. Edges are pairs of ids. Values travel with ids.",
    operations:
      "DFS/BFS from all roots (or a given node list) assigning ids; write value[id] and neighbors[id]; on load create all nodes then attach neighbors.",
    whyNotSimpler:
      "Serializing only by DFS parenthetical form like binary trees fails on DAGs with shared nodes and on cycles. Cloning via value equality merges distinct nodes that share a value.",
    tradeoffs:
      "Adjacency-list text is simple. An edge list plus value map is equivalent. Compact binary formats are unnecessary in interview.",
  },
  algorithm: [
    "Collect all reachable nodes; assign incremental ids.",
    "Emit lines or JSON: id, value, list of neighbor ids.",
    "Deserialize: create Node(id→Node); then for each edge append references.",
  ],
  pseudocode: `function serialize(nodes):
  id = {}
  for each node in walk(nodes): id[node] = next_id
  data = [{id, value, nbr_ids} for node]
  return encode(data)

function deserialize(blob):
  data = decode(blob)
  nodes = {id: Node(value) for each}
  for each record: attach neighbor node refs
  return nodes`,
  implementation: `from __future__ import annotations

from typing import Iterable, Optional


class Node:
    def __init__(self, val: int) -> None:
        self.val = val
        self.neighbors: list[Node] = []


def _walk(roots: Iterable[Node]) -> list[Node]:
    seen: set[int] = set()
    order: list[Node] = []

    def dfs(n: Node) -> None:
        i = id(n)
        if i in seen:
            return
        seen.add(i)
        order.append(n)
        for nxt in n.neighbors:
            dfs(nxt)

    for r in roots:
        dfs(r)
    return order


def serialize(roots: list[Node]) -> str:
    nodes = _walk(roots)
    index = {id(n): i for i, n in enumerate(nodes)}
    lines: list[str] = []
    for i, n in enumerate(nodes):
        nbrs = ",".join(str(index[id(x)]) for x in n.neighbors)
        lines.append(f"{i}:{n.val}:{nbrs}")
    return "\n".join(lines)


def deserialize(data: str) -> list[Node]:
    if not data.strip():
        return []
    rows = []
    for line in data.splitlines():
        i_s, val_s, nbr_s = line.split(":", 2)
        nbrs = [int(x) for x in nbr_s.split(",") if x != ""]
        rows.append((int(i_s), int(val_s), nbrs))
    nodes = [Node(0) for _ in rows]
    for i, val, _ in rows:
        nodes[i].val = val
    for i, _, nbrs in rows:
        nodes[i].neighbors = [nodes[j] for j in nbrs]
    return nodes


def same_structure(a: Node, b: Node, am: Optional[dict[int, int]] = None, bm: Optional[dict[int, int]] = None) -> bool:
    am = {} if am is None else am
    bm = {} if bm is None else bm
    ia, ib = id(a), id(b)
    if ia in am or ib in bm:
        return am.get(ia) == ib and bm.get(ib) == ia
    if a.val != b.val or len(a.neighbors) != len(b.neighbors):
        return False
    am[ia] = ib
    bm[ib] = ia
    return all(same_structure(x, y, am, bm) for x, y in zip(a.neighbors, b.neighbors))


if __name__ == "__main__":
    a, b = Node(1), Node(2)
    a.neighbors = [b, a]  # self-loop + edge
    b.neighbors = [a]
    blob = serialize([a])
    nodes = deserialize(blob)
    assert same_structure(a, nodes[0])
    print("ok")
`,
  complexity: [
    { operation: "serialize", time: "O(V+E)", space: "O(V+E)" },
    { operation: "deserialize", time: "O(V+E)", space: "O(V+E)" },
  ],
  dryRun: {
    setup: "Nodes 1→2, 2→1, 1→1 (cycle + self-loop).",
    steps: [
      "Assign id0=1, id1=2.",
      "Emit 0:1:1,0 and 1:2:0.",
      "Rebuild two nodes then wire neighbors by id.",
    ],
    result: "Round trip preserves values, cycles, and self-loops.",
  },
  edgeCases: [
    "Empty graph.",
    "Single node with self-loop.",
    "Disconnected components (need all roots).",
    "Duplicate values on distinct nodes.",
    "Shared child in a diamond DAG.",
  ],
  followUps: [
    {
      text: "Handle cycles and identity (as in report).",
      kind: "reported",
    },
    {
      text: "Compress format or support incremental updates.",
      kind: "practice",
    },
  ],
  checklist: [
    "Stable ids, not raw pointers.",
    "Two-phase deserialize.",
    "Cycles and self-loops tested.",
    "Duplicate values do not merge nodes.",
  ],
  relatedQuestionIds: ["rp.serialize-binary-tree"],
};
