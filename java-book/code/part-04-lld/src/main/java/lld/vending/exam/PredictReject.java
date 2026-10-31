package lld.vending.exam;

import lld.vending.*;

// ANCHOR: predict-reject
public class PredictReject {
  public static void main(String[] args) {
    VendingMachine m = VendingDemo.machine();
    System.out.println(m.select("A1"));
    m.insert(Coin.QUARTER);
    System.out.println(m.select("Z9"));
    System.out.println(m.select("B2"));
    System.out.println(m.balance());
  }
}
// ANCHOR_END: predict-reject
