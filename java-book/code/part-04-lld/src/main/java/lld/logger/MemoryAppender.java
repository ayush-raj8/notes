package lld.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: memory-appender
/** Keeps formatted lines in memory; the test double for every other appender. */
public final class MemoryAppender implements Appender {
  private final LogFormatter formatter;
  private final List<String> lines = Collections.synchronizedList(new ArrayList<>());

  public MemoryAppender(LogFormatter formatter) {
    this.formatter = formatter;
  }

  @Override
  public void append(LogEvent event) {
    lines.add(formatter.format(event));
  }

  public List<String> lines() {
    synchronized (lines) {
      return List.copyOf(lines);
    }
  }
}
// ANCHOR_END: memory-appender
