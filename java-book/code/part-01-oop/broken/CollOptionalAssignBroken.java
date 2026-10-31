package oop.coll;

import java.util.Optional;

public final class CollOptionalAssignBroken {
  static Optional<String> find() {
    return Optional.of("x");
  }

  public static void main(String[] args) {
    String s = find();
    System.out.println(s);
  }
}
