package oop.io.solutions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import oop.io.IoTemp;

/** Count words in a text file; words are separated by whitespace. */
public final class SolutionWordCount {
  static Map<String, Integer> count(Path file) throws IOException {
    Map<String, Integer> counts = new TreeMap<>();
    for (String line : Files.readAllLines(file)) {
      for (String w : line.trim().split("\\s+")) {
        if (!w.isEmpty()) {
          counts.merge(w.toLowerCase(), 1, Integer::sum);
        }
      }
    }
    return counts;
  }

  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      Path f = dir.resolve("t.txt");
      Files.writeString(f, "the cat\nThe  dog\n\nthe end\n");
      System.out.println(count(f));
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
