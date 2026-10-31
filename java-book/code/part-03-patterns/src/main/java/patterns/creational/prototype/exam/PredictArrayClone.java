package patterns.creational.prototype.exam;

import java.util.Arrays;

// ANCHOR: predict-array-clone
public final class PredictArrayClone {
  public static void main(String[] args) {
    int[] a = {1, 2, 3};
    int[] b = a.clone();
    b[0] = 9;
    System.out.println(Arrays.toString(a));

    int[][] g = {{1}, {2}};
    int[][] h = g.clone();
    h[0][0] = 9;
    System.out.println(g[0][0]);
    System.out.println(g[0] == h[0]);
  }
}
// ANCHOR_END: predict-array-clone
