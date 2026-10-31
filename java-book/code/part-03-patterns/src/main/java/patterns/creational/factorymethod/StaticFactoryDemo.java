package patterns.creational.factorymethod;

// ANCHOR: static-factory-demo
public final class StaticFactoryDemo {
  public static void main(String[] args) {
    System.out.println(Temperature.ofFahrenheit(32).celsius());
    System.out.println(Temperature.ofFahrenheit(212).celsius());
    System.out.println(Temperature.freezing() == Temperature.ofCelsius(0));
    System.out.println(Temperature.ofCelsius(5) == Temperature.ofCelsius(5));
  }
}
// ANCHOR_END: static-factory-demo
