package oop.compare;

// ANCHOR: compare-all-four
class CmpBase {
  String tag = "Base.tag";

  static String kind() {
    return "Base.kind";
  }

  String who() {
    return "Base.who";
  }

  String pick(Object o) {
    return "Base.pick(Object)";
  }
}

class CmpDerived extends CmpBase {
  String tag = "Derived.tag"; // field HIDES Base.tag

  static String kind() { // static method HIDES Base.kind
    return "Derived.kind";
  }

  @Override
  String who() { // OVERRIDES Base.who
    return "Derived.who";
  }

  String pick(String s) { // OVERLOADS pick: new parameter list
    return "Derived.pick(String)";
  }

  String shadow(String tag) { // parameter SHADOWS the field
    return tag + " | " + this.tag + " | " + super.tag;
  }
}

/** One class pair, four relationships. Reference type vs object type decides each line. */
public final class CompareAllFour {
  public static void main(String[] args) {
    CmpBase b = new CmpDerived();
    CmpDerived d = new CmpDerived();

    System.out.println("override : " + b.who());
    System.out.println("hide stat: " + b.kind() + " / " + d.kind());
    System.out.println("hide fld : " + b.tag + " / " + d.tag);
    System.out.println("overload : " + b.pick("s") + " / " + d.pick("s"));
    System.out.println("overload : " + d.pick((Object) "s"));
    System.out.println("shadow   : " + d.shadow("param"));
  }
}
// ANCHOR_END: compare-all-four
