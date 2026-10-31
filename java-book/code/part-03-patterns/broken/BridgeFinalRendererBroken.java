package patterns.structural.bridge;

/** Intentionally invalid: the bridge reference is final, so it cannot be re-pointed. */
public final class BridgeFinalRendererBroken {
  public static void main(String[] args) {
    CircleShape s = new CircleShape(new VectorRenderer(), 1);
    s.renderer = new RasterRenderer();
  }
}
