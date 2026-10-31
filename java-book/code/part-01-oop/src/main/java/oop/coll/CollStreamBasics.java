package oop.coll;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// ANCHOR: coll-stream-basics
public final class CollStreamBasics {
  public static void main(String[] args) {
    List<Integer> nums = List.of(5, 2, 8, 2, 9, 4);

    List<Integer> evens = nums.stream().filter(n -> n % 2 == 0).collect(Collectors.toList());
    System.out.println("filter even      : " + evens);
    System.out.println("map square       : " + nums.stream().map(n -> n * n).toList());
    System.out.println("distinct, sorted : " + nums.stream().distinct().sorted().toList());
    System.out.println("skip 1, limit 3  : " + nums.stream().skip(1).limit(3).toList());
    System.out.println("count / sum / max: " + nums.stream().count() + " / "
        + nums.stream().mapToInt(Integer::intValue).sum() + " / "
        + nums.stream().max(Integer::compare).get());
    System.out.println("reduce           : " + nums.stream().reduce(0, Integer::sum)
        + ", reduce(no identity) = " + nums.stream().reduce(Integer::sum));
    System.out.println("average          : " + IntStream.of(1, 2, 3, 4).average());
    System.out.println("range(1,5) sum   : " + IntStream.range(1, 5).sum() + ", rangeClosed(1,5): "
        + IntStream.rangeClosed(1, 5).sum());
    System.out.println("flatMap          : "
        + Stream.of(List.of(1, 2), List.of(3)).flatMap(List::stream).toList());
    System.out.println("anyMatch/allMatch: " + nums.stream().anyMatch(n -> n > 8) + "/"
        + nums.stream().allMatch(n -> n > 2));
    System.out.println("takeWhile        : " + Stream.of(1, 2, 3, 10, 1).takeWhile(n -> n < 5).toList());
    System.out.println("iterate+limit    : " + Stream.iterate(1, n -> n * 2).limit(6).toList());

    List<Integer> immutable = nums.stream().toList(); // Java 16+: unmodifiable
    try {
      immutable.add(1);
    } catch (UnsupportedOperationException e) {
      System.out.println("Stream.toList().add -> UnsupportedOperationException");
    }
    List<Integer> mutable = nums.stream().collect(Collectors.toCollection(ArrayList::new));
    mutable.add(1);
    System.out.println("toCollection(ArrayList::new).add works: " + mutable.size());

    Stream<Integer> s = nums.stream();
    s.count();
    try {
      s.count();
    } catch (IllegalStateException e) {
      System.out.println("second terminal op -> " + e.getMessage());
    }
    System.out.println("source list unchanged: " + nums);
  }
}
// ANCHOR_END: coll-stream-basics
