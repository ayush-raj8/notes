package lld.logger.exam;

import lld.logger.*;

// ANCHOR: predict-format
public class PredictFormat {
  public static void main(String[] args) {
    LogEvent e = new LogEvent(42, Level.WARN, "db", "slow");
    System.out.println(new PatternFormatter("{ts}|{level}|{logger}|{msg}").format(e));
    System.out.println(new PatternFormatter("[{level}] {msg} {msg}").format(e));
    LogFormatter upper = ev -> ev.message().toUpperCase();
    System.out.println(upper.format(e));
  }
}
// ANCHOR_END: predict-format
