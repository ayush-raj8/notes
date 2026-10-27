import type { LessonBody } from "../../../schema";

/**
 * Lesson bodies keyed by lesson id.
 * A missing key means the lesson is still planned.
 * Register a body here when its file exists. Do not duplicate a lesson in a second file.
 */
export const lessonBodies: Record<string, LessonBody> = {};
