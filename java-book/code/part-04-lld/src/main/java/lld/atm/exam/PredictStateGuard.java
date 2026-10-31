package lld.atm.exam;

import lld.atm.*;

// ANCHOR: predict-state-guard
public class PredictStateGuard {
  public static void main(String[] args) throws Exception {
    InMemoryBank bank = new InMemoryBank();
    bank.open("C1", "1111", 100);
    Atm atm = new Atm(bank, new CashDispenser(1, 1, 1));
    try {
      atm.withdraw(100);
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    atm.insertCard("C1");
    try {
      atm.balance();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    atm.eject();
    System.out.println(atm.state());
  }
}
// ANCHOR_END: predict-state-guard
