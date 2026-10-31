package patterns.behavioral.command;

public final class MacroDemo {
  // ANCHOR: macro-demo
  public static void main(String[] args) {
    TextBuffer buf = new TextBuffer();
    CommandHistory h = new CommandHistory();
    MacroCommand sig =
        new MacroCommand()
            .add(new AppendCommand(buf, "Regards,"))
            .add(new AppendCommand(buf, " Sam"))
            .add(new DeleteLastCommand(buf, 1));
    h.run(sig);
    System.out.println("[" + buf.text() + "]");
    h.undo();
    System.out.println("[" + buf.text() + "]");
  }
  // ANCHOR_END: macro-demo
}
