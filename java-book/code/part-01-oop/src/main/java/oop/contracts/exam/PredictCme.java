package oop.contracts.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-cme
public final class PredictCme {
  public static void main(String[] args) {
    List<String> list = new ArrayList<>(List.of("a", "b", "c"));
    for (String s : list) {
      if (s.equals("b")) {
        list.remove(s);
      }
    }
    System.out.println(list);

    List<String> list2 = new ArrayList<>(List.of("a", "b", "c"));
    try {
      for (String s : list2) {
        if (s.equals("a")) {
          list2.remove(s);
        }
      }
    } catch (java.util.ConcurrentModificationException e) {
      System.out.println("CME");
    }
    System.out.println(list2);
  }
}
// ANCHOR_END: predict-cme
