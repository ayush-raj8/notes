package oop.generics;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: pecs-demo
/** Producer Extends, Consumer Super. */
public final class PecsDemo {
  /** src produces T (extends), dst consumes T (super). */
  static <T> void copy(List<? super T> dst, List<? extends T> src) {
    for (T t : src) {
      dst.add(t);
    }
  }

  static double sum(List<? extends Number> xs) {
    double total = 0;
    for (Number n : xs) {
      total += n.doubleValue();
    }
    return total;
  }

  static void fill(List<? super Integer> sink) {
    sink.add(1);
    sink.add(2);
  }

  static int countAny(List<?> xs) {
    int n = 0;
    for (Object o : xs) {
      n++;
    }
    return n;
  }

  public static void main(String[] args) {
    List<Integer> ints = List.of(1, 2, 3);
    List<Number> nums = new ArrayList<>();
    copy(nums, ints);
    System.out.println(nums);
    System.out.println(sum(List.of(1, 2.5, 3L)));

    List<Object> objs = new ArrayList<>();
    fill(objs);
    fill(nums);
    System.out.println(objs + " " + nums);

    List<?> unknown = ints;
    Object first = unknown.get(0);
    System.out.println(first + " " + countAny(unknown));
  }
}
// ANCHOR_END: pecs-demo
