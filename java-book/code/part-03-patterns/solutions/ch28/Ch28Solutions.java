package patterns.creational.singleton.solutions;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import patterns.creational.singleton.SerialUtil;

public final class Ch28Solutions {
  // ANCHOR: ch28-ex1
  /** Ex1: lazy, lock-free configuration holder. */
  static final class Config {
    private final Map<String, String> values = Map.of("mode", "prod");

    private Config() {}

    private static final class Holder {
      static final Config INSTANCE = new Config();
    }

    static Config get() {
      return Holder.INSTANCE;
    }

    String value(String key) {
      return values.get(key);
    }
  }
  // ANCHOR_END: ch28-ex1

  // ANCHOR: ch28-ex2
  /** Ex2: unsafe lazy singleton repaired with a synchronized accessor. */
  static final class FixedLazy {
    private static FixedLazy instance;
    static final AtomicInteger CREATED = new AtomicInteger();

    private FixedLazy() {
      CREATED.incrementAndGet();
    }

    static synchronized FixedLazy get() {
      if (instance == null) {
        instance = new FixedLazy();
      }
      return instance;
    }
  }
  // ANCHOR_END: ch28-ex2

  // ANCHOR: ch28-ex3
  /** Ex3: readResolve keeps one instance across serialization. */
  static final class Registry implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Registry INSTANCE = new Registry();

    private Registry() {}

    private Object readResolve() {
      return INSTANCE;
    }
  }
  // ANCHOR_END: ch28-ex3

  // ANCHOR: ch28-ex4
  /** Ex4: refuse clone. */
  static final class NoClone implements Cloneable {
    private static final NoClone INSTANCE = new NoClone();

    private NoClone() {}

    @Override
    protected Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException("singleton");
    }
  }
  // ANCHOR_END: ch28-ex4

  // ANCHOR: ch28-ex5
  /** Ex5: enum singleton id generator. */
  enum IdGenerator {
    INSTANCE;

    private int last;

    int next() {
      return ++last;
    }
  }
  // ANCHOR_END: ch28-ex5

  public static void main(String[] args) throws Exception {
    System.out.println("ex1 " + Config.get().value("mode") + " " + (Config.get() == Config.get()));
    FixedLazy.get();
    FixedLazy.get();
    System.out.println("ex2 created=" + FixedLazy.CREATED.get());
    System.out.println("ex3 same=" + (Registry.INSTANCE == SerialUtil.roundTrip(Registry.INSTANCE)));
    try {
      NoClone.INSTANCE.clone();
    } catch (CloneNotSupportedException e) {
      System.out.println("ex4 " + e.getMessage());
    }
    System.out.println("ex5 " + IdGenerator.INSTANCE.next() + IdGenerator.INSTANCE.next());
  }
}
