import { z } from "zod";

export const sourceClassificationSchema = z.enum([
  "reported-google",
  "hello-interview-guide",
  "related-practice",
]);

export const verificationStatusSchema = z.enum(["verified", "partially-verified", "unverified"]);

export const reportedLevelSchema = z.enum(["L3", "L4", "L5", "unspecified"]);

export const prioritySchema = z.enum(["must-revise", "important", "optional"]);

export const designCodingCategorySchema = z.enum([
  "linked-lists-hash-integrity",
  "caches-eviction",
  "rate-limiting-time",
  "graph-serialization",
  "trees-random-selection",
  "tries-route-matching",
  "intervals-range",
  "iterators",
  "scheduling-heaps",
  "file-processing",
  "parsing-substitution",
  "misc-ds-design",
]);

export const sourceRefSchema = z.object({
  label: z.string().min(1),
  url: z.string().url(),
  note: z.string().min(1).optional(),
});

export const followUpSchema = z.object({
  text: z.string().min(1),
  kind: z.enum(["reported", "practice"]),
  guidance: z.string().min(1).optional(),
});

export const complexityOpSchema = z.object({
  operation: z.string().min(1),
  time: z.string().min(1),
  space: z.string().min(1).optional(),
  notes: z.string().min(1).optional(),
});

export const designCodingQuestionSchema = z.object({
  id: z.string().regex(/^(dc|rp)\.[a-z0-9]+(?:-[a-z0-9]+)*$/),
  slug: z.string().regex(/^[a-z0-9]+(?:-[a-z0-9]+)*$/),
  title: z.string().min(1),
  classification: sourceClassificationSchema,
  level: reportedLevelSchema,
  interviewFormat: z.string().min(1).optional(),
  priority: prioritySchema,
  verification: verificationStatusSchema,
  categories: z.array(designCodingCategorySchema).min(1),
  sources: z.array(sourceRefSchema).min(1),
  leetcodeUrl: z.string().url().optional(),
  coreApproach: z.string().min(1),
  summaryComplexity: z.string().min(1),
  originalPrompt: z.string().min(1),
  originalRequirements: z.array(z.string().min(1)).min(1),
  reportedFollowUps: z.array(z.string().min(1)).optional(),
  practiceVariations: z.array(z.string().min(1)).optional(),
  clarifyingQuestions: z.array(z.string().min(1)).min(1),
  approach: z.object({
    whyStructure: z.string().min(1),
    invariant: z.string().min(1),
    operations: z.string().min(1),
    whyNotSimpler: z.string().min(1),
    tradeoffs: z.string().min(1),
  }),
  algorithm: z.array(z.string().min(1)).min(1),
  pseudocode: z.string().min(1),
  implementation: z.string().min(1),
  complexity: z.array(complexityOpSchema).min(1),
  dryRun: z.object({
    setup: z.string().min(1),
    steps: z.array(z.string().min(1)).min(1),
    result: z.string().min(1),
  }),
  edgeCases: z.array(z.string().min(1)).min(1),
  followUps: z.array(followUpSchema).min(1),
  checklist: z.array(z.string().min(1)).min(1),
  relatedQuestionIds: z.array(z.string().min(1)).optional(),
});

export type SourceClassification = z.infer<typeof sourceClassificationSchema>;
export type VerificationStatus = z.infer<typeof verificationStatusSchema>;
export type ReportedLevel = z.infer<typeof reportedLevelSchema>;
export type Priority = z.infer<typeof prioritySchema>;
export type DesignCodingCategory = z.infer<typeof designCodingCategorySchema>;
export type DesignCodingQuestion = z.infer<typeof designCodingQuestionSchema>;

export const categoryLabels: Record<DesignCodingCategory, string> = {
  "linked-lists-hash-integrity": "Linked Lists and Hash Integrity",
  "caches-eviction": "Caches and Eviction",
  "rate-limiting-time": "Rate Limiting and Time-Based Data",
  "graph-serialization": "Graph Serialization",
  "trees-random-selection": "Trees and Random Selection",
  "tries-route-matching": "Tries and Route Matching",
  "intervals-range": "Intervals and Range Queries",
  iterators: "Iterators",
  "scheduling-heaps": "Scheduling and Heaps",
  "file-processing": "File Processing",
  "parsing-substitution": "Parsing and String Substitution",
  "misc-ds-design": "Miscellaneous Data Structure Design",
};

export const classificationLabels: Record<SourceClassification, string> = {
  "reported-google": "Reported Google interview",
  "hello-interview-guide": "Hello Interview guide",
  "related-practice": "Related practice",
};

export const priorityLabels: Record<Priority, string> = {
  "must-revise": "Must Revise",
  important: "Important",
  optional: "Optional",
};

export const verificationLabels: Record<VerificationStatus, string> = {
  verified: "Verified",
  "partially-verified": "Partially verified",
  unverified: "Unverified",
};
