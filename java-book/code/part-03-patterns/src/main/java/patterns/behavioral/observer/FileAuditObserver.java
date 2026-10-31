package patterns.behavioral.observer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// ANCHOR: file-audit
/** An observer with a real side effect: appends each event to a file. */
public final class FileAuditObserver implements PriceObserver {
  private final Path file;

  public FileAuditObserver(Path file) {
    this.file = file;
  }

  @Override
  public void onPrice(String symbol, int price) {
    try {
      Files.writeString(
          file,
          symbol + "," + price + System.lineSeparator(),
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
// ANCHOR_END: file-audit
