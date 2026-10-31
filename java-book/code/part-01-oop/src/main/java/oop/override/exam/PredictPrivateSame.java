package oop.override.exam;

// ANCHOR: predict-private-same
class PrivSameA {
  void run() {
    System.out.println(who());
  }

  private String who() {
    return "A";
  }
}

class PrivSameB extends PrivSameA {
  String who() {
    return "B";
  }
}

public final class PredictPrivateSame {
  public static void main(String[] args) {
    PrivSameB b = new PrivSameB();
    b.run();
    System.out.println(b.who());
  }
}
// ANCHOR_END: predict-private-same
