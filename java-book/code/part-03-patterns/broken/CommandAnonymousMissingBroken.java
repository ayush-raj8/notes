interface Command {
  void execute();

  void undo();
}

public final class CommandAnonymousMissingBroken {
  public static void main(String[] args) {
    Command c =
        new Command() {
          @Override
          public void execute() {
            System.out.println("run");
          }
        };
    c.execute();
  }
}
