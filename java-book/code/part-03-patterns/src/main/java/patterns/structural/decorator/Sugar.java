package patterns.structural.decorator;

// ANCHOR: sugar
public final class Sugar extends CoffeeDecorator {
  public Sugar(Coffee wrapped) {
    super(wrapped);
  }

  @Override
  public int cost() {
    return super.cost() + 20;
  }

  @Override
  public String description() {
    return super.description() + ", sugar";
  }
}
// ANCHOR_END: sugar
