package lld.vending.exam;

import lld.vending.*;

// ANCHOR: predict-switch
public class PredictSwitch {
  static String describe(SelectResult r) {
    return switch (r) {
      case SelectResult.Dispensed d -> d.product().name() + " change=" + d.change().size();
      case SelectResult.Rejected x -> "no: " + x.reason();
    };
  }

  public static void main(String[] args) {
    VendingMachine m = VendingDemo.machine();
    m.insert(Coin.DOLLAR);
    System.out.println(describe(m.select("B2")));
    m.insert(Coin.DOLLAR);
    System.out.println(describe(m.select("B2")));
  }
}
// ANCHOR_END: predict-switch
