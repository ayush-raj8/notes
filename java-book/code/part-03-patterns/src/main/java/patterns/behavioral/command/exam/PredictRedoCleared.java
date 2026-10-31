package patterns.behavioral.command.exam;

import patterns.behavioral.command.AppendCommand;
import patterns.behavioral.command.CommandHistory;
import patterns.behavioral.command.TextBuffer;

// ANCHOR: predict-redo-cleared
public final class PredictRedoCleared {
  public static void main(String[] args) {
    TextBuffer b = new TextBuffer();
    CommandHistory h = new CommandHistory();
    h.run(new AppendCommand(b, "A"));
    h.run(new AppendCommand(b, "B"));
    h.undo();
    h.run(new AppendCommand(b, "C"));
    System.out.println(h.redo());
    System.out.println(b.text());
  }
}
// ANCHOR_END: predict-redo-cleared
