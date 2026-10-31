package oop.immut;

/** Intentionally invalid: record fields are final. */
public final class ImmutRecordSetterBroken {
  record Point(int x, int y) {
    void setX(int nx) {
      this.x = nx;
    }
  }
}
