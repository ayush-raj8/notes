package oop.contracts;

public final class ContTwrNotAutoCloseableBroken {
  static final class Plain {
    void close() {}
  }

  public static void main(String[] args) {
    try (Plain p = new Plain()) {
      System.out.println("body");
    }
  }
}
