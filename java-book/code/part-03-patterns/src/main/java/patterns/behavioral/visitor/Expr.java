package patterns.behavioral.visitor;

// ANCHOR: expr
/** Element role: each node accepts a visitor and calls back the method for its own type. */
public interface Expr {
  <R> R accept(ExprVisitor<R> visitor);
}
// ANCHOR_END: expr
