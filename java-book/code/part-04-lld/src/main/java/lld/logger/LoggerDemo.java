package lld.logger;

import java.util.concurrent.atomic.AtomicLong;

// ANCHOR: logger-demo
public final class LoggerDemo {
  private LoggerDemo() {}

  public static void main(String[] args) {
    AtomicLong tick = new AtomicLong(1000);
    LoggerFactory factory = new LoggerFactory(Level.INFO, tick::getAndIncrement);
    factory.root().addAppender(
        new ConsoleAppender(System.out, new PatternFormatter("{ts} {level} {logger}: {msg}")));

    Logger app = factory.get("app");
    Logger db = factory.get("app.db");
    app.setLevel(Level.DEBUG);
    db.setLevel(Level.WARN);

    app.debug("starting");
    db.info("query ok");
    db.warn("slow query");
    factory.get("app.web").debug("inherits DEBUG from app");
    factory.get("other").debug("inherits INFO from root: hidden");
    int[] built = {0};
    factory.get("other").log(Level.DEBUG, () -> "built " + ++built[0]);
    factory.get("other").log(Level.ERROR, () -> "built " + ++built[0]);
    System.out.println("messages built: " + built[0]);
  }
}
// ANCHOR_END: logger-demo
