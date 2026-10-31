package solid.ocp;

import solid.ocp.rules.Discount;

/** A second abstract method turns the rule into a non-functional interface. */
public final class OcpNotFunctionalBroken {
  interface LabelledDiscount extends Discount {
    String label();
  }

  public static void main(String[] args) {
    LabelledDiscount d = cents -> cents / 2;
  }
}
