interface State {
  void handle();
}

public final class StateInterfaceNewBroken {
  public static void main(String[] args) {
    State s = new State();
    s.handle();
  }
}
