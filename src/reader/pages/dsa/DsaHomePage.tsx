import { Link } from "react-router-dom";
import { dsaSections } from "@/content/subjects/dsa/load";
import { usePageTitle } from "../../usePageTitle";

export function DsaHomePage() {
  usePageTitle("DSA");

  return (
    <article className="page-intro">
      <h1>DSA</h1>
      <p className="lede">
        Interview coding notes kept separate from Computer Networks. New sections can be added here
        without touching other subjects.
      </p>
      <h2>Sections</h2>
      <ol className="module-index">
        {dsaSections.map((section) => (
          <li key={section.id} className="card">
            <h3>
              <Link to={section.path}>{section.title}</Link>
            </h3>
            <p>{section.summary}</p>
          </li>
        ))}
      </ol>
    </article>
  );
}
