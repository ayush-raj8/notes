package patterns.structural.adapter;

// ANCHOR: legacy-printer
/** Adaptee: existing class with an incompatible method name; we cannot change it. */
public class LegacyPrinter {
  public String printLegacy(String text) {
    return "LEGACY[" + text + "]";
  }
}
// ANCHOR_END: legacy-printer
