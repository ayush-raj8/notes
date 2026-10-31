package solid.srp;

import solid.srp.parts.InvoiceFormatter;

/** text() is an instance method of the extracted class. */
public final class SrpFormatterStaticBroken {
  public static void main(String[] args) {
    System.out.println(InvoiceFormatter.text("A", 100));
  }
}
