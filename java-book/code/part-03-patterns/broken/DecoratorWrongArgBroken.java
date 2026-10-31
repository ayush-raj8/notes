package patterns.structural.decorator;

/** Intentionally invalid: a decorator wraps a Coffee, not a String. */
public final class DecoratorWrongArgBroken {
  public static void main(String[] args) {
    Coffee c = new Milk("espresso");
  }
}
