package patterns.behavioral.strategy.exam;

import patterns.behavioral.strategy.Cart;
import patterns.behavioral.strategy.PriceStrategy;

// ANCHOR: predict-captured-state
public final class PredictCapturedState {
  public static void main(String[] args) {
    long[] total = {0};
    PriceStrategy audit =
        base -> {
          total[0] += base;
          return base;
        };
    Cart a = new Cart(audit).add(10);
    Cart b = new Cart(audit).add(20).add(5);
    a.total();
    b.total();
    b.total();
    System.out.println(total[0]);
  }
}
// ANCHOR_END: predict-captured-state
