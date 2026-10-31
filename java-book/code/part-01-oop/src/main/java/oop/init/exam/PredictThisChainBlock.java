package oop.init.exam;

// ANCHOR: predict-this-chain-block
class Box {
  int n = 1;

  {
    n += 10;
  }

  Box() {
    this(5);
    n += 100;
  }

  Box(int k) {
    n += k;
  }
}

public final class PredictThisChainBlock {
  public static void main(String[] args) {
    System.out.println(new Box().n);
  }
}
// ANCHOR_END: predict-this-chain-block
