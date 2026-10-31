package oop.abstracts.exam;

// ANCHOR: predict-abstract-ctor
abstract class PacBase {
  PacBase() {
    System.out.println("base ctor " + name());
  }

  abstract String name();
}

class PacChild extends PacBase {
  private String label = "child";

  PacChild() {
    System.out.println("child ctor " + name());
  }

  @Override
  String name() {
    return label;
  }
}

public final class PredictAbstractCtor {
  public static void main(String[] args) {
    new PacChild();
  }
}
// ANCHOR_END: predict-abstract-ctor
