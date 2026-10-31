package oop.ex;

import java.io.IOException;

public final class ExNeverThrownBroken {
  public static void main(String[] args) {
    try {
      System.out.println("nothing throws IOException here");
    } catch (IOException e) {
      System.out.println("unreachable");
    }
  }
}
