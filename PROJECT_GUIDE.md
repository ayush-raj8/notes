# Project guide

Notes is a single-learner site. Each subject owns its curriculum. Computer Networks lives at `src/content/subjects/cn` and is served under `/cn`. React only renders that data. Progress stays in this browser.

## Commands

Install dependencies with pnpm 12.15.0. This repo allows the esbuild install script in `pnpm-workspace.yaml`. pnpm 12 reads that file even for this single package.

If `pnpm` is not on your PATH, Corepack on this machine failed to activate it. Use `npm exec --yes --package pnpm@12.15.0 -- pnpm <script>` instead.

```bash
pnpm install
pnpm dev
pnpm test
pnpm lint
pnpm typecheck
pnpm build
pnpm test:e2e
pnpm curriculum:doc
pnpm format
```

`pnpm test` runs Vitest. `pnpm test:e2e` runs Playwright against a production build and needs a Chromium install (`pnpm exec playwright install chromium` the first time).

## Layout

- `src/content/subjects/index.ts` lists subjects. Add a subject here before adding its routes.
- `src/content/subjects/cn` holds the Computer Networks syllabus, lesson catalog, lesson bodies, why-questions, and sources. `src/content/quality.ts` is the completion gate. Content must not import React, the reader, visuals, or progress storage.
- `src/visuals/models` will hold pure step functions. Those files must not import React.
- `src/visuals/registry.ts` lists which approved visuals are implemented.
- `src/reader` renders lessons and pages. `/` lists subjects. Computer Networks routes are `/cn`, `/cn/learn`, `/cn/why`, and `/cn/review`.
- `src/progress` is the local progress model. The browser key is `notes.cn.progress.v1`. A read still accepts the earlier key `cn-theory.progress.v1`.
- `tests/content` fails if a syllabus bullet, lesson id, or core why-question disappears.

## Identifiers

Module ids are `m01` through `m13`. Lesson ids look like `m05.handshake` and do not change when a title or the order changes. Why-question ids are `why.01` through `why.55`. URLs use the slugs in the catalog.

`why.01` through `why.50` are the required collection. `why.51` through `why.55` are the approved additions.

## Adding a lesson body

1. Create `src/content/subjects/cn/modules/<module-id>/<lesson-slug>.ts` exporting a `LessonBody`.
2. Register it in `src/content/subjects/cn/bodies/index.ts` under the existing lesson id.
3. Do not create a second file for the same lesson.
4. Set `status` to `draft` until `assessLesson` would accept `complete`.
5. Put a concise why-answer in `src/content/subjects/cn/why/answers.ts` and embed the question with a `why` section. The answer is published only when the primary lesson is `complete`.
6. Run `pnpm test`. If the catalog changed, run `pnpm curriculum:doc` and commit `CURRICULUM.md`.

## Completion

Authoring status (`planned`, `draft`, `complete`) is not the learner's progress. A learner can mark a lesson finished only when its authoring status is `complete`. The home page counts written lessons separately from finished ones.

## Sources

`src/content/subjects/cn/sources.ts` is the catalog. A subtle claim needs a source id, a locator, and `verification: "checked"` only after that locator was actually opened. The default is `unread`. A `complete` lesson cannot carry an unread subtle claim.

When documents disagree, use this order: the relevant RFC, then the Linux man page for host behavior, then current maintainer documentation, then Kurose and Ross or Stevens as teaching references.

## GitHub Pages

Pushes to `main` run `.github/workflows/pages.yml`. That job sets `GITHUB_PAGES=true`, so Vite emits assets under `/notes/`. The project site is `https://ayush-raj8.github.io/notes/`. `public/404.html` stores the requested URL and returns to `/notes/`; the script in `index.html` restores it before the app loads. Local dev, tests, and the CI build leave the base at `/`.
