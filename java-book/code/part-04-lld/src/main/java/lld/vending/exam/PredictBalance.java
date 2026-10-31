package lld.vending.exam;

import lld.vending.*;

// ANCHOR: predict-balance
public class PredictBalance {
  public static void main(String[] args) {
    VendingMachine m = VendingDemo.machine();
    m.insert(Coin.DOLLAR);
    m.insert(Coin.QUARTER);
    m.insert(Coin.QUARTER);
    System.out.println(m.balance());
    System.out.println(m.select("A1"));
    System.out.println(m.balance());
    System.out.println(m.select("A1"));
  }
}
// ANCHOR_END: predict-balance
