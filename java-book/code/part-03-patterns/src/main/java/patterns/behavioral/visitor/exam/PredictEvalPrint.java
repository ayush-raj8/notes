package patterns.behavioral.visitor.exam;

import patterns.behavioral.visitor.Add;
import patterns.behavioral.visitor.Eval;
import patterns.behavioral.visitor.Expr;
import patterns.behavioral.visitor.Mul;
import patterns.behavioral.visitor.Num;
import patterns.behavioral.visitor.Print;

// ANCHOR: predict-eval-print
public final class PredictEvalPrint {
  public static void main(String[] args) {
    Expr e = new Add(new Num(1), new Mul(new Num(2), new Num(3)));
    System.out.println(e.accept(new Print()));
    System.out.println(e.accept(new Eval()));
    Expr f = new Mul(new Add(new Num(1), new Num(2)), new Num(3));
    System.out.println(f.accept(new Print()) + "=" + f.accept(new Eval()));
  }
}
// ANCHOR_END: predict-eval-print
