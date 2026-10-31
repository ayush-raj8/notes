package solid.principles.demeter;

public final class DemeterDemo {
  // ANCHOR: demeter-before
  /** Knows Order, Customer and Address: a change in any of them breaks this line. */
  public static String labelBefore(Order o) {
    return o.customer().address().city().toUpperCase();
  }
  // ANCHOR_END: demeter-before

  // ANCHOR: demeter-after
  /** Talks only to Order. */
  public static String labelAfter(Order o) {
    return o.shippingCity().toUpperCase();
  }
  // ANCHOR_END: demeter-after

  public static void main(String[] args) {
    // ANCHOR: demeter-main
    Order withAddress = new Order("A", new Customer("Asha", new Address("Pune")));
    Order noAddress = new Order("B", new Customer("Ben", null));
    System.out.println("before " + labelBefore(withAddress));
    try {
      System.out.println("before " + labelBefore(noAddress));
    } catch (NullPointerException e) {
      System.out.println("before NullPointerException");
    }
    System.out.println("after  " + labelAfter(withAddress));
    System.out.println("after  " + labelAfter(noAddress));
    // ANCHOR_END: demeter-main
  }
}
