package patterns.behavioral.visitor;

// ANCHOR: node-counter
/** A stateful visitor: it accumulates across a traversal, so use one instance per traversal. */
public final class NodeCounter implements ExprVisitor<Void> {
  private int nums;
  private int ops;

  public int nums() {
    return nums;
  }

  public int ops() {
    return ops;
  }

  @Override
  public Void visitNum(Num n) {
    nums++;
    return null;
  }

  @Override
  public Void visitAdd(Add a) {
    ops++;
    a.left().accept(this);
    a.right().accept(this);
    return null;
  }

  @Override
  public Void visitMul(Mul m) {
    ops++;
    m.left().accept(this);
    m.right().accept(this);
    return null;
  }
}
// ANCHOR_END: node-counter
