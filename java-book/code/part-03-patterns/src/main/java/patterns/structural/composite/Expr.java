package patterns.structural.composite;

// ANCHOR: expr
/** Java 17+ sealed interface + Java 16+ records; pattern switch is Java 21. */
public sealed interface Expr permits Expr.Num, Expr.Add, Expr.Mul {
  record Num(int value) implements Expr {}

  record Add(Expr left, Expr right) implements Expr {}

  record Mul(Expr left, Expr right) implements Expr {}

  static int eval(Expr e) {
    return switch (e) {
      case Num n -> n.value();
      case Add a -> eval(a.left()) + eval(a.right());
      case Mul m -> eval(m.left()) * eval(m.right());
    };
  }

  static String show(Expr e) {
    return switch (e) {
      case Num n -> String.valueOf(n.value());
      case Add a -> "(" + show(a.left()) + " + " + show(a.right()) + ")";
      case Mul m -> show(m.left()) + " * " + show(m.right());
    };
  }
}
// ANCHOR_END: expr
