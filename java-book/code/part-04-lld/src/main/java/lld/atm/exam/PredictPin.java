package lld.atm.exam;

import lld.atm.*;

// ANCHOR: predict-pin
public class PredictPin {
  public static void main(String[] args) {
    InMemoryBank bank = new InMemoryBank();
    bank.open("C1", "1111", 100);
    Atm atm = new Atm(bank, new CashDispenser(1, 1, 1));
    atm.insertCard("C1");
    System.out.println(atm.enterPin("0"));
    System.out.println(atm.enterPin("1"));
    System.out.println(atm.enterPin("2") + " " + atm.state());
    try {
      atm.insertCard("C1");
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: predict-pin
