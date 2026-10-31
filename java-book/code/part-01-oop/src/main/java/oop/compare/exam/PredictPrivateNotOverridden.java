package oop.compare.exam;

// ANCHOR: predict-private-not-overridden
class P4A {
  private String m() {
    return "A.m";
  }

  public String call() {
    return m();
  }
}

class P4B extends P4A {
  public String m() { // unrelated method: P4A.m is not visible here
    return "B.m";
  }
}

public final class PredictPrivateNotOverridden {
  public static void main(String[] args) {
    P4B b = new P4B();
    System.out.println(b.call());
    System.out.println(b.m());
  }
}
// ANCHOR_END: predict-private-not-overridden
