package oop.init.exam;

// ANCHOR: predict-override-ctor
class Shape0 {
  Shape0() {
    describe();
  }

  void describe() {
    System.out.println("shape");
  }
}

class Square0 extends Shape0 {
  int side = 4;

  Square0() {
    describe();
  }

  @Override
  void describe() {
    System.out.println("side=" + side);
  }
}

public final class PredictOverrideCtor {
  public static void main(String[] args) {
    new Square0();
  }
}
// ANCHOR_END: predict-override-ctor
