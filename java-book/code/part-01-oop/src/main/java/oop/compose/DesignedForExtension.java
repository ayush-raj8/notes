package oop.compose;

// ANCHOR: designed-for-extension
/** Inheritance is fine when the base documents its self-use and the subtype is a true is-a. */
public final class DesignedForExtension {
  /** Template: {@code describe()} calls {@code area()} — documented contract for subclasses. */
  abstract static class Shape {
    abstract double area();

    final String describe() {
      return getClass().getSimpleName() + " area=" + area();
    }
  }

  static final class Square extends Shape {
    private final double side;

    Square(double side) {
      this.side = side;
    }

    @Override
    double area() {
      return side * side;
    }
  }

  public static void main(String[] args) {
    System.out.println(new Square(3).describe());
  }
}
// ANCHOR_END: designed-for-extension
