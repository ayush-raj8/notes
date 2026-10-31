package patterns.behavioral.memento.exam;

import patterns.behavioral.memento.Editor;

// ANCHOR: predict-restore-twice
public final class PredictRestoreTwice {
  public static void main(String[] args) {
    Editor e = new Editor();
    e.type("one");
    Editor.Memento m = e.save();
    e.type("two");
    e.restore(m);
    e.type("X");
    System.out.println(e.text());
    e.restore(m);
    System.out.println(e.text() + " " + e.cursor());
    Editor other = new Editor();
    other.restore(m);
    System.out.println(other.text());
    System.out.println(m);
  }
}
// ANCHOR_END: predict-restore-twice
