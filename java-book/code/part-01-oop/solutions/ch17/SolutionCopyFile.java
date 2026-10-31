package oop.io.solutions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import oop.io.IoTemp;

/** Copy with a buffer; the byte count comes from read(), not buf.length. */
public final class SolutionCopyFile {
  static long copy(Path from, Path to) throws IOException {
    long total = 0;
    byte[] buf = new byte[1024];
    try (InputStream in = Files.newInputStream(from); OutputStream out = Files.newOutputStream(to)) {
      int n;
      while ((n = in.read(buf)) != -1) {
        out.write(buf, 0, n);
        total += n;
      }
    }
    return total;
  }

  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      Path a = dir.resolve("a.txt");
      Files.writeString(a, "x".repeat(2500));
      System.out.println(copy(a, dir.resolve("b.txt")));
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
