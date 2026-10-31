package oop.io;

import java.nio.file.Path;

public final class IoPathNewBroken {
  public static void main(String[] args) {
    Path p = new Path("a/b");
    System.out.println(p);
  }
}
