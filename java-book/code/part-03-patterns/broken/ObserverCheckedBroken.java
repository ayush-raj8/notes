import java.io.IOException;

interface PriceObserver {
  void onPrice(String symbol, int price);
}

public final class ObserverCheckedBroken {
  public static void main(String[] args) {
    PriceObserver o =
        (s, p) -> {
          throw new IOException("disk full");
        };
    o.onPrice("X", 1);
  }
}
