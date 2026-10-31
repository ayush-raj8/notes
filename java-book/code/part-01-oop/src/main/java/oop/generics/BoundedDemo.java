package oop.generics;

import java.util.List;

// ANCHOR: bounded-demo
/** Upper bounds give the type parameter the bound's methods. */
public final class BoundedDemo {
  static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
  }

  static <T extends Number & Comparable<T>> T biggest(List<T> xs) {
    T best = xs.get(0);
    for (T x : xs) {
      if (x.compareTo(best) > 0) {
        best = x;
      }
    }
    return best;
  }

  static final class Stats<T extends Number> {
    private final List<T> xs;

    Stats(List<T> xs) {
      this.xs = xs;
    }

    double mean() {
      double sum = 0;
      for (T x : xs) {
        sum += x.doubleValue();
      }
      return sum / xs.size();
    }
  }

  public static void main(String[] args) {
    System.out.println(max(3, 9));
    System.out.println(max("pear", "apple"));
    System.out.println(biggest(List.of(4, 11, 7)));
    System.out.println(new Stats<>(List.of(1, 2, 3, 4)).mean());
  }
}
// ANCHOR_END: bounded-demo
