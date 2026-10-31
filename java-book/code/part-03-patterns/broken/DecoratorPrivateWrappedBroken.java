package patterns.structural.decorator;

/** Intentionally invalid: the base keeps the wrapped component private. */
public final class DecoratorPrivateWrappedBroken extends CoffeeDecorator {
  public DecoratorPrivateWrappedBroken(Coffee wrapped) {
    super(wrapped);
  }

  @Override
  public int cost() {
    return wrapped.cost() + 5;
  }
}
