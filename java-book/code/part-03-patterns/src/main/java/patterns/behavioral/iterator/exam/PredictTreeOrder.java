package patterns.behavioral.iterator.exam;

import patterns.behavioral.iterator.IntTree;

// ANCHOR: predict-tree-order
public final class PredictTreeOrder {
  public static void main(String[] args) {
    IntTree t = new IntTree();
    for (int v : new int[] {4, 9, 1, 6, 4, 2}) t.add(v);
    for (int v : t) System.out.print(v + " ");
    System.out.println();
  }
}
// ANCHOR_END: predict-tree-order
