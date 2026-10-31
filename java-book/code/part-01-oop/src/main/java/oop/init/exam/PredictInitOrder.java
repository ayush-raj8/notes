package oop.init.exam;

// ANCHOR: predict-init-order
class Order {
  static {
    System.out.println("S");
  }

  {
    System.out.println("I");
  }

  Order() {
    System.out.println("C");
  }
}

public final class PredictInitOrder {
  public static void main(String[] args) {
    new Order();
    new Order();
  }
}
// ANCHOR_END: predict-init-order
