import { Link, useParams } from "react-router-dom";
import {
  designCodingQuestionPath,
  dsaDesignCodingPath,
  findDesignCodingQuestion,
  findDesignCodingQuestionById,
} from "@/content/subjects/dsa/load";
import {
  categoryLabels,
  classificationLabels,
  priorityLabels,
  verificationLabels,
} from "@/content/subjects/dsa/schema";
import { NotFoundPage } from "../NotFoundPage";
import { usePageTitle } from "../../usePageTitle";

export function DesignCodingQuestionPage() {
  const { questionSlug } = useParams();
  const question = questionSlug ? findDesignCodingQuestion(questionSlug) : undefined;
  usePageTitle(question?.title ?? "Question not found");

  if (!question) {
    return <NotFoundPage />;
  }

  return (
    <article className="lesson-column design-coding-question">
      <p className="crumb">
        <Link to="/dsa">DSA</Link>
        {" / "}
        <Link to={dsaDesignCodingPath}>PYQ — Design Coding</Link>
      </p>
      <h1>{question.title}</h1>
      <p className="meta">
        <span className="badge">{question.id}</span>
        <span className="badge">{classificationLabels[question.classification]}</span>
        <span className="badge">{question.level}</span>
        <span className="badge">{verificationLabels[question.verification]}</span>
        <span className="badge">{priorityLabels[question.priority]}</span>
      </p>
      {question.interviewFormat ? <p className="muted">{question.interviewFormat}</p> : null}
      <p className="meta">
        {question.categories.map((c) => (
          <span key={c} className="badge">
            {categoryLabels[c]}
          </span>
        ))}
      </p>

      <section className="lesson-section">
        <h2>Sources</h2>
        <ul className="plain-list">
          {question.sources.map((s) => (
            <li key={s.url}>
              <a href={s.url} target="_blank" rel="noreferrer">
                {s.label}
              </a>
              {s.note ? <p className="muted">{s.note}</p> : null}
            </li>
          ))}
        </ul>
        {question.leetcodeUrl ? (
          <p>
            Related LeetCode:{" "}
            <a href={question.leetcodeUrl} target="_blank" rel="noreferrer">
              {question.leetcodeUrl}
            </a>
          </p>
        ) : null}
      </section>

      <section className="lesson-section">
        <h2>Original question</h2>
        <p>{question.originalPrompt}</p>
        <h3>Original requirements</h3>
        <ul>
          {question.originalRequirements.map((r) => (
            <li key={r}>{r}</li>
          ))}
        </ul>
        {question.reportedFollowUps && question.reportedFollowUps.length > 0 ? (
          <>
            <h3>Reported interviewer follow-ups</h3>
            <ul>
              {question.reportedFollowUps.map((r) => (
                <li key={r}>{r}</li>
              ))}
            </ul>
          </>
        ) : null}
        {question.practiceVariations && question.practiceVariations.length > 0 ? (
          <>
            <h3>Additional practice variations</h3>
            <ul>
              {question.practiceVariations.map((r) => (
                <li key={r}>{r}</li>
              ))}
            </ul>
          </>
        ) : null}
      </section>

      <section className="lesson-section">
        <h2>Clarifying questions</h2>
        <ul>
          {question.clarifyingQuestions.map((q) => (
            <li key={q}>{q}</li>
          ))}
        </ul>
      </section>

      <section className="lesson-section">
        <h2>Approach from first principles</h2>
        <dl className="principle-grid">
          <div className="principle-item">
            <dt>Why this structure</dt>
            <dd>{question.approach.whyStructure}</dd>
          </div>
          <div className="principle-item">
            <dt>Invariant</dt>
            <dd>{question.approach.invariant}</dd>
          </div>
          <div className="principle-item">
            <dt>Operations</dt>
            <dd>{question.approach.operations}</dd>
          </div>
          <div className="principle-item">
            <dt>Why simpler approaches fall short</dt>
            <dd>{question.approach.whyNotSimpler}</dd>
          </div>
          <div className="principle-item">
            <dt>Trade-offs</dt>
            <dd>{question.approach.tradeoffs}</dd>
          </div>
        </dl>
      </section>

      <section className="lesson-section">
        <h2>Algorithm</h2>
        <ol>
          {question.algorithm.map((step) => (
            <li key={step}>{step}</li>
          ))}
        </ol>
        <h3>Pseudocode</h3>
        <pre className="code-block">
          <code>{question.pseudocode}</code>
        </pre>
      </section>

      <section className="lesson-section">
        <h2>Implementation (Python 3)</h2>
        <pre className="code-block">
          <code>{question.implementation}</code>
        </pre>
      </section>

      <section className="lesson-section">
        <h2>Complexity</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Operation</th>
                <th>Time</th>
                <th>Space</th>
                <th>Notes</th>
              </tr>
            </thead>
            <tbody>
              {question.complexity.map((row) => (
                <tr key={row.operation}>
                  <td>{row.operation}</td>
                  <td>{row.time}</td>
                  <td>{row.space ?? "—"}</td>
                  <td>{row.notes ?? "—"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <p className="meta">Summary: {question.summaryComplexity}</p>
      </section>

      <section className="lesson-section">
        <h2>Dry run</h2>
        <p>{question.dryRun.setup}</p>
        <ol>
          {question.dryRun.steps.map((s) => (
            <li key={s}>{s}</li>
          ))}
        </ol>
        <p>
          <strong>Result:</strong> {question.dryRun.result}
        </p>
      </section>

      <section className="lesson-section">
        <h2>Edge cases</h2>
        <ul>
          {question.edgeCases.map((e) => (
            <li key={e}>{e}</li>
          ))}
        </ul>
      </section>

      <section className="lesson-section">
        <h2>Follow-ups</h2>
        <ul>
          {question.followUps.map((f) => (
            <li key={f.text}>
              <span className="badge">{f.kind === "reported" ? "Reported" : "Practice"}</span>{" "}
              {f.text}
              {f.guidance ? <p className="muted">{f.guidance}</p> : null}
            </li>
          ))}
        </ul>
      </section>

      <section className="lesson-section">
        <h2>Interview checklist</h2>
        <ul>
          {question.checklist.map((c) => (
            <li key={c}>{c}</li>
          ))}
        </ul>
      </section>

      {question.relatedQuestionIds && question.relatedQuestionIds.length > 0 ? (
        <section className="lesson-section">
          <h2>Related in this section</h2>
          <ul>
            {question.relatedQuestionIds.map((id) => {
              const related = findDesignCodingQuestionById(id);
              if (!related) return null;
              return (
                <li key={id}>
                  <Link to={designCodingQuestionPath(related.slug)}>{related.title}</Link>
                </li>
              );
            })}
          </ul>
        </section>
      ) : null}
    </article>
  );
}
