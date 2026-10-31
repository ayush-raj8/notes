import lld.atm.*;

public class AtmNeverThrownBroken {
  public static void main(String[] args) {
    Atm atm = new Atm(new InMemoryBank(), new CashDispenser(1, 1, 1));
    try {
      atm.balance();
    } catch (InsufficientFundsException e) {
      System.out.println("never");
    }
  }
}
