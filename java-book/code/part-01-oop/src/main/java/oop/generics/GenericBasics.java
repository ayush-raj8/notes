package oop.generics;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: generic-basics
/** Generic class, generic method, explicit type argument, diamond, inference. */
public final class GenericBasics {
  static final class Box<T> {
    private final T value;

    Box(T value) {
      this.value = value;
    }

    T get() {
      return value;
    }
  }

  record Pair<A, B>(A first, B second) {
    Pair<B, A> swap() {
      return new Pair<>(second, first);
    }
  }

  static <T> T firstOf(List<T> xs) {
    return xs.get(0);
  }

  public static void main(String[] args) {
    Box<String> b = new Box<>("hi");
    String s = b.get();
    System.out.println(s);

    Pair<String, Integer> p = new Pair<>("age", 36);
    System.out.println(p + " -> " + p.swap());

    List<Integer> nums = new ArrayList<>(List.of(7, 8));
    int n = firstOf(nums);
    String t = GenericBasics.<String>firstOf(List.of("explicit"));
    System.out.println(n + " " + t);

    var inferred = firstOf(List.of(2.5));
    System.out.println(inferred.getClass().getSimpleName());
  }
}
// ANCHOR_END: generic-basics
