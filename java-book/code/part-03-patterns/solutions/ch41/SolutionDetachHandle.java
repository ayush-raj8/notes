package solutions.ch41;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Exercise 2: subscribe returns a handle that unsubscribes (no need to keep the observer). */
public final class SolutionDetachHandle<T> {
  private final List<Consumer<T>> observers = new ArrayList<>();

  Runnable subscribe(Consumer<T> o) {
    observers.add(o);
    return () -> observers.remove(o);
  }

  void emit(T t) {
    for (Consumer<T> o : List.copyOf(observers)) o.accept(t);
  }

  public static void main(String[] args) {
    SolutionDetachHandle<String> bus = new SolutionDetachHandle<>();
    Runnable undo = bus.subscribe(s -> System.out.println("got " + s));
    bus.emit("a");
    undo.run();
    bus.emit("b");
    System.out.println("end");
  }
}
