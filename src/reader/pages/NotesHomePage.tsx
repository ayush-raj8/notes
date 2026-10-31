import { Link } from "react-router-dom";
import { subjects } from "@/content/subjects";
import { usePageTitle } from "../usePageTitle";

const javaBookHref = `${import.meta.env.BASE_URL}java/`;

export function NotesHomePage() {
  usePageTitle("Notes");

  return (
    <article className="page-intro">
      <h1>Notes</h1>
      <p className="lede">
        Courses kept as separate subjects. Computer Networks and DSA live side by side without
        mixing material. The Java book is a separate static mdBook next to this app.
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
      <h2>Books</h2>
      <ol className="module-index">
        <li className="card">
          <h3>
            <a href={javaBookHref}>Java OOP, SOLID, and LLD</a>
          </h3>
          <p>
            Exam-oriented mdBook (chapters 00–70) with compiling companion sources. Opens outside
            this React reader.
          </p>
        </li>
      </ol>
    </article>
  );
}
