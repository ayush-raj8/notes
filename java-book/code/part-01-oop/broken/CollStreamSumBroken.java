package oop.coll;

import java.util.stream.Stream;

public final class CollStreamSumBroken {
  public static void main(String[] args) {
    int total = Stream.of(1, 2, 3).sum();
    System.out.println(total);
  }
}
