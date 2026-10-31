import { useState } from "react";
import { Link } from "react-router-dom";
import type { ChapterSummary } from "@/content/subjects/cn/chapterSummarySchema";
import { whyQuestionPath } from "@/content/subjects/cn/load";
import { whyQuestions } from "@/content/subjects/cn/why/questions";

function NotesBlock({ text }: { text: string }) {
  return (
    <ul className="plain-list notes-bullets">
      {text
        .split("\n")
        .map((line) => line.trim())
        .filter(Boolean)
        .map((line) => (
          <li key={line}>{line.replace(/^•\s*/, "")}</li>
        ))}
    </ul>
  );
}

export function ChapterSummaryView({ summary }: { summary: ChapterSummary }) {
  const [openCheck, setOpenCheck] = useState<string | null>(null);
  const whyById = new Map(whyQuestions.map((question) => [question.id, question]));

  if (summary.status !== "complete") {
    return (
      <section className="chapter-summary" aria-labelledby="chapter-notes-heading">
        <h2 id="chapter-notes-heading">Chapter notes (10–20 min)</h2>
        <p className="meta">Summary not written yet.</p>
      </section>
    );
  }

  return (
    <section className="chapter-summary" aria-labelledby="chapter-notes-heading">
      <h2 id="chapter-notes-heading">Chapter notes (10–20 min)</h2>
      <p className="meta">
        Target {summary.readingMinutes.min}–{summary.readingMinutes.max} minutes · covers every
        syllabus bullet
      </p>
      <p className="lede">{summary.thesis}</p>

      <h3>After this skim you can</h3>
      <ul className="plain-list">
        {summary.outcomes.map((outcome) => (
          <li key={outcome}>{outcome}</li>
        ))}
      </ul>

      <h3>Syllabus coverage</h3>
      <ol className="chapter-coverage">
        {summary.coverage.map((entry) => (
          <li key={entry.bullet}>
            <p className="coverage-bullet">{entry.bullet}</p>
            <NotesBlock text={entry.notes} />
          </li>
        ))}
      </ol>

      <h3>Mental model</h3>
      <NotesBlock text={summary.mentalModel} />

      <h3>Failures and signals</h3>
      <ul className="plain-list">
        {summary.failuresAndSignals.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>

      {summary.whyAnchors.length > 0 ? (
        <>
          <h3>Why anchors</h3>
          <ul className="plain-list">
            {summary.whyAnchors.map((id) => {
              const question = whyById.get(id);
              if (!question) return <li key={id}>{id}</li>;
              return (
                <li key={id}>
                  <Link to={whyQuestionPath(question.slug)}>
                    {id}: {question.prompt}
                  </Link>
                </li>
              );
            })}
          </ul>
        </>
      ) : null}

      <h3>Connections</h3>
      <p className="meta">Before</p>
      <NotesBlock text={summary.connections.before} />
      <p className="meta">After</p>
      <NotesBlock text={summary.connections.after} />

      <h3>Self-check</h3>
      <ul className="check-list">
        {summary.selfCheck.map((item) => {
          const open = openCheck === item.id;
          return (
            <li key={item.id}>
              <p>{item.prompt}</p>
              <button
                type="button"
                className="button secondary"
                aria-expanded={open}
                onClick={() => setOpenCheck(open ? null : item.id)}
              >
                {open ? "Hide answer" : "Show answer"}
              </button>
              {open ? <p className="check-answer">{item.answer}</p> : null}
            </li>
          );
        })}
      </ul>
    </section>
  );
}
