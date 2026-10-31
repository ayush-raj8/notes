package lld.atm.exam;

import lld.atm.*;

// ANCHOR: predict-debit-order
public class PredictDebitOrder {
  public static void main(String[] args) throws Exception {
    InMemoryBank bank = new InMemoryBank();
    bank.open("C1", "1111", 300);
    CashDispenser cash = new CashDispenser(2, 2, 2);
    Atm atm = new Atm(bank, cash);
    atm.insertCard("C1");
    atm.enterPin("1111");
    try {
      atm.withdraw(500);
    } catch (InsufficientFundsException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(cash.total() + " " + atm.balance());
    System.out.println(atm.withdraw(300) + " " + cash.total() + " " + atm.balance());
  }
}
// ANCHOR_END: predict-debit-order
