package oop.poly.solutions;

import java.util.Optional;

/** Exercise 4: downcast without ever throwing ClassCastException. */
public final class SolutionSafeDowncast {
  static <T> Optional<T> as(Object o, Class<T> type) {
    return type.isInstance(o) ? Optional.of(type.cast(o)) : Optional.empty();
  }

  public static void main(String[] args) {
    Object o = "text";
    System.out.println(as(o, String.class).map(String::length).orElse(-1));
    System.out.println(as(o, Integer.class).isPresent());
    System.out.println(as(null, String.class).isPresent());
  }
}
