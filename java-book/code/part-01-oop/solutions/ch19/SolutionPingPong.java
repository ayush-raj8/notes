package oop.conc.solutions;

/** Exercise 2: two threads print ping, pong, ping, pong, ping, pong in strict alternation. */
public final class SolutionPingPong {
  private final Object lock = new Object();
  private boolean pingTurn = true;

  void turn(String word, boolean mine, int rounds) throws InterruptedException {
    for (int i = 0; i < rounds; i++) {
      synchronized (lock) {
        while (pingTurn != mine) {
          lock.wait();
        }
        System.out.println(word);
        pingTurn = !pingTurn;
        lock.notifyAll();
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    SolutionPingPong p = new SolutionPingPong();
    Thread ping = new Thread(() -> run(p, "ping", true));
    Thread pong = new Thread(() -> run(p, "pong", false));
    pong.start();
    ping.start();
    ping.join();
    pong.join();
  }

  private static void run(SolutionPingPong p, String word, boolean mine) {
    try {
      p.turn(word, mine, 3);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
