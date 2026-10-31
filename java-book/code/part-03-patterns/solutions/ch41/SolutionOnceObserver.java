package solutions.ch41;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Exercise 5: an observer that removes itself after the first event (safe via snapshot). */
public final class SolutionOnceObserver {
  private final List<Consumer<String>> observers = new ArrayList<>();

  void once(Consumer<String> c) {
    Consumer<String>[] self = new Consumer[1];
    self[0] =
        m -> {
          observers.remove(self[0]);
          c.accept(m);
        };
    observers.add(self[0]);
  }

  void publish(String m) {
    for (Consumer<String> o : List.copyOf(observers)) o.accept(m);
  }

  public static void main(String[] args) {
    SolutionOnceObserver s = new SolutionOnceObserver();
    s.once(m -> System.out.println("first only: " + m));
    s.publish("a");
    s.publish("b");
    System.out.println("end");
  }
}
