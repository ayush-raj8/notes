package oop.conc;

/** Intentionally invalid: constructors cannot be synchronized. */
public final class ConcSyncCtorBroken {
  synchronized ConcSyncCtorBroken() {}
}
