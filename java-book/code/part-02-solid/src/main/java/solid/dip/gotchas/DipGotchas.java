package solid.dip.gotchas;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import solid.dip.after.domain.Mailer;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.LogMailer;

public final class DipGotchas {
  // ANCHOR: g1-di-without-dip
  /** Injected, but typed as the concrete class: dependency injection without inversion. */
  static final class ConcreteInjected {
    private final LogMailer mailer;

    ConcreteInjected(LogMailer mailer) {
      this.mailer = mailer;
    }

    int sent() {
      return mailer.sent.size();
    }
  }
  // ANCHOR_END: g1-di-without-dip

  // ANCHOR: g2-single-impl
  interface Calculator {
    int add(int a, int b);
  }

  static final class CalculatorImpl implements Calculator {
    public int add(int a, int b) {
      return a + b;
    }
  }
  // ANCHOR_END: g2-single-impl

  // ANCHOR: g3-locator
  /** A service locator hides dependencies: the constructor signature says "none". */
  static final class Locator {
    static final Map<Class<?>, Object> REGISTRY = new HashMap<>();
  }

  static final class LocatorService {
    private final Mailer mailer = (Mailer) Locator.REGISTRY.get(Mailer.class);

    void notifyOps() {
      mailer.send("ops", "hi");
    }
  }
  // ANCHOR_END: g3-locator

  // ANCHOR: g4-default-ctor
  /** A convenience constructor that picks the concrete class puts the hard-wiring back. */
  static final class ConvenientService {
    private final Mailer mailer;

    ConvenientService() {
      this(new LogMailer());
    }

    ConvenientService(Mailer mailer) {
      this.mailer = mailer;
    }

    String mailerType() {
      return mailer.getClass().getSimpleName();
    }
  }
  // ANCHOR_END: g4-default-ctor

  // ANCHOR: g5-clock
  /** LocalDate.now() is a dependency on the world; a Clock parameter makes it explicit. */
  static long ageDays(LocalDate born, Clock clock) {
    return LocalDate.now(clock).toEpochDay() - born.toEpochDay();
  }
  // ANCHOR_END: g5-clock

  public static void main(String[] args) throws Exception {
    // ANCHOR: g-main
    System.out.println("G1 param=" + ConcreteInjected.class.getDeclaredConstructors()[0].getParameterTypes()[0].getSimpleName()
        + " vs " + OrderService.class.getConstructors()[0].getParameterTypes()[0].getSimpleName());
    System.out.println("G2 " + new CalculatorImpl().add(2, 3));
    System.out.println("G3 ctor params=" + LocatorService.class.getDeclaredConstructors()[0].getParameterCount());
    try {
      new LocatorService().notifyOps();
    } catch (RuntimeException e) {
      System.out.println("G3 fails late: " + e.getClass().getSimpleName());
    }
    System.out.println("G4 " + new ConvenientService().mailerType());
    Clock c = Clock.fixed(Instant.parse("2026-10-31T00:00:00Z"), ZoneOffset.UTC);
    System.out.println("G5 " + ageDays(LocalDate.parse("2026-10-01"), c));
    // ANCHOR_END: g-main
  }
}
