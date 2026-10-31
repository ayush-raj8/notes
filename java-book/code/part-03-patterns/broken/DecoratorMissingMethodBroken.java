package patterns.structural.decorator;

/** Intentionally invalid: a Coffee implementation without description(). */
public final class DecoratorMissingMethodBroken implements Coffee {
  @Override
  public int cost() {
    return 10;
  }
}
