import type { LessonStatus } from "@/content/schema";
import { contentStatusLabel } from "./labels";

export function ContentBadge({ status }: { status: LessonStatus }) {
  return <span className={`badge badge-${status}`}>{contentStatusLabel(status)}</span>;
}
