import { approvedVisuals } from "@/content/subjects/cn/visualContract";

/** Ids whose model and view both exist. Empty until a visualization milestone implements one. */
export const implementedVisualIds: ReadonlySet<string> = new Set();

export function visualDefinition(id: string) {
  return approvedVisuals.find((item) => item.id === id);
}
