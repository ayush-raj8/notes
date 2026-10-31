package oop.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// ANCHOR: char-streams
public final class CharStreamsDemo {
  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      Path f = dir.resolve("lines.txt");
      try (BufferedWriter w = new BufferedWriter(new FileWriter(f.toFile(), StandardCharsets.UTF_8))) {
        w.write("one");
        w.newLine();
        w.write("two");
        w.newLine();
      }
      try (BufferedReader r = new BufferedReader(new FileReader(f.toFile(), StandardCharsets.UTF_8))) {
        String line;
        while ((line = r.readLine()) != null) {
          System.out.println("line: " + line);
        }
      }

      try (FileWriter append = new FileWriter(f.toFile(), StandardCharsets.UTF_8, true)) {
        append.write("three\n");
      }
      List<String> all = Files.readAllLines(f);
      System.out.println(all);

      StringWriter sw = new StringWriter();
      try (PrintWriter pw = new PrintWriter(sw)) {
        pw.printf("%d-%s%n", 7, "x");
      }
      System.out.print(sw);

      byte[] raw = "bridge".getBytes(StandardCharsets.UTF_8);
      try (BufferedReader r =
          new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(raw), StandardCharsets.UTF_8))) {
        System.out.println(r.readLine());
      }
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
// ANCHOR_END: char-streams
