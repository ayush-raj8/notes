package oop.ex;

import java.io.IOException;

// ANCHOR: multi-catch
public final class MultiCatchDemo {
  static void risky(int mode) throws IOException {
    switch (mode) {
      case 0 -> throw new IOException("io");
      case 1 -> throw new IllegalArgumentException("arg");
      case 2 -> throw new ArithmeticException("math");
      default -> System.out.println("ok");
    }
  }

  static void preciseRethrow(int mode) throws IOException {
    try {
      risky(mode);
    } catch (Exception e) {
      System.out.println("log " + e.getClass().getSimpleName());
      throw e;
    }
  }

  public static void main(String[] args) {
    for (int mode = 0; mode < 4; mode++) {
      try {
        risky(mode);
      } catch (IOException | IllegalArgumentException e) {
        System.out.println("multi " + e.getClass().getSimpleName());
      } catch (RuntimeException e) {
        System.out.println("runtime " + e.getClass().getSimpleName());
      }
    }
    try {
      preciseRethrow(0);
    } catch (IOException e) {
      System.out.println("rethrown " + e.getMessage());
    }
  }
}
// ANCHOR_END: multi-catch
