package oop.objectc.exam;

import java.util.Arrays;

// ANCHOR: predict-array-equals
public final class PredictArrayEquals {
  public static void main(String[] args) {
    int[] a = {1, 2, 3};
    int[] b = {1, 2, 3};
    int[][] p = {{1}, {2}};
    int[][] q = {{1}, {2}};
    System.out.println(a.equals(b) + " " + Arrays.equals(a, b) + " " + Arrays.equals(p, q) + " " + Arrays.deepEquals(p, q));
  }
}
// ANCHOR_END: predict-array-equals
