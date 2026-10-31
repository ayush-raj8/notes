package patterns.structural.facade;

import patterns.structural.facade.subsystem.HomeTheaterFacade;

/** Intentionally invalid: watchMovie takes a title. */
public final class FacadeWrongArgBroken {
  public static void main(String[] args) {
    new HomeTheaterFacade().watchMovie(42);
  }
}
