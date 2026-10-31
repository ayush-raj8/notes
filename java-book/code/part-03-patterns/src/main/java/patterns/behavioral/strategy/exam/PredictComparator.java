package patterns.behavioral.strategy.exam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// ANCHOR: predict-comparator
public final class PredictComparator {
  public static void main(String[] args) {
    List<String> w = new ArrayList<>(List.of("bb", "a", "ccc", "aa", "b"));
    w.sort(Comparator.comparingInt(String::length));
    System.out.println(w);
    w.sort(Comparator.comparingInt(String::length).reversed());
    System.out.println(w);
    w.sort(Comparator.comparingInt(String::length).thenComparing(Comparator.reverseOrder()));
    System.out.println(w);
  }
}
// ANCHOR_END: predict-comparator
