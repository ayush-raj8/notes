package patterns.behavioral.strategy;

/** Intentionally invalid: a lambda can only capture effectively final locals. */
public final class StrategyLambdaCaptureBroken {
  public static void main(String[] args) {
    long fee = 5;
    fee = 10;
    PriceStrategy s = base -> base + fee;
  }
}
