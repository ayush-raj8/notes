package solid.lsp.gotchas;

import java.util.HashMap;
import java.util.Map;
import solid.lsp.accounts.before.Account;
import solid.lsp.accounts.before.FeeAccount;

public final class LspGotchas {
  // ANCHOR: g1-bird
  static class Bird {
    String fly() {
      return "flap";
    }
  }

  static class Penguin extends Bird {
    @Override
    String fly() {
      throw new UnsupportedOperationException("penguins cannot fly");
    }
  }
  // ANCHOR_END: g1-bird

  // ANCHOR: g2-instanceof
  /** A client that special-cases a subtype has already admitted the subtype is not substitutable. */
  static long safeMove(Account from, Account to, long amount) {
    long extra = from instanceof FeeAccount ? 1 : 0;
    from.withdraw(amount);
    to.deposit(amount + extra);
    return from.balance() + to.balance();
  }
  // ANCHOR_END: g2-instanceof

  // ANCHOR: g3-equals
  static class Point {
    final int x;
    final int y;

    Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Point p && p.x == x && p.y == y;
    }

    @Override
    public int hashCode() {
      return 31 * x + y;
    }
  }

  static class ColorPoint extends Point {
    final String color;

    ColorPoint(int x, int y, String color) {
      super(x, y);
      this.color = color;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof ColorPoint c && super.equals(o) && c.color.equals(color);
    }
  }
  // ANCHOR_END: g3-equals

  // ANCHOR: g4-mutable-sub
  static class MovablePoint extends Point {
    int mx;

    MovablePoint(int x, int y) {
      super(x, y);
      mx = x;
    }

    @Override
    public int hashCode() {
      return 31 * mx + y; // history constraint of Point (hash never changes) is broken
    }
  }
  // ANCHOR_END: g4-mutable-sub

  // ANCHOR: g5-assert
  /** assert is off unless the JVM runs with -ea, so this "contract" does not exist by default. */
  static long withdrawChecked(long balance, long amount) {
    assert amount > 0 : "amount must be positive";
    return balance - amount;
  }
  // ANCHOR_END: g5-assert

  public static void main(String[] args) {
    // ANCHOR: g-main
    try {
      new Penguin().fly();
    } catch (UnsupportedOperationException e) {
      System.out.println("G1 " + e.getMessage());
    }

    Account a = new FeeAccount(1000);
    System.out.println("G2 total=" + safeMove(a, new Account(0), 100));

    Point p = new Point(1, 2);
    ColorPoint c = new ColorPoint(1, 2, "red");
    System.out.println("G3 p.equals(c)=" + p.equals(c) + " c.equals(p)=" + c.equals(p));

    MovablePoint m = new MovablePoint(1, 2);
    Map<Point, String> map = new HashMap<>();
    map.put(m, "found");
    m.mx = 9;
    System.out.println("G4 " + map.get(m));

    System.out.println("G5 balance=" + withdrawChecked(100, -5));
    // ANCHOR_END: g-main
  }
}
