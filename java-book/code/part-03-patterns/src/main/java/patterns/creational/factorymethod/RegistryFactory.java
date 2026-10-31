package patterns.creational.factorymethod;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// ANCHOR: registry-factory
/** Parameterized factory: new kinds register without editing a switch. */
public final class RegistryFactory {
  private final Map<String, Supplier<Notifier>> makers = new HashMap<>();

  public RegistryFactory register(String kind, Supplier<Notifier> maker) {
    makers.put(kind, maker);
    return this;
  }

  public Notifier create(String kind) {
    Supplier<Notifier> maker = makers.get(kind);
    if (maker == null) {
      throw new IllegalArgumentException("unknown kind: " + kind);
    }
    return maker.get();
  }
}
// ANCHOR_END: registry-factory
