package patterns.behavioral.visitor;

/** Java 21 alternative: sealed records + pattern {@code switch} replace accept/visit. */
public final class PatternEval {
  private PatternEval() {}

  // ANCHOR: pattern-nodes
  public sealed interface Node permits Lit, Plus, Times {}

  public record Lit(int v) implements Node {}

  public record Plus(Node l, Node r) implements Node {}

  public record Times(Node l, Node r) implements Node {}

  public static int eval(Node n) {
    return switch (n) {
      case Lit l -> l.v();
      case Plus p -> eval(p.l()) + eval(p.r());
      case Times t -> eval(t.l()) * eval(t.r());
    };
  }

  public static String show(Node n) {
    return switch (n) {
      case Lit(int v) -> Integer.toString(v);
      case Plus(Node l, Node r) -> "(" + show(l) + " + " + show(r) + ")";
      case Times(var l, var r) -> "(" + show(l) + " * " + show(r) + ")";
    };
  }

  /** Record patterns nest: a rule for "literal times literal" without a visitor class. */
  public static String fold(Node n) {
    return switch (n) {
      case Times(Lit(var a), Lit(var b)) -> "const " + (a * b);
      case Plus(Lit(var a), Lit(var b)) -> "const " + (a + b);
      case Times t -> "times";
      case Plus p -> "plus";
      case Lit l -> "lit";
    };
  }
  // ANCHOR_END: pattern-nodes

  // ANCHOR: pattern-demo
  public static void main(String[] args) {
    Node n = new Times(new Plus(new Lit(2), new Lit(3)), new Lit(4));
    System.out.println(show(n) + " = " + eval(n));
    System.out.println(fold(new Times(new Lit(6), new Lit(7))));
    System.out.println(fold(new Times(new Plus(new Lit(1), new Lit(1)), new Lit(7))));
  }
  // ANCHOR_END: pattern-demo
}
