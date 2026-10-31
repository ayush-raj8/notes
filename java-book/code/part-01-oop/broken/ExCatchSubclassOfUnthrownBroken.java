package oop.ex;

import java.io.FileNotFoundException;

public final class ExCatchSubclassOfUnthrownBroken {
  static void work() {}

  public static void main(String[] args) {
    try {
      work();
    } catch (FileNotFoundException e) {
      System.out.println("fnf");
    }
  }
}
