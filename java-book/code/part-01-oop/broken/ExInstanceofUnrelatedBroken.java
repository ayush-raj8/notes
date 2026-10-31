package oop.ex;

public final class ExInstanceofUnrelatedBroken {
  public static void main(String[] args) {
    StackOverflowError e = new StackOverflowError();
    System.out.println(e instanceof Exception);
  }
}
