package patterns.structural.facade;

import patterns.structural.facade.subsystem.Amplifier;

/** Intentionally invalid: the subsystem class is package-private. */
public final class FacadeSubsystemHiddenBroken {
  public static void main(String[] args) {
    Amplifier a = new Amplifier();
  }
}
