package lld.logger;

// ANCHOR: log-formatter
@FunctionalInterface
public interface LogFormatter {
  String format(LogEvent event);
}
// ANCHOR_END: log-formatter
