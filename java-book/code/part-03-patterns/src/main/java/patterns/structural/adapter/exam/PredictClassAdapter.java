package patterns.structural.adapter.exam;

import patterns.structural.adapter.LegacyPrinter;
import patterns.structural.adapter.Printer;
import patterns.structural.adapter.PrinterClassAdapter;

// ANCHOR: predict-class-adapter
public final class PredictClassAdapter {
  public static void main(String[] args) {
    Printer p = new PrinterClassAdapter();
    System.out.println(p instanceof LegacyPrinter);
    System.out.println(((LegacyPrinter) p).printLegacy("x"));
    System.out.println(p.print("y"));
  }
}
// ANCHOR_END: predict-class-adapter
