package patterns.behavioral.visitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public final class FileTreeDemo {
  // ANCHOR: tree-demo
  public static void main(String[] args) throws IOException {
    Path root = Files.createTempDirectory("tree");
    try {
      Files.writeString(root.resolve("a.txt"), "hello");
      Files.createDirectories(root.resolve("sub/deep"));
      Files.writeString(root.resolve("sub/b.txt"), "abc");
      Files.writeString(root.resolve("sub/deep/d.txt"), "0123456789");
      Files.createDirectories(root.resolve("skip"));
      Files.writeString(root.resolve("skip/c.txt"), "x".repeat(100));

      SizeVisitor v = new SizeVisitor(root);
      Files.walkFileTree(root, v);
      System.out.println("files " + v.files());
      System.out.println("dirs=" + v.dirs() + " bytes=" + v.bytes());
    } finally {
      try (Stream<Path> walk = Files.walk(root)) {
        for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) Files.delete(p);
      }
    }
  }
  // ANCHOR_END: tree-demo
}
