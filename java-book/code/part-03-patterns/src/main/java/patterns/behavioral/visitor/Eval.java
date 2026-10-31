package patterns.behavioral.visitor;

// ANCHOR: eval
/** Operation 1: evaluate. The new operation lives in one class; the nodes are untouched. */
public final class Eval implements ExprVisitor<Integer> {
  @Override
  public Integer visitNum(Num n) {
    return n.value();
  }

  @Override
  public Integer visitAdd(Add a) {
    return a.left().accept(this) + a.right().accept(this);
  }

  @Override
  public Integer visitMul(Mul m) {
    return m.left().accept(this) * m.right().accept(this);
  }
}
// ANCHOR_END: eval
