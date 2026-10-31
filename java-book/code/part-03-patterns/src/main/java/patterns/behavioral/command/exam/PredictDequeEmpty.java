package patterns.behavioral.command.exam;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

// ANCHOR: predict-deque-empty
public final class PredictDequeEmpty {
  public static void main(String[] args) {
    Deque<String> undo = new ArrayDeque<>();
    System.out.println(undo.poll());
    try {
      undo.pop();
    } catch (NoSuchElementException e) {
      System.out.println("NoSuchElementException");
    }
    try {
      undo.push(null);
    } catch (NullPointerException e) {
      System.out.println("NullPointerException");
    }
    System.out.println(undo.size());
  }
}
// ANCHOR_END: predict-deque-empty
