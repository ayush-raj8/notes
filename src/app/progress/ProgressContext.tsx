import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from "react";
import {
  markCheck,
  markLessonLearned,
  markLessonOpened,
  markWhy,
  type Confidence,
  type ContentLessonRef,
  type ProgressState,
} from "@/progress/model";
import { browserProgressStorage, type ProgressStorage } from "@/progress/storage";

type ProgressApi = {
  state: ProgressState;
  replace: (state: ProgressState) => void;
  markLessonOpened: (lesson: ContentLessonRef) => void;
  markLessonLearned: (lesson: ContentLessonRef) => void;
  markCheck: (lesson: ContentLessonRef, checkId: string, confidence: Confidence) => void;
  markWhy: (whyId: string, confidence: Confidence) => void;
};

const ProgressContext = createContext<ProgressApi | undefined>(undefined);

export function ProgressProvider({
  children,
  storage,
}: {
  children: ReactNode;
  storage?: ProgressStorage;
}) {
  const backend = useMemo(() => storage ?? browserProgressStorage(), [storage]);
  const [state, setState] = useState<ProgressState>(() => backend.load());
  const skipFirstSave = useRef(true);

  useEffect(() => {
    if (skipFirstSave.current) {
      skipFirstSave.current = false;
      return;
    }
    backend.save(state);
  }, [backend, state]);

  const api = useMemo<ProgressApi>(() => {
    const now = () => new Date().toISOString();
    return {
      state,
      replace: (next) => setState(next),
      markLessonOpened: (lesson) => setState((current) => markLessonOpened(current, lesson, now())),
      markLessonLearned: (lesson) =>
        setState((current) => markLessonLearned(current, lesson, now())),
      markCheck: (lesson, checkId, confidence) =>
        setState((current) => markCheck(current, lesson, checkId, confidence, now())),
      markWhy: (whyId, confidence) =>
        setState((current) => markWhy(current, whyId, confidence, now())),
    };
  }, [state]);

  return <ProgressContext.Provider value={api}>{children}</ProgressContext.Provider>;
}

export function useProgress(): ProgressApi {
  const value = useContext(ProgressContext);
  if (!value) throw new Error("useProgress must be used within ProgressProvider");
  return value;
}
