package oop.io.solutions;

import java.io.Serializable;
import oop.io.SerialBasics;

/** Serializable with a transient derived field restored by readObject-free recomputation. */
public final class SolutionSerialRoundTrip {
  static final class Rect implements Serializable {
    private static final long serialVersionUID = 1L;
    final int w;
    final int h;
    transient int area;

    Rect(int w, int h) {
      this.w = w;
      this.h = h;
      this.area = w * h;
    }

    int area() {
      return w * h;
    }
  }

  public static void main(String[] args) throws Exception {
    Rect copy = SerialBasics.roundTrip(new Rect(3, 4));
    System.out.println(copy.w + " " + copy.h + " transient=" + copy.area + " computed=" + copy.area());
  }
}
