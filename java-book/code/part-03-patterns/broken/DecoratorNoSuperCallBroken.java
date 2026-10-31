package patterns.structural.decorator;

/** Intentionally invalid: the base has no no-arg constructor to call implicitly. */
public final class DecoratorNoSuperCallBroken extends CoffeeDecorator {
  public DecoratorNoSuperCallBroken() {}
}
