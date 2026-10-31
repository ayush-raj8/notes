package patterns.structural.adapter;

// ANCHOR: printer-class-adapter
/** Class adapter: inherits the adaptee (is-a) and implements the target. */
public final class PrinterClassAdapter extends LegacyPrinter implements Printer {
  @Override
  public String print(String text) {
    return printLegacy(text);
  }
}
// ANCHOR_END: printer-class-adapter
