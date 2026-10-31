final class Context {
  private String state = "NEW";
}

final class PaidState {
  void enter(Context c) {
    c.state = "PAID";
  }
}

public final class StatePrivateContextBroken {
  public static void main(String[] args) {
    new PaidState().enter(new Context());
  }
}
