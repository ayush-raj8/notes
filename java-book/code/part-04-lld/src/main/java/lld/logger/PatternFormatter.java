package lld.logger;

// ANCHOR: pattern-formatter
/** Replaces {ts}, {level}, {logger} and {msg} in a pattern. */
public final class PatternFormatter implements LogFormatter {
  private final String pattern;

  public PatternFormatter(String pattern) {
    this.pattern = pattern;
  }

  @Override
  public String format(LogEvent e) {
    return pattern
        .replace("{ts}", Long.toString(e.timestamp()))
        .replace("{level}", e.level().name())
        .replace("{logger}", e.logger())
        .replace("{msg}", e.message());
  }
}
// ANCHOR_END: pattern-formatter
