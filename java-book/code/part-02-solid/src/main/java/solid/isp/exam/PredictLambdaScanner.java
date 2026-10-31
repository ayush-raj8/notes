package solid.isp.exam;

// ANCHOR: predict-lambda-scanner
import solid.isp.after.DocScanner;
import solid.isp.after.Office;

public final class PredictLambdaScanner {
  public static void main(String[] args) {
    int[] calls = {0};
    DocScanner s = () -> "p" + (++calls[0]);
    System.out.println(Office.archive(s));
    System.out.println(Office.archive(s));
    System.out.println(Office.archive(() -> "x" + 1));
    System.out.println(calls[0]);
  }
}
// ANCHOR_END: predict-lambda-scanner
