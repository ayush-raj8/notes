package oop.contracts;

public final class ContTwrScopeBroken {
  static final class R implements AutoCloseable {
    @Override
    public void close() {}
  }

  public static void main(String[] args) {
    try (R r = new R()) {
      System.out.println("in");
    }
    System.out.println(r);
  }
}
