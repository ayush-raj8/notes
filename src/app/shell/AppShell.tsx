import { Outlet } from "react-router-dom";
import { findModuleBySlug } from "@/content/subjects/cn/load";
import { AppHeader } from "./AppHeader";
import { useLearnPath } from "./learnPath";
import { ModuleRail } from "./ModuleRail";

export function AppShell() {
  const { moduleSlug } = useLearnPath();
  const inModule = Boolean(moduleSlug && findModuleBySlug(moduleSlug));

  return (
    <div className={inModule ? "app-frame has-rail" : "app-frame"}>
      <a className="skip" href="#main">
        Skip to content
      </a>
      <AppHeader />
      {inModule ? <ModuleRail /> : null}
      <main id="main">
        <Outlet />
      </main>
      <footer className="site-footer">
        Each subject keeps its own lessons. A lesson is marked ready only after it passes that
        subject's checks.
      </footer>
    </div>
  );
}
