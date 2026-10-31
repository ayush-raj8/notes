package patterns.behavioral.strategy.exam;

import patterns.behavioral.strategy.Cart;
import patterns.behavioral.strategy.PercentOff;

// ANCHOR: predict-bad-percent
public final class PredictBadPercent {
  public static void main(String[] args) {
    Cart cart = new Cart(new PercentOff(100)).add(999);
    System.out.println(cart.total());
    try {
      cart.setStrategy(new PercentOff(-1));
      System.out.println("swapped");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    cart.setStrategy(new PercentOff(0));
    System.out.println(cart.total());
    try {
      cart.setStrategy(null);
    } catch (NullPointerException e) {
      System.out.println("NPE " + e.getMessage());
    }
    System.out.println(cart.total());
  }
}
// ANCHOR_END: predict-bad-percent
