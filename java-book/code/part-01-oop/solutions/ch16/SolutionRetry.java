package oop.ex.solutions;

import java.io.IOException;

/** Retry a flaky call up to 3 times; rethrow the last failure with the earlier ones suppressed. */
public final class SolutionRetry {
  static int calls = 0;

  static String flaky() throws IOException {
    calls++;
    if (calls < 4) {
      throw new IOException("attempt " + calls);
    }
    return "ok";
  }

  static String withRetry() throws IOException {
    IOException last = null;
    for (int i = 0; i < 3; i++) {
      try {
        return flaky();
      } catch (IOException e) {
        if (last != null) {
          e.addSuppressed(last);
        }
        last = e;
      }
    }
    throw last;
  }

  public static void main(String[] args) {
    try {
      withRetry();
    } catch (IOException e) {
      System.out.println(e.getMessage());
      System.out.println(e.getSuppressed().length);
    }
  }
}
