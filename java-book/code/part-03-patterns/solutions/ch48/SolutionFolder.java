package solutions.ch48;

import patterns.behavioral.visitor.Add;
import patterns.behavioral.visitor.Expr;
import patterns.behavioral.visitor.ExprVisitor;
import patterns.behavioral.visitor.Mul;
import patterns.behavioral.visitor.Num;
import patterns.behavioral.visitor.Print;

/** Exercise 2: a visitor that returns a new tree (constant folding). */
public final class SolutionFolder implements ExprVisitor<Expr> {
  public Expr visitNum(Num n) {
    return n;
  }

  public Expr visitAdd(Add a) {
    Expr l = a.left().accept(this);
    Expr r = a.right().accept(this);
    if (l instanceof Num x && r instanceof Num y) return new Num(x.value() + y.value());
    return new Add(l, r);
  }

  public Expr visitMul(Mul m) {
    Expr l = m.left().accept(this);
    Expr r = m.right().accept(this);
    if (l instanceof Num x && r instanceof Num y) return new Num(x.value() * y.value());
    return new Mul(l, r);
  }

  public static void main(String[] args) {
    Expr e = new Add(new Mul(new Num(2), new Num(3)), new Add(new Num(4), new Num(5)));
    System.out.println(e.accept(new Print()) + " -> " + e.accept(new SolutionFolder()).accept(new Print()));
  }
}
