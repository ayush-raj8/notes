package patterns.creational.singleton.exam;

import patterns.creational.singleton.SynchronizedSingleton;

// ANCHOR: predict-sync-count
public final class PredictSyncCount {
  public static void main(String[] args) {
    System.out.println(SynchronizedSingleton.created());
    SynchronizedSingleton a = SynchronizedSingleton.getInstance();
    SynchronizedSingleton b = SynchronizedSingleton.getInstance();
    System.out.println(a == b);
    System.out.println(SynchronizedSingleton.created());
  }
}
// ANCHOR_END: predict-sync-count
