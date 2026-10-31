package patterns.structural.composite.exam;

import patterns.structural.composite.DirNode;
import patterns.structural.composite.FileNode;

// ANCHOR: predict-tree-size
public final class PredictTreeSize {
  public static void main(String[] args) {
    DirNode a = new DirNode("a").add(new FileNode("x", 4));
    DirNode b = new DirNode("b").add(a).add(new FileNode("y", 6));
    DirNode empty = new DirNode("e");
    System.out.println(a.size());
    System.out.println(b.size());
    System.out.println(empty.size());
  }
}
// ANCHOR_END: predict-tree-size
