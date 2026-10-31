package solutions.ch48;

import patterns.behavioral.visitor.Add;
import patterns.behavioral.visitor.Expr;
import patterns.behavioral.visitor.ExprVisitor;
import patterns.behavioral.visitor.Mul;
import patterns.behavioral.visitor.Num;

/** Exercise 5: a third operation (JSON text) added without touching Num, Add, or Mul. */
public final class SolutionJsonVisitor implements ExprVisitor<String> {
  public String visitNum(Num n) {
    return "{\"num\":" + n.value() + "}";
  }

  public String visitAdd(Add a) {
    return "{\"add\":[" + a.left().accept(this) + "," + a.right().accept(this) + "]}";
  }

  public String visitMul(Mul m) {
    return "{\"mul\":[" + m.left().accept(this) + "," + m.right().accept(this) + "]}";
  }

  public static void main(String[] args) {
    System.out.println(new Add(new Num(1), new Mul(new Num(2), new Num(3))).accept(new SolutionJsonVisitor()));
  }
}
