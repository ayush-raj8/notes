import { createElement, type ComponentType, type ReactNode } from "react";
import { approvedVisuals } from "@/content/subjects/cn/visualContract";
import { TcpTimelineView } from "./views/TcpTimelineView";
import type { VisualViewProps } from "./views/types";

const views: Record<string, { title: string; view: ComponentType<VisualViewProps> }> = {
  "tcp-timeline": { title: "TCP timeline", view: TcpTimelineView },
};

/** Ids whose model, view, and model test all exist. */
export const implementedVisualIds: ReadonlySet<string> = new Set(Object.keys(views));

export function visualDefinition(id: string) {
  return approvedVisuals.find((item) => item.id === id);
}

export function getVisualView(id: string): ComponentType<VisualViewProps> | undefined {
  return views[id]?.view;
}

export function visualTitle(id: string): string {
  return views[id]?.title ?? id;
}

/** Renders an implemented visual, or returns null so the caller can show the fallback. */
export function renderVisual(id: string, props: VisualViewProps): ReactNode {
  const view = getVisualView(id);
  return view ? createElement(view, { key: props.scenario, ...props }) : null;
}
