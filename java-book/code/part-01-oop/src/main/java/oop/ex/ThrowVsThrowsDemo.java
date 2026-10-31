package oop.ex;

import java.io.IOException;

// ANCHOR: throw-vs-throws
public final class ThrowVsThrowsDemo {
  static void declares() throws IOException {
    System.out.println("declares: no throw executed");
  }

  static void throwsUnchecked() {
    throw new UnsupportedOperationException("no throws clause needed");
  }

  static void throwNull() {
    RuntimeException e = null;
    throw e;
  }

  public static void main(String[] args) throws IOException {
    declares();
    try {
      throwsUnchecked();
    } catch (UnsupportedOperationException e) {
      System.out.println(e.getMessage());
    }
    try {
      throwNull();
    } catch (NullPointerException e) {
      System.out.println("throw null -> NullPointerException");
    }
  }
}
// ANCHOR_END: throw-vs-throws
