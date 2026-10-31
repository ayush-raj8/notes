package patterns.behavioral.visitor;

public final class ExprDemo {
  // ANCHOR: expr-demo
  public static void main(String[] args) {
    Expr e = new Mul(new Add(new Num(2), new Num(3)), new Num(4));
    System.out.println(e.accept(new Print()) + " = " + e.accept(new Eval()));

    NodeCounter counter = new NodeCounter();
    e.accept(counter);
    System.out.println("numbers=" + counter.nums() + " operators=" + counter.ops());
    System.out.println(e);
  }
  // ANCHOR_END: expr-demo
}
