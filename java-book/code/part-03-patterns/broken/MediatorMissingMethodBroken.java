final class User {}

interface ChatMediator {
  void broadcast(User from, String message);

  boolean direct(User from, String to, String message);
}

public final class MediatorMissingMethodBroken implements ChatMediator {
  @Override
  public void broadcast(User from, String message) {}

  public static void main(String[] args) {}
}
