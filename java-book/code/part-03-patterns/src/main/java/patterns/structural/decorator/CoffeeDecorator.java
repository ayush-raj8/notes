package patterns.structural.decorator;

// ANCHOR: coffee-decorator
/** Decorator base: IS-A Coffee and HAS-A Coffee; forwards everything by default. */
public abstract class CoffeeDecorator implements Coffee {
  private final Coffee wrapped;

  protected CoffeeDecorator(Coffee wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public int cost() {
    return wrapped.cost();
  }

  @Override
  public String description() {
    return wrapped.description();
  }
}
// ANCHOR_END: coffee-decorator
