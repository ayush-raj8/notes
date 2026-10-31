public final class VisitorSealedMissingCaseBroken {
  sealed interface Node permits Lit, Plus, Times {}

  record Lit(int v) implements Node {}

  record Plus(Node l, Node r) implements Node {}

  record Times(Node l, Node r) implements Node {}

  static int eval(Node n) {
    return switch (n) {
      case Lit l -> l.v();
      case Plus p -> eval(p.l()) + eval(p.r());
    };
  }

  public static void main(String[] args) {
    System.out.println(eval(new Lit(1)));
  }
}
