package patterns.behavioral.visitor;

// ANCHOR: expr-visitor
/** Visitor role: one {@code visit} method per concrete element type. */
public interface ExprVisitor<R> {
  R visitNum(Num n);

  R visitAdd(Add a);

  R visitMul(Mul m);
}
// ANCHOR_END: expr-visitor
