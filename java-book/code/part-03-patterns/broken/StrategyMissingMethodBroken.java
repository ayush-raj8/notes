package patterns.behavioral.strategy;

/** Intentionally invalid: a concrete strategy must implement price(long). */
public final class StrategyMissingMethodBroken implements PriceStrategy {}
