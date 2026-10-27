import { z } from "zod";

export const lessonStatusSchema = z.enum(["planned", "draft", "complete"]);
export const claimScopeSchema = z.enum([
  "rfc",
  "common-implementation",
  "linux",
  "teaching-simplification",
]);

export const claimSchema = z.object({
  id: z.string().min(1),
  statement: z.string().min(1),
  sourceId: z.string().min(1),
  locator: z.string().min(1),
  verification: z.enum(["unread", "checked"]),
  subtle: z.boolean(),
  scope: claimScopeSchema.optional(),
});

export const referenceSchema = z.object({
  sourceId: z.string().min(1),
  note: z.string().min(1).optional(),
});

const sectionId = z.string().min(1);

export const principleSectionSchema = z.object({
  type: z.literal("principle"),
  id: sectionId,
  problem: z.string().min(1),
  whyItMatters: z.string().min(1),
  idea: z.string().min(1),
  howItWorks: z.string().min(1),
  whyItWorks: z.string().min(1),
  limits: z.string().min(1),
  connections: z.string().min(1),
});

export const proseSectionSchema = z.object({
  type: z.literal("prose"),
  id: sectionId,
  title: z.string().min(1).optional(),
  paragraphs: z.array(z.string().min(1)).min(1),
});

export const stepsSectionSchema = z.object({
  type: z.literal("steps"),
  id: sectionId,
  title: z.string().min(1),
  steps: z
    .array(
      z.object({
        title: z.string().min(1),
        body: z.string().min(1),
      }),
    )
    .min(1),
});

export const comparisonSectionSchema = z.object({
  type: z.literal("comparison"),
  id: sectionId,
  title: z.string().min(1),
  caption: z.string().min(1).optional(),
  columns: z.array(z.string().min(1)).min(2),
  rows: z
    .array(
      z.object({
        label: z.string().min(1),
        cells: z.array(z.string().min(1)),
      }),
    )
    .min(1),
});

export const exampleSectionSchema = z.object({
  type: z.literal("example"),
  id: sectionId,
  title: z.string().min(1),
  setup: z.string().min(1),
  walkthrough: z.array(z.string().min(1)).min(1),
  result: z.string().min(1),
});

export const calloutSectionSchema = z.object({
  type: z.literal("callout"),
  id: sectionId,
  tone: z.enum(["note", "caution"]),
  title: z.string().min(1),
  body: z.string().min(1),
  scope: claimScopeSchema.optional(),
});

export const expandableSectionSchema = z.object({
  type: z.literal("expandable"),
  id: sectionId,
  title: z.string().min(1),
  paragraphs: z.array(z.string().min(1)).min(1),
});

export const vizSectionSchema = z.object({
  type: z.literal("viz"),
  id: sectionId,
  vizId: z.string().min(1),
  scenario: z.string().min(1),
  learningObjective: z.string().min(1),
  caption: z.string().min(1),
});

export const whySectionSchema = z.object({
  type: z.literal("why"),
  id: sectionId,
  questionId: z.string().min(1),
});

export const checksSectionSchema = z.object({
  type: z.literal("checks"),
  id: sectionId,
  items: z
    .array(
      z.object({
        id: z.string().min(1),
        prompt: z.string().min(1),
        answer: z.string().min(1),
      }),
    )
    .min(1),
});

export const investigationSectionSchema = z.object({
  type: z.literal("investigation"),
  id: sectionId,
  causes: z.array(z.string().min(1)).min(1),
  order: z.array(z.string().min(1)).min(1),
  commands: z
    .array(
      z.object({
        command: z.string().min(1),
        shows: z.string().min(1),
      }),
    )
    .min(1),
  implications: z.array(z.string().min(1)).min(1),
  distinguishing: z.array(z.string().min(1)).min(1),
  mistakes: z.array(z.string().min(1)).min(1),
});

export const sectionSchema = z.discriminatedUnion("type", [
  principleSectionSchema,
  proseSectionSchema,
  stepsSectionSchema,
  comparisonSectionSchema,
  exampleSectionSchema,
  calloutSectionSchema,
  expandableSectionSchema,
  vizSectionSchema,
  whySectionSchema,
  checksSectionSchema,
  investigationSectionSchema,
]);

export const lessonBodySchema = z.object({
  id: z.string().regex(/^m\d{2}\.[a-z0-9]+(?:-[a-z0-9]+)*$/),
  status: z.enum(["draft", "complete"]),
  objectives: z.array(z.string().min(1)),
  prerequisites: z.array(z.string().min(1)),
  sections: z.array(sectionSchema),
  claims: z.array(claimSchema),
  references: z.array(referenceSchema),
});

export const sourceSchema = z.object({
  id: z.string().regex(/^[a-z0-9]+(?:-[a-z0-9]+)*$/),
  title: z.string().min(1),
  url: z.string().url().optional(),
  kind: z.enum(["rfc", "man-page", "vendor", "textbook"]),
  note: z.string().min(1).optional(),
});

export type LessonStatus = z.infer<typeof lessonStatusSchema>;
export type Claim = z.infer<typeof claimSchema>;
export type Reference = z.infer<typeof referenceSchema>;
export type Section = z.infer<typeof sectionSchema>;
export type LessonBody = z.infer<typeof lessonBodySchema>;
export type Source = z.infer<typeof sourceSchema>;

export type Lesson = {
  id: string;
  moduleId: string;
  slug: string;
  title: string;
  order: number;
  summary: string;
  ownsTopics: { id: string; bullet: string; kind?: "scenario" }[];
  status: LessonStatus;
  objectives: string[];
  prerequisites: string[];
  sections: Section[];
  claims: Claim[];
  references: Reference[];
};
