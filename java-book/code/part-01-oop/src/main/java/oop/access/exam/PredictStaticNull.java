package oop.access.exam;

// ANCHOR: predict-static-null
public final class PredictStaticNull {
  static String hello() {
    return "hi";
  }

  String inst() {
    return "inst";
  }

  public static void main(String[] args) {
    PredictStaticNull p = null;
    System.out.println(p.hello());
    System.out.println(p.inst());
    System.out.println("end");
  }
}
// ANCHOR_END: predict-static-null
