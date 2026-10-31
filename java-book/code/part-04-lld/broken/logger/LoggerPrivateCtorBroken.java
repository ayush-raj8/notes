import lld.logger.Level;
import lld.logger.Logger;

public class LoggerPrivateCtorBroken {
  public static void main(String[] args) {
    Logger l = new Logger("x", null, Level.INFO, () -> 0L);
    System.out.println(l);
  }
}
