package patterns.structural.flyweight;

/** Intentionally invalid: intrinsic state is final, so a shared flyweight cannot be changed. */
public final class FlyFinalFieldBroken {
  public static void main(String[] args) {
    TreeType t = TreeFactory.get("oak", "green");
    t.color = "red";
  }
}
