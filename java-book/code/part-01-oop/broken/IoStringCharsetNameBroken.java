package oop.io;

public final class IoStringCharsetNameBroken {
  public static void main(String[] args) {
    byte[] data = {104, 105};
    String s = new String(data, "UTF-8");
    System.out.println(s);
  }
}
