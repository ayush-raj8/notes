package patterns.creational.singleton;

import java.io.Serializable;

// ANCHOR: safe-serial-singleton
public final class SafeSerialSingleton implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final SafeSerialSingleton INSTANCE = new SafeSerialSingleton();

  private SafeSerialSingleton() {}

  public static SafeSerialSingleton getInstance() {
    return INSTANCE;
  }

  private Object readResolve() {
    return INSTANCE; // replaces the freshly deserialized copy
  }
}
// ANCHOR_END: safe-serial-singleton
