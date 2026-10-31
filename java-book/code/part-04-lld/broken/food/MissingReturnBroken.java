import lld.food.OrderStatus;

public class MissingReturnBroken {
  static String label(OrderStatus s) {
    if (s == OrderStatus.DELIVERED) {
      return "done";
    } else if (s == OrderStatus.CANCELLED) {
      return "void";
    }
  }
}
