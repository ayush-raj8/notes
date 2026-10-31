interface ChatMediator {
  void broadcast(String message);
}

public final class MediatorFinalNotSetBroken {
  private final ChatMediator mediator;

  MediatorFinalNotSetBroken(String name) {
    System.out.println("created " + name);
  }

  public static void main(String[] args) {
    new MediatorFinalNotSetBroken("ann");
  }
}
