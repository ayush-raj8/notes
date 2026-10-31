import lld.atm.*;

public class AtmUnreportedBroken {
  public static void main(String[] args) {
    Atm atm = new Atm(new InMemoryBank(), new CashDispenser(1, 1, 1));
    atm.withdraw(100);
  }
}
