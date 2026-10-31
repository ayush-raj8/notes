package solid.ocp;

import solid.ocp.rules.Discount;

/** A new rule must implement amount(). */
public final class OcpMissingMethodBroken implements Discount {
  public long total(long cents) {
    return cents;
  }
}
