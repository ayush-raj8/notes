package oop.generics.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 3: reverse any list via wildcard capture. */
public final class SolutionSwapCapture {
  static void reverse(List<?> xs) {
    rev(xs);
  }

  private static <T> void rev(List<T> xs) {
    for (int i = 0, j = xs.size() - 1; i < j; i++, j--) {
      T tmp = xs.get(i);
      xs.set(i, xs.get(j));
      xs.set(j, tmp);
    }
  }

  public static void main(String[] args) {
    List<Integer> xs = new ArrayList<>(List.of(1, 2, 3));
    reverse(xs);
    System.out.println(xs);
  }
}
