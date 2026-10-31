package patterns.structural.decorator.exam;

import patterns.structural.decorator.Coffee;
import patterns.structural.decorator.CoffeeDecorator;
import patterns.structural.decorator.Milk;
import patterns.structural.decorator.SimpleCoffee;

// ANCHOR: predict-instanceof-after-wrap
public final class PredictInstanceofAfterWrap {
  public static void main(String[] args) {
    Coffee c = new Milk(new SimpleCoffee());
    System.out.println(c instanceof SimpleCoffee);
    System.out.println(c instanceof CoffeeDecorator);
    System.out.println(c instanceof Coffee);
    System.out.println(c.getClass().getSimpleName());
  }
}
// ANCHOR_END: predict-instanceof-after-wrap
