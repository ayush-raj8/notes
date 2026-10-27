import { describe, expect, it } from "vitest";
import type { Lesson } from "@/content/schema";
import {
  assessCompletedModule,
  assessLesson,
  assessWhyLinkage,
  type QualityContext,
} from "@/content/quality";
import type { ModuleSpec } from "@/content/subjects/cn/syllabus";
import type { WhyQuestion } from "@/content/subjects/cn/why/questions";

const ctx: QualityContext = {
  lessonIds: new Set(["m01.layering", "m01.encapsulation"]),
  whyIds: new Set(["why.03"]),
  sourceIds: new Set(["rfc1122"]),
  approvedVisualIds: new Set(["encapsulation-stack"]),
  implementedVisualIds: new Set(["encapsulation-stack"]),
  scenarioTopicIds: new Set(["m13.dns-ok-tcp-fails"]),
};

function completeLesson(overrides: Partial<Lesson> = {}): Lesson {
  return {
    id: "m01.layering",
    moduleId: "m01",
    slug: "layering",
    title: "Why networks are layered",
    order: 2,
    summary: "Scope",
    ownsTopics: [],
    status: "complete",
    objectives: ["Explain layering."],
    prerequisites: [],
    sections: [
      {
        type: "principle",
        id: "principle",
        problem: "One protocol would have to solve every problem.",
        whyItMatters: "Routers and applications change separately.",
        idea: "Give each layer one job.",
        howItWorks: "Each layer adds a header and hands the result down.",
        whyItWorks: "The lower layer does not need the application's meaning.",
        limits: "A layer cannot repair a problem it is not allowed to see.",
        connections: "TCP sits above IP.",
      },
      {
        type: "why",
        id: "why-layers",
        questionId: "why.03",
      },
      {
        type: "checks",
        id: "checks",
        items: [
          {
            id: "a",
            prompt: "Why not one protocol?",
            answer: "The jobs change at different rates.",
          },
          {
            id: "b",
            prompt: "What does a router look at?",
            answer: "The network header, not the application.",
          },
        ],
      },
    ],
    claims: [
      {
        id: "claim.layers",
        statement: "IP forwarding does not interpret application payloads.",
        sourceId: "rfc1122",
        locator: "Section 1.1.3",
        verification: "checked",
        subtle: true,
        scope: "rfc",
      },
    ],
    references: [{ sourceId: "rfc1122", note: "Host requirements discuss layering." }],
    ...overrides,
  };
}

const question: WhyQuestion = {
  id: "why.03",
  number: 3,
  slug: "why-layers",
  group: "protocol-layering",
  set: "core",
  prompt: "Why is TCP/IP divided into layers instead of using one protocol for everything?",
  primaryLessonId: "m01.layering",
  alsoAppearsIn: [],
  conciseAnswer: "So a router can forward without understanding every application.",
  status: "answered",
};

describe("lesson quality gate", () => {
  it("accepts a complete lesson that meets the bar", () => {
    expect(assessLesson(completeLesson(), ctx)).toEqual([]);
    expect(assessWhyLinkage([completeLesson()], [question])).toEqual([]);
  });

  it("rejects a complete lesson with an unchecked subtle claim", () => {
    const lesson = completeLesson({
      claims: [
        {
          id: "claim.layers",
          statement: "A subtle claim.",
          sourceId: "rfc1122",
          locator: "Section 1",
          verification: "unread",
          subtle: true,
        },
      ],
    });
    expect(assessLesson(lesson, ctx)).toContain(
      "subtle claim claim.layers is not checked against its source",
    );
  });

  it("rejects an unknown visual and a missing principle", () => {
    const lesson = completeLesson({
      sections: [
        {
          type: "viz",
          id: "viz",
          vizId: "not-real",
          scenario: "demo",
          learningObjective: "See something.",
          caption: "A caption.",
        },
        {
          type: "checks",
          id: "checks",
          items: [
            { id: "a", prompt: "One?", answer: "Yes." },
            { id: "b", prompt: "Two?", answer: "Yes." },
          ],
        },
      ],
    });
    const errors = assessLesson(lesson, ctx);
    expect(errors).toContain("visual not-real is not in the approved set");
    expect(errors).toContain("complete lesson needs exactly one principle section");
  });

  it("requires an implemented visual and a worked example when a module is complete", () => {
    const module = {
      id: "m03",
      requirements: ["worked-example"],
    } as unknown as ModuleSpec;
    const lesson = completeLesson({ id: "m03.ipv4-cidr", moduleId: "m03", status: "complete" });
    expect(assessCompletedModule(module, [lesson])).toContain("m03 needs a worked example");
  });

  it("does not treat a planned lesson as complete", () => {
    const lesson = completeLesson({
      status: "planned",
      sections: [],
      claims: [],
      references: [],
      objectives: [],
    });
    expect(assessLesson(lesson, ctx)).toEqual([]);
  });
});
