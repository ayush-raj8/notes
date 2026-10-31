import type { Lesson, Section } from "./schema";
import type { ChapterSummary } from "./subjects/cn/chapterSummarySchema";
import type { ModuleSpec } from "./subjects/cn/syllabus";
import type { WhyQuestion } from "./subjects/cn/why/questions";

export type QualityContext = {
  lessonIds: ReadonlySet<string>;
  whyIds: ReadonlySet<string>;
  sourceIds: ReadonlySet<string>;
  approvedVisualIds: ReadonlySet<string>;
  implementedVisualIds: ReadonlySet<string>;
  scenarioTopicIds: ReadonlySet<string>;
};

function hasSection(lesson: Lesson, type: Section["type"]): boolean {
  return lesson.sections.some((section) => section.type === type);
}

export function assessLesson(lesson: Lesson, ctx: QualityContext): string[] {
  const errors: string[] = [];
  const sectionIds = new Set<string>();

  for (const section of lesson.sections) {
    if (sectionIds.has(section.id)) errors.push(`duplicate section id ${section.id}`);
    sectionIds.add(section.id);

    if (section.type === "viz") {
      if (!ctx.approvedVisualIds.has(section.vizId)) {
        errors.push(`visual ${section.vizId} is not in the approved set`);
      } else if (lesson.status === "complete" && !ctx.implementedVisualIds.has(section.vizId)) {
        errors.push(`visual ${section.vizId} is not implemented`);
      }
    }

    if (section.type === "why" && !ctx.whyIds.has(section.questionId)) {
      errors.push(`unknown why question ${section.questionId}`);
    }

    if (section.type === "comparison") {
      for (const row of section.rows) {
        if (row.cells.length !== section.columns.length) {
          errors.push(`comparison ${section.id} row "${row.label}" has the wrong number of cells`);
        }
      }
    }
  }

  for (const prerequisite of lesson.prerequisites) {
    if (prerequisite === lesson.id) errors.push("lesson lists itself as a prerequisite");
    if (!ctx.lessonIds.has(prerequisite)) errors.push(`unknown prerequisite ${prerequisite}`);
  }

  for (const claim of lesson.claims) {
    if (!ctx.sourceIds.has(claim.sourceId))
      errors.push(`claim ${claim.id} cites unknown source ${claim.sourceId}`);
    if (lesson.status === "complete" && claim.subtle && claim.verification !== "checked") {
      errors.push(`subtle claim ${claim.id} is not checked against its source`);
    }
  }

  for (const reference of lesson.references) {
    if (!ctx.sourceIds.has(reference.sourceId)) {
      errors.push(`reference cites unknown source ${reference.sourceId}`);
    }
  }

  if (lesson.status !== "complete") return errors;

  if (lesson.objectives.length === 0) errors.push("complete lesson needs objectives");
  if (lesson.sections.filter((section) => section.type === "principle").length !== 1) {
    errors.push("complete lesson needs exactly one principle section");
  }

  const checkCount = lesson.sections
    .filter((section) => section.type === "checks")
    .reduce((count, section) => count + (section.type === "checks" ? section.items.length : 0), 0);
  if (checkCount < 2) errors.push("complete lesson needs at least two checks");

  const referencedSources = new Set(lesson.references.map((reference) => reference.sourceId));
  for (const claim of lesson.claims) {
    if (!referencedSources.has(claim.sourceId)) {
      errors.push(`claim ${claim.id} source is missing from the lesson references`);
    }
  }

  const ownsScenario = lesson.ownsTopics.some((item) => ctx.scenarioTopicIds.has(item.id));
  if (ownsScenario && !hasSection(lesson, "investigation")) {
    errors.push("scenario lesson needs an investigation section");
  }

  return errors;
}

export function assessWhyLinkage(lessons: Lesson[], questions: WhyQuestion[]): string[] {
  const errors: string[] = [];
  const byId = new Map(lessons.map((lesson) => [lesson.id, lesson]));

  for (const question of questions) {
    const lesson = byId.get(question.primaryLessonId);
    if (!lesson) {
      errors.push(`${question.id} points at missing lesson ${question.primaryLessonId}`);
      continue;
    }
    const embedded = lesson.sections.some(
      (section) => section.type === "why" && section.questionId === question.id,
    );
    if (question.status === "answered" && lesson.status !== "complete") {
      errors.push(`${question.id} is answered but ${lesson.id} is not complete`);
    }
    if (question.status === "answered" && !embedded) {
      errors.push(`${question.id} is answered but not embedded in ${lesson.id}`);
    }
    if (lesson.status === "complete" && question.status !== "answered") {
      errors.push(`${lesson.id} is complete but does not answer ${question.id}`);
    }
    if (lesson.status === "complete" && !embedded) {
      errors.push(`${lesson.id} is complete but does not embed ${question.id}`);
    }
  }

  return errors;
}

export function assessCompletedModule(module: ModuleSpec, lessons: Lesson[]): string[] {
  if (!lessons.every((lesson) => lesson.status === "complete")) return [];
  const errors: string[] = [];
  const requirements = module.requirements as readonly string[];
  const hasExample = lessons.some((lesson) => hasSection(lesson, "example"));
  const hasTimeline = lessons.some((lesson) =>
    lesson.sections.some((section) => section.type === "viz" && section.vizId === "tcp-timeline"),
  );

  if (requirements.includes("worked-example") && !hasExample) {
    errors.push(`${module.id} needs a worked example`);
  }
  if (requirements.includes("tcp-timeline") && !hasTimeline) {
    errors.push(`${module.id} needs the TCP timeline visualization`);
  }
  if (requirements.includes("investigation-per-scenario")) {
    const scenarios = lessons.filter((lesson) =>
      lesson.ownsTopics.some((topic) => topic.kind === "scenario"),
    );
    for (const lesson of scenarios) {
      if (!hasSection(lesson, "investigation")) {
        errors.push(`${lesson.id} needs an investigation section`);
      }
    }
  }
  return errors;
}

export function assessChapterSummary(
  summary: ChapterSummary,
  module: ModuleSpec,
  questions: WhyQuestion[],
): string[] {
  const errors: string[] = [];
  if (summary.moduleId !== module.id) {
    errors.push(`summary moduleId ${summary.moduleId} does not match ${module.id}`);
  }
  if (summary.status !== "complete") return errors;

  if (summary.readingMinutes.min > summary.readingMinutes.max) {
    errors.push(`${module.id} chapter summary readingMinutes.min exceeds max`);
  }

  const expectedBullets = [...module.bullets];
  if (summary.coverage.length !== expectedBullets.length) {
    errors.push(
      `${module.id} chapter summary coverage has ${summary.coverage.length} entries; expected ${expectedBullets.length}`,
    );
  }
  summary.coverage.forEach((entry, index) => {
    if (entry.bullet !== expectedBullets[index]) {
      errors.push(
        `${module.id} chapter summary coverage[${index}] bullet text does not match the syllabus`,
      );
    }
    if (!entry.notes.includes("•")) {
      errors.push(`${module.id} chapter summary coverage[${index}] notes must use • bullets`);
    }
  });

  const expectedWhy = questions
    .filter((question) => question.primaryLessonId.startsWith(`${module.id}.`))
    .map((question) => question.id)
    .sort();
  const actualWhy = [...summary.whyAnchors].sort();
  if (actualWhy.join(",") !== expectedWhy.join(",")) {
    errors.push(
      `${module.id} chapter summary whyAnchors must be exactly [${expectedWhy.join(", ")}]`,
    );
  }

  for (const field of [
    summary.mentalModel,
    summary.connections.before,
    summary.connections.after,
  ]) {
    if (!field.includes("•")) {
      errors.push(`${module.id} chapter summary prose fields must use • bullets`);
      break;
    }
  }

  return errors;
}
