package oop.io.solutions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import oop.io.IoTemp;

/** Count non-blank lines without loading the file into memory. */
public final class SolutionLineCount {
  static long nonBlank(Path file) throws IOException {
    try (Stream<String> lines = Files.lines(file)) {
      return lines.filter(l -> !l.isBlank()).count();
    }
  }

  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      Path f = dir.resolve("t.txt");
      Files.writeString(f, "a\n\n  \nb\nc\n");
      System.out.println(nonBlank(f));
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
