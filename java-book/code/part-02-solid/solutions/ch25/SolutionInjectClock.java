package solid.dip.solutions;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

/** Exercise 1: make a greeting deterministic by injecting a Clock. */
public final class SolutionInjectClock {
  record Greeter(Clock clock) {
    String greet() {
      return LocalTime.now(clock).getHour() < 12 ? "good morning" : "good afternoon";
    }
  }

  public static void main(String[] args) {
    Clock am = Clock.fixed(Instant.parse("2026-10-31T08:00:00Z"), ZoneOffset.UTC);
    Clock pm = Clock.fixed(Instant.parse("2026-10-31T15:00:00Z"), ZoneOffset.UTC);
    System.out.println(new Greeter(am).greet());
    System.out.println(new Greeter(pm).greet());
  }
}
