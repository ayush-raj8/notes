package solid.srp.exam;

// ANCHOR: predict-rounding
import solid.srp.parts.InvoiceFormatter;

public final class PredictFormatterRounding {
  public static void main(String[] args) {
    InvoiceFormatter f = new InvoiceFormatter();
    for (long cents : new long[] {149, 150, 250, 350}) {
      System.out.println(f.text("R", cents));
    }
  }
}
// ANCHOR_END: predict-rounding
