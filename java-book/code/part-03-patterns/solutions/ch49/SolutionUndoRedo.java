package solutions.ch49;

import java.util.ArrayDeque;
import java.util.Deque;

/** Exercise 3: undo and redo with mementos; a new edit clears redo. */
public final class SolutionUndoRedo {
  private String text = "";
  private final Deque<String> undo = new ArrayDeque<>();
  private final Deque<String> redo = new ArrayDeque<>();

  void set(String t) {
    undo.push(text);
    redo.clear();
    text = t;
  }

  void undo() {
    if (undo.isEmpty()) return;
    redo.push(text);
    text = undo.pop();
  }

  void redo() {
    if (redo.isEmpty()) return;
    undo.push(text);
    text = redo.pop();
  }

  public static void main(String[] args) {
    SolutionUndoRedo d = new SolutionUndoRedo();
    d.set("a");
    d.set("ab");
    d.undo();
    System.out.println("[" + d.text + "]");
    d.redo();
    System.out.println("[" + d.text + "]");
    d.undo();
    d.set("x");
    d.redo();
    System.out.println("[" + d.text + "]");
  }
}
