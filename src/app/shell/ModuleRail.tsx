import { NavLink } from "react-router-dom";
import { findModuleBySlug, lessonPath, modulePath } from "@/content/subjects/cn/load";
import { useLearnPath } from "./learnPath";

export function ModuleRail() {
  const { moduleSlug } = useLearnPath();
  const module = moduleSlug ? findModuleBySlug(moduleSlug) : undefined;
  if (!module) return null;

  return (
    <nav className="module-rail" aria-label={`${module.title} lessons`}>
      <details open>
        <summary>Lessons in this module</summary>
        <NavLink to={modulePath(module.slug)} end className="rail-module">
          {module.number}. {module.title}
        </NavLink>
        <ol>
          {module.lessons.map((lesson) => (
            <li key={lesson.id}>
              <NavLink to={lessonPath(lesson)}>{lesson.title}</NavLink>
            </li>
          ))}
        </ol>
      </details>
    </nav>
  );
}
