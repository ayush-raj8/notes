package oop.conc;

// ANCHOR: thread-basics
/** Two ways to give a thread work: extend Thread, or pass a Runnable. start() creates the thread. */
public final class ThreadBasics {
  static final class Worker extends Thread {
    Worker(String name) {
      super(name);
    }

    @Override
    public void run() { // must be public: Thread.run() is public
      System.out.println("Worker.run on " + getName());
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Thread a = new Worker("t-extends");
    Thread b = new Thread(() -> System.out.println("lambda on " + Thread.currentThread().getName()), "t-runnable");
    a.start();
    a.join(); // wait: otherwise line order is not defined
    b.start();
    b.join();
    System.out.println("main continues after join");
  }
}
// ANCHOR_END: thread-basics
