package patterns.structural.adapter;

/** Intentionally invalid: the adaptee's method is invisible through the target type. */
public final class AdapterAdapteeMethodBroken {
  public static void main(String[] args) {
    Printer p = new PrinterObjectAdapter(new LegacyPrinter());
    System.out.println(p.printLegacy("x"));
  }
}
