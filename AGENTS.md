# Agent notes

Read `PROJECT_GUIDE.md`, `CONTENT_STANDARDS.md`, `VISUALIZATION_STANDARDS.md`, `CURRICULUM.md`, and `PROGRESS.md` before changing the course.

- Keep lesson ids, why-question ids, and syllabus bullet text stable.
- Computer Networks content stays in `src/content/subjects/cn`. Do not put another subject's data there.
- Do not import React from `src/content` or from `src/visuals/models`.
- Do not mark a lesson `complete` while `pnpm test` would reject it.
- Do not invent citations or set `verification` to `checked` without opening the locator.
- Do not regenerate lesson files from a script. Register a body under the existing catalog id.
- After a catalog change, run `pnpm curriculum:doc` and keep `CURRICULUM.md` in the same change.
