package lld.vending;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// ANCHOR: coin-bank
/** Coins held by the machine. {@code withdraw} searches, so it succeeds whenever any exact change exists. */
public final class CoinBank {
  private static final Coin[] DESCENDING = {Coin.DOLLAR, Coin.QUARTER, Coin.DIME, Coin.NICKEL};
  private final Map<Coin, Integer> counts = new EnumMap<>(Coin.class);

  public CoinBank() {
    for (Coin c : Coin.values()) {
      counts.put(c, 0);
    }
  }

  public void deposit(Coin c) {
    counts.merge(c, 1, Integer::sum);
  }

  public int count(Coin c) {
    return counts.get(c);
  }

  public Optional<List<Coin>> withdraw(int cents) {
    List<Coin> picked = new ArrayList<>();
    if (!solve(0, cents, picked)) {
      return Optional.empty();
    }
    for (Coin c : picked) {
      counts.merge(c, -1, Integer::sum);
    }
    return Optional.of(picked);
  }

  private boolean solve(int index, int remaining, List<Coin> picked) {
    if (remaining == 0) {
      return true;
    }
    if (index == DESCENDING.length) {
      return false;
    }
    Coin coin = DESCENDING[index];
    int most = Math.min(counts.get(coin), remaining / coin.cents());
    for (int n = most; n >= 0; n--) {
      for (int i = 0; i < n; i++) {
        picked.add(coin);
      }
      if (solve(index + 1, remaining - n * coin.cents(), picked)) {
        return true;
      }
      for (int i = 0; i < n; i++) {
        picked.remove(picked.size() - 1);
      }
    }
    return false;
  }
}
// ANCHOR_END: coin-bank
