import { Link, useParams } from "react-router-dom";
import { findLesson, lessonNeighbors, lessonPath } from "@/content/subjects/cn/load";
import { usePageTitle } from "../usePageTitle";
import { ContentBadge } from "../status";
import { NotFoundPage } from "./NotFoundPage";

export function LessonPage() {
  const { moduleSlug = "", lessonSlug = "" } = useParams();
  const lesson = findLesson(moduleSlug, lessonSlug);
  usePageTitle(lesson?.title ?? "Not found");
  if (!lesson) return <NotFoundPage />;

  const { previous, next } = lessonNeighbors(lesson.id);

  return (
    <article className="lesson-column">
      <p className="meta">
        <ContentBadge status={lesson.status} />
        <span>{lesson.id}</span>
      </p>
      <h1>{lesson.title}</h1>
      {lesson.sections.length === 0 ? (
        <div className="empty">
          <strong>This lesson is not written yet.</strong>
          <p>The scope below is the syllabus entry, not the explanation.</p>
        </div>
      ) : null}
      <h2>Scope</h2>
      <p>{lesson.summary}</p>
      {lesson.ownsTopics.length > 0 ? (
        <>
          <h2>Syllabus topics</h2>
          <ul className="topic-list">
            {lesson.ownsTopics.map((topic) => (
              <li key={topic.id}>{topic.bullet}</li>
            ))}
          </ul>
        </>
      ) : (
        <p>
          This page frames the scenarios in the module. It does not add a syllabus topic of its own.
        </p>
      )}
      <nav className="pager" aria-label="Lesson">
        {previous ? <Link to={lessonPath(previous)}>Previous: {previous.title}</Link> : <span />}
        {next ? <Link to={lessonPath(next)}>Next: {next.title}</Link> : <span />}
      </nav>
    </article>
  );
}
