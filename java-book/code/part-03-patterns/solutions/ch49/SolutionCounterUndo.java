package solutions.ch49;

import java.util.ArrayDeque;
import java.util.Deque;

/** Exercise 1: the memento for a single int is the int itself (immutable by nature). */
public final class SolutionCounterUndo {
  private int value;
  private final Deque<Integer> history = new ArrayDeque<>();

  void add(int delta) {
    history.push(value);
    value += delta;
  }

  boolean undo() {
    if (history.isEmpty()) return false;
    value = history.pop();
    return true;
  }

  public static void main(String[] args) {
    SolutionCounterUndo c = new SolutionCounterUndo();
    c.add(5);
    c.add(10);
    System.out.println(c.value);
    c.undo();
    System.out.println(c.value);
    c.undo();
    System.out.println(c.value + " " + c.undo());
  }
}
