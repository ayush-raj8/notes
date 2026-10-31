package patterns.behavioral.visitor.exam;

import patterns.behavioral.visitor.PatternEval.Lit;
import patterns.behavioral.visitor.PatternEval.Node;
import patterns.behavioral.visitor.PatternEval.Plus;
import patterns.behavioral.visitor.PatternEval.Times;

// ANCHOR: predict-record-patterns
public final class PredictRecordPatterns {
  static String kind(Node n) {
    return switch (n) {
      case Plus(Lit(var a), Lit(var b)) when a == b -> "double " + a;
      case Plus(Lit l, var r) -> "lit-left " + l.v();
      case Plus(var l, var r) -> "plus";
      case Times(var l, Lit(var b)) when b == 0 -> "zero";
      case Times t -> "times";
      case Lit(var v) when v < 0 -> "negative";
      case Lit l -> "lit";
    };
  }

  public static void main(String[] args) {
    System.out.println(kind(new Plus(new Lit(2), new Lit(2))));
    System.out.println(kind(new Plus(new Lit(2), new Lit(3))));
    System.out.println(kind(new Plus(new Times(new Lit(1), new Lit(1)), new Lit(3))));
    System.out.println(kind(new Times(new Lit(5), new Lit(0))));
    System.out.println(kind(new Lit(-1)));
    System.out.println(kind(new Lit(1)));
  }
}
// ANCHOR_END: predict-record-patterns
