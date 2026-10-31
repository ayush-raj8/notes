package oop.overload;

/** Not allowed: overload by modifiers only (public vs private same signature). */
public final class OverloadModifiersOnlyBroken {
  public void run() {}

  private void run() {}
}
