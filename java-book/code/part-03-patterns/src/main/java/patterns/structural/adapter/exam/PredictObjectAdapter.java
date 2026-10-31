package patterns.structural.adapter.exam;

import patterns.structural.adapter.LegacyPrinter;
import patterns.structural.adapter.Printer;
import patterns.structural.adapter.PrinterObjectAdapter;

// ANCHOR: predict-object-adapter
public final class PredictObjectAdapter {
  public static void main(String[] args) {
    LegacyPrinter legacy = new LegacyPrinter();
    Printer p = new PrinterObjectAdapter(legacy);
    System.out.println(p.print("a"));
    System.out.println(legacy.printLegacy("b"));
    System.out.println(p instanceof LegacyPrinter);
  }
}
// ANCHOR_END: predict-object-adapter
