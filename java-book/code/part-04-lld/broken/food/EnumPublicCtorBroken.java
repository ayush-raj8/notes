public class EnumPublicCtorBroken {
  enum Spice {
    MILD(1),
    HOT(5);

    private final int level;

    public Spice(int level) {
      this.level = level;
    }
  }
}
