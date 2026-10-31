import { Link, useParams } from "react-router-dom";
import { findModuleBySlug, lessonPath } from "@/content/subjects/cn/load";
import { chapterSummaryByModuleId } from "@/content/subjects/cn/chapter-summaries";
import { ChapterSummaryView } from "../sections/ChapterSummaryView";
import { usePageTitle } from "../usePageTitle";
import { ContentBadge } from "../status";
import { NotFoundPage } from "./NotFoundPage";

export function ModulePage() {
  const { moduleSlug = "" } = useParams();
  const module = findModuleBySlug(moduleSlug);
  usePageTitle(module?.title ?? "Not found");
  if (!module) return <NotFoundPage />;

  const chapterSummary = chapterSummaryByModuleId.get(module.id);

  return (
    <article className="lesson-column">
      <p className="meta">Module {module.number}</p>
      <h1>{module.title}</h1>
      <p className="lede">{module.summary}</p>
      <h2>Aims</h2>
      <ul className="plain-list">
        {module.aims.map((aim) => (
          <li key={aim}>{aim}</li>
        ))}
      </ul>
      {chapterSummary ? <ChapterSummaryView summary={chapterSummary} /> : null}
      <h2>Lessons</h2>
      <ol className="lesson-index">
        {module.lessons.map((lesson) => (
          <li key={lesson.id} className="card">
            <h3>
              <Link to={lessonPath(lesson)}>{lesson.title}</Link>
            </h3>
            <p>{lesson.summary}</p>
            <p className="meta">
              <ContentBadge status={lesson.status} />
            </p>
          </li>
        ))}
      </ol>
    </article>
  );
}
