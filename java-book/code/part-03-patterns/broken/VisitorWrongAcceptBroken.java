interface ExprVisitor<R> {
  R visitNum(Num n);

  R visitAdd(Add a);
}

final class Num {
  <R> R accept(ExprVisitor<R> v) {
    return v.visitNum(this);
  }
}

final class Add {
  <R> R accept(ExprVisitor<R> v) {
    return v.visitNum(this);
  }
}

public final class VisitorWrongAcceptBroken {
  public static void main(String[] args) {}
}
