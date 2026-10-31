package oop.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;

public final class IoReaderBufferedWrongTypeBroken {
  public static void main(String[] args) throws IOException {
    BufferedReader r = new BufferedReader(new FileInputStream("a.txt"));
    System.out.println(r.readLine());
  }
}
