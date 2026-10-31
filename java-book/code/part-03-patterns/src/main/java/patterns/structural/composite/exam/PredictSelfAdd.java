package patterns.structural.composite.exam;

import patterns.structural.composite.DirNode;
import patterns.structural.composite.FileNode;

// ANCHOR: predict-self-add
public final class PredictSelfAdd {
  public static void main(String[] args) {
    DirNode d = new DirNode("d").add(new FileNode("f", 2));
    try {
      d.add(d);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(d.size());
    System.out.println(new FileNode("f", 2).equals(new FileNode("f", 2)));
    System.out.println(new FileNode("f", 2));
  }
}
// ANCHOR_END: predict-self-add
