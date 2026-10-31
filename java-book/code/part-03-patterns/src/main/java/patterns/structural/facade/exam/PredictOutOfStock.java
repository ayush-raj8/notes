package patterns.structural.facade.exam;

import patterns.structural.facade.Inventory;
import patterns.structural.facade.OrderFacade;
import patterns.structural.facade.Payment;
import patterns.structural.facade.Shipping;

// ANCHOR: predict-out-of-stock
public final class PredictOutOfStock {
  public static void main(String[] args) {
    OrderFacade f = new OrderFacade(new Inventory(), new Payment(), new Shipping());
    System.out.println(f.place("ink", 1));
    System.out.println(f.place("cap", 1));
    System.out.println(f.place("pen", 1));
    System.out.println(f.place("pen", 1));
  }
}
// ANCHOR_END: predict-out-of-stock
