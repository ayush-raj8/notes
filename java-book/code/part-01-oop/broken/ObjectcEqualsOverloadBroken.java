package oop.objectc;

/** Intentionally invalid: @Override on equals(Pt) — it does not override equals(Object). */
public final class ObjectcEqualsOverloadBroken {
  static final class Pt {
    final int x;

    Pt(int x) {
      this.x = x;
    }

    @Override
    public boolean equals(Pt other) {
      return other.x == x;
    }
  }
}
