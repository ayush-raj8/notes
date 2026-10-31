package patterns.behavioral.strategy;

/** Intentionally invalid: the lambda must take exactly one long parameter. */
public final class StrategyLambdaTwoArgsBroken {
  public static void main(String[] args) {
    PriceStrategy s = (base, extra) -> base + extra;
  }
}
