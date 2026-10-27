import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
  designCodingCounts,
  designCodingQuestionPath,
  designCodingQuestions,
} from "@/content/subjects/dsa/load";
import {
  categoryLabels,
  classificationLabels,
  priorityLabels,
  verificationLabels,
  type DesignCodingCategory,
  type DesignCodingQuestion,
  type Priority,
  type ReportedLevel,
  type SourceClassification,
  type VerificationStatus,
} from "@/content/subjects/dsa/schema";
import { usePageTitle } from "../../usePageTitle";

type Filters = {
  level: ReportedLevel | "all";
  category: DesignCodingCategory | "all";
  priority: Priority | "all";
  classification: SourceClassification | "all";
  verification: VerificationStatus | "all";
  revisionOnly: boolean;
};

const initialFilters: Filters = {
  level: "all",
  category: "all",
  priority: "all",
  classification: "all",
  verification: "all",
  revisionOnly: false,
};

function matches(q: DesignCodingQuestion, f: Filters): boolean {
  if (f.level !== "all" && q.level !== f.level) return false;
  if (f.category !== "all" && !q.categories.includes(f.category)) return false;
  if (f.priority !== "all" && q.priority !== f.priority) return false;
  if (f.classification !== "all" && q.classification !== f.classification) return false;
  if (f.verification !== "all" && q.verification !== f.verification) return false;
  if (f.revisionOnly && q.priority === "optional" && q.classification === "related-practice") {
    return false;
  }
  return true;
}

export function DesignCodingIndexPage() {
  usePageTitle("PYQ — Design Coding");
  const [filters, setFilters] = useState<Filters>(initialFilters);
  const counts = designCodingCounts();

  const visible = useMemo(
    () => designCodingQuestions.filter((q) => matches(q, filters)),
    [filters],
  );

  const primary = visible.filter((q) => q.classification !== "related-practice");
  const practice = visible.filter((q) => q.classification === "related-practice");

  return (
    <article className="page-intro design-coding-index">
      <p className="crumb">
        <Link to="/dsa">DSA</Link> / PYQ — Design Coding
      </p>
      <h1>PYQ — Design Coding</h1>
      <p className="lede">
        Design-oriented Google coding PYQs and Hello Interview guide problems. Related LeetCode
        practice stays in its own list so ordinary problems are never labeled as interview reports.
      </p>

      <ul className="count-strip meta" aria-label="Question counts">
        <li>
          <strong>{counts.reportedVerified}</strong> verified reported
        </li>
        <li>
          <strong>{counts.partial}</strong> partially verified reported
        </li>
        <li>
          <strong>{counts.hello}</strong> Hello Interview guide
        </li>
        <li>
          <strong>{counts.unverified}</strong> unverified
        </li>
        <li>
          <strong>{counts.practice}</strong> related practice
        </li>
      </ul>

      <section className="filter-panel" aria-label="Filters">
        <h2>Filters</h2>
        <div className="filter-grid">
          <label>
            Level
            <select
              value={filters.level}
              onChange={(e) =>
                setFilters((f) => ({ ...f, level: e.target.value as Filters["level"] }))
              }
            >
              <option value="all">All</option>
              <option value="L3">L3</option>
              <option value="L4">L4</option>
              <option value="L5">L5</option>
              <option value="unspecified">Unspecified</option>
            </select>
          </label>
          <label>
            Category
            <select
              value={filters.category}
              onChange={(e) =>
                setFilters((f) => ({ ...f, category: e.target.value as Filters["category"] }))
              }
            >
              <option value="all">All</option>
              {(Object.keys(categoryLabels) as DesignCodingCategory[]).map((key) => (
                <option key={key} value={key}>
                  {categoryLabels[key]}
                </option>
              ))}
            </select>
          </label>
          <label>
            Priority
            <select
              value={filters.priority}
              onChange={(e) =>
                setFilters((f) => ({ ...f, priority: e.target.value as Filters["priority"] }))
              }
            >
              <option value="all">All</option>
              <option value="must-revise">Must Revise</option>
              <option value="important">Important</option>
              <option value="optional">Optional</option>
            </select>
          </label>
          <label>
            Source
            <select
              value={filters.classification}
              onChange={(e) =>
                setFilters((f) => ({
                  ...f,
                  classification: e.target.value as Filters["classification"],
                }))
              }
            >
              <option value="all">All</option>
              <option value="reported-google">Reported Google</option>
              <option value="hello-interview-guide">Hello Interview guide</option>
              <option value="related-practice">Related practice</option>
            </select>
          </label>
          <label>
            Verification
            <select
              value={filters.verification}
              onChange={(e) =>
                setFilters((f) => ({
                  ...f,
                  verification: e.target.value as Filters["verification"],
                }))
              }
            >
              <option value="all">All</option>
              <option value="verified">Verified</option>
              <option value="partially-verified">Partially verified</option>
              <option value="unverified">Unverified</option>
            </select>
          </label>
        </div>
        <label className="filter-check">
          <input
            type="checkbox"
            checked={filters.revisionOnly}
            onChange={(e) => setFilters((f) => ({ ...f, revisionOnly: e.target.checked }))}
          />
          Last-minute revision mode (hide optional related practice)
        </label>
        <button
          type="button"
          className="button secondary"
          onClick={() => setFilters(initialFilters)}
        >
          Reset filters
        </button>
      </section>

      {filters.revisionOnly ? (
        <section>
          <h2>Revision cards</h2>
          <ol className="revision-list">
            {visible.map((q) => (
              <li key={q.id} className="card revision-card">
                <h3>
                  <Link to={designCodingQuestionPath(q.slug)}>{q.title}</Link>
                </h3>
                <p className="meta">
                  <span className="badge">{q.level}</span>
                  <span className="badge">{classificationLabels[q.classification]}</span>
                  <span className="badge">{priorityLabels[q.priority]}</span>
                </p>
                <p>{q.coreApproach}</p>
                <p className="meta">{q.summaryComplexity}</p>
              </li>
            ))}
          </ol>
        </section>
      ) : (
        <>
          <section>
            <h2>Primary index</h2>
            <p className="muted">{primary.length} questions</p>
            <QuestionList questions={primary} />
          </section>
          <section>
            <h2>Related practice</h2>
            <p className="muted">
              Technique drills. Not labeled as Google PYQs unless a report independently verifies
              them.
            </p>
            <QuestionList questions={practice} />
          </section>
        </>
      )}
    </article>
  );
}

function QuestionList({ questions }: { questions: DesignCodingQuestion[] }) {
  if (questions.length === 0) {
    return <p className="empty">No questions match these filters.</p>;
  }
  return (
    <ol className="lesson-index">
      {questions.map((q) => (
        <li key={q.id} className="card">
          <h3>
            <Link to={designCodingQuestionPath(q.slug)}>{q.title}</Link>
          </h3>
          <p className="meta">
            <span className="badge">{q.level}</span>
            <span className="badge">{classificationLabels[q.classification]}</span>
            <span className="badge">{verificationLabels[q.verification]}</span>
            <span className="badge">{priorityLabels[q.priority]}</span>
          </p>
          <p>{q.coreApproach}</p>
          <p className="meta">{q.categories.map((c) => categoryLabels[c]).join(" · ")}</p>
        </li>
      ))}
    </ol>
  );
}
