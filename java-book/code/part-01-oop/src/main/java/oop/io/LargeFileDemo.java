package oop.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

// ANCHOR: large-file
public final class LargeFileDemo {
  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      Path big = dir.resolve("big.txt");
      try (BufferedWriter w = Files.newBufferedWriter(big)) {
        for (int i = 1; i <= 100_000; i++) {
          w.write("line " + i);
          w.newLine();
        }
      }
      System.out.println("size > 1MB: " + (Files.size(big) > 1_000_000));

      try (Stream<String> lines = Files.lines(big)) {
        System.out.println("lines (stream): " + lines.count());
      }

      long evens = 0;
      try (BufferedReader r = Files.newBufferedReader(big)) {
        String line;
        int n = 0;
        while ((line = r.readLine()) != null) {
          if (++n % 2 == 0) {
            evens++;
          }
        }
      }
      System.out.println("even lines: " + evens);

      long total = 0;
      byte[] buf = new byte[8192];
      try (InputStream in = Files.newInputStream(big)) {
        int n;
        while ((n = in.read(buf)) != -1) {
          total += n;
        }
      }
      System.out.println("chunked total equals size: " + (total == Files.size(big)));

      Path copy = dir.resolve("copy.txt");
      Files.copy(big, copy);
      System.out.println("copy equal size: " + (Files.size(copy) == Files.size(big)));
      System.out.println("mismatch: " + Files.mismatch(big, copy));
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
// ANCHOR_END: large-file
