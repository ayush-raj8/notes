package oop.ex.exam;

// ANCHOR: predict-catch-order
public final class PredictCatchOrder {
  static void run(Runnable r) {
    try {
      r.run();
      System.out.println("3");
    } catch (ClassCastException e) {
      System.out.println("cce");
    } catch (NullPointerException e) {
      System.out.println("npe");
    } catch (RuntimeException e) {
      System.out.println("re");
    }
  }

  public static void main(String[] args) {
    run(() -> {});
    run(() -> {
      Object o = "s";
      Integer i = (Integer) o;
    });
    run(() -> {
      String s = null;
      s.length();
    });
  }
}
// ANCHOR_END: predict-catch-order
