package patterns.behavioral.visitor;

public record Mul(Expr left, Expr right) implements Expr {
  @Override
  public <R> R accept(ExprVisitor<R> visitor) {
    return visitor.visitMul(this);
  }
}
