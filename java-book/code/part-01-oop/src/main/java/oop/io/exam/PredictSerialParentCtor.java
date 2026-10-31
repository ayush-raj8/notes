package oop.io.exam;

import java.io.Serializable;
import oop.io.SerialBasics;

// ANCHOR: predict-serial-ctor
public final class PredictSerialParentCtor {
  static class P {
    String tag = "P-default";

    P() {
      System.out.println("P()");
    }
  }

  static class C extends P implements Serializable {
    private static final long serialVersionUID = 1L;

    C() {
      System.out.println("C()");
      tag = "C-set";
    }
  }

  public static void main(String[] args) throws Exception {
    C c = new C();
    C copy = SerialBasics.roundTrip(c);
    System.out.println(copy.tag);
  }
}
// ANCHOR_END: predict-serial-ctor
