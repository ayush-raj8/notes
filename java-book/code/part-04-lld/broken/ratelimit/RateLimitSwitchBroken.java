import lld.ratelimit.Algorithm;

public class RateLimitSwitchBroken {
  static String memory(Algorithm a) {
    return switch (a) {
      case TOKEN_BUCKET -> "O(1)";
      case FIXED_WINDOW -> "O(1)";
    };
  }

  public static void main(String[] args) {
    System.out.println(memory(Algorithm.TOKEN_BUCKET));
  }
}
