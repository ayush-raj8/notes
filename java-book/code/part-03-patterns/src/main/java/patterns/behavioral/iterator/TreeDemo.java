package patterns.behavioral.iterator;

public final class TreeDemo {
  // ANCHOR: tree-demo
  public static void main(String[] args) {
    IntTree t = new IntTree();
    for (int v : new int[] {5, 2, 8, 1, 3, 8, 7}) t.add(v);
    StringBuilder sb = new StringBuilder();
    for (int v : t) sb.append(v).append(' ');
    System.out.println(sb.toString().trim());
  }
  // ANCHOR_END: tree-demo
}
