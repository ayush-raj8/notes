package solid.principles.exam;

// ANCHOR: predict-demeter-null
import solid.principles.demeter.Address;
import solid.principles.demeter.Customer;
import solid.principles.demeter.Order;

public final class PredictDemeterNull {
  public static void main(String[] args) {
    Order o = new Order("A", new Customer("Ben", null));
    System.out.println(o.shippingCity());
    try {
      System.out.println(o.customer().address().city());
    } catch (NullPointerException e) {
      System.out.println("NPE");
    }
    Order p = new Order("B", new Customer("Asha", new Address("Pune")));
    System.out.println(p.shippingCity() + " " + p.customer().address().city());
  }
}
// ANCHOR_END: predict-demeter-null
