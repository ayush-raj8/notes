package patterns.structural.decorator;

// ANCHOR: milk
public final class Milk extends CoffeeDecorator {
  public Milk(Coffee wrapped) {
    super(wrapped);
  }

  @Override
  public int cost() {
    return super.cost() + 50;
  }

  @Override
  public String description() {
    return super.description() + ", milk";
  }
}
// ANCHOR_END: milk
