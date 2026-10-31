package oop.compose.exam;

// ANCHOR: predict-swap-engine
public final class PredictSwapEngine {
  interface Gear {
    String name();
  }

  static final class Car {
    Gear gear = () -> "manual";

    String shift() {
      return gear.name();
    }
  }

  public static void main(String[] args) {
    Car a = new Car();
    Car b = new Car();
    a.gear = () -> "auto";
    System.out.println(a.shift() + " " + b.shift());
  }
}
// ANCHOR_END: predict-swap-engine
