package patterns.behavioral.state;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LogSessionDemo {
  // ANCHOR: log-demo
  public static void main(String[] args) throws IOException {
    Path f = Files.createTempFile("session", ".log");
    try {
      LogSession s = new LogSession();
      System.out.println(s.state());
      try {
        s.write("too early");
      } catch (IllegalStateException e) {
        System.out.println(e.getMessage());
      }
      s.open(f);
      s.write("one");
      s.write("two");
      try {
        s.open(f);
      } catch (IllegalStateException e) {
        System.out.println(e.getMessage());
      }
      System.out.println(s.state());
      s.close();
      s.close();
      System.out.println(s.state());
      System.out.println(Files.readAllLines(f));
    } finally {
      Files.deleteIfExists(f);
    }
  }
  // ANCHOR_END: log-demo
}
