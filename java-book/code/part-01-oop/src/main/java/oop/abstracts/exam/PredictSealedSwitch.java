package oop.abstracts.exam;

// ANCHOR: predict-sealed-switch
sealed interface PssNode permits PssLeaf, PssPair {}

record PssLeaf(int v) implements PssNode {}

record PssPair(PssNode l, PssNode r) implements PssNode {}

public final class PredictSealedSwitch {
  static int sum(PssNode n) {
    return switch (n) {
      case PssLeaf l -> l.v();
      case PssPair p -> sum(p.l()) + sum(p.r());
    };
  }

  public static void main(String[] args) {
    PssNode tree = new PssPair(new PssLeaf(1), new PssPair(new PssLeaf(2), new PssLeaf(3)));
    System.out.println(sum(tree));
    System.out.println(tree);
  }
}
// ANCHOR_END: predict-sealed-switch
