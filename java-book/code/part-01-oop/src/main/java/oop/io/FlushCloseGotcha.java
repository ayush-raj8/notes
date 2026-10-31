package oop.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// ANCHOR: flush-close
public final class FlushCloseGotcha {
  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      Path f = dir.resolve("out.txt");

      BufferedWriter leaky = new BufferedWriter(new FileWriter(f.toFile()));
      leaky.write("hello");
      System.out.println("not flushed: " + Files.size(f));
      leaky.flush();
      System.out.println("after flush: " + Files.size(f));
      leaky.close();

      try (BufferedWriter w = new BufferedWriter(new FileWriter(f.toFile()))) {
        w.write("hi");
      }
      System.out.println("after try-with-resources: " + Files.size(f));

      try (FileWriter w = new FileWriter(f.toFile(), true)) {
        w.write("++");
      }
      System.out.println("append mode: " + Files.readString(f));

      try (FileWriter w = new FileWriter(f.toFile())) {
        w.write("x");
      }
      System.out.println("default truncates: " + Files.readString(f));

      BufferedWriter closed = new BufferedWriter(new FileWriter(f.toFile()));
      closed.close();
      try {
        closed.write("late");
      } catch (IOException e) {
        System.out.println("write after close: " + e.getMessage());
      }
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
// ANCHOR_END: flush-close
