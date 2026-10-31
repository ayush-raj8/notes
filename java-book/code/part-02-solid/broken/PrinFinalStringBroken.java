package solid.principles;

/** Composition over inheritance is sometimes the only option: String is final. */
public final class PrinFinalStringBroken {
  static class ShoutingString extends String {
    String shout() {
      return toUpperCase();
    }
  }
}
