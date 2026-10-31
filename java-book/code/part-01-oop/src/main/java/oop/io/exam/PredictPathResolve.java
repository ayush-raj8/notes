package oop.io.exam;

import java.nio.file.Path;

// ANCHOR: predict-path
public final class PredictPathResolve {
  public static void main(String[] args) {
    Path a = Path.of("data");
    System.out.println(a.resolve("x.txt"));
    System.out.println(a.resolve("/etc/x.txt"));
    System.out.println(Path.of("a/./b/../c").normalize());
    System.out.println(Path.of("a/b").relativize(Path.of("a/b/c/d")));
    System.out.println(Path.of("a/b/c.txt").getParent().getFileName());
  }
}
// ANCHOR_END: predict-path
