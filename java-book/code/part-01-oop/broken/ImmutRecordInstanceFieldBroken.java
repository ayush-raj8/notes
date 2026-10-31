package oop.immut;

/** Intentionally invalid: a record cannot declare instance fields beyond its components. */
public final class ImmutRecordInstanceFieldBroken {
  record Point(int x, int y) {
    int extra;
  }
}
