package oop.poly.exam;

// ANCHOR: predict-field-poly
class FieldA {
  int x = 1;

  int get() {
    return x;
  }
}

class FieldB extends FieldA {
  int x = 2;

  @Override
  int get() {
    return x;
  }
}

public final class PredictFieldPoly {
  public static void main(String[] args) {
    FieldA a = new FieldB();
    System.out.println(a.x);
    System.out.println(a.get());
    System.out.println(((FieldB) a).x);
    System.out.println(((FieldA) new FieldB()).get());
  }
}
// ANCHOR_END: predict-field-poly
