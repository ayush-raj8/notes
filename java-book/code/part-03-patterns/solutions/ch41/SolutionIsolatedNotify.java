package solutions.ch41;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Exercise 3: one failing observer must not stop the others. */
public final class SolutionIsolatedNotify {
  private final List<Consumer<String>> observers = new ArrayList<>();

  void attach(Consumer<String> o) {
    observers.add(o);
  }

  void publish(String msg) {
    for (Consumer<String> o : List.copyOf(observers)) {
      try {
        o.accept(msg);
      } catch (RuntimeException e) {
        System.out.println("observer failed: " + e.getMessage());
      }
    }
  }

  public static void main(String[] args) {
    SolutionIsolatedNotify s = new SolutionIsolatedNotify();
    s.attach(m -> System.out.println("one " + m));
    s.attach(
        m -> {
          throw new IllegalStateException("boom");
        });
    s.attach(m -> System.out.println("three " + m));
    s.publish("hi");
  }
}
