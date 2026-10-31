package patterns.creational.singleton;

// ANCHOR: eager-singleton
public final class EagerSingleton {
  private static final EagerSingleton INSTANCE = new EagerSingleton();

  private EagerSingleton() {
    System.out.println("EagerSingleton created");
  }

  public static EagerSingleton getInstance() {
    return INSTANCE;
  }
}
// ANCHOR_END: eager-singleton
