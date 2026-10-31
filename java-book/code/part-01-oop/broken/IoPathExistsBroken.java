package oop.io;

import java.nio.file.Path;

public final class IoPathExistsBroken {
  public static void main(String[] args) {
    Path p = Path.of("a.txt");
    System.out.println(p.exists());
  }
}
