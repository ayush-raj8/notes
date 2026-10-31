package lld.logger;

// ANCHOR: appender
/** Destination of log events. Implementations must be safe to call from many threads. */
public interface Appender {
  void append(LogEvent event);
}
// ANCHOR_END: appender
