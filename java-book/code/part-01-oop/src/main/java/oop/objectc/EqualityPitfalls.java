package oop.objectc;

import java.util.Arrays;

// ANCHOR: equality-pitfalls
/** == compares references; equals compares what the class defines. */
public final class EqualityPitfalls {
  public static void main(String[] args) {
    String a = "java";
    String b = "java";
    String c = new String("java");
    System.out.println("a==b " + (a == b));
    System.out.println("a==c " + (a == c));
    System.out.println("a.equals(c) " + a.equals(c));
    System.out.println("a==c.intern() " + (a == c.intern()));

    int[] x = {1, 2};
    int[] y = {1, 2};
    System.out.println("x.equals(y) " + x.equals(y));
    System.out.println("Arrays.equals " + Arrays.equals(x, y));

    Integer i = 127;
    Integer j = 127;
    Integer m = 128;
    Integer n = 128;
    System.out.println("127==127 " + (i == j));
    System.out.println("128==128 " + (m == n));
    System.out.println("128 equals " + m.equals(n));
  }
}
// ANCHOR_END: equality-pitfalls
