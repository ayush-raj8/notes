package patterns.behavioral.visitor;

/** Why accept() exists: overloads are chosen at compile time from the static type. */
public final class OverloadTrap {
  // ANCHOR: overload-trap
  interface Shape {
    String accept(Visitor v);
  }

  static final class Circle implements Shape {
    public String accept(Visitor v) {
      return v.visit(this);
    }
  }

  static final class Square implements Shape {
    public String accept(Visitor v) {
      return v.visit(this);
    }
  }

  static final class Visitor {
    String visit(Shape s) {
      return "visit(Shape)";
    }

    String visit(Circle c) {
      return "visit(Circle)";
    }

    String visit(Square s) {
      return "visit(Square)";
    }
  }

  public static void main(String[] args) {
    Visitor v = new Visitor();
    Shape s = new Circle();
    System.out.println(v.visit(s));
    System.out.println(v.visit((Circle) s));
    System.out.println(s.accept(v));
    System.out.println(new Square().accept(v));
  }
  // ANCHOR_END: overload-trap
}
