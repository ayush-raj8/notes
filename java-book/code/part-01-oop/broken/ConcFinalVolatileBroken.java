package oop.conc;

/** Intentionally invalid: final and volatile contradict each other. */
public final class ConcFinalVolatileBroken {
  private final volatile int ready = 0;
}
