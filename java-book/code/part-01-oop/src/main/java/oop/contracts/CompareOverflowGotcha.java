package oop.contracts;

import java.util.Comparator;

// ANCHOR: compare-overflow
public final class CompareOverflowGotcha {
  public static void main(String[] args) {
    Comparator<Integer> bad = (a, b) -> a - b;
    Comparator<Integer> good = Integer::compare;
    int big = Integer.MAX_VALUE;
    int small = -2;
    System.out.println(bad.compare(big, small));
    System.out.println(good.compare(big, small));
  }
}
// ANCHOR_END: compare-overflow
