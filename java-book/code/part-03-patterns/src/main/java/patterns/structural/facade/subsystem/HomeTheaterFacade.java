package patterns.structural.facade.subsystem;

import java.util.List;

// ANCHOR: home-theater-facade
/** Facade: two public methods hide the order and details of five subsystem calls. */
public final class HomeTheaterFacade {
  private final Amplifier amp = new Amplifier();
  private final Projector projector = new Projector();
  private final Lights lights = new Lights();

  public List<String> watchMovie(String title) {
    return List.of(
        lights.dim(10), projector.on(), projector.wide(), amp.on(), amp.volume(5), "playing " + title);
  }

  public List<String> endMovie() {
    return List.of(amp.off(), projector.off(), lights.dim(100));
  }

  private String tuneForMovie() {
    return amp.volume(5);
  }
}
// ANCHOR_END: home-theater-facade
