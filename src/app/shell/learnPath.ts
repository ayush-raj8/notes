import { useLocation } from "react-router-dom";

export function useLearnPath(): { moduleSlug?: string; lessonSlug?: string; inSubject: boolean } {
  const { pathname } = useLocation();
  const [subject, section, moduleSlug, lessonSlug] = pathname.split("/").filter(Boolean);
  const inSubject = subject === "cn";
  if (!inSubject || section !== "learn" || !moduleSlug) return { inSubject };
  return { inSubject, moduleSlug, lessonSlug };
}
