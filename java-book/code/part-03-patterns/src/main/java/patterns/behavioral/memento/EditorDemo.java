package patterns.behavioral.memento;

public final class EditorDemo {
  // ANCHOR: editor-demo
  static void show(String step, Editor e) {
    System.out.println(step + ": \"" + e.text() + "\" cursor=" + e.cursor());
  }

  public static void main(String[] args) {
    Editor ed = new Editor();
    History h = new History(10);

    h.push(ed.save());
    ed.type("Hello");
    show("typed", ed);

    h.push(ed.save());
    ed.type(" World");
    ed.moveTo(0);
    ed.type(">> ");
    show("edited", ed);

    h.pop().ifPresent(ed::restore);
    show("undo 1", ed);
    h.pop().ifPresent(ed::restore);
    show("undo 2", ed);
    System.out.println("more history? " + h.pop().isPresent());
  }
  // ANCHOR_END: editor-demo
}
