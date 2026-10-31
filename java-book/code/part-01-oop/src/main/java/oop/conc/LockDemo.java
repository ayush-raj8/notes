package oop.conc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// ANCHOR: lock-demo
/** ReentrantLock: explicit unlock in finally, tryLock with timeout, Condition instead of wait/notify. */
public final class LockDemo {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition ready = lock.newCondition();
  private boolean flag;

  void awaitFlag() throws InterruptedException {
    lock.lock();
    try {
      while (!flag) { // loop: guard against spurious wakeups
        ready.await(); // releases the lock while waiting
      }
      System.out.println("flag seen");
    } finally {
      lock.unlock(); // always in finally
    }
  }

  void setFlag() {
    lock.lock();
    try {
      flag = true;
      ready.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) throws Exception {
    LockDemo d = new LockDemo();
    CountDownLatch held = new CountDownLatch(1);
    CountDownLatch release = new CountDownLatch(1);
    Thread holder = new Thread(() -> {
      d.lock.lock();
      try {
        held.countDown();
        release.await();
      } catch (InterruptedException ignored) {
      } finally {
        d.lock.unlock();
      }
    });
    holder.start();
    held.await();
    System.out.println("tryLock while held: " + d.lock.tryLock()); // no blocking
    System.out.println("tryLock(10ms) while held: " + d.lock.tryLock(10, TimeUnit.MILLISECONDS));
    release.countDown();
    holder.join();
    System.out.println("tryLock after release: " + d.lock.tryLock());
    d.lock.unlock();

    Thread waiter = new Thread(() -> {
      try {
        d.awaitFlag();
      } catch (InterruptedException ignored) {
      }
    });
    waiter.start();
    d.setFlag();
    waiter.join();
  }
}
// ANCHOR_END: lock-demo
