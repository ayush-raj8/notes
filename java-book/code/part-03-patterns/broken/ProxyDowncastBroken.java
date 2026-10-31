package patterns.structural.proxy;

/** Intentionally invalid: a subject reference cannot be assigned to the real type without a cast. */
public final class ProxyDowncastBroken {
  public static void main(String[] args) {
    Image img = new ImageProxy("a");
    RealImage real = img;
  }
}
