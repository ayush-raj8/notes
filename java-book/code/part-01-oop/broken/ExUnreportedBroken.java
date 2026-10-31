package oop.ex;

import java.io.IOException;

public final class ExUnreportedBroken {
  static void load() throws IOException {
    throw new IOException("x");
  }

  public static void main(String[] args) {
    load();
  }
}
