package patterns.behavioral.iterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

// ANCHOR: line-iterable
/** Real I/O: lazily reads one line at a time; each {@code iterator()} reopens the file. */
public final class LineIterable implements Iterable<String> {
  private final Path path;

  public LineIterable(Path path) {
    this.path = path;
  }

  @Override
  public LineIterator iterator() {
    try {
      return new LineIterator(Files.newBufferedReader(path, StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** Covariant return type: still usable in for-each, also closable in try-with-resources. */
  public static final class LineIterator implements Iterator<String>, AutoCloseable {
    private final BufferedReader reader;
    private String line;
    private boolean closed;

    LineIterator(BufferedReader reader) {
      this.reader = reader;
      advance();
    }

    private void advance() {
      try {
        line = reader.readLine();
        if (line == null) close();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    @Override
    public boolean hasNext() {
      return line != null;
    }

    @Override
    public String next() {
      if (line == null) throw new NoSuchElementException();
      String current = line;
      advance();
      return current;
    }

    public boolean isClosed() {
      return closed;
    }

    @Override
    public void close() {
      try {
        reader.close();
        closed = true;
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }
}
// ANCHOR_END: line-iterable
