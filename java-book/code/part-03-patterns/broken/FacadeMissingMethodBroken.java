package patterns.structural.facade;

import patterns.structural.facade.subsystem.HomeTheaterFacade;

/** Intentionally invalid: the facade exposes only what it declares. */
public final class FacadeMissingMethodBroken {
  public static void main(String[] args) {
    new HomeTheaterFacade().pauseMovie();
  }
}
