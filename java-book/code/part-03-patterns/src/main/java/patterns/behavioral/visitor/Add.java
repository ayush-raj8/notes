package patterns.behavioral.visitor;

public record Add(Expr left, Expr right) implements Expr {
  @Override
  public <R> R accept(ExprVisitor<R> visitor) {
    return visitor.visitAdd(this);
  }
}
