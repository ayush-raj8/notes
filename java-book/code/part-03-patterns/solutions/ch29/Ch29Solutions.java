package patterns.creational.factorymethod.solutions;

import java.util.Locale;
import patterns.creational.factorymethod.Notifier;
import patterns.creational.factorymethod.NotifierCreator;
import patterns.creational.factorymethod.RegistryFactory;

public final class Ch29Solutions {
  // ANCHOR: ch29-ex1
  /** Ex1: add a new product and creator without touching existing classes. */
  static final class PushCreator extends NotifierCreator {
    @Override
    protected Notifier create() {
      return msg -> "PUSH:" + msg;
    }
  }
  // ANCHOR_END: ch29-ex1

  // ANCHOR: ch29-ex2
  /** Ex2: static factory with a named constructor and validation. */
  record Percent(int value) {
    static Percent of(int v) {
      if (v < 0 || v > 100) {
        throw new IllegalArgumentException("0..100 expected: " + v);
      }
      return new Percent(v);
    }

    static Percent half() {
      return of(50);
    }
  }
  // ANCHOR_END: ch29-ex2

  // ANCHOR: ch29-ex3
  /** Ex3: simple (non-GoF) factory using a switch expression. */
  static Notifier simple(String kind) {
    return switch (kind.toLowerCase(Locale.ROOT)) {
      case "email" -> new patterns.creational.factorymethod.EmailNotifier();
      case "sms" -> new patterns.creational.factorymethod.SmsNotifier();
      default -> throw new IllegalArgumentException("unknown: " + kind);
    };
  }
  // ANCHOR_END: ch29-ex3

  // ANCHOR: ch29-ex4
  /** Ex4: creator whose factory method returns a different product per call. */
  static final class RoundRobinCreator extends NotifierCreator {
    private int calls;

    @Override
    protected Notifier create() {
      return (calls++ % 2 == 0) ? msg -> "A:" + msg : msg -> "B:" + msg;
    }
  }
  // ANCHOR_END: ch29-ex4

  // ANCHOR: ch29-ex5
  /** Ex5: registry that lets a caller add a kind at runtime. */
  static RegistryFactory withSlack() {
    return new RegistryFactory().register("slack", () -> msg -> "SLACK:" + msg);
  }
  // ANCHOR_END: ch29-ex5

  public static void main(String[] args) {
    System.out.println("ex1 " + new PushCreator().deliver("hi"));
    System.out.println("ex2 " + Percent.half().value());
    try {
      Percent.of(101);
    } catch (IllegalArgumentException e) {
      System.out.println("ex2 " + e.getMessage());
    }
    System.out.println("ex3 " + simple("SMS").send("x"));
    RoundRobinCreator rr = new RoundRobinCreator();
    System.out.println("ex4 " + rr.deliver("1") + " " + rr.deliver("2") + " " + rr.deliver("3"));
    System.out.println("ex5 " + withSlack().create("slack").send("go"));
  }
}
