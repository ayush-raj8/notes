package oop.objectc;

import java.util.HashSet;
import java.util.Set;

// ANCHOR: good-point-demo
public final class GoodPointDemo {
  public static void main(String[] args) {
    GoodPoint p = new GoodPoint(1, 2);
    GoodPoint q = new GoodPoint(1, 2);
    System.out.println("p.equals(q)=" + p.equals(q));
    System.out.println("p==q=" + (p == q));
    System.out.println("p.equals(null)=" + p.equals(null));
    System.out.println("hash equal=" + (p.hashCode() == q.hashCode()) + " value=" + p.hashCode());
    Set<GoodPoint> set = new HashSet<>();
    set.add(p);
    System.out.println("contains(q)=" + set.contains(q));
    System.out.println("println: " + p);
    Object nothing = null;
    System.out.println("concat: " + nothing);
  }
}
// ANCHOR_END: good-point-demo
