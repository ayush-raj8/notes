package oop.access;

import java.util.Arrays;

// ANCHOR: final-ref-edge
/** {@code final} fixes the reference, not the object it points to. */
public final class FinalRefEdge {
  public static void main(String[] args) {
    final int[] a = {1, 2};
    a[0] = 9;
    System.out.println(Arrays.toString(a));

    final StringBuilder sb = new StringBuilder("x");
    sb.append("y");
    System.out.println(sb);
  }
}
// ANCHOR_END: final-ref-edge
