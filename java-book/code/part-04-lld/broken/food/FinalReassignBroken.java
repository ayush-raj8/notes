import lld.food.Order;

public class FinalReassignBroken {
  static long total(Order o) {
    final long total = o.subtotalCents();
    total = total + o.deliveryFeeCents();
    return total;
  }
}
