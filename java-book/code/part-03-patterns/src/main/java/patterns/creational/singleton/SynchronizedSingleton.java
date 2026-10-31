package patterns.creational.singleton;

import java.util.concurrent.atomic.AtomicInteger;

// ANCHOR: synchronized-singleton
public final class SynchronizedSingleton {
  private static final AtomicInteger CREATED = new AtomicInteger();
  private static SynchronizedSingleton instance;

  private SynchronizedSingleton() {
    CREATED.incrementAndGet();
  }

  /** Correct, but every call pays for the lock. */
  public static synchronized SynchronizedSingleton getInstance() {
    if (instance == null) {
      instance = new SynchronizedSingleton();
    }
    return instance;
  }

  public static int created() {
    return CREATED.get();
  }
}
// ANCHOR_END: synchronized-singleton
