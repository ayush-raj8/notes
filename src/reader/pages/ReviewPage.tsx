import { useState } from "react";
import { Link } from "react-router-dom";
import { findLessonById, lessonPath, whyQuestionPath } from "@/content/subjects/cn/load";
import { whyQuestions } from "@/content/subjects/cn/why/questions";
import { useProgress } from "@/app/progress/ProgressContext";
import { dueItems, parseProgress, serializeProgress } from "@/progress/model";
import { usePageTitle } from "../usePageTitle";

function readFileText(file: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.addEventListener("load", () => resolve(String(reader.result ?? "")));
    reader.addEventListener("error", () =>
      reject(reader.error ?? new Error("Could not read that file.")),
    );
    reader.readAsText(file);
  });
}

export function ReviewPage() {
  usePageTitle("Review");
  const { state, replace } = useProgress();
  const due = dueItems(state, new Date().toISOString());
  const [error, setError] = useState<string | null>(null);
  const [pending, setPending] = useState<string | null>(null);

  function download() {
    const blob = new Blob([serializeProgress(state)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "cn-theory-progress.json";
    link.click();
    URL.revokeObjectURL(url);
  }

  async function onFile(file: File | undefined) {
    setError(null);
    setPending(null);
    if (!file) return;
    let text: string;
    try {
      text = await readFileText(file);
    } catch {
      setError("Could not read that file.");
      return;
    }
    const parsed = parseProgress(text);
    if (!parsed.ok) {
      setError(parsed.error);
      return;
    }
    setPending(serializeProgress(parsed.state));
  }

  return (
    <article className="lesson-column">
      <h1>Review</h1>
      <p className="lede">
        Shaky and missed checks stay here until you mark them known. A why-question comes back after
        you grade it: the next day if you missed it, in three days if it was shaky, and in fourteen
        days if you knew it.
      </p>
      {due.length === 0 ? (
        <div className="empty">
          <strong>Nothing is due.</strong>
          <p>Work through a written lesson and mark a check to build this queue.</p>
        </div>
      ) : (
        <ul className="plain-list">
          {due.map((item) => {
            if (item.kind === "why") {
              const question = whyQuestions.find((entry) => entry.id === item.whyId);
              return (
                <li key={`why-${item.whyId}`}>
                  {question ? (
                    <Link to={whyQuestionPath(question.slug)}>{question.prompt}</Link>
                  ) : (
                    item.whyId
                  )}{" "}
                  <span className="badge">{item.confidence}</span>
                </li>
              );
            }
            const lesson = findLessonById(item.lessonId);
            return (
              <li key={`check-${item.lessonId}-${item.checkId}`}>
                {lesson ? <Link to={lessonPath(lesson)}>{lesson.title}</Link> : item.lessonId}
                <span>
                  {" "}
                  · check {item.checkId} · {item.confidence}
                </span>
              </li>
            );
          })}
        </ul>
      )}
      <h2>Saved on this browser</h2>
      <p>
        Progress stays in local storage. Export it if you want a copy. Import replaces what is
        stored here.
      </p>
      <div className="actions">
        <button type="button" className="button secondary" onClick={download}>
          Export progress
        </button>
      </div>
      <label>
        Import a progress file
        <input
          className="file-input"
          type="file"
          accept="application/json,.json"
          onChange={(event) => {
            const file = event.target.files?.[0];
            void onFile(file);
            event.target.value = "";
          }}
        />
      </label>
      {error ? (
        <p className="alert" role="alert">
          {error}
        </p>
      ) : null}
      {pending ? (
        <div className="card">
          <p>Replace the progress stored in this browser with this file?</p>
          <div className="actions">
            <button
              type="button"
              className="button"
              onClick={() => {
                const parsed = parseProgress(pending);
                if (parsed.ok) replace(parsed.state);
                setPending(null);
              }}
            >
              Replace progress
            </button>
            <button type="button" className="button secondary" onClick={() => setPending(null)}>
              Cancel
            </button>
          </div>
        </div>
      ) : null}
    </article>
  );
}
