package solutions.ch46;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Scanner;

/** Exercise 5: iterate the words of a real file with Scanner (an Iterator over tokens). */
public final class SolutionWordIterator {
  public static void main(String[] args) throws IOException {
    Path f = Files.createTempFile("words", ".txt");
    try {
      Files.writeString(f, "to be or\nnot  to be\n");
      int count = 0;
      StringBuilder longest = new StringBuilder();
      try (Scanner sc = new Scanner(f)) {
        Iterator<String> words = sc;
        while (words.hasNext()) {
          String w = words.next();
          count++;
          if (w.length() > longest.length()) longest = new StringBuilder(w);
        }
      }
      System.out.println(count + " words, longest=" + longest);
    } finally {
      Files.deleteIfExists(f);
    }
  }
}
