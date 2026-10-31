package lld.logger.exam;

import java.util.concurrent.atomic.AtomicInteger;
import lld.logger.*;

// ANCHOR: predict-lazy
public class PredictLazy {
  public static void main(String[] args) {
    LoggerFactory f = new LoggerFactory(Level.INFO, () -> 0L);
    AtomicInteger built = new AtomicInteger();
    Logger log = f.get("x");
    log.log(Level.DEBUG, () -> "debug " + built.incrementAndGet());
    System.out.println(built.get());
    log.log(Level.WARN, () -> "warn " + built.incrementAndGet());
    System.out.println(built.get());
    log.log(Level.DEBUG, "eager " + built.incrementAndGet());
    System.out.println(built.get());
  }
}
// ANCHOR_END: predict-lazy
