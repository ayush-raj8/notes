package patterns.structural.facade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// ANCHOR: jdk-facade-demo
public final class JdkFacadeDemo {
  public static void main(String[] args) throws IOException {
    // Files hides streams, buffers, charsets and close().
    Path tmp = Files.createTempFile("facade", ".txt");
    try {
      Files.writeString(tmp, "a\nb\nc");
      System.out.println(Files.readString(tmp).length());
      System.out.println(Files.readAllLines(tmp));
    } finally {
      Files.delete(tmp);
    }
    // String.format hides java.util.Formatter.
    System.out.println(String.format("%05.1f|%-4s|%3d", 3.14159, "ab", 7));
  }
}
// ANCHOR_END: jdk-facade-demo
