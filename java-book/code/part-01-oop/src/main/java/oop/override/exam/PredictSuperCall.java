package oop.override.exam;

// ANCHOR: predict-super-call
class SuperCallA {
  String m() {
    return "A";
  }

  String twice() {
    return m() + m();
  }
}

class SuperCallB extends SuperCallA {
  @Override
  String m() {
    return "B";
  }

  @Override
  String twice() {
    return super.twice() + super.m();
  }
}

public final class PredictSuperCall {
  public static void main(String[] args) {
    System.out.println(new SuperCallB().twice());
  }
}
// ANCHOR_END: predict-super-call
