package patterns.behavioral.chain.exam;

// ANCHOR: predict-catch-order
public final class PredictCatchOrder {
  static void run(RuntimeException toThrow) {
    try {
      throw toThrow;
    } catch (IllegalArgumentException e) {
      System.out.print("IAE ");
    } catch (RuntimeException e) {
      System.out.print("RTE ");
    } finally {
      System.out.print("finally ");
    }
    System.out.println("after");
  }

  public static void main(String[] args) {
    run(new NumberFormatException("n"));
    run(new IllegalStateException("s"));
    try {
      run(null);
    } catch (NullPointerException e) {
      System.out.println("NPE");
    }
  }
}
// ANCHOR_END: predict-catch-order
