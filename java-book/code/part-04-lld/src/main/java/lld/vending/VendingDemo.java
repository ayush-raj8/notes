package lld.vending;

// ANCHOR: vending-demo
public final class VendingDemo {
  private VendingDemo() {}

  public static VendingMachine machine() {
    Inventory inv = new Inventory();
    inv.load(new Product("A1", "Cola", 125), 2);
    inv.load(new Product("B2", "Chips", 60), 1);
    CoinBank bank = new CoinBank();
    bank.deposit(Coin.QUARTER);
    bank.deposit(Coin.DIME);
    bank.deposit(Coin.DIME);
    bank.deposit(Coin.NICKEL);
    bank.deposit(Coin.NICKEL);
    return new VendingMachine(inv, bank);
  }

  public static void main(String[] args) {
    VendingMachine m = machine();
    System.out.println(m.select("A1") + " " + m.stateName());
    m.insert(Coin.DOLLAR);
    System.out.println(m.stateName() + " balance=" + m.balance());
    System.out.println(m.select("B2"));
    System.out.println(m.stateName() + " balance=" + m.balance());
    m.insert(Coin.DOLLAR);
    System.out.println(m.select("B2"));
    System.out.println("cancel -> " + m.cancel());
    m.insert(Coin.DIME);
    System.out.println("cancel -> " + m.cancel() + " " + m.stateName());
  }
}
// ANCHOR_END: vending-demo
