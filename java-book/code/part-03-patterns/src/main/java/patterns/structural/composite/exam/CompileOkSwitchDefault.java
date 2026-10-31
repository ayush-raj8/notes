package patterns.structural.composite.exam;

import patterns.structural.composite.Expr;

// ANCHOR: compile-ok-switch-default
/** Compiles: a default branch makes the switch exhaustive without listing every subtype. */
public final class CompileOkSwitchDefault {
  static int value(Expr e) {
    return switch (e) {
      case Expr.Num n -> n.value();
      default -> -1;
    };
  }

  public static void main(String[] args) {
    System.out.println(value(new Expr.Num(7)));
    System.out.println(value(new Expr.Add(new Expr.Num(1), new Expr.Num(2))));
  }
}
// ANCHOR_END: compile-ok-switch-default
