package patterns.behavioral.visitor.exam;

// ANCHOR: predict-overload
public final class PredictOverload {
  static class Shape {}

  static class Circle extends Shape {}

  static class Visitor {
    String visit(Shape s) {
      return "Shape";
    }

    String visit(Circle c) {
      return "Circle";
    }
  }

  static class LoudVisitor extends Visitor {
    @Override
    String visit(Shape s) {
      return "LOUD Shape";
    }
  }

  public static void main(String[] args) {
    Shape s = new Circle();
    Visitor v = new LoudVisitor();
    System.out.println(v.visit(s));
    System.out.println(v.visit(new Circle()));
    System.out.println(v.visit((Circle) s));
    System.out.println(new Visitor().visit(new Shape()));
  }
}
// ANCHOR_END: predict-overload
