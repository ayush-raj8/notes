package oop.ex.exam;

// ANCHOR: predict-loop-finally
public final class PredictLoopFinally {
  public static void main(String[] args) {
    for (int i = 0; i < 4; i++) {
      try {
        if (i == 1) {
          continue;
        }
        if (i == 2) {
          break;
        }
        System.out.println("body " + i);
      } finally {
        System.out.println("fin " + i);
      }
    }
    System.out.println("done");
  }
}
// ANCHOR_END: predict-loop-finally
