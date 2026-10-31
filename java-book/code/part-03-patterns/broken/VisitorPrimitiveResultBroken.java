final class Num {}

interface ExprVisitor<R> {
  R visitNum(Num n);
}

public final class VisitorPrimitiveResultBroken implements ExprVisitor<Integer> {
  @Override
  public int visitNum(Num n) {
    return 1;
  }

  public static void main(String[] args) {}
}
