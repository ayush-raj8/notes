interface Command {
  void execute();
}

public final class CommandWeakerAccessBroken implements Command {
  void execute() {
    System.out.println("run");
  }

  public static void main(String[] args) {
    new CommandWeakerAccessBroken().execute();
  }
}
