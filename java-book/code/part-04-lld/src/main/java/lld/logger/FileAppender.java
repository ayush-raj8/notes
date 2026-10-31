package lld.logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// ANCHOR: file-appender
/** Appends one line per event (UTF-8). Close it to flush. */
public final class FileAppender implements Appender, AutoCloseable {
  private final BufferedWriter out;
  private final LogFormatter formatter;

  public FileAppender(Path file, LogFormatter formatter) throws IOException {
    this.out = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    this.formatter = formatter;
  }

  @Override
  public synchronized void append(LogEvent event) {
    try {
      out.write(formatter.format(event));
      out.newLine();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public synchronized void close() throws IOException {
    out.close();
  }
}
// ANCHOR_END: file-appender
