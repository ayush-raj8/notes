package oop.abstracts.exam;

// ANCHOR: predict-diamond-super
interface PdsA {
  default String m() {
    return "A";
  }
}

interface PdsB extends PdsA {
  @Override
  default String m() {
    return "B" + PdsA.super.m();
  }
}

interface PdsC extends PdsA {}

class PdsImpl implements PdsB, PdsC {}

public final class PredictDiamondSuper {
  public static void main(String[] args) {
    System.out.println(new PdsImpl().m());
    PdsA a = new PdsImpl();
    System.out.println(a.m());
  }
}
// ANCHOR_END: predict-diamond-super
