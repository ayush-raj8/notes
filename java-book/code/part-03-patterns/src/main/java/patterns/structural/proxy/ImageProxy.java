package patterns.structural.proxy;

// ANCHOR: image-proxy
/** Virtual proxy: defers creating the RealImage until display() is first called. */
public final class ImageProxy implements Image {
  private final String file;
  private RealImage real; // not final: assigned lazily

  public ImageProxy(String file) {
    this.file = file;
  }

  @Override
  public String display() {
    if (real == null) {
      real = new RealImage(file);
    }
    return real.display();
  }
}
// ANCHOR_END: image-proxy
