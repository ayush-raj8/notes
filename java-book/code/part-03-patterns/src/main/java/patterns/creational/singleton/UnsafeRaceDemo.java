package patterns.creational.singleton;

// ANCHOR: unsafe-race-demo
public final class UnsafeRaceDemo {
  public static void main(String[] args) throws Exception {
    LazyUnsafeSingleton[] seen = new LazyUnsafeSingleton[2];
    Thread t0 = new Thread(() -> seen[0] = get());
    Thread t1 = new Thread(() -> seen[1] = get());
    t0.start();
    t1.start();
    t0.join();
    t1.join();
    System.out.println("created=" + LazyUnsafeSingleton.CREATED.get());
    System.out.println("same=" + (seen[0] == seen[1]));
  }

  private static LazyUnsafeSingleton get() {
    try {
      return LazyUnsafeSingleton.getInstance();
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }
}
// ANCHOR_END: unsafe-race-demo
