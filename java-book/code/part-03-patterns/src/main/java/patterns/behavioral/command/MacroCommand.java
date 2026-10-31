package patterns.behavioral.command;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: macro
/** Composite command: execute in order, undo in reverse order. */
public final class MacroCommand implements Command {
  private final List<Command> steps = new ArrayList<>();

  public MacroCommand add(Command c) {
    steps.add(c);
    return this;
  }

  @Override
  public void execute() {
    for (Command c : steps) c.execute();
  }

  @Override
  public void undo() {
    for (int i = steps.size() - 1; i >= 0; i--) steps.get(i).undo();
  }
}
// ANCHOR_END: macro
