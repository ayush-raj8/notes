package patterns.structural.decorator;

/** Intentionally invalid: the decorator base class is abstract. */
public final class DecoratorAbstractNewBroken {
  public static void main(String[] args) {
    Coffee c = new CoffeeDecorator(new SimpleCoffee());
  }
}
