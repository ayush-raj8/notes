package patterns.behavioral.strategy;

/** Intentionally invalid: enum constants are the only instances. */
public final class StrategyEnumNewBroken {
  public static void main(String[] args) {
    Op op = new Op("^");
  }
}
