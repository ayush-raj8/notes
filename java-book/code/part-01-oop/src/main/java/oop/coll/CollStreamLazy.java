package oop.coll;

import java.util.stream.Stream;

// ANCHOR: coll-stream-lazy
public final class CollStreamLazy {
  public static void main(String[] args) {
    System.out.println("== no terminal operation: nothing runs");
    Stream.of("a", "bb", "ccc").peek(s -> System.out.println("peek " + s)).map(String::length);

    System.out.println("== element-at-a-time, not stage-at-a-time");
    Stream.of("a", "bb", "ccc")
        .peek(s -> System.out.println("peek   " + s))
        .filter(s -> s.length() > 1)
        .peek(s -> System.out.println("filter " + s))
        .map(String::toUpperCase)
        .forEach(s -> System.out.println("each   " + s));

    System.out.println("== short-circuit: findFirst stops early");
    String found = Stream.of("x", "yy", "zzz", "wwww")
        .peek(s -> System.out.println("visit " + s))
        .filter(s -> s.length() >= 2)
        .findFirst()
        .get();
    System.out.println("found " + found);

    System.out.println("== sorted is a barrier: it needs every element first");
    Stream.of(3, 1, 2)
        .peek(n -> System.out.println("before sort " + n))
        .sorted()
        .forEach(n -> System.out.println("after sort  " + n));
  }
}
// ANCHOR_END: coll-stream-lazy
