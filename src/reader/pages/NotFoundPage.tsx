import { Link } from "react-router-dom";
import { usePageTitle } from "../usePageTitle";

export function NotFoundPage() {
  usePageTitle("Page not found");

  return (
    <article className="lesson-column">
      <h1>Page not found</h1>
      <p>That address is not a subject, lesson, or question in these notes.</p>
      <p className="actions">
        <Link className="button" to="/">
          Back to notes
        </Link>
        <Link className="button secondary" to="/cn">
          Computer Networks
        </Link>
      </p>
    </article>
  );
}
