package patterns.structural.facade;

import patterns.structural.facade.subsystem.HomeTheaterFacade;

// ANCHOR: facade-demo
public final class FacadeDemo {
  public static void main(String[] args) {
    HomeTheaterFacade theater = new HomeTheaterFacade();
    theater.watchMovie("Up").forEach(System.out::println);
    System.out.println(theater.endMovie());

    Inventory inventory = new Inventory();
    OrderFacade orders = new OrderFacade(inventory, new Payment(), new Shipping());
    System.out.println(orders.place("pen", 500));
    System.out.println(orders.place("pen", 500));
    System.out.println(orders.place("ink", 20_000) + " left=" + inventory.left("ink"));
  }
}
// ANCHOR_END: facade-demo
