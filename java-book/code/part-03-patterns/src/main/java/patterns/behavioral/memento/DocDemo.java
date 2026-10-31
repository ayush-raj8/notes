package patterns.behavioral.memento;

public final class DocDemo {
  // ANCHOR: doc-demo
  public static void main(String[] args) {
    Doc d = new Doc();
    d.add("a");
    d.add("b");
    Doc.Snap shallow = d.saveShallow();
    Doc.Snap deep = d.saveDeep();
    d.add("c");
    System.out.println("live    " + d.lines());
    System.out.println("shallow " + shallow.lines());
    System.out.println("deep    " + deep.lines());

    d.restore(deep);
    System.out.println("restored from deep    " + d.lines());
    d.restore(shallow);
    System.out.println("restored from shallow " + d.lines());
  }
  // ANCHOR_END: doc-demo
}
