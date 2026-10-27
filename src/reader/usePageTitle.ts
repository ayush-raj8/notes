import { useEffect } from "react";
import { useLocation } from "react-router-dom";

export function usePageTitle(title: string) {
  const { pathname } = useLocation();
  const inSubject = pathname === "/cn" || pathname.startsWith("/cn/");

  useEffect(() => {
    const brand = inSubject ? "Computer Networks" : "Notes";
    document.title = title === brand ? brand : `${title} · ${brand}`;
  }, [inSubject, title]);
}
