# Introduction

This book is a companion to compiled Java 21 sources under `code/`. Every snippet in a chapter is included from a real file with mdBook anchors (`{{` + `#include path:anchor}}`). Pages serves the HTML only; compilation and tests run in CI with Maven.

Most exams mark **predict output**, **compile-or-not**, **short rules**, and **write a small fragment**. Chapter standards are written for that: see [CONTENT_STANDARDS.md](https://github.com/ayush-raj8/notes/blob/main/java-book/CONTENT_STANDARDS.md) in the repo (`java-book/CONTENT_STANDARDS.md`).

## How to read a chapter

1. **Exam flash rules** — one-liners worth underlining before a paper.
2. **Rules table** — allowed / not allowed, with the exact compiler message when it fails.
3. **Walkthrough** — several files, each followed by captured program output.
4. **Predict the output** / **Compile or not?** — exam drills; answers below the stems.
5. **Gotchas** — traps backed by code.
6. **UML** — Mermaid class diagrams aligned with the Java types.
7. **Interview Q&A**, **exam writing prompts**, and **exercises** — solutions under `code/`.

## Layout

| Path | Role |
|------|------|
| `java-book/src/` | Chapter Markdown |
| `java-book/code/` | Maven multi-module sources |
| `java-book/code/*/broken/` | Intentionally invalid files, excluded from the build |
| `java-book/code/*/exam/` | Predict-output and compile-or-not stems |
| `java-book/code/*/solutions/` | Exercise solutions |
| `java-book/diagrams/` | PlantUML (`.puml`) and Mermaid (`.mmd`) diagram sources; chapters include the Mermaid files |
| `java-book/CONTENT_STANDARDS.md` | Exam-oriented chapter contract |

## Local build

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # macOS
cd java-book/code && mvn -q verify
cd .. && mdbook build
```

Published HTML is at `/notes/java/` next to the Computer Networks notes at `/notes/`.
