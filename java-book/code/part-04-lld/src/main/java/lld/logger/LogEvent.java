package lld.logger;

// ANCHOR: log-event
/** Immutable event passed from a logger to appenders. */
public record LogEvent(long timestamp, Level level, String logger, String message) {}
// ANCHOR_END: log-event
