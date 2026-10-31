package patterns.behavioral.iterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LineDemo {
  // ANCHOR: line-demo
  public static void main(String[] args) throws IOException {
    Path f = Files.createTempFile("lines", ".txt");
    try {
      Files.writeString(f, "alpha\nbeta\ngamma\n");
      LineIterable lines = new LineIterable(f);

      int n = 0;
      for (String s : lines) System.out.println(++n + ": " + s);

      try (LineIterable.LineIterator it = lines.iterator()) {
        System.out.println("first only: " + it.next());
        System.out.println("closed before block ends? " + it.isClosed());
      }

      LineIterable.LineIterator full = lines.iterator();
      while (full.hasNext()) full.next();
      System.out.println("closed after last line: " + full.isClosed());
    } finally {
      Files.deleteIfExists(f);
    }
  }
  // ANCHOR_END: line-demo
}
