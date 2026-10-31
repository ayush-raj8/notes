package patterns.behavioral.visitor;

// ANCHOR: print
/** Operation 2: print. Different result type (String) from the same element hierarchy. */
public final class Print implements ExprVisitor<String> {
  @Override
  public String visitNum(Num n) {
    return Integer.toString(n.value());
  }

  @Override
  public String visitAdd(Add a) {
    return "(" + a.left().accept(this) + " + " + a.right().accept(this) + ")";
  }

  @Override
  public String visitMul(Mul m) {
    return "(" + m.left().accept(this) + " * " + m.right().accept(this) + ")";
  }
}
// ANCHOR_END: print
