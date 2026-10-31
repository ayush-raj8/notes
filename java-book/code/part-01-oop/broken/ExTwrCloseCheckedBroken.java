package oop.ex;

public class ExTwrCloseCheckedBroken {
  static class Res implements AutoCloseable {
    @Override
    public void close() throws Exception {}
  }

  public static void main(String[] args) {
    try (Res r = new Res()) {
      System.out.println("body");
    }
  }
}
