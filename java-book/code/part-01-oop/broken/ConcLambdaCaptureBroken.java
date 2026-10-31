package oop.conc;

/** Intentionally invalid: a lambda cannot capture a local that is modified. */
public final class ConcLambdaCaptureBroken {
  public static void main(String[] args) throws InterruptedException {
    int counter = 0;
    Thread t = new Thread(() -> {
      counter++;
    });
    t.start();
    t.join();
  }
}
