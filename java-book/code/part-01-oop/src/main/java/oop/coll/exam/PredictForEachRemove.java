package oop.coll.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-for-each-remove
public final class PredictForEachRemove {
  static void remove(String target) {
    List<String> list = new ArrayList<>(List.of("a", "b", "c"));
    try {
      for (String s : list) {
        if (s.equals(target)) {
          list.remove(s);
        }
      }
      System.out.println("removed " + target + " -> " + list);
    } catch (RuntimeException e) {
      System.out.println("removed " + target + " -> " + e.getClass().getSimpleName());
    }
  }

  public static void main(String[] args) {
    remove("a");
    remove("b");
    remove("c");
  }
}
// ANCHOR_END: predict-for-each-remove
