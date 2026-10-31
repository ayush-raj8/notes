package oop.override;

// ANCHOR: override-covariant
class Doc {
  Doc copy() {
    return new Doc();
  }

  String kind() {
    return "Doc";
  }
}

class Memo extends Doc {
  @Override
  Memo copy() {
    return new Memo();
  }

  @Override
  String kind() {
    return "Memo";
  }

  String memoOnly() {
    return "memo-only";
  }
}

/** Covariant return: the overriding method may return a subtype of the parent's return type. */
public final class OverrideCovariant {
  public static void main(String[] args) {
    Memo m = new Memo();
    Memo c = m.copy();
    System.out.println(c.memoOnly());
    Doc d = m;
    System.out.println(d.copy().kind());
    System.out.println(d.copy().getClass().getSimpleName());
  }
}
// ANCHOR_END: override-covariant
