package patterns.behavioral.strategy;

// ANCHOR: strategy-demo
public final class StrategyDemo {
  public static void main(String[] args) {
    Cart cart = new Cart(new NoDiscount()).add(1000).add(500);
    System.out.println("none    " + cart.total());
    cart.setStrategy(new PercentOff(20));
    System.out.println("20%     " + cart.total());
    cart.setStrategy(new FlatOff(300));
    System.out.println("flat300 " + cart.total());
    cart.setStrategy(base -> base > 1000 ? base - 100 : base);
    System.out.println("lambda  " + cart.total());
    cart.setStrategy(Math::abs);
    System.out.println("mref    " + cart.total());
  }
}
// ANCHOR_END: strategy-demo
