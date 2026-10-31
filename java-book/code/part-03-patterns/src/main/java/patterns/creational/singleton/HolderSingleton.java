package patterns.creational.singleton;

// ANCHOR: holder-singleton
public final class HolderSingleton {
  private HolderSingleton() {
    System.out.println("HolderSingleton created");
  }

  private static final class Holder {
    static final HolderSingleton INSTANCE = new HolderSingleton();
  }

  /** Lazy and lock-free: the JVM initializes Holder once, on first use. */
  public static HolderSingleton getInstance() {
    return Holder.INSTANCE;
  }

  public static void hello() {
    System.out.println("hello");
  }
}
// ANCHOR_END: holder-singleton
