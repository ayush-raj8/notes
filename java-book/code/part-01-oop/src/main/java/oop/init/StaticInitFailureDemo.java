package oop.init;

// ANCHOR: static-init-failure
class FailingStatic {
  static int value = Integer.parseInt("x");
}

public final class StaticInitFailureDemo {
  public static void main(String[] args) {
    try {
      System.out.println(FailingStatic.value);
    } catch (ExceptionInInitializerError e) {
      System.out.println("first: " + e.getClass().getSimpleName()
          + ", cause " + e.getCause().getClass().getSimpleName());
    }
    try {
      System.out.println(FailingStatic.value);
    } catch (NoClassDefFoundError e) {
      System.out.println("second: " + e.getClass().getSimpleName()
          + ", " + e.getMessage());
    }
  }
}
// ANCHOR_END: static-init-failure
