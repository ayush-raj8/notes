package patterns.creational.singleton;

// ANCHOR: dcl-singleton
public final class DclSingleton {
  private static volatile DclSingleton instance; // volatile is required

  private DclSingleton() {}

  public static DclSingleton getInstance() {
    DclSingleton local = instance; // one volatile read on the fast path
    if (local == null) {
      synchronized (DclSingleton.class) {
        local = instance;
        if (local == null) {
          instance = local = new DclSingleton();
        }
      }
    }
    return local;
  }
}
// ANCHOR_END: dcl-singleton
