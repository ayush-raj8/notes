package oop.conc;

/** Intentionally invalid: an abstract method has no body to lock around. */
public abstract class ConcAbstractSyncBroken {
  abstract synchronized void work();
}
