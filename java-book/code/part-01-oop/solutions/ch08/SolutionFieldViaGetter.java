package oop.compare.solutions;

/** Exercise 3: avoid field hiding; one field, one getter. */
public final class SolutionFieldViaGetter {
  static class Vehicle {
    private final String wheels;

    Vehicle(String wheels) {
      this.wheels = wheels;
    }

    String wheels() {
      return wheels;
    }
  }

  static class Bike extends Vehicle {
    Bike() {
      super("2");
    }
  }

  public static void main(String[] args) {
    Vehicle v = new Bike();
    System.out.println(v.wheels());
  }
}
