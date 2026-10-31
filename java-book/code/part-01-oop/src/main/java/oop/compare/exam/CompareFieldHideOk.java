package oop.compare.exam;

// ANCHOR: compare-field-hide-ok
class OkP {
  String tag = "p";

  Object make() {
    return "obj";
  }
}

/** Looks wrong; compiles: a hiding field may have another type, an override may narrow the return. */
class OkC extends OkP {
  int tag = 42; // hides String tag; types are unrelated

  @Override
  String make() { // covariant return
    return "str";
  }
}

public final class CompareFieldHideOk {
  public static void main(String[] args) {
    OkP p = new OkC();
    System.out.println(p.tag + " " + ((OkC) p).tag + " " + p.make());
  }
}
// ANCHOR_END: compare-field-hide-ok
