package oop.ex;

// ANCHOR: static-init
public final class StaticInitFailureDemo {
  static final class Config {
    static final int VALUE = compute();

    static int compute() {
      throw new IllegalStateException("config missing");
    }
  }

  public static void main(String[] args) {
    try {
      System.out.println(Config.VALUE);
    } catch (ExceptionInInitializerError e) {
      System.out.println("first: " + e.getClass().getSimpleName());
      System.out.println("cause: " + e.getCause());
    }
    try {
      System.out.println(Config.VALUE);
    } catch (NoClassDefFoundError e) {
      System.out.println("second: " + e.getClass().getSimpleName());
    }
  }
}
// ANCHOR_END: static-init
