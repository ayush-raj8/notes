package solutions.ch42;

import java.util.ArrayDeque;
import java.util.Deque;

/** Exercise 3: history that remembers only the last N commands. */
public final class SolutionBoundedHistory {
  private final Deque<Runnable> undoActions = new ArrayDeque<>();
  private final int limit;

  SolutionBoundedHistory(int limit) {
    this.limit = limit;
  }

  void record(Runnable undoAction) {
    undoActions.push(undoAction);
    if (undoActions.size() > limit) undoActions.removeLast();
  }

  boolean undo() {
    Runnable r = undoActions.poll();
    if (r == null) return false;
    r.run();
    return true;
  }

  public static void main(String[] args) {
    SolutionBoundedHistory h = new SolutionBoundedHistory(2);
    for (int i = 1; i <= 4; i++) {
      int n = i;
      h.record(() -> System.out.println("undo " + n));
    }
    System.out.println(h.undo());
    System.out.println(h.undo());
    System.out.println(h.undo());
  }
}
