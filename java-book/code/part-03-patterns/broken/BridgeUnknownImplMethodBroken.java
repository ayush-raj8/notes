package patterns.structural.bridge;

/** Intentionally invalid: the abstraction can only call what the implementor interface declares. */
public final class BridgeUnknownImplMethodBroken extends Shape {
  public BridgeUnknownImplMethodBroken(Renderer renderer) {
    super(renderer);
  }

  @Override
  public String draw() {
    return renderer.triangle(3);
  }
}
