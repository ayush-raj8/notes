import { readFileSync } from "node:fs";
import { resolve } from "node:path";
import { describe, expect, it } from "vitest";
import { lessonBodies } from "@/content/subjects/cn/bodies";
import { renderCurriculumMarkdown } from "@/content/subjects/cn/curriculumDocument";
import { curriculum } from "@/content/subjects/cn/load";
import { framingLessonIds, lessonCatalog } from "@/content/subjects/cn/lessonCatalog";
import {
  assessCompletedModule,
  assessLesson,
  assessWhyLinkage,
  type QualityContext,
} from "@/content/quality";
import { sourceById } from "@/content/subjects/cn/sources";
import { modules } from "@/content/subjects/cn/syllabus";
import { approvedVisualIds } from "@/content/subjects/cn/visualContract";
import { whyAnswers } from "@/content/subjects/cn/why/answers";
import { whyQuestions } from "@/content/subjects/cn/why/questions";
import { implementedVisualIds } from "@/visuals/registry";

const expectedBulletCounts: Record<string, number> = {
  m01: 7,
  m02: 7,
  m03: 10,
  m04: 9,
  m05: 29,
  m06: 8,
  m07: 12,
  m08: 16,
  m09: 15,
  m10: 11,
  m11: 16,
  m12: 12,
  m13: 12,
};

function qualityContext(): QualityContext {
  return {
    lessonIds: new Set(curriculum.lessons.map((lesson) => lesson.id)),
    whyIds: new Set(whyQuestions.map((question) => question.id)),
    sourceIds: new Set(sourceById.keys()),
    approvedVisualIds,
    implementedVisualIds,
    scenarioTopicIds: new Set(
      curriculum.lessons.flatMap((lesson) =>
        lesson.ownsTopics.filter((topic) => topic.kind === "scenario").map((topic) => topic.id),
      ),
    ),
  };
}

describe("curriculum catalog", () => {
  it("keeps the 13 modules and their syllabus bullet counts", () => {
    expect(modules.map((module) => module.id)).toEqual([
      "m01",
      "m02",
      "m03",
      "m04",
      "m05",
      "m06",
      "m07",
      "m08",
      "m09",
      "m10",
      "m11",
      "m12",
      "m13",
    ]);
    for (const module of modules) {
      expect(module.bullets, module.id).toHaveLength(expectedBulletCounts[module.id] ?? -1);
      expect(module.bullets.join("\n")).toMatch(/\.$/);
    }
    const joined = modules.flatMap((module) => module.bullets).join("\n");
    expect(joined).toContain("TIME_WAIT and its purpose.");
    expect(joined).toContain("Head-of-line blocking within a TCP byte stream.");
    expect(joined).toContain(
      "One microservice cannot reach another despite both being healthy locally.",
    );
    expect(joined).toContain(
      "Common Linux network diagnostic tools: `ss`, `ip`, `ping`, `traceroute`, `dig`, `curl`, and `tcpdump`.",
    );
  });

  it("gives every syllabus bullet exactly the lessons that claim it, and every topic one owner", () => {
    const topicOwners = new Map<string, string>();
    for (const lesson of lessonCatalog) {
      expect(lesson.id.startsWith(`${lesson.moduleId}.`)).toBe(true);
      if (lesson.ownsTopics.length === 0) {
        expect(framingLessonIds.has(lesson.id)).toBe(true);
      }
      for (const topic of lesson.ownsTopics) {
        expect(topic.id.startsWith(`${lesson.moduleId}.`)).toBe(true);
        expect(topicOwners.has(topic.id), topic.id).toBe(false);
        topicOwners.set(topic.id, lesson.id);
      }
    }

    for (const module of modules) {
      const owned = lessonCatalog
        .filter((lesson) => lesson.moduleId === module.id)
        .flatMap((lesson) => lesson.ownsTopics.map((topic) => topic.bullet));
      expect(new Set(owned)).toEqual(new Set(module.bullets));
    }
  });

  it("keeps lesson ids, slugs, and order stable inside each module", () => {
    const ids = lessonCatalog.map((lesson) => lesson.id);
    expect(new Set(ids).size).toBe(ids.length);
    for (const module of modules) {
      const lessons = lessonCatalog.filter((lesson) => lesson.moduleId === module.id);
      expect(lessons.map((lesson) => lesson.order)).toEqual(lessons.map((_, index) => index + 1));
      const slugs = lessons.map((lesson) => lesson.slug);
      expect(new Set(slugs).size).toBe(slugs.length);
    }
  });

  it("registers lesson bodies only for known lessons", () => {
    for (const id of Object.keys(lessonBodies)) {
      expect(lessonCatalog.some((lesson) => lesson.id === id)).toBe(true);
    }
  });

  it("keeps why.01 through why.55 linked to real lessons", () => {
    expect(whyQuestions).toHaveLength(55);
    expect(whyQuestions.filter((question) => question.set === "core")).toHaveLength(50);
    const lessonIds = new Set(lessonCatalog.map((lesson) => lesson.id));
    whyQuestions.forEach((question, index) => {
      expect(question.number).toBe(index + 1);
      expect(question.id).toBe(`why.${String(question.number).padStart(2, "0")}`);
      expect(lessonIds.has(question.primaryLessonId)).toBe(true);
      for (const id of question.alsoAppearsIn) expect(lessonIds.has(id)).toBe(true);
    });
    expect(whyQuestions[0]?.prompt).toBe("If TCP already exists, why do we need HTTP?");
    expect(whyQuestions[10]?.prompt).toBe(
      "Why does TCP use a three-way handshake rather than a two-way handshake?",
    );
    expect(whyQuestions[49]?.prompt).toBe(
      "Why is debugging network failures a process of eliminating hypotheses rather than checking one component?",
    );
    const slugs = whyQuestions.map((question) => question.slug);
    expect(new Set(slugs).size).toBe(slugs.length);
    for (const id of Object.keys(whyAnswers)) {
      expect(whyQuestions.some((question) => question.id === id)).toBe(true);
    }
  });

  it("matches the generated curriculum inventory", () => {
    const onDisk = readFileSync(resolve(process.cwd(), "CURRICULUM.md"), "utf8");
    expect(onDisk).toBe(renderCurriculumMarkdown());
  });

  it("has no completeness or link errors in the current catalog", () => {
    const ctx = qualityContext();
    const errors = curriculum.lessons.flatMap((lesson) =>
      assessLesson(lesson, ctx).map((error) => `${lesson.id}: ${error}`),
    );
    expect(errors).toEqual([]);
    expect(assessWhyLinkage(curriculum.lessons, whyQuestions)).toEqual([]);
    for (const module of curriculum.modules) {
      expect(assessCompletedModule(module, module.lessons)).toEqual([]);
    }
  });
});
