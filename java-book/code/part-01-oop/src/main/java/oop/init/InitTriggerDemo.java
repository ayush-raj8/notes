package oop.init;

// ANCHOR: init-trigger
class Constants {
  static final int K = 10;
  static final String S = "s";
  static final int NOT_CONSTANT = Integer.parseInt("11");

  static {
    System.out.println("Constants static block");
  }
}

public final class InitTriggerDemo {
  public static void main(String[] args) {
    System.out.println("K=" + Constants.K);
    System.out.println("S=" + Constants.S);
    System.out.println("NOT_CONSTANT=" + Constants.NOT_CONSTANT);
  }
}
// ANCHOR_END: init-trigger
