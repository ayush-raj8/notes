package lld.atm;

// ANCHOR: atm-demo
public final class AtmDemo {
  private AtmDemo() {}

  public static void main(String[] args) throws Exception {
    InMemoryBank bank = new InMemoryBank();
    bank.open("4111", "1234", 5000);
    bank.open("4222", "9999", 300);
    bank.open("4333", "0000", 100);
    CashDispenser cash = new CashDispenser(5, 5, 10);
    Atm atm = new Atm(bank, cash);

    atm.insertCard("4111");
    System.out.println(atm.enterPin("0000") + " then " + atm.enterPin("1234"));
    System.out.println("balance " + atm.balance());
    System.out.println("notes " + atm.withdraw(1300) + " balance " + atm.balance());
    try {
      atm.withdraw(150);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    try {
      atm.withdraw(3500);
    } catch (CashUnavailableException e) {
      System.out.println(e.getMessage() + "; balance still " + atm.balance());
    }
    atm.eject();

    atm.insertCard("4222");
    atm.enterPin("9999");
    int before = cash.total();
    try {
      atm.withdraw(500);
    } catch (InsufficientFundsException e) {
      System.out.println(e.getMessage() + "; cash unchanged " + (before == cash.total()));
    }
    atm.eject();

    atm.insertCard("4333");
    System.out.println(atm.enterPin("1") + " " + atm.enterPin("2") + " " + atm.enterPin("3"));
    try {
      atm.insertCard("4333");
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage() + ", state " + atm.state());
    }
  }
}
// ANCHOR_END: atm-demo
