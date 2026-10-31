package oop.io.exam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

// ANCHOR: predict-read-byte
public final class PredictReadByte {
  public static void main(String[] args) throws IOException {
    byte[] data = {(byte) 0xFF, 0, 65};
    InputStream in = new ByteArrayInputStream(data);
    System.out.println(data[0]);
    System.out.println(in.read());
    System.out.println(in.read());
    System.out.println((char) in.read());
    System.out.println(in.read());
    System.out.println(in.read());
  }
}
// ANCHOR_END: predict-read-byte
