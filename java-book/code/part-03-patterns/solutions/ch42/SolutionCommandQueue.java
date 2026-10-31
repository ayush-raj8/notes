package solutions.ch42;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;

/** Exercise 4: queue commands, run them later, append results to a real file. */
public final class SolutionCommandQueue {
  public static void main(String[] args) throws IOException {
    Path out = Files.createTempFile("queue", ".log");
    try {
      Queue<Runnable> q = new ArrayDeque<>();
      for (String job : new String[] {"compile", "test", "package"}) {
        q.add(
            () -> {
              try {
                Files.writeString(out, job + "\n", java.nio.file.StandardOpenOption.APPEND);
              } catch (IOException e) {
                throw new java.io.UncheckedIOException(e);
              }
            });
      }
      System.out.println("queued " + q.size() + ", file size " + Files.size(out));
      q.forEach(Runnable::run);
      System.out.print(Files.readString(out));
    } finally {
      Files.deleteIfExists(out);
    }
  }
}
