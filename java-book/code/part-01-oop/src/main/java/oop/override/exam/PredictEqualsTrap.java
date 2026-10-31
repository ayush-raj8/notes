package oop.override.exam;

import java.util.List;

// ANCHOR: predict-equals-trap
class PredictPt {
  final int x;

  PredictPt(int x) {
    this.x = x;
  }

  public boolean equals(PredictPt other) {
    return other != null && x == other.x;
  }
}

public final class PredictEqualsTrap {
  public static void main(String[] args) {
    PredictPt a = new PredictPt(1);
    PredictPt b = new PredictPt(1);
    Object o = b;
    System.out.println(a.equals(b));
    System.out.println(a.equals(o));
    System.out.println(List.of(a).contains(b));
  }
}
// ANCHOR_END: predict-equals-trap
