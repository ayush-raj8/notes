package oop.immut;

import java.util.Arrays;

// ANCHOR: record-array-gotcha
/** A record is shallowly immutable: an array component stays mutable and uses reference equality. */
public final class RecordArrayGotcha {
  record Data(int[] values) {}

  record SafeData(int[] values) {
    SafeData {
      values = values.clone();
    }

    @Override
    public int[] values() {
      return values.clone();
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof SafeData d && Arrays.equals(values, d.values);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(values);
    }
  }

  public static void main(String[] args) {
    Data a = new Data(new int[] {1, 2});
    Data b = new Data(new int[] {1, 2});
    System.out.println("same contents equal: " + a.equals(b));
    a.values()[0] = 99;
    System.out.println("mutated via accessor: " + a.values()[0]);

    SafeData s = new SafeData(new int[] {1, 2});
    SafeData t = new SafeData(new int[] {1, 2});
    s.values()[0] = 99;
    System.out.println("safe equal: " + s.equals(t) + " first=" + s.values()[0]);
  }
}
// ANCHOR_END: record-array-gotcha
