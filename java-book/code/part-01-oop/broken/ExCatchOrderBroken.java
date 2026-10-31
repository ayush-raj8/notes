package oop.ex;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class ExCatchOrderBroken {
  static void open() throws FileNotFoundException {
    throw new FileNotFoundException("f");
  }

  public static void main(String[] args) {
    try {
      open();
    } catch (IOException e) {
      System.out.println("io");
    } catch (FileNotFoundException e) {
      System.out.println("fnf");
    }
  }
}
