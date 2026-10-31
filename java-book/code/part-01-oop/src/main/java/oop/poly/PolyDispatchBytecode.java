package oop.poly;

// ANCHOR: poly-dispatch-bytecode
interface PzNamed {
  String name();
}

abstract class PzShape implements PzNamed {
  abstract double area();

  @Override
  public String name() {
    return "shape";
  }
}

class PzCircle extends PzShape {
  @Override
  double area() {
    return 3.0;
  }
}

class PzSquare extends PzShape {
  @Override
  double area() {
    return 4.0;
  }

  @Override
  public String name() {
    return "sq:" + super.name();
  }
}

/** Four call shapes, four invoke instructions. See javap output in the chapter. */
public final class PolyDispatchBytecode {
  static double viaClass(PzShape s) {
    return s.area();
  }

  static String viaInterface(PzNamed n) {
    return n.name();
  }

  static int viaStatic() {
    return Math.abs(-1);
  }

  public static void main(String[] args) {
    PzShape[] shapes = {new PzCircle(), new PzSquare()};
    for (PzShape s : shapes) {
      System.out.println(viaClass(s) + " " + viaInterface(s));
    }
    System.out.println(viaStatic());
  }
}
// ANCHOR_END: poly-dispatch-bytecode
