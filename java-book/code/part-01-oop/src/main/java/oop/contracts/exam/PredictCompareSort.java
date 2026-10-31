package oop.contracts.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: predict-compare-sort
public final class PredictCompareSort {
  static final class V implements Comparable<V> {
    final String s;

    V(String s) {
      this.s = s;
    }

    @Override
    public int compareTo(V o) {
      return s.length() - o.s.length();
    }

    @Override
    public String toString() {
      return s;
    }
  }

  public static void main(String[] args) {
    List<V> list = new ArrayList<>();
    for (String s : new String[] {"ccc", "a", "bb", "dd", "e"}) {
      list.add(new V(s));
    }
    Collections.sort(list);
    System.out.println(list);
  }
}
// ANCHOR_END: predict-compare-sort
