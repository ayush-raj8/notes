import { Link } from "react-router-dom";
import { curriculum, whyQuestionPath } from "@/content/subjects/cn/load";
import { whyGroups } from "@/content/subjects/cn/why/questions";
import { usePageTitle } from "../usePageTitle";

export function WhyIndexPage() {
  usePageTitle("Why Does Networking Work This Way?");

  return (
    <article>
      <div className="page-intro">
        <h1>Why Does Networking Work This Way?</h1>
        <p className="lede">
          Questions that test the architecture. The concise answer is published with the lesson that
          owns the question. Until then, the prompt is here and the answer is not.
        </p>
      </div>
      {whyGroups.map((group) => (
        <section key={group.id} aria-labelledby={group.id}>
          <h2 id={group.id}>{group.title}</h2>
          <ol className="plain-list">
            {curriculum.whyQuestions
              .filter((question) => question.group === group.id)
              .map((question) => (
                <li key={question.id}>
                  <Link to={whyQuestionPath(question.slug)}>
                    {question.number}. {question.prompt}
                  </Link>{" "}
                  <span className="badge">
                    {question.status === "answered" ? "Answered" : "Answer pending"}
                  </span>
                </li>
              ))}
          </ol>
        </section>
      ))}
    </article>
  );
}
