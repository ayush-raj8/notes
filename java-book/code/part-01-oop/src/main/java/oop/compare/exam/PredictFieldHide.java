package oop.compare.exam;

// ANCHOR: predict-field-hide
class P3A {
  int v = 1;

  int get() {
    return v;
  }
}

class P3B extends P3A {
  int v = 2;
}

public final class PredictFieldHide {
  public static void main(String[] args) {
    P3A r = new P3B();
    System.out.println(r.v + " " + ((P3B) r).v + " " + r.get());
  }
}
// ANCHOR_END: predict-field-hide
