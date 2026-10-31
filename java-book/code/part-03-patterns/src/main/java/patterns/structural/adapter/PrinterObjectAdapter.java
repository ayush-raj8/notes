package patterns.structural.adapter;

import java.util.Objects;

// ANCHOR: printer-object-adapter
/** Object adapter: wraps (has-a) the adaptee, so any LegacyPrinter subtype works. */
public final class PrinterObjectAdapter implements Printer {
  private final LegacyPrinter adaptee;

  public PrinterObjectAdapter(LegacyPrinter adaptee) {
    this.adaptee = Objects.requireNonNull(adaptee, "adaptee");
  }

  @Override
  public String print(String text) {
    return adaptee.printLegacy(text);
  }
}
// ANCHOR_END: printer-object-adapter
