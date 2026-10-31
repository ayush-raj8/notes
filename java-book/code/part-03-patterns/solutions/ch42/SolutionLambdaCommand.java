package solutions.ch42;

import patterns.behavioral.command.Command;

/** Exercise 2: build a Command from two lambdas. */
public final class SolutionLambdaCommand {
  static Command of(Runnable doIt, Runnable undoIt) {
    return new Command() {
      public void execute() {
        doIt.run();
      }

      public void undo() {
        undoIt.run();
      }
    };
  }

  public static void main(String[] args) {
    StringBuilder sb = new StringBuilder();
    Command c = of(() -> sb.append("on"), () -> sb.setLength(0));
    c.execute();
    System.out.println(sb);
    c.undo();
    System.out.println("[" + sb + "]");
  }
}
