package patterns.structural.composite;

/** Intentionally invalid: a switch over a sealed type must cover every permitted subtype. */
public final class CompositeSwitchMissingCaseBroken {
  static int eval(Expr e) {
    return switch (e) {
      case Expr.Num n -> n.value();
      case Expr.Add a -> eval(a.left()) + eval(a.right());
    };
  }
}
