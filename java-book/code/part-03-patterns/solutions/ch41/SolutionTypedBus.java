package solutions.ch41;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Exercise 4: observers choose the event type they receive. */
public final class SolutionTypedBus {
  private final Map<Class<?>, List<Consumer<Object>>> map = new HashMap<>();

  @SuppressWarnings("unchecked")
  <E> void on(Class<E> type, Consumer<E> c) {
    map.computeIfAbsent(type, k -> new ArrayList<>()).add(o -> c.accept((E) o));
  }

  void post(Object event) {
    for (Consumer<Object> c : map.getOrDefault(event.getClass(), List.of())) c.accept(event);
  }

  public static void main(String[] args) {
    SolutionTypedBus bus = new SolutionTypedBus();
    bus.on(String.class, s -> System.out.println("string " + s));
    bus.on(Integer.class, n -> System.out.println("int " + (n + 1)));
    bus.post("hi");
    bus.post(41);
    bus.post(2.5);
  }
}
