package patterns.behavioral.strategy.exam;

import patterns.behavioral.strategy.Cart;
import patterns.behavioral.strategy.FlatOff;
import patterns.behavioral.strategy.NoDiscount;
import patterns.behavioral.strategy.PercentOff;

// ANCHOR: predict-swap-strategy
public final class PredictSwapStrategy {
  public static void main(String[] args) {
    Cart cart = new Cart(new NoDiscount()).add(200).add(300);
    System.out.println(cart.total());
    cart.setStrategy(new PercentOff(10));
    System.out.println(cart.total());
    cart.add(500);
    System.out.println(cart.total());
    cart.setStrategy(new FlatOff(2000));
    System.out.println(cart.total());
  }
}
// ANCHOR_END: predict-swap-strategy
