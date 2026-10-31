package patterns.behavioral.mediator.exam;

import patterns.behavioral.mediator.TempSync;

// ANCHOR: predict-sync-guard
public final class PredictSyncGuard {
  public static void main(String[] args) {
    TempSync t = new TempSync();
    t.celsius.set(100);
    t.celsius.set(100);
    System.out.println(t.fahrenheit.value() + " " + t.notifications());
    t.fahrenheit.set(32);
    System.out.println(t.celsius.value() + " " + t.notifications());
    t.celsius.set(0);
    System.out.println(t.fahrenheit.value() + " " + t.notifications());
    t.celsius.set(37);
    t.fahrenheit.set(t.fahrenheit.value());
    System.out.println(t.fahrenheit.value() + " " + t.celsius.value() + " " + t.notifications());
  }
}
// ANCHOR_END: predict-sync-guard
