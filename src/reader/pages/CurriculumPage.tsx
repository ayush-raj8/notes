import { Link } from "react-router-dom";
import {
  curriculum,
  lessonPath,
  moduleContentStatus,
  modulePath,
} from "@/content/subjects/cn/load";
import { usePageTitle } from "../usePageTitle";
import { moduleStatusLabel } from "../labels";
import { ContentBadge } from "../status";

export function CurriculumPage() {
  usePageTitle("Curriculum");

  return (
    <article>
      <div className="page-intro">
        <h1>Curriculum</h1>
        <p className="lede">
          Thirteen modules, in teaching order. Topics stay attached to one lesson so later edits do
          not fork the explanation.
        </p>
      </div>
      {curriculum.modules.map((module) => (
        <section key={module.id} aria-labelledby={module.id}>
          <h2 id={module.id}>
            <Link to={modulePath(module.slug)}>
              {module.number}. {module.title}
            </Link>
          </h2>
          <p className="meta">
            <span>{moduleStatusLabel(moduleContentStatus(module.lessons))}</span>
          </p>
          <ol className="plain-list">
            {module.lessons.map((lesson) => (
              <li key={lesson.id}>
                <Link to={lessonPath(lesson)}>{lesson.title}</Link>{" "}
                <ContentBadge status={lesson.status} />
              </li>
            ))}
          </ol>
        </section>
      ))}
    </article>
  );
}
