import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lld.logger.*;

/** Exercises 1-5 of chapter 56. */
public class SolutionLogger {
  // Ex 1: an appender that only forwards events at or above a threshold (Decorator).
  static final class ThresholdAppender implements Appender {
    private final Level min;
    private final Appender next;

    ThresholdAppender(Level min, Appender next) {
      this.min = min;
      this.next = next;
    }

    public void append(LogEvent e) {
      if (e.level().atLeast(min)) {
        next.append(e);
      }
    }
  }

  // Ex 2: a formatter as a lambda.
  static final LogFormatter SHOUT = e -> e.level() + "! " + e.message().toUpperCase();

  public static void main(String[] args) {
    AtomicLong t = new AtomicLong();
    LoggerFactory f = new LoggerFactory(Level.DEBUG, t::incrementAndGet);
    MemoryAppender all = new MemoryAppender(new PatternFormatter("{ts} {level} {msg}"));
    MemoryAppender errors = new MemoryAppender(SHOUT);
    f.root().addAppender(all);
    f.root().addAppender(new ThresholdAppender(Level.WARN, errors));

    Logger log = f.get("billing.invoice");
    log.debug("rendering");
    log.warn("late payment");
    log.error("charge failed");
    System.out.println(all.lines());
    System.out.println(errors.lines());

    // Ex 3: per-package level without touching code that logs.
    f.get("billing").setLevel(Level.ERROR);
    log.warn("hidden now");
    System.out.println(all.lines().size());

    // Ex 4: lazy message avoided entirely when disabled.
    long[] work = {0};
    log.log(Level.DEBUG, () -> "expensive " + (++work[0]));
    System.out.println("work=" + work[0]);

    // Ex 5: the effective level chain.
    System.out.println(List.of(f.get("billing.invoice").effectiveLevel(), f.get("other").effectiveLevel()));
  }
}
