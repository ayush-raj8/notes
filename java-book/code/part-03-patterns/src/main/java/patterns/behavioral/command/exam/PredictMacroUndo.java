package patterns.behavioral.command.exam;

import patterns.behavioral.command.Command;
import patterns.behavioral.command.MacroCommand;

// ANCHOR: predict-macro-undo
public final class PredictMacroUndo {
  static Command step(String name) {
    return new Command() {
      @Override
      public void execute() {
        System.out.print("+" + name + " ");
      }

      @Override
      public void undo() {
        System.out.print("-" + name + " ");
      }
    };
  }

  public static void main(String[] args) {
    MacroCommand m = new MacroCommand().add(step("a")).add(step("b")).add(step("c"));
    m.execute();
    System.out.println();
    m.undo();
    System.out.println();
  }
}
// ANCHOR_END: predict-macro-undo
