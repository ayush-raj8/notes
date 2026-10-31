interface PriceObserver {
  void onPrice(String symbol, int price);
}

public final class ObserverMissingMethodBroken implements PriceObserver {
  public static void main(String[] args) {
    PriceObserver o = new ObserverMissingMethodBroken();
  }
}
