package solutions.ch41;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/** Exercise 1: an observable int that notifies only when the value changes. */
public final class SolutionObservableInt {
  private final List<IntConsumer> listeners = new ArrayList<>();
  private int value;

  void listen(IntConsumer l) {
    listeners.add(l);
  }

  void set(int v) {
    if (v == value) return;
    value = v;
    for (IntConsumer l : List.copyOf(listeners)) l.accept(v);
  }

  public static void main(String[] args) {
    SolutionObservableInt x = new SolutionObservableInt();
    x.listen(v -> System.out.println("now " + v));
    x.set(0);
    x.set(3);
    x.set(3);
    x.set(4);
  }
}
