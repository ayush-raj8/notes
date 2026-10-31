package oop.access;

// ANCHOR: final-method-demo
class Shape {
  final String kind() {
    return "shape";
  }

  String name() {
    return "shape";
  }
}

class Circle extends Shape {
  @Override
  String name() {
    return "circle";
  }
}

/** A {@code final} class cannot be extended. */
final class Radius {
  final int value;

  Radius(int value) {
    this.value = value;
  }
}

/** {@code final} method is inherited but not overridden; non-final methods may be. */
public class FinalMethodDemo {
  public static void main(String[] args) {
    Shape s = new Circle();
    System.out.println(s.kind());
    System.out.println(s.name());
    System.out.println(new Radius(4).value);
  }
}
// ANCHOR_END: final-method-demo
