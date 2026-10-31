package solutions.ch42;

import java.util.ArrayDeque;
import java.util.Deque;

/** Exercise 1: increment / decrement commands with undo on a counter receiver. */
public final class SolutionCounterCommands {
  interface Cmd {
    void execute();

    void undo();
  }

  static final class Counter {
    int value;
  }

  static Cmd add(Counter c, int delta) {
    return new Cmd() {
      public void execute() {
        c.value += delta;
      }

      public void undo() {
        c.value -= delta;
      }
    };
  }

  public static void main(String[] args) {
    Counter c = new Counter();
    Deque<Cmd> done = new ArrayDeque<>();
    for (int d : new int[] {5, -2, 10}) {
      Cmd cmd = add(c, d);
      cmd.execute();
      done.push(cmd);
    }
    System.out.println(c.value);
    done.pop().undo();
    done.pop().undo();
    System.out.println(c.value);
  }
}
