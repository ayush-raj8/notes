# Progress

## Subject layout

Computer Networks is a subject under Notes, not the site root. Its files are in `src/content/subjects/cn`. Routes start at `/cn`. The site root lists subjects. GitHub Pages publishes `main` to `https://ayush-raj8.github.io/notes/`.

## Milestone 1 — website foundation

Done in the working tree. Not committed.

- Vite, React, TypeScript, React Router, Zod, Vitest, Testing Library, Playwright, ESLint, and Prettier are installed.
- The shell has a sticky header, a menu button below 720px, and a lesson rail on module and lesson routes.
- All 13 modules, 67 lessons, and 55 why-questions are in the catalog. Unwritten lessons say so. Unknown routes show a not-found page.
- Progress is a versioned local store with export, confirmed import, and review scheduling. Learner completion is refused for lessons that are not written.
- The content gate, syllabus coverage test, and shell tests pass.

## Not done

- No lesson body is written. Zero lessons are `complete`.
- No interactive visual is implemented. The approved ids are registered and unimplemented.
- Why-answers are empty. The index lists the prompts and marks them pending.
- Milestone 2 (lesson renderer) has not started.
- Playwright was not run as part of the command log below until the e2e section of this file is updated.

## Correction from the approved plan

`why.20` asks why a TCP connection can be established while the application request still fails. The plan mapped it to the scenario where TCP establishment fails. The primary lesson is `m05.service-model`, with links to the HTTP lesson and the 502/503 scenario.

## Checks run for milestone 1

- `tsc -b --pretty false` passed.
- `vitest run`: 20 tests passed across catalog, quality gate, progress, and shell.
- `eslint .` passed.
- `vite build` passed.
- `playwright test`: 5 passed, 1 skipped. The skipped test is the narrow-viewport menu check on the desktop project. Desktop and mobile both opened a module, an unwritten lesson, and the not-found page. Mobile opened the menu. Neither viewport overflowed horizontally on the lesson page.
- Every URL in `src/content/subjects/cn/sources.ts` returned HTTP 200 (39 URLs). No lesson claim is marked checked. The catalog entries are addresses, not verified quotations.

## Next task

Milestone 2: render principle sections, steps, comparisons, examples, callouts, expandable details, source references, why-embeds, checks, previous/next navigation, and learner completion. Keep unwritten lessons on the current empty state.
