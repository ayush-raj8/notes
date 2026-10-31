package patterns.behavioral.command;

import java.util.ArrayDeque;
import java.util.Deque;

// ANCHOR: history
/** Invoker: runs commands and keeps the undo / redo stacks. */
public final class CommandHistory {
  private final Deque<Command> undoStack = new ArrayDeque<>();
  private final Deque<Command> redoStack = new ArrayDeque<>();

  public void run(Command c) {
    c.execute();
    undoStack.push(c);
    redoStack.clear();
  }

  public boolean undo() {
    Command c = undoStack.poll();
    if (c == null) return false;
    c.undo();
    redoStack.push(c);
    return true;
  }

  public boolean redo() {
    Command c = redoStack.poll();
    if (c == null) return false;
    c.execute();
    undoStack.push(c);
    return true;
  }
}
// ANCHOR_END: history
