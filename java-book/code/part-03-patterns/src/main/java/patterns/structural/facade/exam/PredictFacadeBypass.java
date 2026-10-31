package patterns.structural.facade.exam;

import patterns.structural.facade.Inventory;
import patterns.structural.facade.OrderFacade;
import patterns.structural.facade.Payment;
import patterns.structural.facade.Shipping;

// ANCHOR: predict-facade-bypass
public final class PredictFacadeBypass {
  public static void main(String[] args) {
    Inventory inv = new Inventory();
    inv.reserve("pen");
    OrderFacade f = new OrderFacade(inv, new Payment(), new Shipping());
    System.out.println(f.place("pen", 100));
    System.out.println(inv.left("pen"));
  }
}
// ANCHOR_END: predict-facade-bypass
