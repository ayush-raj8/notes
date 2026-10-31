package oop.conc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

/** Six ways to hold one instance, and which ones are safe under threads. */
public final class SingletonVariants {
  private SingletonVariants() {}

  static final List<String> log = new java.util.concurrent.CopyOnWriteArrayList<>();

  // ANCHOR: singleton-eager
  /** Created when the class initializes. Thread-safe (class init is serialized by the JVM). Not lazy. */
  static final class Eager {
    private static final Eager INSTANCE = new Eager();

    private Eager() {
      log.add("Eager created");
    }

    static Eager getInstance() {
      return INSTANCE;
    }
  }
  // ANCHOR_END: singleton-eager

  // ANCHOR: singleton-lazy-unsafe
  /** Lazy but NOT thread-safe: two threads can both see null and create two instances. */
  static final class LazyUnsafe {
    private static LazyUnsafe instance;

    private LazyUnsafe() {}

    static LazyUnsafe getInstance() {
      if (instance == null) { // check ...
        instance = new LazyUnsafe(); // ... then act: race window between the two
      }
      return instance;
    }
  }
  // ANCHOR_END: singleton-lazy-unsafe

  // ANCHOR: singleton-sync-method
  /** Lazy and safe; every call pays for the lock. */
  static final class SyncMethod {
    private static SyncMethod instance;

    private SyncMethod() {}

    static synchronized SyncMethod getInstance() {
      if (instance == null) {
        instance = new SyncMethod();
      }
      return instance;
    }
  }
  // ANCHOR_END: singleton-sync-method

  // ANCHOR: singleton-dcl
  /** Double-checked locking. volatile is required: it stops a half-constructed object being published. */
  static final class Dcl {
    private static volatile Dcl instance;

    private Dcl() {}

    static Dcl getInstance() {
      Dcl local = instance; // first check: no lock on the fast path
      if (local == null) {
        synchronized (Dcl.class) {
          local = instance; // second check: inside the lock
          if (local == null) {
            instance = local = new Dcl();
          }
        }
      }
      return local;
    }
  }
  // ANCHOR_END: singleton-dcl

  // ANCHOR: singleton-holder
  /** Initialization-on-demand holder: lazy, lock-free, safe. Holder loads on first getInstance(). */
  static final class Holder {
    private Holder() {
      log.add("Holder created");
    }

    private static final class Lazy {
      static final Holder INSTANCE = new Holder();
    }

    static Holder getInstance() {
      return Lazy.INSTANCE;
    }
  }
  // ANCHOR_END: singleton-holder

  // ANCHOR: singleton-enum
  /** Enum singleton: safe, serialization-safe, reflection-safe. Cannot extend another class. */
  enum EnumSingleton {
    INSTANCE;

    int next() {
      return ++counter;
    }

    private int counter;
  }
  // ANCHOR_END: singleton-enum

  // ANCHOR: singleton-demo
  static int distinctUnderThreads(java.util.function.Supplier<Object> getter, int threads) throws InterruptedException {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    List<Thread> ts = new ArrayList<>();
    java.util.concurrent.CyclicBarrier start = new java.util.concurrent.CyclicBarrier(threads);
    for (int i = 0; i < threads; i++) {
      Thread t = new Thread(() -> {
        try {
          start.await(); // release all threads together to widen the race window
        } catch (Exception e) {
          throw new IllegalStateException(e);
        }
        seen.add(getter.get());
      });
      ts.add(t);
      t.start();
    }
    for (Thread t : ts) t.join();
    return seen.size();
  }

  public static void main(String[] args) throws InterruptedException {
    System.out.println("log before any access: " + log);
    Eager.getInstance();
    System.out.println("after Eager.getInstance: " + log);
    Holder.getInstance();
    System.out.println("after Holder.getInstance: " + log);
    System.out.println("same Holder: " + (Holder.getInstance() == Holder.getInstance()));
    System.out.println("SyncMethod distinct: " + distinctUnderThreads(SyncMethod::getInstance, 16));
    System.out.println("Dcl distinct:        " + distinctUnderThreads(Dcl::getInstance, 16));
    System.out.println("Holder distinct:     " + distinctUnderThreads(Holder::getInstance, 16));
    System.out.println("Enum distinct:       " + distinctUnderThreads(() -> EnumSingleton.INSTANCE, 16));
    System.out.println("Enum next: " + EnumSingleton.INSTANCE.next() + "," + EnumSingleton.INSTANCE.next());
  }
  // ANCHOR_END: singleton-demo
}
