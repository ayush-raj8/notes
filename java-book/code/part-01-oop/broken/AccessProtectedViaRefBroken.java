package oop.access.other;

import oop.access.AccessLevels;

/**
 * Intentionally invalid: a subclass in another package may read {@code prot} only through its own
 * type (or a subtype), not through a parent-typed reference.
 */
public class AccessProtectedViaRefBroken extends AccessLevels {
  String peek(AccessLevels other) {
    return other.prot;
  }
}
