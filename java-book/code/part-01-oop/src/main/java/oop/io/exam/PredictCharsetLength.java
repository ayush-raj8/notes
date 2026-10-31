package oop.io.exam;

import java.nio.charset.StandardCharsets;

// ANCHOR: predict-charset
public final class PredictCharsetLength {
  public static void main(String[] args) {
    String s = "A\u00e9\u20ac";
    System.out.println(s.length());
    System.out.println(s.getBytes(StandardCharsets.UTF_8).length);
    System.out.println(s.getBytes(StandardCharsets.ISO_8859_1).length);
    System.out.println(java.util.Arrays.toString(s.getBytes(StandardCharsets.ISO_8859_1)));
  }
}
// ANCHOR_END: predict-charset
