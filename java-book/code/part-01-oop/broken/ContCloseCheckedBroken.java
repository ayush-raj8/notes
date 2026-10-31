package oop.contracts;

public final class ContCloseCheckedBroken {
  static final class R implements AutoCloseable {
    @Override
    public void close() throws Exception {
      System.out.println("close");
    }
  }

  public static void main(String[] args) {
    try (R r = new R()) {
      System.out.println("body");
    }
  }
}
