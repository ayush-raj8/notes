package solutions.ch48;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.stream.Stream;

/** Exercise 4: count lines of every .txt file in a real directory tree. */
public final class SolutionLineCounter {
  public static void main(String[] args) throws IOException {
    Path root = Files.createTempDirectory("lc");
    try {
      Files.createDirectories(root.resolve("d"));
      Files.writeString(root.resolve("a.txt"), "1\n2\n3\n");
      Files.writeString(root.resolve("d/b.txt"), "x\ny\n");
      Files.writeString(root.resolve("d/c.md"), "ignored\nignored\n");
      long[] lines = {0};
      Files.walkFileTree(
          root,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path f, BasicFileAttributes a) throws IOException {
              if (f.toString().endsWith(".txt")) {
                try (Stream<String> s = Files.lines(f)) {
                  lines[0] += s.count();
                }
              }
              return FileVisitResult.CONTINUE;
            }
          });
      System.out.println("txt lines=" + lines[0]);
    } finally {
      try (Stream<Path> walk = Files.walk(root)) {
        for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) Files.delete(p);
      }
    }
  }
}
