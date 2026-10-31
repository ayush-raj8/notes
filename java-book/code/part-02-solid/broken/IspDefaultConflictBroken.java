package solid.isp;

/** Two role interfaces that both bring a default hi(): the class must choose. */
public final class IspDefaultConflictBroken {
  interface Left {
    default String hi() {
      return "L";
    }
  }

  interface Right {
    default String hi() {
      return "R";
    }
  }

  static class Both implements Left, Right {}
}
