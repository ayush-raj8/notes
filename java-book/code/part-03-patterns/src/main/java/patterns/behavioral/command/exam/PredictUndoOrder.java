package patterns.behavioral.command.exam;

import patterns.behavioral.command.AppendCommand;
import patterns.behavioral.command.CommandHistory;
import patterns.behavioral.command.TextBuffer;

// ANCHOR: predict-undo-order
public final class PredictUndoOrder {
  public static void main(String[] args) {
    TextBuffer b = new TextBuffer();
    CommandHistory h = new CommandHistory();
    h.run(new AppendCommand(b, "A"));
    h.run(new AppendCommand(b, "B"));
    h.run(new AppendCommand(b, "C"));
    h.undo();
    h.undo();
    System.out.println(b.text());
  }
}
// ANCHOR_END: predict-undo-order
