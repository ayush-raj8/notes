package patterns.structural.composite.exam;

import patterns.structural.composite.DirNode;
import patterns.structural.composite.FileNode;

// ANCHOR: predict-render
public final class PredictRender {
  public static void main(String[] args) {
    DirNode d = new DirNode("d").add(new DirNode("s").add(new FileNode("f", 1)));
    System.out.println(d.render(0));
    System.out.println(d.render(2));
  }
}
// ANCHOR_END: predict-render
