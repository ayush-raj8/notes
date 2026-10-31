package oop.override;

// ANCHOR: override-abstract
abstract class Figure {
  abstract double area();

  String describe() {
    return getClass().getSimpleName() + " area=" + area();
  }
}

class Square extends Figure {
  private final double side;

  Square(double side) {
    this.side = side;
  }

  @Override
  double area() {
    return side * side;
  }
}

class Rect extends Figure {
  private final double w;
  private final double h;

  Rect(double w, double h) {
    this.w = w;
    this.h = h;
  }

  @Override
  double area() {
    return w * h;
  }
}

class Runner {
  String run() {
    return "run";
  }
}

/** A subclass may turn a concrete method back into an abstract one; the next concrete class must implement it. */
abstract class AbstractRunner extends Runner {
  @Override
  abstract String run();
}

class FastRunner extends AbstractRunner {
  @Override
  String run() {
    return "fast-run";
  }
}

public final class OverrideAbstract {
  public static void main(String[] args) {
    Figure[] all = {new Square(3), new Rect(2, 5)};
    for (Figure f : all) {
      System.out.println(f.describe());
    }
    Runner r = new FastRunner();
    System.out.println(r.run());
  }
}
// ANCHOR_END: override-abstract
