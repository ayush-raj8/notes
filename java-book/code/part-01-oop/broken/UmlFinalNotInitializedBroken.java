package oop.uml;

import java.util.List;

/** Intentionally invalid: multiplicity "1" modelled as a final field that no constructor sets. */
public final class UmlFinalNotInitializedBroken {
  static class Room {}

  static class House {
    private final List<Room> rooms;
  }
}
