import lld.atm.*;

public class AtmCatchOrderBroken {
  public static void main(String[] args) {
    Atm atm = new Atm(new InMemoryBank(), new CashDispenser(1, 1, 1));
    try {
      atm.withdraw(100);
    } catch (Exception e) {
      System.out.println("generic");
    } catch (InsufficientFundsException e) {
      System.out.println("funds");
    }
  }
}
