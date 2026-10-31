package oop.ex.exam;

import java.io.IOException;

// ANCHOR: compile-catch-exception-ok
public final class CompileCatchExceptionOk {
  static void a() {
    try {
      System.out.println("nothing throws");
    } catch (Exception e) { // legal: Exception includes RuntimeException
      System.out.println("unreachable at run time, legal at compile time");
    }
  }

  static void b() {
    try {
      System.out.println("nothing throws");
    } catch (Throwable t) { // legal for the same reason
    }
  }

  static void c() throws IOException {
    try {
      throw new IOException("io");
    } catch (Exception e) {
      throw e; // precise rethrow: only IOException, so `throws IOException` is enough
    }
  }

  public static void main(String[] args) {
    a();
    b();
    try {
      c();
    } catch (IOException e) {
      System.out.println("c threw " + e.getMessage());
    }
  }
}
// ANCHOR_END: compile-catch-exception-ok
