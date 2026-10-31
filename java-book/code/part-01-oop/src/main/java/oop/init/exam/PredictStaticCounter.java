package oop.init.exam;

// ANCHOR: predict-static-counter
class Id {
  static int next = 1;
  final int id = next++;
}

public final class PredictStaticCounter {
  public static void main(String[] args) {
    new Id();
    new Id();
    System.out.println(new Id().id);
  }
}
// ANCHOR_END: predict-static-counter
