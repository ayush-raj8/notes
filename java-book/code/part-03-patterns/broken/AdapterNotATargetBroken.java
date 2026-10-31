package patterns.structural.adapter;

/** Intentionally invalid: the adaptee is not a Printer. */
public final class AdapterNotATargetBroken {
  public static void main(String[] args) {
    Printer p = new LegacyPrinter();
  }
}
