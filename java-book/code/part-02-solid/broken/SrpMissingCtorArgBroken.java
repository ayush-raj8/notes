package solid.srp;

import solid.srp.after.InvoiceService;
import solid.srp.parts.InvoiceCalculator;

/** The service needs all four collaborators; no partial constructor exists. */
public final class SrpMissingCtorArgBroken {
  public static void main(String[] args) {
    InvoiceService svc = new InvoiceService(new InvoiceCalculator());
  }
}
