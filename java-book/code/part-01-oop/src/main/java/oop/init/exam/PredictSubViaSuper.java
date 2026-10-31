package oop.init.exam;

// ANCHOR: predict-sub-via-super
class Sup {
  static int v = 1;

  static {
    System.out.println("Sup static");
  }
}

class Sub extends Sup {
  static {
    System.out.println("Sub static");
  }
}

public final class PredictSubViaSuper {
  public static void main(String[] args) {
    System.out.println("v=" + Sub.v);
  }
}
// ANCHOR_END: predict-sub-via-super
