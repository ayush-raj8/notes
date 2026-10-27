import { describe, expect, it } from "vitest";
import {
  continueLessonId,
  dueAtFor,
  dueItems,
  emptyProgress,
  markCheck,
  markLessonLearned,
  markLessonOpened,
  markWhy,
  parseProgress,
  serializeProgress,
  summarizeProgress,
} from "@/progress/model";
import { createMemoryStorage } from "@/progress/storage";

const now = "2026-01-01T00:00:00.000Z";
const written = { id: "m01.layering", status: "complete" as const };
const planned = { id: "m01.encapsulation", status: "planned" as const };

describe("progress model", () => {
  it("does not record progress for an unwritten lesson", () => {
    const opened = markLessonOpened(emptyProgress(), planned, now);
    expect(opened.lessons).toEqual({});
    const learned = markLessonLearned(emptyProgress(), planned, now);
    expect(learned.lessons).toEqual({});
    const checked = markCheck(emptyProgress(), planned, "check.a", "missed", now);
    expect(checked.lessons).toEqual({});
  });

  it("schedules why-questions and keeps shaky checks due", () => {
    let state = markWhy(emptyProgress(), "why.11", "missed", now);
    expect(state.why["why.11"]?.dueAt).toBe(dueAtFor("missed", now));
    expect(dueItems(state, now)).toEqual([]);
    expect(dueItems(state, "2026-01-02T00:00:00.000Z")).toEqual([
      { kind: "why", whyId: "why.11", confidence: "missed" },
    ]);

    state = markCheck(state, written, "check.a", "shaky", now);
    expect(dueItems(state, now)).toEqual([
      { kind: "check", lessonId: written.id, checkId: "check.a", confidence: "shaky" },
    ]);
    state = markCheck(state, written, "check.a", "known", now);
    expect(dueItems(state, now)).toEqual([]);
  });

  it("does not downgrade a finished lesson when it is opened again", () => {
    const finished = markLessonLearned(emptyProgress(), written, now);
    const reopened = markLessonOpened(finished, written, "2026-02-01T00:00:00.000Z");
    expect(reopened.lessons[written.id]?.status).toBe("complete");
  });

  it("round-trips a valid file and rejects a corrupt one", () => {
    const state = markWhy(emptyProgress(), "why.03", "known", now);
    const parsed = parseProgress(serializeProgress(state));
    expect(parsed.ok).toBe(true);
    if (parsed.ok) expect(parsed.state).toEqual(state);
    expect(parseProgress("{").ok).toBe(false);
    expect(parseProgress("{}").ok).toBe(false);
  });

  it("counts only written lessons as finishable and continues the in-progress one", () => {
    const lessons = [planned, written];
    expect(summarizeProgress(emptyProgress(), lessons)).toEqual({
      written: 1,
      finished: 0,
      total: 2,
    });
    const started = markLessonOpened(emptyProgress(), written, now);
    expect(continueLessonId(started, lessons)).toBe(written.id);
    const storage = createMemoryStorage();
    storage.save(started);
    expect(storage.load()).toEqual(started);
  });
});
