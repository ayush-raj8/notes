package oop.ex;

// ANCHOR: stack-trace
public final class StackTraceDemo {
  static void c() {
    throw new IllegalStateException("deep");
  }

  static void b() {
    try {
      c();
    } catch (IllegalStateException e) {
      throw new RuntimeException("wrapped", e);
    }
  }

  static void a() {
    b();
  }

  public static void main(String[] args) {
    try {
      a();
    } catch (RuntimeException e) {
      e.printStackTrace(System.out);
      StackTraceElement top = e.getStackTrace()[0];
      System.out.println(top.getMethodName() + " " + top.getLineNumber());
      System.out.println(e.getCause().getStackTrace().length);
    }
  }
}
// ANCHOR_END: stack-trace
