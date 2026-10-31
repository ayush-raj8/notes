package patterns.behavioral.chain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class PathChecksDemo {
  // ANCHOR: path-demo
  public static void main(String[] args) throws IOException {
    Path dir = Files.createTempDirectory("checks");
    Path small = dir.resolve("a.txt");
    Path big = dir.resolve("big.txt");
    Path wrong = dir.resolve("b.bin");
    Path ghost = dir.resolve("ghost.txt");
    try {
      Files.writeString(small, "hello");
      Files.writeString(big, "x".repeat(2_000));
      Files.writeString(wrong, "bin");

      List<PathChecks.PathCheck> chain =
          List.of(PathChecks.exists(), PathChecks.extension(".txt"), PathChecks.maxSize(100));
      for (Path p : List.of(small, big, wrong, ghost)) {
        System.out.println(
            p.getFileName() + " -> " + PathChecks.firstFailure(chain, p).orElse("ok"));
      }
    } finally {
      for (Path p : List.of(small, big, wrong)) Files.deleteIfExists(p);
      Files.deleteIfExists(dir);
    }
  }
  // ANCHOR_END: path-demo
}
