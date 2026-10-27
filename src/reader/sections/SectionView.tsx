import { useState } from "react";
import { Link } from "react-router-dom";
import type { Lesson, Section } from "@/content/schema";
import { whyQuestionPath } from "@/content/subjects/cn/load";
import { whyQuestions } from "@/content/subjects/cn/why/questions";
import {
  implementedVisualIds,
  renderVisual,
  visualDefinition,
  visualTitle,
} from "@/visuals/registry";
import { useProgress } from "@/app/progress/ProgressContext";
import { ConfidenceButtons } from "../ConfidenceButtons";

function Paragraphs({ paragraphs }: { paragraphs: string[] }) {
  return (
    <div className="stack">
      {paragraphs.map((paragraph) => (
        <p key={paragraph.slice(0, 48)}>{paragraph}</p>
      ))}
    </div>
  );
}

function PrincipleView({ section }: { section: Extract<Section, { type: "principle" }> }) {
  const fields = [
    { label: "The problem", body: section.problem },
    { label: "Why it matters", body: section.whyItMatters },
    { label: "The idea", body: section.idea },
    { label: "How it works", body: section.howItWorks },
    { label: "Why it works", body: section.whyItWorks },
    { label: "Limits", body: section.limits },
    { label: "Connections", body: section.connections },
  ];
  return (
    <section className="lesson-section principle" aria-labelledby={`${section.id}-heading`}>
      <h2 id={`${section.id}-heading`}>First principles</h2>
      <dl className="principle-grid">
        {fields.map((field) => (
          <div key={field.label} className="principle-item">
            <dt>{field.label}</dt>
            <dd>{field.body}</dd>
          </div>
        ))}
      </dl>
    </section>
  );
}

function ChecksView({
  section,
  lesson,
}: {
  section: Extract<Section, { type: "checks" }>;
  lesson: Lesson;
}) {
  const { state, markCheck } = useProgress();
  const [open, setOpen] = useState<Record<string, boolean>>({});
  const lessonProgress = state.lessons[lesson.id];

  return (
    <section className="lesson-section" aria-labelledby={`${section.id}-heading`}>
      <h2 id={`${section.id}-heading`}>Checks</h2>
      <ol className="check-list">
        {section.items.map((item) => {
          const revealed = open[item.id] ?? false;
          return (
            <li key={item.id} className="check-item">
              <p className="check-prompt">{item.prompt}</p>
              {revealed ? (
                <p className="check-answer">{item.answer}</p>
              ) : (
                <button
                  type="button"
                  className="button secondary"
                  onClick={() => setOpen((current) => ({ ...current, [item.id]: true }))}
                >
                  Reveal model answer
                </button>
              )}
              {revealed ? (
                <ConfidenceButtons
                  legend="How sure were you?"
                  value={lessonProgress?.checks[item.id]}
                  onChange={(confidence) => markCheck(lesson, item.id, confidence)}
                />
              ) : null}
            </li>
          );
        })}
      </ol>
    </section>
  );
}

function WhyView({ section }: { section: Extract<Section, { type: "why" }> }) {
  const { state, markWhy } = useProgress();
  const question = whyQuestions.find((item) => item.id === section.questionId);
  if (!question) return null;
  const answered = question.status === "answered";

  return (
    <section className="lesson-section why-embed" aria-labelledby={`${section.id}-heading`}>
      <h2 id={`${section.id}-heading`}>Why question</h2>
      <p className="why-prompt">
        <Link to={whyQuestionPath(question.slug)}>
          {question.number}. {question.prompt}
        </Link>
      </p>
      {answered ? (
        <>
          <p>{question.conciseAnswer}</p>
          <ConfidenceButtons
            legend="Mark this answer for review"
            value={state.why[question.id]?.confidence}
            onChange={(confidence) => markWhy(question.id, confidence)}
          />
        </>
      ) : (
        <p className="muted">The concise answer will appear when this lesson is finished.</p>
      )}
    </section>
  );
}

function VizView({ section }: { section: Extract<Section, { type: "viz" }> }) {
  const definition = visualDefinition(section.vizId);
  const view = implementedVisualIds.has(section.vizId)
    ? renderVisual(section.vizId, {
        scenario: section.scenario,
        learningObjective: section.learningObjective,
        caption: section.caption,
      })
    : null;

  return (
    <section className="lesson-section viz" aria-labelledby={`${section.id}-heading`}>
      <h2 id={`${section.id}-heading`}>{visualTitle(section.vizId)}</h2>
      {view ? (
        view
      ) : (
        <>
          <p className="meta">
            <span>Objective: {section.learningObjective}</span>
          </p>
          <div className="empty">
            <strong>Interactive diagram not built yet.</strong>
            <p>{section.caption}</p>
            {definition ? <p>{definition.learningObjective}</p> : null}
          </div>
        </>
      )}
    </section>
  );
}

function InvestigationView({ section }: { section: Extract<Section, { type: "investigation" }> }) {
  return (
    <section className="lesson-section" aria-labelledby={`${section.id}-heading`}>
      <h2 id={`${section.id}-heading`}>Investigation</h2>
      <h3>Likely causes</h3>
      <ul className="plain-list">
        {section.causes.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
      <h3>Order to check</h3>
      <ol className="plain-list">
        {section.order.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ol>
      <h3>Commands</h3>
      <ul className="plain-list">
        {section.commands.map((item) => (
          <li key={item.command}>
            <code>{item.command}</code> — {item.shows}
          </li>
        ))}
      </ul>
      <h3>What the output implies</h3>
      <ul className="plain-list">
        {section.implications.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
      <h3>Separating hypotheses</h3>
      <ul className="plain-list">
        {section.distinguishing.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
      <h3>Common mistakes</h3>
      <ul className="plain-list">
        {section.mistakes.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </section>
  );
}

export function SectionView({ section, lesson }: { section: Section; lesson: Lesson }) {
  switch (section.type) {
    case "principle":
      return <PrincipleView section={section} />;
    case "prose":
      return (
        <section
          className="lesson-section"
          {...(section.title ? { "aria-labelledby": `${section.id}-heading` } : {})}
        >
          {section.title ? <h2 id={`${section.id}-heading`}>{section.title}</h2> : null}
          <Paragraphs paragraphs={section.paragraphs} />
        </section>
      );
    case "steps":
      return (
        <section className="lesson-section" aria-labelledby={`${section.id}-heading`}>
          <h2 id={`${section.id}-heading`}>{section.title}</h2>
          <ol className="step-list">
            {section.steps.map((step) => (
              <li key={step.title}>
                <h3>{step.title}</h3>
                <p>{step.body}</p>
              </li>
            ))}
          </ol>
        </section>
      );
    case "comparison":
      return (
        <section className="lesson-section" aria-labelledby={`${section.id}-heading`}>
          <h2 id={`${section.id}-heading`}>{section.title}</h2>
          {section.caption ? <p className="muted">{section.caption}</p> : null}
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th scope="col">Topic</th>
                  {section.columns.map((column) => (
                    <th key={column} scope="col">
                      {column}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {section.rows.map((row) => (
                  <tr key={row.label}>
                    <th scope="row">{row.label}</th>
                    {row.cells.map((cell, index) => (
                      <td key={`${row.label}-${index}`}>{cell}</td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      );
    case "example":
      return (
        <section className="lesson-section" aria-labelledby={`${section.id}-heading`}>
          <h2 id={`${section.id}-heading`}>{section.title}</h2>
          <p>
            <strong>Setup.</strong> {section.setup}
          </p>
          <ol className="plain-list">
            {section.walkthrough.map((step) => (
              <li key={step}>{step}</li>
            ))}
          </ol>
          <p>
            <strong>Result.</strong> {section.result}
          </p>
        </section>
      );
    case "callout":
      return (
        <aside
          className={section.tone === "caution" ? "callout callout-caution" : "callout"}
          aria-labelledby={`${section.id}-heading`}
        >
          <h2 id={`${section.id}-heading`}>{section.title}</h2>
          <p>{section.body}</p>
          {section.scope ? <p className="meta">Scope: {section.scope}</p> : null}
        </aside>
      );
    case "expandable":
      return (
        <details className="lesson-expandable">
          <summary>{section.title}</summary>
          <Paragraphs paragraphs={section.paragraphs} />
        </details>
      );
    case "viz":
      return <VizView section={section} />;
    case "why":
      return <WhyView section={section} />;
    case "checks":
      return <ChecksView section={section} lesson={lesson} />;
    case "investigation":
      return <InvestigationView section={section} />;
    default: {
      const _exhaustive: never = section;
      return _exhaustive;
    }
  }
}
