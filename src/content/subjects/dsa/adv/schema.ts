import { z } from "zod";

export const advModuleIdSchema = z.enum([
  "hashing-sets",
  "stack-queue-deque",
  "cache-design",
  "ordered-time",
  "randomized",
  "heaps-streaming",
  "tries-prefix",
  "trees-ordered-set",
  "linked-multi",
  "union-find",
  "fenwick-segment",
  "counters-leaderboards",
  "stateful-apis",
]);

export const advTierSchema = z.enum(["core", "advanced", "extension"]);
export const advDifficultySchema = z.enum(["easy", "medium", "hard"]);
export const advSourceSchema = z.enum(["leetcode", "custom"]);

export const complexityTargetSchema = z.object({
  operation: z.string().min(1),
  target: z.string().min(1),
});

export const approachSchema = z.object({
  naive: z.string().min(1),
  whyNaiveFails: z.string().min(1),
  optimized: z.string().min(1),
  alternatives: z.string().min(1),
});

export const structuresSchema = z.object({
  pieces: z.array(z.string().min(1)).min(1),
  invariants: z.array(z.string().min(1)).min(1),
  syncRisk: z.string().min(1),
});

export const followUpSchema = z.object({
  title: z.string().min(1),
  change: z.string().min(1),
  why: z.string().min(1),
});

export const revisionSummarySchema = z.object({
  pattern: z.string().min(1),
  invariant: z.string().min(1),
  hardestOp: z.string().min(1),
  tradeoff: z.string().min(1),
  mistakes: z.array(z.string().min(1)).min(1),
});

export const advDesignQuestionSchema = z.object({
  id: z.string().regex(/^adv\.[a-z0-9]+(?:\.[a-z0-9-]+)+$/),
  slug: z.string().regex(/^[a-z0-9]+(?:-[a-z0-9]+)*$/),
  title: z.string().min(1),
  moduleId: advModuleIdSchema,
  tier: advTierSchema,
  difficulty: advDifficultySchema,
  source: advSourceSchema,
  leetcodeId: z.number().int().positive().optional(),
  leetcodeUrl: z.string().url().optional(),
  learningOrder: z.number().int().positive(),
  primaryStructures: z.array(z.string().min(1)).min(1),
  combinedStructures: z.array(z.string().min(1)).default([]),
  keyOperations: z.array(z.string().min(1)).min(1),
  complexityTargets: z.array(complexityTargetSchema).min(1),
  prerequisites: z.array(z.string().min(1)).default([]),
  relatedIds: z.array(z.string().min(1)).default([]),
  statement: z.string().min(1),
  operations: z.array(z.string().min(1)).min(1),
  constraints: z.array(z.string().min(1)).min(1),
  edgeCases: z.array(z.string().min(1)).min(1),
  example: z.object({
    setup: z.string().min(1),
    steps: z.array(z.string().min(1)).min(1),
  }),
  clarifyingQuestions: z.array(z.string().min(1)).min(1),
  approaches: approachSchema,
  structures: structuresSchema,
  algorithm: z.array(z.string().min(1)).min(1),
  pseudocode: z.string().min(1),
  implementation: z.string().min(1),
  dryRun: z.object({
    setup: z.string().min(1),
    steps: z.array(z.string().min(1)).min(1),
    result: z.string().min(1),
  }),
  complexityNotes: z.string().min(1),
  followUps: z.array(followUpSchema).min(1),
  revision: revisionSummarySchema,
});

export type AdvModuleId = z.infer<typeof advModuleIdSchema>;
export type AdvTier = z.infer<typeof advTierSchema>;
export type AdvDifficulty = z.infer<typeof advDifficultySchema>;
export type AdvSource = z.infer<typeof advSourceSchema>;
export type AdvDesignQuestion = z.infer<typeof advDesignQuestionSchema>;

export const advModuleMeta: Record<AdvModuleId, { order: number; title: string; summary: string }> =
  {
    "hashing-sets": {
      order: 1,
      title: "Hashing and Set-Based Design",
      summary: "Maps, sets, and auxiliary indexes for O(1) insert/delete/lookup.",
    },
    "stack-queue-deque": {
      order: 2,
      title: "Stack, Queue, and Deque Design",
      summary: "Fixed rings, dual-stack queues, and min/max with amortized costs.",
    },
    "cache-design": {
      order: 3,
      title: "Cache Design",
      summary: "LRU/LFU eviction with hash maps, lists, and frequency buckets.",
    },
    "ordered-time": {
      order: 4,
      title: "Ordered Data and Time-Based Queries",
      summary: "Timestamped history, snapshots, and binary search over versions.",
    },
    randomized: {
      order: 5,
      title: "Randomized and Sampling Structures",
      summary: "Uniform and weighted random with swap-delete indexes.",
    },
    "heaps-streaming": {
      order: 6,
      title: "Heaps, Priority Queues, and Streaming",
      summary: "Medians, top-k streams, lazy heap deletes, and feeds.",
    },
    "tries-prefix": {
      order: 7,
      title: "Tries and Prefix-Based APIs",
      summary: "Prefix trees, wildcards, autocomplete, and delete metadata.",
    },
    "trees-ordered-set": {
      order: 8,
      title: "Trees and Ordered-Set Design",
      summary: "Rank, predecessor/successor, and range queries under updates.",
    },
    "linked-multi": {
      order: 9,
      title: "Linked Lists and Multi-Structure Design",
      summary: "Sentinels, node handles, and map+list composition.",
    },
    "union-find": {
      order: 10,
      title: "Union-Find and Dynamic Connectivity",
      summary: "Reusable connectivity APIs with component metadata.",
    },
    "fenwick-segment": {
      order: 11,
      title: "Fenwick Tree and Segment Tree APIs",
      summary: "Mutable range queries when prefix sums alone are not enough.",
    },
    "counters-leaderboards": {
      order: 12,
      title: "Counters, Frequency, and Leaderboards",
      summary: "All-O(1) frequency maps and dynamic top-k rankings.",
    },
    "stateful-apis": {
      order: 13,
      title: "Stateful APIs and Object Design",
      summary: "Multi-structure APIs with clear contracts and state transitions.",
    },
  };

export const tierLabels: Record<AdvTier, string> = {
  core: "Core",
  advanced: "Advanced",
  extension: "Extension",
};

export const difficultyLabels: Record<AdvDifficulty, string> = {
  easy: "Easy",
  medium: "Medium",
  hard: "Hard",
};

export const sourceLabels: Record<AdvSource, string> = {
  leetcode: "LeetCode",
  custom: "Custom Practice",
};
