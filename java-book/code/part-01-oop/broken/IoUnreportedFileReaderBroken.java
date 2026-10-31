package oop.io;

import java.io.FileReader;

public final class IoUnreportedFileReaderBroken {
  public static void main(String[] args) {
    FileReader r = new FileReader("data.txt");
    System.out.println(r);
  }
}
