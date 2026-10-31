package oop.ex;

import java.io.IOException;

public final class ExMultiCatchAssignBroken {
  static void work(int n) throws IOException {
    if (n > 0) {
      throw new IOException("io");
    }
  }

  public static void main(String[] args) {
    try {
      work(1);
    } catch (IOException | IllegalStateException e) {
      e = new IOException("replaced");
      System.out.println(e.getMessage());
    }
  }
}
