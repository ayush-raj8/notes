package oop.io;

import java.nio.file.Path;

// ANCHOR: path-ops
public final class PathOpsDemo {
  public static void main(String[] args) {
    Path base = Path.of("project", "src");
    System.out.println(base);
    System.out.println(base.resolve("Main.java"));
    System.out.println(base.resolve("/abs/Main.java"));
    System.out.println(Path.of("a", "b", "..", "c").normalize());
    System.out.println(Path.of("a/b").relativize(Path.of("a/c/d")));
    Path p = Path.of("x/y/z.txt");
    System.out.println(p.getParent() + " | " + p.getFileName() + " | " + p.getNameCount());
    System.out.println(p.startsWith("x") + " " + p.endsWith("z.txt") + " " + p.endsWith("txt"));
    System.out.println(p.isAbsolute());
    System.out.println(p.resolveSibling("w.txt"));
    System.out.println(Path.of("a").resolve(""));
  }
}
// ANCHOR_END: path-ops
