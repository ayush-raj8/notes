import { Link } from "react-router-dom";
import {
  cnLearnPath,
  curriculum,
  findLessonById,
  lessonPath,
  moduleContentStatus,
  modulePath,
} from "@/content/subjects/cn/load";
import { continueLessonId, summarizeProgress } from "@/progress/model";
import { useProgress } from "@/app/progress/ProgressContext";
import { usePageTitle } from "../usePageTitle";
import { moduleStatusLabel } from "../labels";

export function HomePage() {
  usePageTitle("Computer Networks");
  const { state } = useProgress();
  const counts = summarizeProgress(state, curriculum.lessons);
  const nextId = continueLessonId(state, curriculum.lessons);
  const next = nextId ? findLessonById(nextId) : undefined;
  const inProgress = curriculum.lessons.some(
    (lesson) => state.lessons[lesson.id]?.status === "in_progress",
  );
  const continueText = inProgress || counts.written > 0 ? "Continue" : "Start at the first lesson";

  return (
    <article className="page-intro">
      <h1>Computer Networks</h1>
      <p className="lede">
        A course for software engineers. Each lesson starts from the problem a mechanism solves. The
        syllabus is mapped. A lesson appears as ready only when it is actually written.
      </p>
      <p className="meta">
        <span>
          {counts.written} of {counts.total} lessons written
        </span>
        <span>{counts.finished} finished</span>
      </p>
      {next ? (
        <p className="actions">
          <Link className="button" to={lessonPath(next)}>
            {continueText}
          </Link>
          <Link className="button secondary" to={cnLearnPath}>
            View the curriculum
          </Link>
        </p>
      ) : null}
      <h2>Modules</h2>
      <ol className="module-index">
        {curriculum.modules.map((module) => (
          <li key={module.id} className="card">
            <h3>
              <Link to={modulePath(module.slug)}>
                {module.number}. {module.title}
              </Link>
            </h3>
            <p>{module.summary}</p>
            <p className="meta">
              <span>{moduleStatusLabel(moduleContentStatus(module.lessons))}</span>
              <span>{module.lessons.length} lessons</span>
            </p>
          </li>
        ))}
      </ol>
    </article>
  );
}
