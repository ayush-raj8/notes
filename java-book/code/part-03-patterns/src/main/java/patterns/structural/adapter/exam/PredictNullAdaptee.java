package patterns.structural.adapter.exam;

import patterns.structural.adapter.PrinterObjectAdapter;

// ANCHOR: predict-null-adaptee
public final class PredictNullAdaptee {
  public static void main(String[] args) {
    try {
      new PrinterObjectAdapter(null);
      System.out.println("created");
    } catch (NullPointerException e) {
      System.out.println("NPE " + e.getMessage());
    }
    System.out.println("end");
  }
}
// ANCHOR_END: predict-null-adaptee
