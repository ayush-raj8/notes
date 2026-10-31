public final class VisitorRecordArityBroken {
  sealed interface Node permits Lit, Plus {}

  record Lit(int v) implements Node {}

  record Plus(Node l, Node r) implements Node {}

  static String show(Node n) {
    return switch (n) {
      case Lit(var v) -> "lit";
      case Plus(var only) -> "plus";
    };
  }

  public static void main(String[] args) {
    System.out.println(show(new Lit(1)));
  }
}
