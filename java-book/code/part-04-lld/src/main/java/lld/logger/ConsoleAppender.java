package lld.logger;

import java.io.PrintStream;

// ANCHOR: console-appender
public final class ConsoleAppender implements Appender {
  private final PrintStream out;
  private final LogFormatter formatter;

  public ConsoleAppender(PrintStream out, LogFormatter formatter) {
    this.out = out;
    this.formatter = formatter;
  }

  @Override
  public void append(LogEvent event) {
    out.println(formatter.format(event));
  }
}
// ANCHOR_END: console-appender
