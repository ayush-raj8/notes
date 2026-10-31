package patterns.behavioral.visitor.exam;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.stream.Stream;

// ANCHOR: predict-file-visitor
public final class PredictFileVisitor {
  static int pre;
  static int post;
  static int files;

  public static void main(String[] args) throws IOException {
    Path root = Files.createTempDirectory("pv");
    try {
      Files.createDirectories(root.resolve("a/b"));
      Files.writeString(root.resolve("a/one.txt"), "1");
      Files.writeString(root.resolve("a/b/two.txt"), "22");
      Files.writeString(root.resolve("top.txt"), "333");

      Files.walkFileTree(
          root,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes a) {
              pre++;
              return d.getFileName().toString().equals("b")
                  ? FileVisitResult.SKIP_SUBTREE
                  : FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path f, BasicFileAttributes a) {
              files++;
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path d, IOException e) {
              post++;
              return FileVisitResult.CONTINUE;
            }
          });
      System.out.println("pre=" + pre + " post=" + post + " files=" + files);
    } finally {
      try (Stream<Path> walk = Files.walk(root)) {
        for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) Files.delete(p);
      }
    }
  }
}
// ANCHOR_END: predict-file-visitor
