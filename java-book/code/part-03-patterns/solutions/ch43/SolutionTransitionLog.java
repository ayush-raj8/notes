package solutions.ch43;

import java.util.ArrayList;
import java.util.List;

/** Exercise 4: record every transition so the history can be printed. */
public final class SolutionTransitionLog {
  enum S {
    IDLE,
    RUNNING,
    DONE
  }

  private S state = S.IDLE;
  private final List<String> log = new ArrayList<>();

  void go(S next) {
    log.add(state + "->" + next);
    state = next;
  }

  public static void main(String[] args) {
    SolutionTransitionLog m = new SolutionTransitionLog();
    m.go(S.RUNNING);
    m.go(S.DONE);
    System.out.println(m.log);
  }
}
