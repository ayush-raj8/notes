package patterns.creational.singleton;

import java.util.concurrent.atomic.AtomicInteger;

// ANCHOR: lazy-unsafe-singleton
/** Lazy but NOT thread-safe. The sleep widens the race window so the demo repeats. */
public final class LazyUnsafeSingleton {
  static final AtomicInteger CREATED = new AtomicInteger();
  private static LazyUnsafeSingleton instance;

  private LazyUnsafeSingleton() {
    CREATED.incrementAndGet();
  }

  public static LazyUnsafeSingleton getInstance() throws InterruptedException {
    if (instance == null) {
      Thread.sleep(100);
      instance = new LazyUnsafeSingleton();
    }
    return instance;
  }
}
// ANCHOR_END: lazy-unsafe-singleton
