package oop.compose;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

// ANCHOR: stack-leak
/** {@code Stack extends Vector}: is-a leaks Vector operations that break LIFO. */
public final class StackLeak {
  public static void main(String[] args) {
    Stack<Integer> s = new Stack<>();
    s.push(1);
    s.push(2);
    s.push(3);
    System.out.println("removed bottom=" + s.remove(0));
    System.out.println("stack=" + s);

    Deque<Integer> d = new ArrayDeque<>();
    d.push(1);
    d.push(2);
    d.push(3);
    System.out.println("deque=" + d);
  }
}
// ANCHOR_END: stack-leak
