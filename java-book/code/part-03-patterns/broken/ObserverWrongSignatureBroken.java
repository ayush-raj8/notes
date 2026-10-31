interface PriceObserver {
  void onPrice(String symbol, int price);
}

public final class ObserverWrongSignatureBroken {
  static final class Logger implements PriceObserver {
    @Override
    public void onPrice(String symbol, long price) {}
  }

  public static void main(String[] args) {}
}
