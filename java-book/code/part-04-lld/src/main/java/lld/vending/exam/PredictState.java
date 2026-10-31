package lld.vending.exam;

import lld.vending.*;

// ANCHOR: predict-state
public class PredictState {
  public static void main(String[] args) {
    VendingMachine m = VendingDemo.machine();
    System.out.println(m.stateName());
    m.insert(Coin.NICKEL);
    System.out.println(m.stateName() + " " + m.balance());
    System.out.println(m.cancel());
    System.out.println(m.stateName() + " " + m.balance());
    System.out.println(m.cancel());
  }
}
// ANCHOR_END: predict-state
