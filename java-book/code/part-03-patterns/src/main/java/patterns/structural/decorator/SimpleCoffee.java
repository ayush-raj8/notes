package patterns.structural.decorator;

// ANCHOR: simple-coffee
public final class SimpleCoffee implements Coffee {
  @Override
  public int cost() {
    return 200;
  }

  @Override
  public String description() {
    return "coffee";
  }
}
// ANCHOR_END: simple-coffee
