package oop.abstracts.exam;

import java.util.Arrays;

// ANCHOR: predict-encapsulation-alias
class R6Leaky {
  private final int[] data;

  R6Leaky(int[] data) {
    this.data = data;
  }

  int first() {
    return data[0];
  }
}

class R6Safe {
  private final int[] data;

  R6Safe(int[] data) {
    this.data = Arrays.copyOf(data, data.length);
  }

  int first() {
    return data[0];
  }
}

public final class PredictEncapsulationAlias {
  public static void main(String[] args) {
    int[] a = {1, 2};
    R6Leaky leaky = new R6Leaky(a);
    R6Safe safe = new R6Safe(a);
    a[0] = 99;
    System.out.println(leaky.first() + " " + safe.first());
  }
}
// ANCHOR_END: predict-encapsulation-alias
