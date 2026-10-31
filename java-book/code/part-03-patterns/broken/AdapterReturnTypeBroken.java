package patterns.structural.adapter;

/** Intentionally invalid: an adapter must convert, not silently change, the return type. */
public final class AdapterReturnTypeBroken {
  private final LegacyPrinter adaptee = new LegacyPrinter();

  public int print(String text) {
    return adaptee.printLegacy(text);
  }
}
