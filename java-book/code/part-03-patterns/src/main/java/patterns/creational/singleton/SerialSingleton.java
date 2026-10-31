package patterns.creational.singleton;

import java.io.Serializable;

// ANCHOR: serial-singleton
/** Serializable singleton WITHOUT readResolve: deserialization builds a second object. */
public final class SerialSingleton implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final SerialSingleton INSTANCE = new SerialSingleton();

  private SerialSingleton() {}

  public static SerialSingleton getInstance() {
    return INSTANCE;
  }
}
// ANCHOR_END: serial-singleton
