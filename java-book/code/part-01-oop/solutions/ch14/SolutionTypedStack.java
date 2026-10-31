package oop.generics.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 5: generic stack without generic array creation. */
public final class SolutionTypedStack {
  static final class Stack<T> {
    private final List<T> items = new ArrayList<>();

    void push(T t) {
      items.add(t);
    }

    T pop() {
      return items.remove(items.size() - 1);
    }

    boolean isEmpty() {
      return items.isEmpty();
    }
  }

  public static void main(String[] args) {
    Stack<Integer> s = new Stack<>();
    s.push(1);
    s.push(2);
    System.out.println(s.pop() + " " + s.isEmpty());
  }
}
