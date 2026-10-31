package patterns.structural.bridge;

/** Intentionally invalid: a Tv is a Device, not a Renderer. */
public final class BridgeWrongImplTypeBroken {
  public static void main(String[] args) {
    new CircleShape(new Tv(), 1);
  }
}
