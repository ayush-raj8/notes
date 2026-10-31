package oop.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class IoReadIntToCharBroken {
  public static void main(String[] args) throws IOException {
    InputStream in = new ByteArrayInputStream(new byte[] {65});
    char c = in.read();
    System.out.println(c);
  }
}
