package patterns.structural.bridge;

/** Intentionally invalid: an implementor must provide every Renderer method. */
public final class BridgeMissingImplMethodBroken implements Renderer {
  @Override
  public String circle(int radius) {
    return "c";
  }
}
