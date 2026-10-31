package oop.ex.exam;

// ANCHOR: predict-finally-ref
public final class PredictFinallyRef {
  static StringBuilder h() {
    StringBuilder sb = new StringBuilder("a");
    try {
      return sb;
    } finally {
      sb.append("b");
      sb = new StringBuilder("z");
    }
  }

  public static void main(String[] args) {
    System.out.println(h());
  }
}
// ANCHOR_END: predict-finally-ref
