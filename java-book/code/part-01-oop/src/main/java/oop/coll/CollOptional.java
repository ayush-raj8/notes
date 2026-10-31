package oop.coll;

import java.util.NoSuchElementException;
import java.util.Optional;

// ANCHOR: coll-optional
public final class CollOptional {
  static String fallback(String why) {
    System.out.println("  fallback evaluated (" + why + ")");
    return "default";
  }

  static Optional<String> find(boolean present) {
    return present ? Optional.of("value") : Optional.empty();
  }

  public static void main(String[] args) {
    System.out.println("of/empty       : " + Optional.of("x") + " " + Optional.empty());
    System.out.println("ofNullable(null): " + Optional.ofNullable(null).isPresent());
    try {
      Optional.of(null);
    } catch (NullPointerException e) {
      System.out.println("Optional.of(null) -> NullPointerException");
    }
    try {
      find(false).get();
    } catch (NoSuchElementException e) {
      System.out.println("empty.get() -> " + e.getMessage());
    }

    System.out.println("map/filter     : " + find(true).map(String::toUpperCase).filter(s -> s.startsWith("V")));
    System.out.println("flatMap        : " + find(true).flatMap(s -> Optional.of(s.length())));
    System.out.println("orElse (present value, argument still evaluated):");
    find(true).orElse(fallback("orElse"));
    System.out.println("orElseGet (present value, supplier not called):");
    find(true).orElseGet(() -> fallback("orElseGet"));
    System.out.println("orElseGet on empty -> " + find(false).orElseGet(() -> fallback("empty")));

    find(true).ifPresentOrElse(v -> System.out.println("present " + v), () -> System.out.println("absent"));
    find(false).ifPresentOrElse(v -> System.out.println("present " + v), () -> System.out.println("absent"));
    try {
      find(false).orElseThrow(() -> new IllegalStateException("missing"));
    } catch (IllegalStateException e) {
      System.out.println("orElseThrow -> " + e.getMessage());
    }
    System.out.println("or(...)        : " + find(false).or(() -> Optional.of("alt")).get());
    System.out.println("isEmpty (11+)  : " + find(false).isEmpty());
  }
}
// ANCHOR_END: coll-optional
