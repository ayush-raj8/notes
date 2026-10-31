package oop.generics.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 2: copy with PECS. */
public final class SolutionPecsCopy {
  static <T> void copyAll(List<? super T> dst, List<? extends T> src) {
    for (T t : src) {
      dst.add(t);
    }
  }

  public static void main(String[] args) {
    List<Object> out = new ArrayList<>();
    copyAll(out, List.of("a", "b"));
    copyAll(out, List.of(1));
    System.out.println(out);
  }
}
