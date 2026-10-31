package patterns.structural.facade.exam;

import patterns.structural.facade.Inventory;
import patterns.structural.facade.OrderFacade;
import patterns.structural.facade.Payment;
import patterns.structural.facade.Shipping;

// ANCHOR: predict-order-rollback
public final class PredictOrderRollback {
  public static void main(String[] args) {
    Inventory inv = new Inventory();
    OrderFacade f = new OrderFacade(inv, new Payment(), new Shipping());
    System.out.println(f.place("pen", 99_999));
    System.out.println(inv.left("pen"));
    System.out.println(f.place("pen", 100));
    System.out.println(inv.left("pen"));
  }
}
// ANCHOR_END: predict-order-rollback
