import lld.atm.Bank;

public class AtmMissingMethodsBroken implements Bank {
  public boolean verifyPin(String card, String pin) { return true; }
}
