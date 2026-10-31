package patterns.structural.proxy;

// ANCHOR: real-image
/** RealSubject: expensive to build (we count constructions). */
public final class RealImage implements Image {
  private static int loads;
  private final String file;

  public RealImage(String file) {
    this.file = file;
    loads++;
  }

  public static int loads() {
    return loads;
  }

  public static void resetLoads() {
    loads = 0;
  }

  @Override
  public String display() {
    return "showing " + file;
  }
}
// ANCHOR_END: real-image
