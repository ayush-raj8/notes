package solid.srp.solutions;

import solid.srp.parts.InvoiceFormatter;

/** Exercise 3: a second formatter changes layout without touching pricing or storage. */
public final class SolutionCsvFormatter {
  static String csv(String id, long totalCents) {
    return id + "," + totalCents / 100 + "." + String.format("%02d", totalCents % 100);
  }

  public static void main(String[] args) {
    System.out.println(csv("INV-1", 11999));
    System.out.println(new InvoiceFormatter().text("INV-1", 11999));
  }
}
