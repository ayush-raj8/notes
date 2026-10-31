# Notes

Study notes with one subject per course. Computer Networks is the first subject, kept under `src/content/subjects/cn`. A lesson is shown as ready only after it is written and checked.

Setup and the rules for later edits are in [PROJECT_GUIDE.md](PROJECT_GUIDE.md). The Computer Networks inventory is [CURRICULUM.md](CURRICULUM.md).

The published site is [https://ayush-raj8.github.io/notes/](https://ayush-raj8.github.io/notes/). GitHub Actions builds it with `GITHUB_PAGES=true`, which sets the Vite base to `/notes/`. Local `pnpm dev` stays at `/`.

```bash
pnpm install
pnpm dev
```

## Java OOP / SOLID / LLD book

Sibling mdBook under [`java-book/`](java-book/). Exam-oriented chapter contract: [`java-book/CONTENT_STANDARDS.md`](java-book/CONTENT_STANDARDS.md). Plan and progress: [`java-book/PLAN.md`](java-book/PLAN.md), [`java-book/PROGRESS.md`](java-book/PROGRESS.md).

- Published HTML: [https://ayush-raj8.github.io/notes/java/](https://ayush-raj8.github.io/notes/java/) (static only; Java is not executed on Pages)
- Series landing: [https://ayush-raj8.github.io/notes/library/](https://ayush-raj8.github.io/notes/library/)
- CI: `.github/workflows/java-ci.yml` runs `mvn verify` + `mdbook build` on `java-book/**` changes

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)  # macOS
cd java-book/code && mvn -q verify
cd .. && mdbook build
```
