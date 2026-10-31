package patterns.creational.singleton;

// ANCHOR: guarded-singleton
/** Defends against reflection by refusing a second construction. */
public final class GuardedSingleton {
  private static final GuardedSingleton INSTANCE = new GuardedSingleton();
  private static boolean built;

  private GuardedSingleton() {
    if (built) {
      throw new IllegalStateException("already constructed");
    }
    built = true;
  }

  public static GuardedSingleton getInstance() {
    return INSTANCE;
  }
}
// ANCHOR_END: guarded-singleton
