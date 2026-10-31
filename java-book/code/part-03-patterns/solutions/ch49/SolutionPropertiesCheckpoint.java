package solutions.ch49;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Exercise 4: a text-file memento (java.util.Properties) instead of Java serialization. */
public final class SolutionPropertiesCheckpoint {
  static final class Settings {
    String name = "default";
    int volume = 5;
  }

  static void save(Settings s, Path f) throws IOException {
    Properties p = new Properties();
    p.setProperty("name", s.name);
    p.setProperty("volume", Integer.toString(s.volume));
    try (Writer w = Files.newBufferedWriter(f)) {
      p.store(w, null);
    }
  }

  static void load(Settings s, Path f) throws IOException {
    Properties p = new Properties();
    try (Reader r = Files.newBufferedReader(f)) {
      p.load(r);
    }
    s.name = p.getProperty("name");
    s.volume = Integer.parseInt(p.getProperty("volume"));
  }

  public static void main(String[] args) throws IOException {
    Path f = Files.createTempFile("settings", ".properties");
    try {
      Settings s = new Settings();
      s.name = "studio";
      s.volume = 11;
      save(s, f);
      s.name = "broken";
      s.volume = 0;
      load(s, f);
      System.out.println(s.name + " " + s.volume);
    } finally {
      Files.deleteIfExists(f);
    }
  }
}
