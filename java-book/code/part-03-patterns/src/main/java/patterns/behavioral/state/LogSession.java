package patterns.behavioral.state;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// ANCHOR: log-session
/** Real I/O state machine: a writer that is only usable while the session is OPEN. */
public final class LogSession {
  private interface St {
    String name();

    St open(Path p) throws IOException;

    St write(String line) throws IOException;

    St close() throws IOException;
  }

  private static final St CLOSED =
      new St() {
        public String name() {
          return "CLOSED";
        }

        public St open(Path p) throws IOException {
          return new Open(Files.newBufferedWriter(p, StandardCharsets.UTF_8));
        }

        public St write(String line) {
          throw new IllegalStateException("write on CLOSED");
        }

        public St close() {
          return this;
        }
      };

  private static final class Open implements St {
    private final BufferedWriter out;

    Open(BufferedWriter out) {
      this.out = out;
    }

    public String name() {
      return "OPEN";
    }

    public St open(Path p) {
      throw new IllegalStateException("already OPEN");
    }

    public St write(String line) throws IOException {
      out.write(line);
      out.newLine();
      return this;
    }

    public St close() throws IOException {
      out.close();
      return CLOSED;
    }
  }

  private St state = CLOSED;

  public void open(Path p) throws IOException {
    state = state.open(p);
  }

  public void write(String line) throws IOException {
    state = state.write(line);
  }

  public void close() throws IOException {
    state = state.close();
  }

  public String state() {
    return state.name();
  }
}
// ANCHOR_END: log-session
