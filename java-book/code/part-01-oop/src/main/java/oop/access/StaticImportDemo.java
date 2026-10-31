package oop.access;

// ANCHOR: static-import-demo
import static java.lang.Math.max;
import static oop.access.other.Consts.RATE;
import static oop.access.other.Consts.twice;

/** Single-static-import: use the member name without the class prefix. */
public class StaticImportDemo {
  public static void main(String[] args) {
    System.out.println(max(3, 7));
    System.out.println(twice(4));
    System.out.println(RATE);
  }
}
// ANCHOR_END: static-import-demo
