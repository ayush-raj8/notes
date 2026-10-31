package patterns.structural.composite.exam;

import patterns.structural.composite.Expr;
import patterns.structural.composite.Expr.Add;
import patterns.structural.composite.Expr.Mul;
import patterns.structural.composite.Expr.Num;

// ANCHOR: predict-expr-eval
public final class PredictExprEval {
  public static void main(String[] args) {
    Expr a = new Add(new Num(2), new Mul(new Num(3), new Num(4)));
    Expr b = new Mul(new Add(new Num(2), new Num(3)), new Num(4));
    System.out.println(Expr.eval(a));
    System.out.println(Expr.eval(b));
    System.out.println(Expr.show(a));
    System.out.println(new Num(5).equals(new Num(5)));
  }
}
// ANCHOR_END: predict-expr-eval
