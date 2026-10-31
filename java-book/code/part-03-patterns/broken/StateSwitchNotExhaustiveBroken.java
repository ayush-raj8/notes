public final class StateSwitchNotExhaustiveBroken {
  enum Light {
    RED,
    GREEN,
    YELLOW
  }

  static Light next(Light l) {
    return switch (l) {
      case RED -> Light.GREEN;
      case GREEN -> Light.YELLOW;
    };
  }

  public static void main(String[] args) {
    System.out.println(next(Light.RED));
  }
}
