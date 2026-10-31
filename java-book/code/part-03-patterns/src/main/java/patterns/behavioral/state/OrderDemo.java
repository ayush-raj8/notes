package patterns.behavioral.state;

public final class OrderDemo {
  // ANCHOR: order-demo
  public static void main(String[] args) {
    Order o = new Order();
    System.out.println(o.state());
    o.pay();
    System.out.println(o.state());
    o.ship();
    System.out.println(o.state());
    try {
      o.cancel();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(o.state());
  }
  // ANCHOR_END: order-demo
}
