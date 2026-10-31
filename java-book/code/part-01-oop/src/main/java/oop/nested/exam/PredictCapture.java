package oop.nested.exam;

// ANCHOR: predict-capture
public final class PredictCapture {
  public static void main(String[] args) {
    int[] box = {1};
    String s = "a";
    Runnable r =
        new Runnable() {
          @Override
          public void run() {
            box[0]++;
            System.out.println(s + box[0]);
          }
        };
    r.run();
    r.run();
    System.out.println(box[0]);
  }
}
// ANCHOR_END: predict-capture
