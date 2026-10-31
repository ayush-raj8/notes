package oop.coll.exam;

import java.util.List;
import java.util.stream.Stream;

// ANCHOR: predict-stream-reuse
public final class PredictStreamReuse {
  public static void main(String[] args) {
    List<Integer> source = List.of(1, 2, 3);
    Stream<Integer> s = source.stream().map(n -> n * 10);
    System.out.println(s.toList());
    try {
      System.out.println(s.count());
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(source.stream().map(n -> n * 10).count());
    System.out.println(source);
  }
}
// ANCHOR_END: predict-stream-reuse
