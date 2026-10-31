package oop.conc.solutions;

import java.util.ArrayDeque;
import java.util.Deque;

/** Writing prompt 1: thread-safe stack whose pop blocks while empty. */
public final class WritingSyncStack<T> {
  private final Deque<T> items = new ArrayDeque<>();

  public synchronized void push(T x) {
    items.push(x);
    notifyAll();
  }

  public synchronized T pop() throws InterruptedException {
    while (items.isEmpty()) {
      wait();
    }
    return items.pop();
  }

  public static void main(String[] args) throws InterruptedException {
    WritingSyncStack<String> s = new WritingSyncStack<>();
    Thread t = new Thread(() -> {
      try {
        System.out.println("popped " + s.pop());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });
    t.start();
    s.push("x");
    t.join();
  }
}
