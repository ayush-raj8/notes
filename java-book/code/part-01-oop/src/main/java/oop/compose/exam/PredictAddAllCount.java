package oop.compose.exam;

import java.util.List;
import oop.compose.CountingHashSet;

// ANCHOR: predict-addall-count
public final class PredictAddAllCount {
  public static void main(String[] args) {
    CountingHashSet<String> s = new CountingHashSet<>();
    s.add("a");
    s.addAll(List.of("b", "c"));
    System.out.println(s.addCount() + " " + s.size());
  }
}
// ANCHOR_END: predict-addall-count
