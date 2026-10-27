# Content standards

A lesson teaches the mechanism. It does not stop at the vocabulary.

## Order

Use this order when it fits the topic:

1. The problem.
2. The intuition for why a solution has to look a certain way.
3. The mechanism.
4. A worked example, when the syllabus calls for one.
5. A visualization, when stepping through state teaches more than a static figure.
6. Edge cases, including behavior that is Linux-specific or only common in implementations.
7. What the idea does and does not imply for a running service.

The required principle section has seven fields: problem, why it matters, the idea, how it works, why it works, limits, and connections. A `complete` lesson has exactly one of these, plus at least two checks whose model answers can be revealed.

Keep the core short enough to read in a few minutes. Put header diagrams, rare cases, and implementation notes in expandable sections. Do not delete a caveat to keep the page short.

## Accuracy

Label claims with a scope when it matters:

- `rfc` — required by the specification.
- `common-implementation` — what widely used stacks do, not a requirement.
- `linux` — observed or documented on Linux.
- `teaching-simplification` — a deliberate reduction. Say what was left out.

Do not invent a citation. Do not mark `verification: "checked"` unless the locator was opened in that writing session. Quote only what is needed to point at a section. Paraphrase the explanation.

Subtle areas that always need a checked locator before `complete`: TCP retransmission and termination, TLS, QUIC, HTTP version behavior, NAT connection tracking, and socket-buffer or backlog behavior.

## Syllabus

`src/content/syllabus.ts` is the module list and the original bullets. A bullet may be split across two topic ids when two lessons each own part of it. Both topic ids are required. The bullet text stays verbatim.

Do not remove, merge away, or silently reword a bullet. `tests/content/catalog.test.ts` checks the bullet counts and that every bullet has an owner.

`m13.method` is the investigation frame. It owns no bullet. Each scenario lesson owns one scenario bullet and, once complete, an `investigation` section with causes, order, commands, what outputs imply, how to separate hypotheses, and common mistakes.

## Why-questions

The prompt text for `why.01` through `why.50` is fixed. Do not reword it. The concise answer lives in `src/content/subjects/cn/why/answers.ts` and is rendered inside the primary lesson and on the question page. Do not copy the answer into a second lesson. Link with `alsoAppearsIn` or a cross-reference.

`why.20` is owned by `m05.service-model`. The question is what a successful handshake still does not guarantee. It is not the scenario where TCP establishment fails.

## What not to ship

- Placeholder paragraphs that read like a finished explanation.
- A second copy of a lesson.
- A visual with no learning objective.
- `complete` on a lesson that still has an open research question in a subtle claim.
