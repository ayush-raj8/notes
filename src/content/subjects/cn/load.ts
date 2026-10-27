import { lessonBodies } from "./bodies";
import { lessonCatalog, type CatalogLesson } from "./lessonCatalog";
import { lessonBodySchema, type Lesson, type LessonStatus } from "../../schema";
import { modules, type ModuleId, type ModuleSpec } from "./syllabus";
import { whyQuestions, type WhyQuestion } from "./why/questions";

const parsedBodies = new Map(
  Object.entries(lessonBodies).map(([id, body]) => {
    const parsed = lessonBodySchema.parse(body);
    if (parsed.id !== id) {
      throw new Error(`Lesson body key ${id} does not match id ${parsed.id}`);
    }
    return [id, parsed] as const;
  }),
);

function mergeLesson(entry: CatalogLesson): Lesson {
  const body = parsedBodies.get(entry.id);
  return {
    id: entry.id,
    moduleId: entry.moduleId,
    slug: entry.slug,
    title: entry.title,
    order: entry.order,
    summary: entry.summary,
    ownsTopics: entry.ownsTopics,
    status: body?.status ?? "planned",
    objectives: body?.objectives ?? [],
    prerequisites: body?.prerequisites ?? [],
    sections: body?.sections ?? [],
    claims: body?.claims ?? [],
    references: body?.references ?? [],
  };
}

export type CourseModule = ModuleSpec & { lessons: Lesson[] };

const lessons = lessonCatalog.map(mergeLesson);

export const curriculum = {
  modules: modules.map((module) => ({
    ...module,
    lessons: lessons
      .filter((lesson) => lesson.moduleId === module.id)
      .sort((a, b) => a.order - b.order),
  })),
  lessons,
  whyQuestions,
};

export function findModuleBySlug(slug: string): CourseModule | undefined {
  return curriculum.modules.find((module) => module.slug === slug);
}

export function findLesson(moduleSlug: string, lessonSlug: string): Lesson | undefined {
  return findModuleBySlug(moduleSlug)?.lessons.find((lesson) => lesson.slug === lessonSlug);
}

export function findLessonById(id: string): Lesson | undefined {
  return curriculum.lessons.find((lesson) => lesson.id === id);
}

export function findWhyBySlug(slug: string): WhyQuestion | undefined {
  return curriculum.whyQuestions.find((question) => question.slug === slug);
}

export const cnHomePath = "/cn";
export const cnLearnPath = "/cn/learn";
export const cnWhyPath = "/cn/why";
export const cnReviewPath = "/cn/review";

export function modulePath(moduleSlug: string): string {
  return `${cnLearnPath}/${moduleSlug}`;
}

export function whyQuestionPath(slug: string): string {
  return `${cnWhyPath}/${slug}`;
}

export function lessonPath(lesson: Pick<Lesson, "moduleId" | "slug">): string {
  const module = modules.find((item) => item.id === lesson.moduleId);
  if (!module) throw new Error(`Missing module ${lesson.moduleId}`);
  return `${cnLearnPath}/${module.slug}/${lesson.slug}`;
}

export function lessonNeighbors(lessonId: string): { previous?: Lesson; next?: Lesson } {
  const index = curriculum.lessons.findIndex((lesson) => lesson.id === lessonId);
  if (index === -1) return {};
  return {
    previous: curriculum.lessons[index - 1],
    next: curriculum.lessons[index + 1],
  };
}

export function moduleContentStatus(lessonsInModule: Lesson[]): LessonStatus {
  if (lessonsInModule.every((lesson) => lesson.status === "complete")) return "complete";
  if (lessonsInModule.some((lesson) => lesson.status !== "planned")) return "draft";
  return "planned";
}

export function moduleById(id: ModuleId): ModuleSpec {
  const module = modules.find((item) => item.id === id);
  if (!module) throw new Error(`Missing module ${id}`);
  return module;
}
