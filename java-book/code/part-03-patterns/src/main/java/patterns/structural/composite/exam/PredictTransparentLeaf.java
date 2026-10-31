package patterns.structural.composite.exam;

import patterns.structural.composite.TransparentComposite.Group;
import patterns.structural.composite.TransparentComposite.Leaf;
import patterns.structural.composite.TransparentComposite.Node;

// ANCHOR: predict-transparent-leaf
public final class PredictTransparentLeaf {
  public static void main(String[] args) {
    Node g = new Group();
    g.add(new Leaf());
    g.add(new Leaf());
    System.out.println(g.count());
    Node l = new Leaf();
    try {
      l.add(g);
      System.out.println("added");
    } catch (UnsupportedOperationException e) {
      System.out.println("rejected");
    }
    System.out.println(l.count());
  }
}
// ANCHOR_END: predict-transparent-leaf
