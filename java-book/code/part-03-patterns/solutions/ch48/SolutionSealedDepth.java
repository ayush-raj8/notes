package solutions.ch48;

import patterns.behavioral.visitor.PatternEval.Lit;
import patterns.behavioral.visitor.PatternEval.Node;
import patterns.behavioral.visitor.PatternEval.Plus;
import patterns.behavioral.visitor.PatternEval.Times;

/** Exercise 3: depth of a tree with a pattern switch instead of a visitor class. */
public final class SolutionSealedDepth {
  static int depth(Node n) {
    return switch (n) {
      case Lit l -> 1;
      case Plus(var l, var r) -> 1 + Math.max(depth(l), depth(r));
      case Times(var l, var r) -> 1 + Math.max(depth(l), depth(r));
    };
  }

  public static void main(String[] args) {
    System.out.println(depth(new Lit(1)));
    System.out.println(depth(new Times(new Plus(new Lit(2), new Lit(3)), new Lit(4))));
  }
}
