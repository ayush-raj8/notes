package lld.logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

// ANCHOR: logger
/**
 * Named logger in a hierarchy. A null level means "inherit"; events go to this logger's appenders
 * and then to every ancestor's appenders (additive), as in the common logging frameworks.
 */
public final class Logger {
  private final String name;
  private final Logger parent;
  private final LongSupplier clock;
  private volatile Level level;
  private final List<Appender> appenders = new CopyOnWriteArrayList<>();

  Logger(String name, Logger parent, Level level, LongSupplier clock) {
    this.name = name;
    this.parent = parent;
    this.level = level;
    this.clock = clock;
  }

  public String name() {
    return name;
  }

  public void setLevel(Level level) {
    if (level == null && parent == null) {
      throw new IllegalArgumentException("root logger needs a level");
    }
    this.level = level;
  }

  public void addAppender(Appender appender) {
    appenders.add(appender);
  }

  public Level effectiveLevel() {
    Level l = level;
    return l != null ? l : parent.effectiveLevel();
  }

  public boolean isEnabled(Level l) {
    return l.atLeast(effectiveLevel());
  }

  public void log(Level l, String message) {
    if (isEnabled(l)) {
      deliver(new LogEvent(clock.getAsLong(), l, name, message));
    }
  }

  /** The message is built only when the level is enabled. */
  public void log(Level l, Supplier<String> message) {
    if (isEnabled(l)) {
      deliver(new LogEvent(clock.getAsLong(), l, name, message.get()));
    }
  }

  public void debug(String m) { log(Level.DEBUG, m); }
  public void info(String m) { log(Level.INFO, m); }
  public void warn(String m) { log(Level.WARN, m); }
  public void error(String m) { log(Level.ERROR, m); }

  private void deliver(LogEvent event) {
    for (Logger l = this; l != null; l = l.parent) {
      for (Appender a : l.appenders) {
        a.append(event);
      }
    }
  }
}
// ANCHOR_END: logger
