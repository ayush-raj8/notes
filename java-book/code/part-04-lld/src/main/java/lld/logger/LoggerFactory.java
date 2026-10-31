package lld.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

// ANCHOR: logger-factory
/**
 * Registry of loggers by dotted name: "app.db" has parent "app", which has the root. An instance
 * (not a static singleton) keeps tests isolated; production code keeps one instance.
 */
public final class LoggerFactory {
  private final Map<String, Logger> loggers = new ConcurrentHashMap<>();
  private final LongSupplier clock;
  private final Logger root;

  public LoggerFactory(Level rootLevel, LongSupplier clock) {
    this.clock = clock;
    this.root = new Logger("root", null, rootLevel, clock);
    loggers.put("root", root);
  }

  public Logger root() {
    return root;
  }

  public Logger get(String name) {
    Logger existing = loggers.get(name);
    if (existing != null) {
      return existing;
    }
    int dot = name.lastIndexOf('.');
    Logger parent = dot < 0 ? root : get(name.substring(0, dot));
    return loggers.computeIfAbsent(name, n -> new Logger(n, parent, null, clock));
  }
}
// ANCHOR_END: logger-factory
