package oop.immut;

import java.util.Arrays;

// ANCHOR: defensive-array
/** Arrays are always mutable: clone in the constructor and in the getter. */
public final class DefensiveArray {
  static final class Scores {
    private final int[] values;

    Scores(int[] values) {
      this.values = values.clone();
    }

    int[] values() {
      return values.clone();
    }

    int at(int i) {
      return values[i];
    }
  }

  public static void main(String[] args) {
    int[] src = {1, 2, 3};
    Scores s = new Scores(src);
    src[0] = 99;
    s.values()[1] = 99;
    System.out.println(s.at(0) + " " + s.at(1) + " " + Arrays.toString(s.values()));
  }
}
// ANCHOR_END: defensive-array
