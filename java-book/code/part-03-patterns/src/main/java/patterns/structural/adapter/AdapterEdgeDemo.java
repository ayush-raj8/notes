package patterns.structural.adapter;

// ANCHOR: adapter-edge-demo
public final class AdapterEdgeDemo {
  /** An adaptee subtype: the object adapter accepts it, the class adapter cannot. */
  static final class FancyLegacyPrinter extends LegacyPrinter {
    @Override
    public String printLegacy(String text) {
      return "FANCY<" + text + ">";
    }
  }

  public static void main(String[] args) {
    Printer viaSubtype = new PrinterObjectAdapter(new FancyLegacyPrinter());
    System.out.println(viaSubtype.print("x"));

    try {
      new PrinterObjectAdapter(null);
    } catch (NullPointerException e) {
      System.out.println("NPE: " + e.getMessage());
    }

    PrinterClassAdapter ca = new PrinterClassAdapter();
    LegacyPrinter asAdaptee = ca;
    Printer asTarget = ca;
    System.out.println(asAdaptee.printLegacy("a") + " " + asTarget.print("b"));
  }
}
// ANCHOR_END: adapter-edge-demo
