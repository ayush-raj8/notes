package oop.abstracts;

// ANCHOR: inheritance-demo
class Vehicle {
  protected final String id;
  private int odometer;

  Vehicle(String id) {
    this.id = id;
  }

  void drive(int km) {
    odometer += km;
  }

  int odometer() {
    return odometer;
  }

  String describe() {
    return "vehicle " + id;
  }
}

class Truck extends Vehicle {
  private final int loadKg;

  Truck(String id, int loadKg) {
    super(id);
    this.loadKg = loadKg;
  }

  @Override
  String describe() {
    return "truck " + id + " load=" + loadKg;
  }
}

/** IS-A: a Truck is a Vehicle. It inherits non-private members and the Object root. */
public final class InheritanceDemo {
  public static void main(String[] args) {
    Truck t = new Truck("T1", 900);
    t.drive(40);
    System.out.println(t.describe());
    System.out.println(t.odometer());
    Vehicle v = t; // IS-A: widening reference conversion
    System.out.println(v.describe());
    for (Class<?> c = t.getClass(); c != null; c = c.getSuperclass()) {
      System.out.println(c.getSimpleName());
    }
  }
}
// ANCHOR_END: inheritance-demo
