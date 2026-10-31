package patterns.behavioral.observer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileAuditDemo {
  // ANCHOR: file-audit-demo
  public static void main(String[] args) throws IOException {
    Path log = Files.createTempFile("audit", ".csv");
    try {
      PriceSubject subject = new PriceSubject();
      subject.attach(new FileAuditObserver(log));
      subject.attach((s, p) -> System.out.println("seen " + s));
      subject.publish("ACME", 90);
      subject.publish("INIT", 7);
      System.out.println("file lines:");
      Files.readAllLines(log).forEach(System.out::println);
    } finally {
      Files.deleteIfExists(log);
    }
  }
  // ANCHOR_END: file-audit-demo
}
