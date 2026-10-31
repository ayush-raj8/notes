package oop.ex;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class ExMultiCatchRelatedBroken {
  static void open() throws IOException {
    throw new FileNotFoundException("f");
  }

  public static void main(String[] args) {
    try {
      open();
    } catch (FileNotFoundException | IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
