package patterns.creational.prototype;

import java.util.List;

// ANCHOR: clone-demo
public final class CloneDemo {
  public static void main(String[] args) {
    Document original = new Document("doc", List.of("p1"));
    Document shallow = original.clone();
    Document deep = original.deepCopy();

    shallow.addPage("p2");
    System.out.println("original " + original);
    System.out.println("shallow  " + shallow);
    System.out.println("deep     " + deep);
    System.out.println(shallow.pages() == original.pages());
    System.out.println(deep.pages() == original.pages());
  }
}
// ANCHOR_END: clone-demo
