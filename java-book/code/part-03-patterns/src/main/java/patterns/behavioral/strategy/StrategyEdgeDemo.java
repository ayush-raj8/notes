package patterns.behavioral.strategy;

// ANCHOR: strategy-edge-demo
public final class StrategyEdgeDemo {
  public static void main(String[] args) {
    try {
      new Cart(null);
    } catch (NullPointerException e) {
      System.out.println("NPE: " + e.getMessage());
    }
    try {
      new PercentOff(150);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(new Cart(new FlatOff(5000)).add(100).total());
    System.out.println(new Cart(new PercentOff(100)).add(100).total());
    int[] calls = {0};
    PriceStrategy counting =
        base -> {
          calls[0]++;
          return base;
        };
    Cart a = new Cart(counting).add(1);
    Cart b = new Cart(counting).add(2);
    a.total();
    a.total();
    b.total();
    System.out.println(calls[0]);
  }
}
// ANCHOR_END: strategy-edge-demo
