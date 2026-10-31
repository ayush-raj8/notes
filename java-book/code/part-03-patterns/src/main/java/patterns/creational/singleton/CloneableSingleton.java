package patterns.creational.singleton;

// ANCHOR: cloneable-singleton
/** Anti-example: Cloneable plus a public clone() hands out a second instance. */
public final class CloneableSingleton implements Cloneable {
  private static final CloneableSingleton INSTANCE = new CloneableSingleton();

  private CloneableSingleton() {}

  public static CloneableSingleton getInstance() {
    return INSTANCE;
  }

  @Override
  public CloneableSingleton clone() {
    try {
      return (CloneableSingleton) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }
}
// ANCHOR_END: cloneable-singleton
