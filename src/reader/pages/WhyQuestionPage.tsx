import { Link, useParams } from "react-router-dom";
import { findLessonById, findWhyBySlug, lessonPath } from "@/content/subjects/cn/load";
import { useProgress } from "@/app/progress/ProgressContext";
import { ConfidenceButtons } from "../ConfidenceButtons";
import { usePageTitle } from "../usePageTitle";
import { NotFoundPage } from "./NotFoundPage";

export function WhyQuestionPage() {
  const { questionSlug = "" } = useParams();
  const question = findWhyBySlug(questionSlug);
  const { state, markWhy } = useProgress();
  usePageTitle(question ? `Question ${question.number}` : "Not found");
  if (!question) return <NotFoundPage />;

  const lesson = findLessonById(question.primaryLessonId);
  const answered = question.status === "answered";

  return (
    <article className="lesson-column">
      <p className="meta">
        <span>Question {question.number}</span>
        {question.set === "added" ? <span>Added beyond the core 50</span> : null}
      </p>
      <h1>{question.prompt}</h1>
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
