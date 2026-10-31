import lld.logger.Level;

public class LoggerSwitchBroken {
  static String tag(Level l) {
    return switch (l) {
      case DEBUG, INFO -> "quiet";
      case WARN -> "loud";
    };
  }

  public static void main(String[] args) {
    System.out.println(tag(Level.ERROR));
  }
}
