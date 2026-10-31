interface Command {
  void execute();

  void undo();
}

public final class CommandLambdaTwoMethodsBroken {
  public static void main(String[] args) {
    Command c = () -> System.out.println("run");
    c.execute();
  }
}
