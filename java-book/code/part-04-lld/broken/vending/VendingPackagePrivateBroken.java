import lld.vending.Coin;
import lld.vending.VendingDemo;
import lld.vending.VendingMachine;

public class VendingPackagePrivateBroken {
  public static void main(String[] args) {
    VendingMachine m = VendingDemo.machine();
    m.deposit(Coin.DIME);
  }
}
