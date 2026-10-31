package patterns.behavioral.template;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class FileReportDemo {
  // ANCHOR: file-report
  public static void main(String[] args) throws IOException {
    Path f = Files.createTempFile("report", ".html");
    try {
      try (BufferedWriter w = Files.newBufferedWriter(f, StandardCharsets.UTF_8)) {
        new HtmlReport().write(w, List.of("café", "naïve", ""));
      }
      System.out.println("bytes: " + Files.size(f));
      Files.readAllLines(f, StandardCharsets.UTF_8).forEach(System.out::println);
    } finally {
      Files.deleteIfExists(f);
    }
  }
  // ANCHOR_END: file-report
}
