package patterns.behavioral.strategy.exam;

import patterns.behavioral.strategy.Cart;
import patterns.behavioral.strategy.PriceStrategy;

// ANCHOR: predict-lambda-strategy
public final class PredictLambdaStrategy {
  public static void main(String[] args) {
    PriceStrategy half = base -> base / 2;
    PriceStrategy plusTax = base -> base + base / 10;
    Cart cart = new Cart(half).add(101);
    System.out.println(cart.total());
    cart.setStrategy(plusTax);
    System.out.println(cart.total());
    cart.setStrategy(base -> plusTax.price(half.price(base)));
    System.out.println(cart.total());
  }
}
// ANCHOR_END: predict-lambda-strategy
