package patterns.structural.adapter.solutions;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import patterns.structural.adapter.LegacyPrinter;
import patterns.structural.adapter.Printer;

public final class Ch33Solutions {
  // ANCHOR: ch33-ex1
  /** Ex1: Enumeration -> Iterator. */
  static <T> Iterator<T> iteratorOf(Enumeration<T> e) {
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return e.hasMoreElements();
      }

      @Override
      public T next() {
        if (!e.hasMoreElements()) {
          throw new NoSuchElementException();
        }
        return e.nextElement();
      }
    };
  }
  // ANCHOR_END: ch33-ex1

  // ANCHOR: ch33-ex2
  /** Ex2: Runnable -> Callable<Void>. */
  static Callable<Void> callableOf(Runnable r) {
    return () -> {
      r.run();
      return null;
    };
  }
  // ANCHOR_END: ch33-ex2

  // ANCHOR: ch33-ex3
  /** Ex3: lambda adapter for a one-method target. */
  static Printer upper(LegacyPrinter legacy) {
    return text -> legacy.printLegacy(text.toUpperCase());
  }
  // ANCHOR_END: ch33-ex3

  // ANCHOR: ch33-ex4
  /** Ex4: two-way adapter: both interfaces implemented over one state. */
  interface Metres {
    double metres();
  }

  interface Feet {
    double feet();
  }

  static final class Length implements Metres, Feet {
    private final double metres;

    Length(double metres) {
      this.metres = metres;
    }

    @Override
    public double metres() {
      return metres;
    }

    @Override
    public double feet() {
      return metres * 3.28084;
    }
  }
  // ANCHOR_END: ch33-ex4

  // ANCHOR: ch33-ex5
  /** Ex5: adapter that adds behaviour the adaptee lacks (null-safe). */
  static Printer nullSafe(LegacyPrinter legacy) {
    return text -> legacy.printLegacy(text == null ? "" : text);
  }
  // ANCHOR_END: ch33-ex5

  public static void main(String[] args) throws Exception {
    Iterator<String> it = iteratorOf(java.util.Collections.enumeration(List.of("a", "b")));
    System.out.println("ex1 " + it.next() + it.next() + it.hasNext());
    StringBuilder log = new StringBuilder();
    callableOf(() -> log.append("ran")).call();
    System.out.println("ex2 " + log);
    System.out.println("ex3 " + upper(new LegacyPrinter()).print("ab"));
    System.out.println("ex4 " + new Length(2).metres() + " " + Math.round(new Length(2).feet() * 100) / 100.0);
    System.out.println("ex5 " + nullSafe(new LegacyPrinter()).print(null));
  }
}
