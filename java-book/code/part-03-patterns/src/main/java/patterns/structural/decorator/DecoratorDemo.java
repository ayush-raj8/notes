package patterns.structural.decorator;

// ANCHOR: decorator-demo
public final class DecoratorDemo {
  public static void main(String[] args) {
    Coffee plain = new SimpleCoffee();
    Coffee order = new Sugar(new Milk(new Milk(plain)));
    System.out.println(plain.description() + " " + plain.cost());
    System.out.println(order.description() + " " + order.cost());

    TextSource raw = TextSource.of("  hello ");
    System.out.println("[" + raw.then(String::trim).then(String::toUpperCase).read() + "]");
    System.out.println("[" + raw.then(s -> new StringBuilder(s).reverse().toString()).then(String::trim).read() + "]");
  }
}
// ANCHOR_END: decorator-demo
