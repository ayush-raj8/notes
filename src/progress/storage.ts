import { emptyProgress, parseProgress, type ProgressState } from "./model";

export type ProgressStorage = {
  load: () => ProgressState;
  save: (state: ProgressState) => void;
};

export const PROGRESS_STORAGE_KEY = "notes.cn.progress.v1";
const LEGACY_PROGRESS_STORAGE_KEY = "cn-theory.progress.v1";

export function createMemoryStorage(initial: ProgressState = emptyProgress()): ProgressStorage {
  let state = initial;
  return {
    load: () => state,
    save: (next) => {
      state = next;
    },
  };
}

export function createLocalStorage(storage: Pick<Storage, "getItem" | "setItem">): ProgressStorage {
  return {
    load() {
      const raw =
        storage.getItem(PROGRESS_STORAGE_KEY) ?? storage.getItem(LEGACY_PROGRESS_STORAGE_KEY);
      if (!raw) return emptyProgress();
      const parsed = parseProgress(raw);
      return parsed.ok ? parsed.state : emptyProgress();
    },
    save(state) {
      storage.setItem(PROGRESS_STORAGE_KEY, JSON.stringify(state));
    },
  };
}

export function browserProgressStorage(): ProgressStorage {
  return createLocalStorage(localStorage);
}
