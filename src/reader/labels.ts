import type { LessonStatus } from "@/content/schema";

export function contentStatusLabel(status: LessonStatus): string {
  if (status === "planned") return "Not written";
  if (status === "draft") return "Partly written";
  return "Ready";
}

export function moduleStatusLabel(status: LessonStatus): string {
  return contentStatusLabel(status);
}
