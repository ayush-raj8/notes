package patterns.structural.facade;

import patterns.structural.facade.subsystem.HomeTheaterFacade;

/** Intentionally invalid: calling a private helper of the facade. */
public final class FacadePrivateHelperBroken {
  public static void main(String[] args) {
    HomeTheaterFacade f = new HomeTheaterFacade();
    System.out.println(f.tuneForMovie());
  }
}
