package oop.conc.exam;

// ANCHOR: compile-ok-sync-not-inherited
/** Compiles. synchronized is not part of the signature: an override may drop it, and then it no longer locks. */
public final class CompileOkSyncNotInherited {
  static class Base {
    synchronized void work() {
      System.out.println("Base holds lock: " + Thread.holdsLock(this));
    }
  }

  static class Derived extends Base {
    @Override
    void work() { // no 'synchronized'
      System.out.println("Derived holds lock: " + Thread.holdsLock(this));
    }
  }

  public static void main(String[] args) {
    new Base().work();
    new Derived().work();
  }
}
// ANCHOR_END: compile-ok-sync-not-inherited
