package oop.conc;

import java.util.concurrent.locks.ReentrantLock;

// ANCHOR: reentrancy
/** Intrinsic locks and ReentrantLock are reentrant: the owner may lock again without blocking itself. */
public final class Reentrancy {
  private final ReentrantLock lock = new ReentrantLock();

  synchronized void outer() {
    System.out.println("outer holds this: " + Thread.holdsLock(this));
    inner(); // same thread, same lock: allowed
  }

  synchronized void inner() {
    System.out.println("inner holds this: " + Thread.holdsLock(this));
  }

  void withLock() {
    lock.lock();
    try {
      lock.lock(); // hold count 2
      try {
        System.out.println("hold count: " + lock.getHoldCount());
      } finally {
        lock.unlock();
      }
    } finally {
      lock.unlock();
    }
    System.out.println("locked after both unlocks: " + lock.isLocked());
  }

  public static void main(String[] args) {
    Reentrancy r = new Reentrancy();
    r.outer();
    r.withLock();
    System.out.println("main holds Reentrancy.class: " + Thread.holdsLock(Reentrancy.class));
  }
}
// ANCHOR_END: reentrancy
