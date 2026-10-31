package lld.ratelimit.exam;

import lld.ratelimit.*;

// ANCHOR: predict-factory
public class PredictFactory {
  public static void main(String[] args) {
    ManualClock clock = new ManualClock(0);
    LimiterConfig cfg = new LimiterConfig(5, 60_000);
    for (Algorithm a : Algorithm.values()) {
      System.out.println(a.ordinal() + " " + a.create(cfg, clock).getClass().getSimpleName());
    }
    try {
      new LimiterConfig(0, 1000);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(cfg);
  }
}
// ANCHOR_END: predict-factory
