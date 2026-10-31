interface Expr {}

final class Num implements Expr {}

final class Mul implements Expr {}

interface ExprVisitor<R> {
  R visitNum(Num n);

  R visitMul(Mul m);
}

public final class VisitorMissingVisitBroken implements ExprVisitor<Integer> {
  @Override
  public Integer visitNum(Num n) {
    return 1;
  }

  public static void main(String[] args) {}
}
