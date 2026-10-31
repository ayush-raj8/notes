package oop.io;

import java.io.Serializable;

// ANCHOR: serial-parent
public final class SerialParentGotcha {
  static class Base {
    int baseValue = 5;

    Base() {
      System.out.println("Base() runs");
    }
  }

  static final class Child extends Base implements Serializable {
    private static final long serialVersionUID = 1L;
    int childValue;

    Child(int childValue) {
      this.childValue = childValue;
      this.baseValue = 99;
    }
  }

  public static void main(String[] args) throws Exception {
    Child c = new Child(7);
    System.out.println("before: " + c.baseValue + " " + c.childValue);
    Child copy = SerialBasics.roundTrip(c);
    System.out.println("after: " + copy.baseValue + " " + copy.childValue);
  }
}
// ANCHOR_END: serial-parent
