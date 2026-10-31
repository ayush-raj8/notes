package oop.memory.exam;

// ANCHOR: predict-var
public final class PredictVar {
  public static void main(String[] args) {
    var a = 1;
    var b = a + 2;
    System.out.println(b);
    System.out.println(((Object) b).getClass().getSimpleName());
  }
}
// ANCHOR_END: predict-var
