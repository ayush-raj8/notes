interface Command {
  void execute();
}

public final class CommandReturnTypeBroken implements Command {
  @Override
  public int execute() {
    return 1;
  }

  public static void main(String[] args) {
    new CommandReturnTypeBroken().execute();
  }
}
