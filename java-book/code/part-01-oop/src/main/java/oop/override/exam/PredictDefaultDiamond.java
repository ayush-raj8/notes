package oop.override.exam;

// ANCHOR: predict-default-diamond
interface DiamondLeft {
  default String hi() {
    return "L";
  }
}

interface DiamondRight extends DiamondLeft {
  @Override
  default String hi() {
    return "R" + DiamondLeft.super.hi();
  }
}

class DiamondBoth implements DiamondLeft, DiamondRight {}

public final class PredictDefaultDiamond {
  public static void main(String[] args) {
    System.out.println(new DiamondBoth().hi());
    DiamondLeft l = new DiamondBoth();
    System.out.println(l.hi());
  }
}
// ANCHOR_END: predict-default-diamond
