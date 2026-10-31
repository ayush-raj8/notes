package patterns.creational.abstractfactory;

// ANCHOR: ui-factories
public final class UiFactories {
  private UiFactories() {}

  /** Picks ONE family once; the client never names a concrete product. */
  public static UiFactory of(String theme) {
    return switch (theme) {
      case "light" -> new LightFactory();
      case "dark" -> new DarkFactory();
      default -> throw new IllegalArgumentException("unknown theme: " + theme);
    };
  }
}
// ANCHOR_END: ui-factories
