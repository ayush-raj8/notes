package patterns.behavioral.strategy;

/** Intentionally invalid: the lambda body must produce a long. */
public final class StrategyWrongReturnBroken {
  public static void main(String[] args) {
    PriceStrategy s = base -> "free";
  }
}
