package patterns.structural.proxy;

/** Intentionally invalid: a lazily assigned field cannot be final. */
public final class ProxyFinalLazyBroken implements Image {
  private final RealImage real = null;

  @Override
  public String display() {
    if (real == null) {
      real = new RealImage("a");
    }
    return real.display();
  }
}
