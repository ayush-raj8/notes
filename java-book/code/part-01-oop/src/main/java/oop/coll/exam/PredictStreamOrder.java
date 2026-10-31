package oop.coll.exam;

import java.util.stream.Stream;

// ANCHOR: predict-stream-order
public final class PredictStreamOrder {
  public static void main(String[] args) {
    Stream.of("a", "bb", "cc")
        .peek(s -> System.out.println("P " + s))
        .filter(s -> s.length() == 2)
        .map(String::toUpperCase)
        .forEach(s -> System.out.println("E " + s));

    Stream.of(1, 2, 3).peek(n -> System.out.println("never " + n)).map(n -> n + 1);
    System.out.println("end");
  }
}
// ANCHOR_END: predict-stream-order
