import lld.logger.Level;

public class LoggerUnknownLevelBroken {
  public static void main(String[] args) {
    Level l = Level.TRACE;
    System.out.println(l);
  }
}
