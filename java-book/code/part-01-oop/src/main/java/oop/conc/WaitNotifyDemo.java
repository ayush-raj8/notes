package oop.conc;

// ANCHOR: wait-notify
/** wait/notify: call inside synchronized on the same object; test the condition in a while loop. */
public final class WaitNotifyDemo {
  private final Object lock = new Object();
  private String message;

  String take() throws InterruptedException {
    synchronized (lock) {
      while (message == null) {
        lock.wait(); // releases lock, waits for notify
      }
      String m = message;
      message = null;
      return m;
    }
  }

  void put(String m) {
    synchronized (lock) {
      message = m;
      lock.notifyAll();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    WaitNotifyDemo box = new WaitNotifyDemo();
    Thread consumer = new Thread(() -> {
      try {
        System.out.println("got " + box.take());
      } catch (InterruptedException ignored) {
      }
    });
    consumer.start();
    box.put("hello");
    consumer.join();
  }
}
// ANCHOR_END: wait-notify
