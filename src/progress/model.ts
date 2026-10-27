import { z } from "zod";

export const confidenceSchema = z.enum(["known", "shaky", "missed"]);
export type Confidence = z.infer<typeof confidenceSchema>;

export const progressSchema = z.object({
  version: z.literal(1),
  lessons: z.record(
    z.string(),
    z.object({
      status: z.enum(["in_progress", "complete"]),
      updatedAt: z.string().datetime(),
      checks: z.record(z.string(), confidenceSchema),
    }),
  ),
  why: z.record(
    z.string(),
    z.object({
      confidence: confidenceSchema,
      lastSeen: z.string().datetime(),
      dueAt: z.string().datetime(),
    }),
  ),
});

export type ProgressState = z.infer<typeof progressSchema>;

export type ContentLessonRef = {
  id: string;
  status: "planned" | "draft" | "complete";
};

const DAY_MS = 24 * 60 * 60 * 1000;
export const REVIEW_DELAY_DAYS: Record<Confidence, number> = {
  missed: 1,
  shaky: 3,
  known: 14,
};

export function emptyProgress(): ProgressState {
  return { version: 1, lessons: {}, why: {} };
}

export function dueAtFor(confidence: Confidence, fromIso: string): string {
  return new Date(
    new Date(fromIso).getTime() + REVIEW_DELAY_DAYS[confidence] * DAY_MS,
  ).toISOString();
}

export function markLessonOpened(
  state: ProgressState,
  lesson: ContentLessonRef,
  nowIso: string,
): ProgressState {
  if (lesson.status === "planned") return state;
  const current = state.lessons[lesson.id];
  if (current?.status === "complete") return state;
  return {
    ...state,
    lessons: {
      ...state.lessons,
      [lesson.id]: {
        status: "in_progress",
        updatedAt: nowIso,
        checks: current?.checks ?? {},
      },
    },
  };
}

export function markLessonLearned(
  state: ProgressState,
  lesson: ContentLessonRef,
  nowIso: string,
): ProgressState {
  if (lesson.status !== "complete") return state;
  const current = state.lessons[lesson.id];
  return {
    ...state,
    lessons: {
      ...state.lessons,
      [lesson.id]: {
        status: "complete",
        updatedAt: nowIso,
        checks: current?.checks ?? {},
      },
    },
  };
}

export function markCheck(
  state: ProgressState,
  lesson: ContentLessonRef,
  checkId: string,
  confidence: Confidence,
  nowIso: string,
): ProgressState {
  if (lesson.status === "planned") return state;
  const opened = markLessonOpened(state, lesson, nowIso);
  const current = opened.lessons[lesson.id];
  if (!current) return state;
  return {
    ...opened,
    lessons: {
      ...opened.lessons,
      [lesson.id]: {
        ...current,
        updatedAt: nowIso,
        checks: { ...current.checks, [checkId]: confidence },
      },
    },
  };
}

export function markWhy(
  state: ProgressState,
  whyId: string,
  confidence: Confidence,
  nowIso: string,
): ProgressState {
  return {
    ...state,
    why: {
      ...state.why,
      [whyId]: {
        confidence,
        lastSeen: nowIso,
        dueAt: dueAtFor(confidence, nowIso),
      },
    },
  };
}

export type DueItem =
  | { kind: "check"; lessonId: string; checkId: string; confidence: "missed" | "shaky" }
  | { kind: "why"; whyId: string; confidence: Confidence };

export function dueItems(state: ProgressState, nowIso: string): DueItem[] {
  const now = new Date(nowIso).getTime();
  const items: DueItem[] = [];
  for (const [lessonId, lesson] of Object.entries(state.lessons)) {
    for (const [checkId, confidence] of Object.entries(lesson.checks)) {
      if (confidence === "missed" || confidence === "shaky") {
        items.push({ kind: "check", lessonId, checkId, confidence });
      }
    }
  }
  for (const [whyId, record] of Object.entries(state.why)) {
    if (new Date(record.dueAt).getTime() <= now) {
      items.push({ kind: "why", whyId, confidence: record.confidence });
    }
  }
  return items;
}

export function summarizeProgress(
  state: ProgressState,
  lessons: { id: string; status: ContentLessonRef["status"] }[],
): { written: number; finished: number; total: number } {
  const written = lessons.filter((lesson) => lesson.status === "complete").length;
  const finished = lessons.filter(
    (lesson) => lesson.status === "complete" && state.lessons[lesson.id]?.status === "complete",
  ).length;
  return { written, finished, total: lessons.length };
}

export function parseProgress(
  raw: string,
): { ok: true; state: ProgressState } | { ok: false; error: string } {
  let json: unknown;
  try {
    json = JSON.parse(raw);
  } catch {
    return { ok: false, error: "That file is not valid JSON." };
  }
  const result = progressSchema.safeParse(json);
  if (!result.success) {
    return { ok: false, error: "That file is not a progress export from this course." };
  }
  return { ok: true, state: result.data };
}

export function serializeProgress(state: ProgressState): string {
  return JSON.stringify(state, null, 2);
}

export type ContinueTarget = { moduleId: string; lessonId: string };

export function continueLessonId(
  state: ProgressState,
  lessons: { id: string; status: ContentLessonRef["status"] }[],
): string | undefined {
  const inProgress = lessons.find((lesson) => state.lessons[lesson.id]?.status === "in_progress");
  if (inProgress) return inProgress.id;
  const nextReady = lessons.find(
    (lesson) => lesson.status === "complete" && state.lessons[lesson.id]?.status !== "complete",
  );
  if (nextReady) return nextReady.id;
  return lessons[0]?.id;
}
