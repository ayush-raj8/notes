package oop.compare;

import java.util.List;

// ANCHOR: compare-overload-trap
class CmpPoint {
  final int x;

  CmpPoint(int x) {
    this.x = x;
  }

  public boolean equals(CmpPoint other) { // OVERLOAD, not an override of equals(Object)
    return other != null && other.x == x;
  }
}

class CmpPointOk {
  final int x;

  CmpPointOk(int x) {
    this.x = x;
  }

  @Override
  public boolean equals(Object other) { // real override
    return other instanceof CmpPointOk p && p.x == x;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(x);
  }
}

/** Wrong parameter type turns an intended override into an overload. */
public final class CompareOverloadTrap {
  public static void main(String[] args) {
    CmpPoint a = new CmpPoint(1);
    CmpPoint b = new CmpPoint(1);
    Object o = b;
    System.out.println("typed   : " + a.equals(b));
    System.out.println("Object  : " + a.equals(o));
    System.out.println("list    : " + List.of(a).contains(b));
    System.out.println("fixed   : " + List.of(new CmpPointOk(1)).contains(new CmpPointOk(1)));
  }
}
// ANCHOR_END: compare-overload-trap
