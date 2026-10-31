package patterns.behavioral.command;

// ANCHOR: delete-command
/** Stores what it removed so that undo can put it back. */
public final class DeleteLastCommand implements Command {
  private final TextBuffer buffer;
  private final int count;
  private String removed = "";

  public DeleteLastCommand(TextBuffer buffer, int count) {
    this.buffer = buffer;
    this.count = count;
  }

  @Override
  public void execute() {
    removed = buffer.deleteLast(count);
  }

  @Override
  public void undo() {
    buffer.append(removed);
  }
}
// ANCHOR_END: delete-command
