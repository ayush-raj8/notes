package patterns.behavioral.memento.exam;

import patterns.behavioral.memento.Doc;

// ANCHOR: predict-doc-shallow
public final class PredictDocShallow {
  public static void main(String[] args) {
    Doc d = new Doc();
    d.add("a");
    Doc.Snap s1 = d.saveShallow();
    Doc.Snap s2 = d.saveDeep();
    d.add("b");
    System.out.println(s1.lines() + " " + s2.lines());
    d.restore(s2);
    d.add("z");
    System.out.println(d.lines() + " " + s2.lines());
  }
}
// ANCHOR_END: predict-doc-shallow
