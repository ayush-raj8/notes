package patterns.behavioral.memento.exam;

import patterns.behavioral.memento.Editor;
import patterns.behavioral.memento.History;

// ANCHOR: predict-undo-stack
public final class PredictUndoStack {
  public static void main(String[] args) {
    Editor e = new Editor();
    History h = new History(5);
    e.type("a");
    h.push(e.save());
    e.type("b");
    h.push(e.save());
    e.type("c");
    h.pop().ifPresent(e::restore);
    System.out.println(e.text() + " " + e.cursor());
    h.pop().ifPresent(e::restore);
    System.out.println(e.text() + " " + e.cursor());
    System.out.println(h.pop().isPresent());
    System.out.println(e.text());
  }
}
// ANCHOR_END: predict-undo-stack
