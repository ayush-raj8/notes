package oop.objectc;

// ANCHOR: finalize-warned
/** Overriding finalize() without suppression: javac prints a removal warning. */
public final class FinalizeWarned {
  @Override
  protected void finalize() {
    // never rely on this running
  }
}
// ANCHOR_END: finalize-warned
