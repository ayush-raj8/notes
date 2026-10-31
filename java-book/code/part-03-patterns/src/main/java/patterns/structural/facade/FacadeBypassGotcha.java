package patterns.structural.facade;

// ANCHOR: facade-bypass-gotcha
/** Gotcha: a facade does not hide public subsystem classes; callers can skip it. */
public final class FacadeBypassGotcha {
  public static void main(String[] args) {
    Inventory inventory = new Inventory();
    System.out.println(inventory.reserve("pen")); // reserved without payment or shipping
    System.out.println(inventory.left("pen"));
    OrderFacade orders = new OrderFacade(inventory, new Payment(), new Shipping());
    System.out.println(orders.place("pen", 100));
  }
}
// ANCHOR_END: facade-bypass-gotcha
