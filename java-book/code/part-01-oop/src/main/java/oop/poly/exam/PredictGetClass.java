package oop.poly.exam;

// ANCHOR: predict-get-class
class GcBase {
  @Override
  public boolean equals(Object o) {
    return o != null && getClass() == o.getClass();
  }

  @Override
  public int hashCode() {
    return 1;
  }
}

class GcChild extends GcBase {}

public final class PredictGetClass {
  public static void main(String[] args) {
    GcBase base = new GcBase();
    GcBase child = new GcChild();
    System.out.println(child instanceof GcBase);
    System.out.println(base.equals(child));
    System.out.println(child.equals(base));
    System.out.println(child.equals(new GcChild()));
  }
}
// ANCHOR_END: predict-get-class
