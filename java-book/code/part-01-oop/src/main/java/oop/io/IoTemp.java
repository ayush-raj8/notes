package oop.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/** Temp-directory helper so every demo is repeatable and leaves nothing behind. */
public final class IoTemp {
  private IoTemp() {}

  public static Path newDir() throws IOException {
    return Files.createTempDirectory("javabook-io");
  }

  public static void deleteTree(Path root) throws IOException {
    if (!Files.exists(root)) {
      return;
    }
    try (Stream<Path> walk = Files.walk(root)) {
      walk.sorted(Comparator.reverseOrder()).forEach(p -> {
        try {
          Files.delete(p);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      });
    }
  }
}
