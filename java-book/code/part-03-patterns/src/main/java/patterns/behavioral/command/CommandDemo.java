package patterns.behavioral.command;

public final class CommandDemo {
  // ANCHOR: command-demo
  public static void main(String[] args) {
    TextBuffer buf = new TextBuffer();
    CommandHistory h = new CommandHistory();
    h.run(new AppendCommand(buf, "Hello"));
    h.run(new AppendCommand(buf, " World"));
    h.run(new DeleteLastCommand(buf, 3));
    System.out.println("[" + buf.text() + "]");
    h.undo();
    System.out.println("[" + buf.text() + "]");
    h.undo();
    System.out.println("[" + buf.text() + "]");
    h.redo();
    System.out.println("[" + buf.text() + "]");
    System.out.println(h.undo() + " " + h.undo() + " " + h.undo());
    System.out.println("[" + buf.text() + "]");
  }
  // ANCHOR_END: command-demo
}
