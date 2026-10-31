package oop.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

// ANCHOR: twr-files
public final class TwrFilesDemo {
  static void copyUpper(Path from, Path to) throws IOException {
    try (BufferedReader in = Files.newBufferedReader(from, StandardCharsets.UTF_8);
        BufferedWriter out = Files.newBufferedWriter(to, StandardCharsets.UTF_8)) {
      String line;
      while ((line = in.readLine()) != null) {
        out.write(line.toUpperCase());
        out.newLine();
      }
    }
  }

  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      Path a = dir.resolve("a.txt");
      Path b = dir.resolve("b.txt");
      Files.writeString(a, "alpha\nbeta\n");
      copyUpper(a, b);
      System.out.print(Files.readString(b));
      try (Stream<String> lines = Files.lines(b)) {
        System.out.println(lines.filter(s -> s.startsWith("B")).count());
      }
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
// ANCHOR_END: twr-files
