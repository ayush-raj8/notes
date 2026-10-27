import { describe, expect, it } from "vitest";
import {
  designCodingCounts,
  designCodingQuestions,
  findDesignCodingQuestion,
} from "@/content/subjects/dsa/load";
import { designCodingQuestionSchema } from "@/content/subjects/dsa/schema";

describe("PYQ design coding catalog", () => {
  it("parses every question and keeps unique ids and slugs", () => {
    const ids = new Set<string>();
    const slugs = new Set<string>();
    for (const question of designCodingQuestions) {
      expect(() => designCodingQuestionSchema.parse(question)).not.toThrow();
      expect(ids.has(question.id), question.id).toBe(false);
      expect(slugs.has(question.slug), question.slug).toBe(false);
      ids.add(question.id);
      slugs.add(question.slug);
      expect(question.implementation.length).toBeGreaterThan(40);
      expect(question.sources.length).toBeGreaterThan(0);
    }
  });

  it("keeps related practice out of the reported-google classification", () => {
    for (const question of designCodingQuestions) {
      if (question.id.startsWith("rp.")) {
        expect(question.classification).toBe("related-practice");
      }
      if (question.classification === "reported-google") {
        expect(question.id.startsWith("dc.")).toBe(true);
      }
    }
  });

  it("exposes stable lookup and summary counts", () => {
    expect(findDesignCodingQuestion("secure-linked-list")?.id).toBe("dc.secure-linked-list");
    const counts = designCodingCounts();
    expect(counts.total).toBe(designCodingQuestions.length);
    expect(counts.reported + counts.hello + counts.practice).toBe(counts.total);
    expect(counts.reportedVerified).toBeGreaterThanOrEqual(7);
    expect(counts.hello).toBeGreaterThanOrEqual(2);
    expect(counts.practice).toBeGreaterThanOrEqual(8);
  });

  it("records the reported secure-list hash rule", () => {
    const q = findDesignCodingQuestion("secure-linked-list");
    expect(q?.originalRequirements.join(" ")).toMatch(/hash\(data \+ next\.hashValue\)/);
    expect(q?.originalRequirements.join(" ")).toMatch(/hash\(data\)/);
  });
});
