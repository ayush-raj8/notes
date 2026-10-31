package solid.ocp;

import solid.ocp.rules.PercentDiscount;

/** Records are final: extension by inheritance is closed; extend by implementing Discount. */
public class OcpFinalExtendBroken extends PercentDiscount {
  public OcpFinalExtendBroken() {
    super(10);
  }
}
