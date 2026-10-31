# Java book content standards (exam-oriented)

Most readers face **university / semester / campus / GATE-style Java papers**, not only design interviews. Every chapter must teach the mechanism **and** train the exam skills papers actually score: predict output, compile-or-fail, pick the rule, write a short fragment, spot the trap.

CN notes keep their own `CONTENT_STANDARDS.md`. This file governs **only** `java-book/`.

---

## Non-negotiables (every chapter)

1. **Every code sample** comes from `code/` via mdBook include anchors. No pasted uncompiled Java in Markdown.
2. **Min 4 code files** per chapter (typical 6–10): baseline, `broken/` (excluded from Maven) with **verbatim** `javac` output, fixed version, edge case.
3. **Captured output only** — run the program; do not invent stdout/stderr.
4. **Coverage checklist** in `PLAN.md` for that chapter: every item has prose + a proving file, then tick.
5. **Neutral voice** — no author/course/brand names, no filler.
6. **Java 21** · Maven module for the part · JUnit 5 where behaviour is asserted.
7. **Exam layer** (this document) is mandatory from chapter 01 onward; chapter 00 may be lighter but should still show the shape.

---

## Exam skill targets

Papers usually mark these. Map each chapter to at least the ones that apply.

| Skill | What the student must practice in-chapter |
|-------|---------------------------------------------|
| **Compile-or-not** | Disallowed cases with real compiler text; “which lines fail?” |
| **Predict output** | Short runnable mains; trace order (init, override, finally, etc.) |
| **Rule recall** | One-line rules examiners quote (tables, not essays) |
| **Choose among options** | Ambiguous overload/override/`null`/access — MCQ-style stems |
| **Write a fragment** | 5–15 line “write a class / method that …” exercises |
| **Compare terms** | Side-by-side tables (overload vs override, abstract vs interface, …) |
| **Diagram → code** | UML (or relationship) mapped to exact Java |
| **Code → diagram** | From includes, draw/read the class diagram |
| **Complexity / API pick** | Collections, I/O, concurrency chapters: “which type / method?” |
| **Design under constraints** | SOLID/LLD: requirements → types → tests (still exam-short where possible) |

If a checklist topic is a classic exam trap (initialization order, covariant return, `finally` vs return, sealed/`permits`, PECS, etc.), the chapter **must** include a predict-output or compile-or-not item for it — not only a prose mention.

---

## Chapter skeleton (required sections)

Use this order. Keep headings stable so search and audits stay easy.

1. **What and why** — short; name the exam-relevant payoff in one sentence.
2. **Exam flash rules** — 8–15 one-liners (or a tight rules table). These are what you underline the night before the paper.
3. **Rules table** — allowed / not allowed; **compiler message** for each disallowed case (verbatim).
4. **Walkthrough** — multiple include-backed files; each followed by real output or real `javac` text.
5. **Predict the output** — ≥5 self-contained stems; answers in a folded/below section (no spoilers above). Each stem backed by a file under `code/.../exam/` or a main already shown.
6. **Compile or not?** — ≥4 stems; “compiles / fails” + exact error or reason; `broken/` or `exam/` sources.
7. **Gotchas** — top 5 traps; each backed by a code file.
8. **UML** — Mermaid `classDiagram` for the chapter types (PlantUML source under `diagrams/` when the chapter is diagram-heavy).
9. **Interview Q&A** — ≥10; answers code-backed where a rule is claimed.
10. **Exam writing prompts** — ≥3 “write code / fill signature / fix the class” items (semester style).
11. **Exercises** — ≥5; solutions under `code/.../solutions/`.
12. **Recap checklist** — student-facing; mirror `PLAN.md` ticks.

Chapter 00 (pipeline) may omit full predict/compile banks; from **chapter 01** onward, missing exam sections = chapter not done.

---

## How exam stems must look

### Predict the output

- Show a **complete** small program (include from `code/`).
- Ask: exact stdout (and whether an exception aborts later lines).
- Answer shows **verbatim** run output, then a **one-line trace** (e.g. “static B → static A → …”).
- Prefer single-concept stems; one mixed “integration” stem per chapter is fine.

### Compile or not?

- Show the fragment or full type.
- Student answers: compiles **or** fails.
- If fails: quote the **real** `javac` line (or the decisive part).
- Cover both “obvious fail” and “looks fine but fails” (return-type overload, narrower access on override, checked exception widening, etc.).

### Exam flash rules

- Imperative, testable sentences: “A constructor is not inherited.” / “`finally` runs even when catch returns, unless JVM exits.”
- No motivational wording.
- Prefer the same wording students can reproduce in a 2-mark answer.

### MCQ-style traps (optional subsection, encouraged)

- 3–6 stems with 4 options when the topic is overload resolution, keywords, or access.
- Mark the correct option; one-line why the distractors fail.

---

## Marking-scheme alignment (write for 2 / 5 / 10 marks)

| Marks (typical) | Chapter must supply |
|-----------------|---------------------|
| **1–2** | Flash rule or one-line definition; true/false; single MCQ |
| **3–5** | Predict output / compile-or-not / short difference table |
| **8–10** | Write a small design or multi-class fragment; UML + code pair |
| **Long LLD** | Part 4 case studies; still include a “short answer” strip (entities, relationships, APIs) usable in written exams |

Do not only teach interview storytelling. Always leave a **short-answer trail** examiners can mark.

---

## Language / JLS framing

- Default: **Java SE 21** behaviour as implemented by the book’s toolchain (Temurin 21 in CI).
- When a rule is language-lawyer subtle, say **language rule** vs **common exam simplification** explicitly.
- Do not invent compiler messages. Do not claim “undefined” for deterministic `javac` rejects.
- Exam papers often still say “JDK 8/11 style”; when 21-only syntax appears (`sealed`, pattern `instanceof`, records), label **Java 16+ / 17+ / 21** so students know what older papers omit.

---

## Code layout conventions

```
code/part-XX-.../
  src/main/java/.../          # compiling demos
  src/test/java/.../          # JUnit
  broken/                     # does not compile; never on javac path
  exam/                       # predict-output + compile-or-not sources (compiling subset may live under src/main if shared)
  solutions/                  # exercise solutions
```

- Name exam mains clearly: `PredictInitOrder.java`, `CompileOverrideAccess.java`.
- One stem ↔ one type when possible (easier includes and re-runs).

---

## Tone and density

- Notes style: short sentences, tables, bullet rules.
- No essay paragraphs that bury the rule.
- Same visual language as CN notes (tokens in `theme/custom.css`); content stays exam-hard, layout stays calm.

---

## Done means

A chapter is done only when:

1. All `PLAN.md` checklist rows for it are ticked with proving paths.
2. Exam sections above are present (from ch 01).
3. `mvn -q verify` passes; `mdbook build` passes; includes resolve.
4. Every “does not compile” / “prints …” claim matches a captured run.
5. A student could revise **only** Exam flash rules + Predict/Compile banks and still clear a typical midterm unit test on that topic.
