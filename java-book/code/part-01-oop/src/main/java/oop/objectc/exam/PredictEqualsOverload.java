package oop.objectc.exam;

// ANCHOR: predict-equals-overload
public final class PredictEqualsOverload {
  static final class Pt {
    final int x;

    Pt(int x) {
      this.x = x;
    }

    public boolean equals(Pt other) {
      return other.x == x;
    }
  }

  public static void main(String[] args) {
    Pt a = new Pt(1);
    Pt b = new Pt(1);
    Object o = b;
    System.out.println(a.equals(b) + " " + a.equals(o));
  }
}
// ANCHOR_END: predict-equals-overload
