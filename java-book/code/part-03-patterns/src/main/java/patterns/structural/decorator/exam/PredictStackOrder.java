package patterns.structural.decorator.exam;

import patterns.structural.decorator.Coffee;
import patterns.structural.decorator.Milk;
import patterns.structural.decorator.SimpleCoffee;
import patterns.structural.decorator.Sugar;

// ANCHOR: predict-stack-order
public final class PredictStackOrder {
  public static void main(String[] args) {
    Coffee a = new Milk(new Sugar(new SimpleCoffee()));
    Coffee b = new Sugar(new Milk(new SimpleCoffee()));
    System.out.println(a.description());
    System.out.println(b.description());
    System.out.println(a.cost() == b.cost());
  }
}
// ANCHOR_END: predict-stack-order
