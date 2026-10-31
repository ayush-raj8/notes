package patterns.structural.adapter;

/** Intentionally invalid: the adapter forgets to implement the target method. */
public final class AdapterMissingMethodBroken implements Printer {
  private final LegacyPrinter adaptee = new LegacyPrinter();
}
