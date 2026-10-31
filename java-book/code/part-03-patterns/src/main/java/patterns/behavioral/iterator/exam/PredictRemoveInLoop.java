package patterns.behavioral.iterator.exam;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

// ANCHOR: predict-remove-in-loop
public final class PredictRemoveInLoop {
  static void remove(String victim) {
    List<String> list = new ArrayList<>(List.of("a", "b", "c"));
    try {
      for (String s : list) {
        if (s.equals(victim)) list.remove(s);
      }
      System.out.println("removed " + victim + " -> " + list);
    } catch (ConcurrentModificationException e) {
      System.out.println("CME removing " + victim + " -> " + list);
    }
  }

  public static void main(String[] args) {
    remove("a");
    remove("b");
    remove("c");
  }
}
// ANCHOR_END: predict-remove-in-loop
