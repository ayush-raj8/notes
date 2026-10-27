import { Outlet, Route, Routes } from "react-router-dom";
import { AppShell } from "./shell/AppShell";
import { CurriculumPage } from "@/reader/pages/CurriculumPage";
import { HomePage } from "@/reader/pages/HomePage";
import { LessonPage } from "@/reader/pages/LessonPage";
import { ModulePage } from "@/reader/pages/ModulePage";
import { NotesHomePage } from "@/reader/pages/NotesHomePage";
import { NotFoundPage } from "@/reader/pages/NotFoundPage";
import { ReviewPage } from "@/reader/pages/ReviewPage";
import { WhyIndexPage } from "@/reader/pages/WhyIndexPage";
import { WhyQuestionPage } from "@/reader/pages/WhyQuestionPage";

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<AppShell />}>
        <Route index element={<NotesHomePage />} />
        <Route path="cn" element={<Outlet />}>
          <Route index element={<HomePage />} />
          <Route path="learn" element={<CurriculumPage />} />
          <Route path="learn/:moduleSlug" element={<ModulePage />} />
          <Route path="learn/:moduleSlug/:lessonSlug" element={<LessonPage />} />
          <Route path="why" element={<WhyIndexPage />} />
          <Route path="why/:questionSlug" element={<WhyQuestionPage />} />
          <Route path="review" element={<ReviewPage />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
