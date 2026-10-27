import type { DesignCodingQuestion } from "../../../schema";

export const httpRouteMatching: DesignCodingQuestion = {
  id: "dc.http-route-matching",
  slug: "http-route-matching",
  title: "HTTP Route Matching for a Web Framework",
  classification: "hello-interview-guide",
  level: "L4",
  interviewFormat: "Coding (listed on Hello Interview Google L4 and L5 guides)",
  priority: "must-revise",
  verification: "verified",
  categories: ["tries-route-matching"],
  sources: [
    {
      label: "Hello Interview — Google L4 guide (commonly asked coding)",
      url: "https://www.hellointerview.com/guides/google/l4",
      note: "Lists 'Implement HTTP Route Matching for a Web Framework' among most commonly asked coding questions.",
    },
    {
      label: "Hello Interview — Google L5 guide",
      url: "https://www.hellointerview.com/guides/google/l5",
      note: "Same question listed for L5 coding.",
    },
    {
      label: "Hello Interview community question page",
      url: "https://www.hellointerview.com/community/questions/http-route-matching/cmou3jp0m13rl0cadz84s19xd",
      note: "registerHandler(pattern) and getHandler(path); patterns may include {parameter} placeholders; return matching pattern or null.",
    },
  ],
  coreApproach:
    "Trie of path segments: static segments as map keys; one child slot for a parameter segment. Prefer static matches over parameterized when both fit; reject ambiguous registrations if required.",
  summaryComplexity: "register O(segments) · match O(segments) · space O(total pattern segments)",
  originalPrompt:
    "Implement registerHandler(pattern) and getHandler(path). Patterns may include {parameter} placeholders. getHandler returns the matching pattern or null.",
  originalRequirements: [
    "registerHandler(pattern): add a URL pattern.",
    "getHandler(path): match a request path; return the matching pattern or null.",
    "Support {parameter} placeholders in patterns.",
  ],
  clarifyingQuestions: [
    "Leading/trailing slashes — normalize?",
    "Multiple parameters per path?",
    "Wildcards * beyond {param}?",
    "Precedence when static and param patterns both match?",
    "Duplicate registration behavior?",
  ],
  approach: {
    whyStructure:
      "Path matching is prefix search over segments. A trie makes each hop O(1) dictionary lookup and cleanly separates static vs param edges.",
    invariant:
      "Each trie path from root to a terminal node stores at most one registered pattern string. Param edges are named placeholders.",
    operations:
      "register: split on '/'; walk/create nodes; mark terminal with pattern. match: walk preferring static child; else try param child; success only if terminal at end.",
    whyNotSimpler:
      "Linear scan of all patterns is O(patterns * length). Regex per pattern is harder to reason about for precedence.",
    tradeoffs:
      "Static-over-param is a common framework rule; document it. Full radix trees are overkill for interview.",
  },
  algorithm: [
    "Normalize and split path into segments.",
    "Trie node: static children map, optional param child (name + node), pattern at terminal.",
    "Match: at each segment try static then param.",
  ],
  pseudocode: `register(pattern):
  walk segments; for {x} use param edge else static edge
  mark terminal.pattern = pattern

get(path):
  walk: prefer static[seg] else param child
  return terminal.pattern or null`,
  implementation: `from __future__ import annotations

from typing import Optional


class _Node:
    def __init__(self) -> None:
        self.static: dict[str, _Node] = {}
        self.param: Optional[tuple[str, _Node]] = None  # (name, child)
        self.pattern: Optional[str] = None


class Router:
    def __init__(self) -> None:
        self._root = _Node()

    @staticmethod
    def _segments(path: str) -> list[str]:
        return [s for s in path.strip("/").split("/") if s != ""]

    def register_handler(self, pattern: str) -> None:
        node = self._root
        for seg in self._segments(pattern):
            if seg.startswith("{") and seg.endswith("}"):
                name = seg[1:-1]
                if node.param is None:
                    node.param = (name, _Node())
                elif node.param[0] != name:
                    # Same position already has a different param name — still share child.
                    node.param = (name, node.param[1])
                node = node.param[1]
            else:
                node = node.static.setdefault(seg, _Node())
        node.pattern = pattern

    def get_handler(self, path: str) -> Optional[str]:
        node: Optional[_Node] = self._root
        for seg in self._segments(path):
            if node is None:
                return None
            if seg in node.static:
                node = node.static[seg]
            elif node.param is not None:
                node = node.param[1]
            else:
                return None
        return None if node is None else node.pattern


if __name__ == "__main__":
    r = Router()
    r.register_handler("/users/{id}")
    r.register_handler("/users/me")
    assert r.get_handler("/users/me") == "/users/me"
    assert r.get_handler("/users/42") == "/users/{id}"
    assert r.get_handler("/posts/1") is None
    print("ok")
`,
  complexity: [
    { operation: "register_handler", time: "O(k) segments", space: "O(k) new nodes worst case" },
    { operation: "get_handler", time: "O(k)", space: "O(1)" },
  ],
  dryRun: {
    setup: "Register /users/me and /users/{id}; query /users/me and /users/42.",
    steps: [
      "Static 'users' then static 'me' terminal.",
      "Param branch under users for {id}.",
      "Path users/me takes static child; users/42 takes param.",
    ],
    result: "Static wins for /users/me; param catches /users/42.",
  },
  edgeCases: [
    "Empty path /.",
    "Only parameterized segments.",
    "No match mid-path.",
    "Trailing slash normalization.",
  ],
  followUps: [
    {
      text: "Wildcards and precedence rules.",
      kind: "practice",
      guidance: "Only add if interviewer asks; keep labeled as practice beyond the HI statement.",
    },
  ],
  checklist: [
    "Classify as Hello Interview guide, not a candidate report.",
    "Trie of segments.",
    "Static before param.",
    "Return pattern string or null.",
  ],
  relatedQuestionIds: ["rp.trie"],
};
