package oop.override.exam;

// ANCHOR: predict-field-hide
class FieldHideA {
  String v = "A";

  String v() {
    return v;
  }
}

class FieldHideB extends FieldHideA {
  String v = "B";

  @Override
  String v() {
    return v;
  }
}

public final class PredictFieldHide {
  public static void main(String[] args) {
    FieldHideA a = new FieldHideB();
    System.out.println(a.v);
    System.out.println(a.v());
    System.out.println(((FieldHideA) new FieldHideB()).v);
  }
}
// ANCHOR_END: predict-field-hide
