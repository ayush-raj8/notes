package patterns.structural.bridge;

/** Intentionally invalid: Renderer has two abstract methods, so it is not a functional interface. */
public final class BridgeLambdaRendererBroken {
  public static void main(String[] args) {
    Renderer r = radius -> "circle";
  }
}
