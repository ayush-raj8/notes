package oop.uml.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: which-relationship
/** Name the UML relationship from A to B in each pair. Answers are in the chapter. */
public final class WhichRelationship {
  static class B {}

  static class A1 { // (1)
    B make() {
      return new B();
    }
  }

  static class A2 { // (2)
    private B b;

    void set(B b) {
      this.b = b;
    }
  }

  static class A3 { // (3)
    private final List<B> parts = new ArrayList<>(List.of(new B(), new B()));
  }

  static class A4 extends B {} // (4)

  interface I {}

  static class A5 implements I {} // (5)

  static class A6 { // (6)
    private final List<B> held;

    A6(List<B> held) {
      this.held = held;
    }
  }

  public static void main(String[] args) {
    System.out.println("compiles; classify by reading the fields, parameters and clauses");
  }
}
// ANCHOR_END: which-relationship
