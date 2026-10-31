package patterns.behavioral.strategy;

// ANCHOR: op
/** Enum strategies: each constant carries its own algorithm. */
public enum Op {
  ADD("+") {
    @Override
    public int apply(int a, int b) {
      return a + b;
    }
  },
  MUL("*") {
    @Override
    public int apply(int a, int b) {
      return a * b;
    }
  },
  MAX("max") {
    @Override
    public int apply(int a, int b) {
      return Math.max(a, b);
    }
  };

  private final String symbol;

  Op(String symbol) {
    this.symbol = symbol;
  }

  public String symbol() {
    return symbol;
  }

  public abstract int apply(int a, int b);
}
// ANCHOR_END: op
