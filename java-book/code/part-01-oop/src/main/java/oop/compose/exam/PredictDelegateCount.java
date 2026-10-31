package oop.compose.exam;

import java.util.HashSet;
import java.util.List;
import oop.compose.CountingSet;

// ANCHOR: predict-delegate-count
public final class PredictDelegateCount {
  public static void main(String[] args) {
    CountingSet<String> s = new CountingSet<>(new HashSet<>());
    s.add("a");
    s.addAll(List.of("b", "c"));
    System.out.println(s.addCount() + " " + s.size());
  }
}
// ANCHOR_END: predict-delegate-count
