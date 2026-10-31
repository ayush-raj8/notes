package oop.coll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: coll-views
public final class CollViews {
  public static void main(String[] args) {
    List<String> original = new ArrayList<>(List.of("a", "b"));

    List<String> readOnlyView = Collections.unmodifiableList(original); // wrapper
    List<String> snapshot = List.copyOf(original); // independent immutable copy

    original.add("c");
    System.out.println("view sees later change : " + readOnlyView);
    System.out.println("snapshot does not      : " + snapshot);
    try {
      readOnlyView.add("x");
    } catch (UnsupportedOperationException e) {
      System.out.println("view.add -> UnsupportedOperationException");
    }

    List<String> same = Collections.unmodifiableList(original);
    System.out.println("defensive getter that wraps still leaks changes: " + (same.size() == original.size()));
  }
}
// ANCHOR_END: coll-views
