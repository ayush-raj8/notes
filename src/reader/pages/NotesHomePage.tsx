import { Link } from "react-router-dom";
import { subjects } from "@/content/subjects";
import { usePageTitle } from "../usePageTitle";

export function NotesHomePage() {
  usePageTitle("Notes");

  return (
    <article className="page-intro">
      <h1>Notes</h1>
      <p className="lede">
        Courses kept as separate subjects. Computer Networks and DSA live side by side without
        mixing material.
      </p>
      <h2>Subjects</h2>
      <ol className="module-index">
        {subjects.map((subject) => (
          <li key={subject.id} className="card">
            <h3>
              <Link to={`/${subject.slug}`}>{subject.title}</Link>
            </h3>
            <p>{subject.summary}</p>
          </li>
        ))}
      </ol>
    </article>
  );
}
