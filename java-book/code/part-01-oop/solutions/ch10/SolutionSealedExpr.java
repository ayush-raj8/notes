package oop.abstracts.solutions;

/** Exercise 2: sealed interface + records + exhaustive switch (no default). */
public final class SolutionSealedExpr {
  sealed interface Expr permits Num, Add, Mul {}

  record Num(int v) implements Expr {}

  record Add(Expr l, Expr r) implements Expr {}

  record Mul(Expr l, Expr r) implements Expr {}

  static int eval(Expr e) {
    return switch (e) {
      case Num n -> n.v();
      case Add a -> eval(a.l()) + eval(a.r());
      case Mul m -> eval(m.l()) * eval(m.r());
    };
  }

  public static void main(String[] args) {
    Expr e = new Add(new Num(2), new Mul(new Num(3), new Num(4)));
    System.out.println(eval(e));
  }
}
