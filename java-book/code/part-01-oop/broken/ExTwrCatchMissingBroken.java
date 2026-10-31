package oop.ex;

import java.io.ByteArrayOutputStream;

public final class ExTwrCatchMissingBroken {
  public static void main(String[] args) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      out.write(65);
      System.out.println(out.size());
    }
  }
}
