package oop.conc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

// ANCHOR: deadlock-demo
/** Opposite lock order + both hold the first lock = circular wait. tryLock(timeout) turns a hang into a result:
 * t1 gives up after 100 ms and releases its lock, so t2 (willing to wait 2 s) then gets both. */
public final class DeadlockDemo {
  public static void main(String[] args) throws InterruptedException {
    ReentrantLock a = new ReentrantLock();
    ReentrantLock b = new ReentrantLock();
    CountDownLatch bothHold = new CountDownLatch(2);
    List<String> log = new CopyOnWriteArrayList<>();

    Thread t1 = new Thread(() -> grab("t1", a, b, 100, bothHold, log));
    Thread t2 = new Thread(() -> grab("t2", b, a, 2000, bothHold, log));
    t1.start(); t2.start(); t1.join(); t2.join();
    log.stream().sorted().forEach(System.out::println);
  }

  static void grab(String name, ReentrantLock first, ReentrantLock second, long waitMs, CountDownLatch bothHold, List<String> log) {
    first.lock();
    try {
      bothHold.countDown();
      bothHold.await(); // now each thread holds one lock and wants the other
      if (second.tryLock(waitMs, TimeUnit.MILLISECONDS)) {
        second.unlock();
        log.add(name + " got both");
      } else {
        log.add(name + " timed out on second lock");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      first.unlock();
    }
  }
}
// ANCHOR_END: deadlock-demo
