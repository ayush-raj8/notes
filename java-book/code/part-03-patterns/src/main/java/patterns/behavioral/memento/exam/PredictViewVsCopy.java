package patterns.behavioral.memento.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: predict-view-vs-copy
public final class PredictViewVsCopy {
  public static void main(String[] args) {
    List<String> live = new ArrayList<>(List.of("a"));
    List<String> view = Collections.unmodifiableList(live);
    List<String> copy = List.copyOf(live);
    List<String> ctor = new ArrayList<>(live);
    live.add("b");
    System.out.println(view + " " + copy + " " + ctor);
    try {
      view.add("c");
    } catch (UnsupportedOperationException e) {
      System.out.println("view.add: UnsupportedOperationException");
    }
    try {
      copy.add("c");
    } catch (UnsupportedOperationException e) {
      System.out.println("copy.add: UnsupportedOperationException");
    }
  }
}
// ANCHOR_END: predict-view-vs-copy
