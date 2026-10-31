package patterns.behavioral.visitor;

// ANCHOR: num
public record Num(int value) implements Expr {
  @Override
  public <R> R accept(ExprVisitor<R> visitor) {
    return visitor.visitNum(this);
  }
}
// ANCHOR_END: num
