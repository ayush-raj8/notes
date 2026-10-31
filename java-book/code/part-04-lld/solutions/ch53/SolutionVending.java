import java.util.List;
import lld.vending.*;

/** Exercises 1-5 of chapter 53. */
public class SolutionVending {
  // Ex 1: render any result with an exhaustive pattern switch.
  static String render(SelectResult r) {
    return switch (r) {
      case SelectResult.Dispensed d -> "dispensed " + d.product().code() + " change " + d.change();
      case SelectResult.Rejected x -> "rejected " + x.reason();
    };
  }

  // Ex 2: total value of a list of coins.
  static int value(List<Coin> coins) {
    return coins.stream().mapToInt(Coin::cents).sum();
  }

  public static void main(String[] args) {
    VendingMachine m = VendingDemo.machine();
    m.insert(Coin.DOLLAR);
    System.out.println(render(m.select("B2")));

    // Ex 3: a machine with an empty float cannot change 25 for a 20 item.
    Inventory inv = new Inventory();
    inv.load(new Product("C3", "Gum", 20), 1);
    VendingMachine tight = new VendingMachine(inv, new CoinBank());
    tight.insert(Coin.QUARTER);
    System.out.println(render(tight.select("C3")));
    System.out.println(tight.cancel());

    // Ex 4: restock then buy.
    inv.load(new Product("C3", "Gum", 20), 2);
    tight.insert(Coin.DIME);
    tight.insert(Coin.DIME);
    System.out.println(render(tight.select("C3")));

    // Ex 5: refund equals what was inserted.
    VendingMachine r = VendingDemo.machine();
    r.insert(Coin.QUARTER);
    r.insert(Coin.NICKEL);
    System.out.println(value(r.cancel()));
  }
}
