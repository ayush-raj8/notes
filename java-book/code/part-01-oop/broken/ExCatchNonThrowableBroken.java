package oop.ex;

public class ExCatchNonThrowableBroken {
  public static void main(String[] args) {
    try {
      System.out.println("x");
    } catch (String s) {
      System.out.println(s);
    }
  }
}
