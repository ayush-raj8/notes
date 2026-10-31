package oop.immut.exam;

// ANCHOR: predict-compact-normalize
public final class PredictCompactNormalize {
  record Temp(double c) {
    Temp {
      if (c < -273.15) {
        c = -273.15;
      }
    }
  }

  public static void main(String[] args) {
    Temp t = new Temp(-500);
    System.out.println(t + " " + t.equals(new Temp(-273.15)));
  }
}
// ANCHOR_END: predict-compact-normalize
