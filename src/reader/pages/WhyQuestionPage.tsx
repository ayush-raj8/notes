import { Link, useParams } from "react-router-dom";
import { findLessonById, findWhyBySlug, lessonPath } from "@/content/subjects/cn/load";
import { usePageTitle } from "../usePageTitle";
import { NotFoundPage } from "./NotFoundPage";

export function WhyQuestionPage() {
  const { questionSlug = "" } = useParams();
  const question = findWhyBySlug(questionSlug);
  usePageTitle(question ? `Question ${question.number}` : "Not found");
  if (!question) return <NotFoundPage />;

  const lesson = findLessonById(question.primaryLessonId);

  return (
    <article className="lesson-column">
      <p className="meta">
        <span>Question {question.number}</span>
        {question.set === "added" ? <span>Added beyond the core 50</span> : null}
      </p>
      <h1>{question.prompt}</h1>
      {question.status === "answered" ? (
        <p>{question.conciseAnswer}</p>
      ) : (
        <div className="empty">
          <strong>Answer not written yet.</strong>
          <p>It will be published with the lesson that owns this question.</p>
        </div>
      )}
      {lesson ? (
        <p>
          <Link to={lessonPath(lesson)}>Primary lesson: {lesson.title}</Link>
        </p>
      ) : null}
    </article>
  );
}
