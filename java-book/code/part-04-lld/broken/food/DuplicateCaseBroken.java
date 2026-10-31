import lld.food.OrderStatus;

public class DuplicateCaseBroken {
  static int rank(OrderStatus s) {
    switch (s) {
      case PLACED:
        return 1;
      case READY:
        return 4;
      case PLACED:
        return 2;
      default:
        return 0;
    }
  }
}
