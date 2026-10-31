package solutions.ch44;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Exercise 2: a file-reading template; subclasses only decide what a line contributes. */
public final class SolutionLineProcessor {
  abstract static class LineProcessor {
    final long run(Path p) throws IOException {
      long total = 0;
      try (BufferedReader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
        String line;
        while ((line = r.readLine()) != null) {
          total += score(line);
        }
      }
      return total;
    }

    abstract long score(String line);
  }

  public static void main(String[] args) throws IOException {
    Path f = Files.createTempFile("lines", ".txt");
    try {
      Files.writeString(f, "alpha\nbe\n\ngamma ray\n");
      long chars =
          new LineProcessor() {
            long score(String l) {
              return l.length();
            }
          }.run(f);
      long words =
          new LineProcessor() {
            long score(String l) {
              return l.isBlank() ? 0 : l.trim().split("\\s+").length;
            }
          }.run(f);
      System.out.println("chars=" + chars + " words=" + words);
    } finally {
      Files.deleteIfExists(f);
    }
  }
}
