package solid.principles;

import solid.principles.demeter.Address;
import solid.principles.demeter.Customer;
import solid.principles.demeter.Order;

/** Record components are private: reach through the accessor, or better, ask Order. */
public final class PrinDemeterPrivateBroken {
  public static void main(String[] args) {
    Order o = new Order("A", new Customer("Asha", new Address("Pune")));
    System.out.println(o.customer.address().city());
  }
}
