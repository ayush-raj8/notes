package patterns.behavioral.visitor.exam;

import patterns.behavioral.visitor.Add;
import patterns.behavioral.visitor.Expr;
import patterns.behavioral.visitor.NodeCounter;
import patterns.behavioral.visitor.Num;

// ANCHOR: predict-stateful
public final class PredictStatefulVisitor {
  public static void main(String[] args) {
    Expr e = new Add(new Num(1), new Add(new Num(2), new Num(3)));
    NodeCounter shared = new NodeCounter();
    e.accept(shared);
    System.out.println(shared.nums() + " " + shared.ops());
    e.accept(shared);
    System.out.println(shared.nums() + " " + shared.ops());
    NodeCounter fresh = new NodeCounter();
    new Num(9).accept(fresh);
    System.out.println(fresh.nums() + " " + fresh.ops());
  }
}
// ANCHOR_END: predict-stateful
