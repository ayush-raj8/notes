import { useState } from "react";
import { NavLink, useLocation } from "react-router-dom";
import { cnLearnPath, cnReviewPath, cnWhyPath } from "@/content/subjects/cn/load";

const subjectLinks = [
  { to: cnLearnPath, label: "Curriculum" },
  { to: cnWhyPath, label: "Why" },
  { to: cnReviewPath, label: "Review" },
];

export function AppHeader() {
  const location = useLocation();
  const inSubject = location.pathname === "/cn" || location.pathname.startsWith("/cn/");
  const [openPath, setOpenPath] = useState<string | null>(null);
  const open = openPath === location.pathname;

  return (
    <header className="site-header">
      <NavLink to="/" end className="brand">
        Notes
      </NavLink>
      {inSubject ? (
        <NavLink to="/cn" end className="brand-subject">
          Computer Networks
        </NavLink>
      ) : null}
      {inSubject ? (
        <button
          type="button"
          className="nav-toggle"
          aria-expanded={open}
          aria-controls="site-nav"
          onClick={() => setOpenPath(open ? null : location.pathname)}
        >
          Menu
        </button>
      ) : null}
      {inSubject ? (
        <nav id="site-nav" className={open ? "site-nav is-open" : "site-nav"} aria-label="Primary">
          {subjectLinks.map((link) => (
            <NavLink key={link.to} to={link.to}>
              {link.label}
            </NavLink>
          ))}
        </nav>
      ) : null}
    </header>
  );
}
