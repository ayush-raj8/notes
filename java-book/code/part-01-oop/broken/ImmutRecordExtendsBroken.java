package oop.immut;

/** Intentionally invalid: a record cannot extend a class. */
public final class ImmutRecordExtendsBroken {
  static class Base {}

  record Point(int x, int y) extends Base {}
}
