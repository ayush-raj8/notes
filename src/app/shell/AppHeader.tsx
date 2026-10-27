import { useState } from "react";
import { NavLink, useLocation } from "react-router-dom";
import { cnLearnPath, cnReviewPath, cnWhyPath } from "@/content/subjects/cn/load";
import { dsaDesignCodingPath, dsaHomePath } from "@/content/subjects/dsa/load";
import { findSubject } from "@/content/subjects";

type NavLinkItem = { to: string; label: string; end?: boolean };

const subjectNav: Record<string, { title: string; links: NavLinkItem[] }> = {
  cn: {
    title: "Computer Networks",
    links: [
      { to: cnLearnPath, label: "Curriculum" },
      { to: cnWhyPath, label: "Why" },
      { to: cnReviewPath, label: "Review" },
    ],
  },
  dsa: {
    title: "DSA",
    links: [
      { to: dsaHomePath, label: "Home", end: true },
      { to: dsaDesignCodingPath, label: "Design Coding" },
    ],
  },
};

export function AppHeader() {
  const location = useLocation();
  const subjectSlug = location.pathname.split("/").filter(Boolean)[0];
  const subject = subjectSlug ? findSubject(subjectSlug) : undefined;
  const nav = subjectSlug ? subjectNav[subjectSlug] : undefined;
  const inSubject = Boolean(subject && nav);
  const [openPath, setOpenPath] = useState<string | null>(null);
  const open = openPath === location.pathname;

  return (
    <header className="site-header">
      <NavLink to="/" end className="brand">
        Notes
      </NavLink>
      {inSubject && nav ? (
        <NavLink to={`/${subjectSlug}`} end className="brand-subject">
          {nav.title}
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
      {inSubject && nav ? (
        <nav id="site-nav" className={open ? "site-nav is-open" : "site-nav"} aria-label="Primary">
          {nav.links.map((link) => (
            <NavLink key={link.to} to={link.to} end={link.end}>
              {link.label}
            </NavLink>
          ))}
        </nav>
      ) : null}
    </header>
  );
}
