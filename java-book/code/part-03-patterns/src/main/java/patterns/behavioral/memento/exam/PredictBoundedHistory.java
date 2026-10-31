package patterns.behavioral.memento.exam;

import patterns.behavioral.memento.Editor;
import patterns.behavioral.memento.History;

// ANCHOR: predict-bounded
public final class PredictBoundedHistory {
  public static void main(String[] args) {
    Editor e = new Editor();
    History h = new History(2);
    for (String s : new String[] {"1", "2", "3"}) {
      h.push(e.save());
      e.type(s);
    }
    System.out.println(e.text() + " history=" + h.size());
    h.pop().ifPresent(e::restore);
    System.out.println("[" + e.text() + "]");
    h.pop().ifPresent(e::restore);
    System.out.println("[" + e.text() + "]");
    h.pop().ifPresent(e::restore);
    System.out.println("[" + e.text() + "] " + h.size());
  }
}
// ANCHOR_END: predict-bounded
