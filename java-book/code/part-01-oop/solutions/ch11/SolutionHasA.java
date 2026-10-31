package oop.compose.solutions;

import java.util.ArrayDeque;
import java.util.Deque;

/** Exercise 1: IntStack as has-a over a deque; no Vector methods leak. */
public final class SolutionHasA {
  static final class IntStack {
    private final Deque<Integer> items = new ArrayDeque<>();

    void push(int v) {
      items.push(v);
    }

    int pop() {
      return items.pop();
    }

    int size() {
      return items.size();
    }
  }

  public static void main(String[] args) {
    IntStack s = new IntStack();
    s.push(1);
    s.push(2);
    System.out.println(s.pop() + " " + s.size());
  }
}
