package patterns.behavioral.command;

// ANCHOR: append-command
public final class AppendCommand implements Command {
  private final TextBuffer buffer;
  private final String text;

  public AppendCommand(TextBuffer buffer, String text) {
    this.buffer = buffer;
    this.text = text;
  }

  @Override
  public void execute() {
    buffer.append(text);
  }

  @Override
  public void undo() {
    buffer.deleteLast(text.length());
  }
}
// ANCHOR_END: append-command
