import lld.logger.Appender;

public class LoggerMissingAppendBroken implements Appender {
  public void flush() {}
}
