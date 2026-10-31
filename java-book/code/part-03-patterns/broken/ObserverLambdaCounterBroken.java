interface PriceObserver {
  void onPrice(String symbol, int price);
}

public final class ObserverLambdaCounterBroken {
  public static void main(String[] args) {
    int count = 0;
    PriceObserver counter = (s, p) -> count++;
    counter.onPrice("X", 1);
  }
}
