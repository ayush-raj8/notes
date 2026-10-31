package oop.override;

// ANCHOR: override-ctor-not-inherited
class Vehicle {
  Vehicle(String id) {
    System.out.println("Vehicle(" + id + ")");
  }
}

class Car extends Vehicle {
  Car(String id) {
    super(id);
    System.out.println("Car(" + id + ")");
  }

  Car() {
    this("default");
  }
}

/** Constructors are not members, so they are neither inherited nor overridden; they chain. */
public final class OverrideCtorNotInherited {
  public static void main(String[] args) {
    new Car("a");
    new Car();
    System.out.println(Vehicle.class.getDeclaredConstructors().length);
    System.out.println(Car.class.getDeclaredConstructors().length);
  }
}
// ANCHOR_END: override-ctor-not-inherited
