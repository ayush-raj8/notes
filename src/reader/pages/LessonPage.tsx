import { useEffect } from "react";
import { Link, useParams } from "react-router-dom";
import {
  findLesson,
  findLessonById,
  lessonNeighbors,
  lessonPath,
} from "@/content/subjects/cn/load";
import { sourceById } from "@/content/subjects/cn/sources";
import { useProgress } from "@/app/progress/ProgressContext";
import { usePageTitle } from "../usePageTitle";
import { ContentBadge } from "../status";
import { SectionView } from "../sections/SectionView";
import { NotFoundPage } from "./NotFoundPage";

export function LessonPage() {
  const { moduleSlug = "", lessonSlug = "" } = useParams();
  const lesson = findLesson(moduleSlug, lessonSlug);
  const { state, markLessonOpened, markLessonLearned } = useProgress();
  usePageTitle(lesson?.title ?? "Not found");

  useEffect(() => {
    if (!lesson) return;
    markLessonOpened(lesson);
  }, [lesson, markLessonOpened]);

  if (!lesson) return <NotFoundPage />;

  const { previous, next } = lessonNeighbors(lesson.id);
  const written = lesson.sections.length > 0;
  const learnerComplete = state.lessons[lesson.id]?.status === "complete";
  const canFinish = lesson.status === "complete" && !learnerComplete;

  return (
    <article className="lesson-column">
      <p className="meta">
        <ContentBadge status={lesson.status} />
        <span>{lesson.id}</span>
        {learnerComplete ? <span>Finished</span> : null}
      </p>
      <h1>{lesson.title}</h1>
      {!written ? (
        <div className="empty">
          <strong>This lesson is not written yet.</strong>
          <p>The scope below is the syllabus entry, not the explanation.</p>
        </div>
      ) : null}
      <p>{lesson.summary}</p>
      {lesson.objectives.length > 0 ? (
        <>
          <h2>Objectives</h2>
          <ul className="plain-list">
            {lesson.objectives.map((objective) => (
              <li key={objective}>{objective}</li>
            ))}
          </ul>
        </>
      ) : null}
      {lesson.prerequisites.length > 0 ? (
        <>
          <h2>Prerequisites</h2>
          <ul className="plain-list">
            {lesson.prerequisites.map((id) => {
              const prerequisite = findLessonById(id);
              return (
                <li key={id}>
                  {prerequisite ? (
                    <Link to={lessonPath(prerequisite)}>{prerequisite.title}</Link>
                  ) : (
                    id
                  )}
                </li>
              );
            })}
          </ul>
        </>
      ) : null}
      {written
        ? lesson.sections.map((section) => (
            <SectionView key={section.id} section={section} lesson={lesson} />
          ))
        : null}
      {!written && lesson.ownsTopics.length > 0 ? (
        <>
          <h2>Syllabus topics</h2>
          <ul className="topic-list">
            {lesson.ownsTopics.map((topic) => (
              <li key={topic.id}>{topic.bullet}</li>
            ))}
          </ul>
        </>
      ) : null}
      {!written && lesson.ownsTopics.length === 0 ? (
        <p>
          This page frames the scenarios in the module. It does not add a syllabus topic of its own.
        </p>
      ) : null}
      {lesson.references.length > 0 ? (
        <section className="lesson-section" aria-labelledby="sources-heading">
          <h2 id="sources-heading">Sources</h2>
          <ul className="plain-list">
            {lesson.references.map((reference) => {
              const source = sourceById.get(reference.sourceId);
              const label = source?.title ?? reference.sourceId;
              return (
                <li key={`${reference.sourceId}-${reference.note ?? ""}`}>
                  {source?.url ? (
                    <a href={source.url} target="_blank" rel="noreferrer">
                      {label}
                    </a>
                  ) : (
                    label
                  )}
                  {reference.note ? ` — ${reference.note}` : null}
                </li>
              );
            })}
          </ul>
        </section>
      ) : null}
      {canFinish ? (
        <p className="actions">
          <button type="button" className="button" onClick={() => markLessonLearned(lesson)}>
            Mark lesson finished
          </button>
        </p>
      ) : null}
      <nav className="pager" aria-label="Lesson">
        {previous ? <Link to={lessonPath(previous)}>Previous: {previous.title}</Link> : <span />}
        {next ? <Link to={lessonPath(next)}>Next: {next.title}</Link> : <span />}
      </nav>
    </article>
  );
}
