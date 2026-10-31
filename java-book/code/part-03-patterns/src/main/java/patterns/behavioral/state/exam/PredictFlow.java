package patterns.behavioral.state.exam;

import patterns.behavioral.state.Order;

// ANCHOR: predict-flow
public final class PredictFlow {
  public static void main(String[] args) {
    Order o = new Order();
    o.pay();
    o.cancel();
    System.out.println(o.state());
    try {
      o.pay();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(o.state());
  }
}
// ANCHOR_END: predict-flow
