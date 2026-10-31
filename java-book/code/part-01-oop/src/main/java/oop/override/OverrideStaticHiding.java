package oop.override;

// ANCHOR: override-static-hiding
class HideParent {
  static String who() {
    return "HideParent.who";
  }

  String me() {
    return "HideParent.me";
  }
}

class HideChild extends HideParent {
  static String who() {
    return "HideChild.who";
  }

  @Override
  String me() {
    return "HideChild.me";
  }
}

/** Instance methods are chosen by the object's class; static methods by the reference's type. */
public final class OverrideStaticHiding {
  public static void main(String[] args) {
    HideParent p = new HideChild();
    System.out.println(p.me());
    System.out.println(p.who());
    System.out.println(HideParent.who());
    System.out.println(HideChild.who());
    HideChild c = new HideChild();
    System.out.println(c.me());
  }
}
// ANCHOR_END: override-static-hiding
