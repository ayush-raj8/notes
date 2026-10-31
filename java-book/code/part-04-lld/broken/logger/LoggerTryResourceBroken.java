import lld.logger.ConsoleAppender;
import lld.logger.PatternFormatter;

public class LoggerTryResourceBroken {
  public static void main(String[] args) {
    try (ConsoleAppender c = new ConsoleAppender(System.out, new PatternFormatter("{msg}"))) {
      System.out.println("open");
    }
  }
}
