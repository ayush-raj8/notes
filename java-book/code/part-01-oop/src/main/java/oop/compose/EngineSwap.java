package oop.compose;

// ANCHOR: engine-swap
/** Has-a: the part is chosen (and replaced) at run time through an interface. */
public final class EngineSwap {
  interface Engine {
    String start();
  }

  record Petrol() implements Engine {
    public String start() {
      return "vroom";
    }
  }

  record Electric() implements Engine {
    public String start() {
      return "hum";
    }
  }

  static final class Car {
    private Engine engine;

    Car(Engine engine) {
      this.engine = engine;
    }

    void swap(Engine next) {
      this.engine = next;
    }

    String drive() {
      return "car:" + engine.start();
    }
  }

  public static void main(String[] args) {
    Car car = new Car(new Petrol());
    System.out.println(car.drive());
    car.swap(new Electric());
    System.out.println(car.drive());
  }
}
// ANCHOR_END: engine-swap
