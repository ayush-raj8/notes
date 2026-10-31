package oop.abstracts.exam;

// ANCHOR: predict-interface-static
interface PisLimits {
  int MAX = 10;

  static int doubled() {
    return MAX * 2;
  }
}

class PisUser implements PisLimits {
  int read() {
    return MAX + 1; // inherited constant, no qualifier needed
  }
}

public final class PredictInterfaceStatic {
  public static void main(String[] args) {
    PisUser u = new PisUser();
    System.out.println(u.read());
    System.out.println(PisLimits.doubled());
    System.out.println(PisUser.MAX);
  }
}
// ANCHOR_END: predict-interface-static
