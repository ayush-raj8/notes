package oop.poly.exam;

// ANCHOR: predict-super-dispatch
class Q5A {
  String name() {
    return "A";
  }

  String hello() {
    return "hi " + name();
  }
}

class Q5B extends Q5A {
  @Override
  String name() {
    return "B";
  }

  @Override
  String hello() {
    return super.hello() + "!";
  }
}

public final class PredictSuperDispatch {
  public static void main(String[] args) {
    Q5A x = new Q5B();
    System.out.println(x.hello());
    System.out.println(new Q5A().hello());
  }
}
// ANCHOR_END: predict-super-dispatch
