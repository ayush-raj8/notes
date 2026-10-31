package patterns.structural.composite.exam;

import patterns.structural.composite.DirNode;
import patterns.structural.composite.FileNode;

// ANCHOR: predict-shared-child
public final class PredictSharedChild {
  public static void main(String[] args) {
    DirNode shared = new DirNode("shared").add(new FileNode("x", 1));
    DirNode p1 = new DirNode("p1").add(shared);
    DirNode p2 = new DirNode("p2").add(shared);
    System.out.println(p1.size() + " " + p2.size());
    shared.add(new FileNode("y", 10));
    System.out.println(p1.size() + " " + p2.size());
  }
}
// ANCHOR_END: predict-shared-child
