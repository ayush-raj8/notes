import { z } from "zod";

/**
 * Fixed shape for every module chapter summary (10–20 min skim notes).
 * See CONTENT_STANDARDS.md → Chapter summaries.
 */
export const chapterSummarySchema = z.object({
  moduleId: z.string().regex(/^m\d{2}$/),
  status: z.enum(["draft", "complete"]),
  /** Target study time in minutes; complete summaries must use 10–20. */
  readingMinutes: z.object({
    min: z.number().int().min(10).max(20),
    max: z.number().int().min(10).max(20),
  }),
  /** One sentence: what this chapter is for. */
  thesis: z.string().min(1),
  /** 3–6 things you should be able to explain after the skim. */
  outcomes: z.array(z.string().min(1)).min(3).max(6),
  /**
   * One entry per syllabus bullet, in syllabus order.
   * `notes` uses notes-style bullets (• lines).
   */
  coverage: z
    .array(
      z.object({
        bullet: z.string().min(1),
        notes: z.string().min(1),
      }),
    )
    .min(1),
  /** Diagram-in-words / running mental model for the chapter. */
  mentalModel: z.string().min(1),
  /** What breaks in production and what you observe. */
  failuresAndSignals: z.array(z.string().min(1)).min(3),
  /** Why-question ids primarily owned by lessons in this module. */
  whyAnchors: z.array(z.string().regex(/^why\.\d{2}$/)),
  connections: z.object({
    before: z.string().min(1),
    after: z.string().min(1),
  }),
  /** 2–4 revealable checks. */
  selfCheck: z
    .array(
      z.object({
        id: z.string().min(1),
        prompt: z.string().min(1),
        answer: z.string().min(1),
      }),
    )
    .min(2)
    .max(4),
});

export type ChapterSummary = z.infer<typeof chapterSummarySchema>;
