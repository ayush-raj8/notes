package patterns.behavioral.memento;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

// ANCHOR: history
/** Caretaker: stores mementos and hands them back; it never looks inside. */
public final class History {
  private final Deque<Editor.Memento> stack = new ArrayDeque<>();
  private final int limit;

  public History(int limit) {
    this.limit = limit;
  }

  public void push(Editor.Memento m) {
    stack.push(m);
    if (stack.size() > limit) stack.removeLast();
  }

  public Optional<Editor.Memento> pop() {
    return Optional.ofNullable(stack.poll());
  }

  public int size() {
    return stack.size();
  }
}
// ANCHOR_END: history
